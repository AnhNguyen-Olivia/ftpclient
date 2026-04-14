package ftpclient.com;
import java.io.*;
import java.net.*;
import java.util.*;

public class FtpClient {

    private Socket socket;
    private BufferedWriter Network_out;
    private BufferedReader Network_in;

    public void connect(String host, int port) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), FtpConfig.CONNECT_TIMEOUT_MS);
        socket.setSoTimeout(FtpConfig.READ_TIMEOUT_MS);
        
        Network_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Network_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String greeting = readResponse(Network_in);
        if (greeting == null) {
            throw new IOException("[System]> Server closed the connection before sending a greeting.");
        }
        printResponse(greeting);
    }
    
    /**
     * Method to disconnect from the FTP server. 
     * It closes the socket and associated streams.
     * @throws IOException
     */
    public void disconnect() throws IOException{
        if(socket != null) socket.close();
    }

    public boolean login(String user, String password) throws IOException {
        sendCommand("USER " + user);
        String response = readResponse(Network_in);
        if (response == null) {
            throw new IOException("[System]> No response received after USER command.");
        }
        printResponse(response);
        if(response.startsWith("331")){
            sendCommand("PASS " + password);
            response = readResponse(Network_in);
            if (response == null) {
                throw new IOException("[System]> No response received after PASS command.");
            }
            printResponse(response);
            return response.startsWith("230");
        }else if(response.startsWith("230")){
            return true;
        }
        return false;
    }

    /**
     * Method to get the current working directory on the FTP server.
     * @return
     * @throws IOException
     */
    public String pwd() throws IOException {
        sendCommand("PWD");
        String response = readResponse(Network_in);
        printResponse(response);
        return response;
    }

    /**
     * Method to change the current working directory on the FTP server.
     * @param dir
     * @return
     * @throws IOException
     */
    public String cd(String dir) throws IOException {
        sendCommand("CWD " + dir);
        String response = readResponse(Network_in);
        printResponse(response);
        return response;
    }

    /**
     * Method to delete a file on the FTP server.
     * @param fileName
     * @return
     * @throws IOException
     */
    public String delete(String fileName) throws IOException {
        sendCommand("DELE " + fileName);
        String response = readResponse(Network_in);
        printResponse(response);
        return response;
    }

    /**
     * Method to create a new directory on the FTP server.
     * @param dirName
     * @return
     * @throws IOException
     */
    public String mkdir(String dirName)throws IOException{
        sendCommand("MKD " + dirName);
        String response = readResponse(Network_in);
        printResponse(response);
        return response;
    }

    /**
     * Method to remove a directory on the FTP server.
     * @param dirName
     * @return
     * @throws IOException
     */
    public String rmdir(String dirName)throws IOException{
        sendCommand("RMD " + dirName);
        String response = readResponse(Network_in);
        printResponse(response);
        return response;
    }

    /**
     * Method to quit the FTP session. 
     * It sends the QUIT command and then disconnects from the server.
     * @return
     * @throws IOException
     */
    public String quit() throws IOException {
        sendCommand("QUIT");
        String response = readResponse(Network_in);
        printResponse(response);
        disconnect();
        return response;
    }

    /**
     * Method to get the size of a file on the FTP server.
     * @param fileName
     * @return
     * @throws IOException
     */
    public long getSize(String fileName) throws IOException{
        sendCommand("SIZE " + fileName);
        String response = readResponse(Network_in);
        printResponse(response);
        if(response == null || !response.startsWith("213")) return -1;
        // FIX: was "//s+" (wrong) — must be "\\s+" to match whitespace
        long size = Long.parseLong(response.trim().split("\\s+")[1]);
        return size;
    }

    /**
     * Method to list the files in the current directory on the FTP server.
     * @return
     * @throws IOException
     */
    public List<String> listDirectory() throws IOException{
        List<String> fileList = new ArrayList<>();
        try{
            Socket dataSocket = createDataSocket();
            sendCommand("LIST");
            String response = readResponse(Network_in);
            printResponse(response);
            if(!response.startsWith("150")){
                throw new IOException("[System]> Failed to list directory: " + response);
            }
            BufferedReader dataIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            String fileName;
            while((fileName = dataIn.readLine()) != null){
                fileList.add(fileName);
            }
            dataIn.close();
            dataSocket.close();
            String completionResponse = readResponse(Network_in);
            printResponse(completionResponse);

        } catch (IOException e) {
            System.out.println("[System]> Error listing directory: " + e.getMessage());
            e.printStackTrace();
        }
        return fileList;
    }

    /**
     * Method to download a file from the FTP server. 
     * It creates a data socket, sends the RETR command, 
     * and saves the file to the local download directory.
     * Add a spinner and percentage display to show download progress. :D
     * @param fileName
     * @return
     * @throws Exception
     */
    public File downloadFile(String fileName) throws Exception{
        File downloadFolder = new File(FtpConfig.DEFAULT_DOWNLOAD_DIRECTORY);
        if(!downloadFolder.exists()) downloadFolder.mkdir();
        File localFile = new File(downloadFolder, fileName);

        long totalSize = getSize(fileName);

        sendCommand("TYPE I");
        String typeResponse = readResponse(Network_in);
        printResponse(typeResponse);
        Socket dataSocket = createDataSocket();
        
        sendCommand("RETR " + fileName);
        String response = readResponse(Network_in);
        printResponse(response);
        if(!response.startsWith("150")){
            dataSocket.close();
            throw new IOException("[System]> Failed to download file: " + response);
        }
        
        InputStream dataIn = dataSocket.getInputStream();
        FileOutputStream fileOut = new FileOutputStream(localFile);
        byte[] buffer = new byte[FtpConfig.BUFFER_SIZE];
        int bytesRead;
        long transferred = 0;
        int spinnerIndex = 0;
        System.out.println("[System]> Downloading " + fileName + "...");
        while((bytesRead = dataIn.read(buffer)) != -1){
            fileOut.write(buffer, 0, bytesRead);
            transferred += bytesRead;
            printTransferProgress("Downloading", fileName, transferred, totalSize, spinnerIndex++);
        }
        System.out.println();
        fileOut.close();
        dataIn.close();
        dataSocket.close();
        String completionResponse = readResponse(Network_in);
        printResponse(completionResponse);
        return localFile;
    }

    /**
     * Method to upload a file to the FTP server. 
     * It creates a data socket, sends the STOR command, 
     * and reads the file from the local upload directory to send it to the server.
     * Add a spinner and percentage display to show upload progress. :D
     * @param fileName
     * @throws IOException
     */
    public void uploadFile(String fileName) throws IOException{
        File uploadFolder = new File(FtpConfig.DEFAULT_UPLOAD_DIRECTORY);
        if(!uploadFolder.exists()) uploadFolder.mkdir();
        
        File fileToUpload = new File(uploadFolder, fileName);
        if(!fileToUpload.exists()){
            throw new FileNotFoundException("[System]> File not found in upload directory: " + fileToUpload.getAbsolutePath());
        }

        sendCommand("TYPE I");
        String typeResponse = readResponse(Network_in);
        printResponse(typeResponse);
        Socket dataSocket = createDataSocket();

        sendCommand("STOR " + fileToUpload.getName());
        String response = readResponse(Network_in);
        printResponse(response);
        if(!response.startsWith("150")){
            dataSocket.close();
            throw new IOException("[System]> Failed to upload file: " + response);
        }

        /*By taking the full size and count the bytes transferred, we can display the progress*/
        FileInputStream fileIn = new FileInputStream(fileToUpload);
        OutputStream dataOut = dataSocket.getOutputStream();
        byte[] buffer = new byte[FtpConfig.BUFFER_SIZE];
        int bytesRead;
        long totalSize = fileToUpload.length();
        long transferred = 0;
        int spinnerIndex = 0;
        System.out.println("[System]> Uploading " + fileToUpload.getName() + "...");
        while((bytesRead = fileIn.read(buffer)) != -1){
            dataOut.write(buffer, 0, bytesRead);
            transferred += bytesRead;
            printTransferProgress("Uploading", fileToUpload.getName(), transferred, totalSize, spinnerIndex++);
        }
        System.out.println();
        fileIn.close();
        dataOut.close();
        dataSocket.close();
        String completionResponse = readResponse(Network_in);
        printResponse(completionResponse);
    }


    /**
     * Private method to send a command to the FTP server. 
     * It appends the necessary CRLF and flushes the output stream.
     * @param cmd
     * @throws IOException
     */
    private void sendCommand(String cmd) throws IOException{
        Network_out.write(cmd + "\r\n");
        Network_out.flush();
    }

    private void printResponse(String response) {
        if (response == null || response.isBlank()) {
            return;
        }
        for (String line : response.split("\\R")) {
            if (!line.isBlank()) {
                System.out.println("[Server]> " + line);
            }
        }
    }

    /**
     * Private method to print the progress of file transfers (both upload and download).
     * @param action
     * @param fileName
     * @param transferred
     * @param totalSize
     * @param spinnerIndex
     */
    private static void printTransferProgress(String action, String fileName, long transferred, long totalSize, int spinnerIndex) {
        String[] progressChar = {"|", "/", "-", "\\"};
        String spinner = progressChar[spinnerIndex % progressChar.length];

        if (totalSize > 0) {
            int percent = (int) (transferred * 100 / totalSize);
            System.out.print("\r[System]> " + action + " " + fileName + ": " + percent + "% " + spinner + " (" + transferred + "/" + totalSize + " bytes)");
        } else {
            System.out.print("\r[System]> " + action + " " + fileName + ": " + transferred + " bytes " + spinner);
        }
    }

    /**
     * Private method that reads the full response from the FTP server, 
     * handling multi-line responses according to the FTP protocol.
     * @param in
     * @return
     * @throws IOException
     */
    private String readResponse(BufferedReader in) throws IOException{
        String response = in.readLine();

        if(response == null ||
           response.length() < 4 || 
           !Character.isDigit(response.charAt(0)) || 
           !Character.isDigit(response.charAt(1)) || 
           !Character.isDigit(response.charAt(2))){
            return response;
        }

        if (response.charAt(3) != '-') {
            return response;
        }

        String responseCode = response.substring(0, 3);
        StringBuilder fullResponse = new StringBuilder(response).append("\n");
        while(true){
            String nextResponse = in.readLine();
            if(nextResponse == null){
                break;
            }
            
            fullResponse.append(nextResponse).append("\n");
            
            if(nextResponse.length() >= 4 &&
               nextResponse.startsWith(responseCode) && 
               nextResponse.charAt(3) == ' '){
                break;
            }
        }
        return fullResponse.toString();
    }

    /**
     * Private method to create a data socket for file transfers. 
     * It sends the PASV command and parses the server's response to establish a new socket connection for data transfer.
     * @return
     * @throws IOException
     */
    private Socket createDataSocket() throws IOException{
        sendCommand("PASV");
        String response = readResponse(Network_in);
        if(!response.startsWith("227")){
            throw new IOException("[System]> Failed to enter passive mode: " + response);
        }

        int start = response.indexOf('(');
        int end = response.indexOf(')');

        String[] PasvInfo = response.substring(start + 1, end).split(",");
        String ip = PasvInfo[0] + "." + PasvInfo[1] + "." + PasvInfo[2] + "." + PasvInfo[3];
        int port = Integer.parseInt(PasvInfo[4].trim()) * 256 + Integer.parseInt(PasvInfo[5].trim());
        return new Socket(ip, port);
    }
}
package ftpclient.com;

import java.io.*;
import java.net.*;

/**
 * Helper class that contains various static methods to assist with FTP operations such as sending commands, 
 * reading server replies, handling file uploads/downloads, and managing progress display.
 * This class is designed to keep the main logic in the Main class clean and organized by abstracting away common tasks 
 * and utilities related to FTP communication and file handling.
 * /ᐠ - ˕ -マ - ᶻ 𝗓 𐰁
 * @author Anh_Nguyen
 */
public class helper {
    /**
     * Helper to send command
     * @param out
     * @param cmd
     * @throws IOException
     */
    static public void sendCommand(BufferedWriter out, String cmd) throws IOException{
        out.write(cmd + "\r\n");
        out.flush();
    }

    /**
     * Helper to read and print out server reply. Run in a loop and only break if server send a ' 'at index 3
     * @param in
     * @throws IOException
     */
    static String readReply(BufferedReader in) throws IOException{
        String serverText = in.readLine();
        if(serverText == null) return null;

        System.out.println("[Server]> " + serverText);
        if(serverText.length() < 3 || !Character.isDigit(serverText.charAt(0)) || !Character.isDigit(serverText.charAt(1)) || !Character.isDigit(serverText.charAt(2))){
            return serverText;
        }

        String code = serverText.substring(0, 3);
        boolean multiline = serverText.length() >= 4 && serverText.charAt(3) == '-';
        if(!multiline) return serverText;

        while(true){
            String line = in.readLine();
            if(line == null) return serverText;

            System.out.println("[Server]> " + line);
            serverText = line;

            if(line.length() >= 4 && line.startsWith(code) && line.charAt(3) == ' '){
                return serverText;
            }
        }
    }

    /**
     * method to automatic login as Custom user
     * @param in
     * @param out
     * @throws IOException
     */
    static public void CustomLogin(BufferedReader in, BufferedWriter out) throws IOException{
        helper.readReply(in);
        sendCommand(out, "USER dlpuser");
        readReply(in);
            
        sendCommand(out, "PASS rNrKYTX9g7z3RgJRmxWuGHbeu");
        readReply(in);
    }

    /**
     * method to automatic login as Custom userAnonymous
     * @param in
     * @param out
     * @throws IOException
     */
    static public void AnonymousLogin(BufferedReader in, BufferedWriter out) throws IOException{
        helper.readReply(in);
        sendCommand(out, "USER anonymous");
        readReply(in);
            
        sendCommand(out, "PASS guest");
        readReply(in);
    }

    /**
     * Helper to create data socket, send PASV command and parse the respond to get ip and port, then create a new socket for data transfer
     * @param in
     * @param out
     * @return
     * @throws Exception
     */
    static public Socket createDataSocket(BufferedReader in, BufferedWriter out) throws Exception{
        sendCommand(out, "PASV");
        String serverRespond = readReply(in);
        if(serverRespond == null || !serverRespond.startsWith("227")){
            throw new Exception("PASV failed. Server reply: " + serverRespond);
        }

        int startIndex = serverRespond.indexOf('(');
        int endIndex = serverRespond.indexOf(')');

        if(startIndex == -1 || endIndex == -1){
            throw new Exception("Server did not return a valid PASV address. The respond was: " + serverRespond);
        }
        
        String[] parts = serverRespond.substring(startIndex + 1, endIndex).split(",");
        if(parts.length != 6){
            throw new Exception("Invalid PASV tuple. Server reply: " + serverRespond);
        }

        String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
        
        int FifthNum = Integer.parseInt(parts[4]);
        int SixthNum = Integer.parseInt(parts[5]);
        int port = FifthNum * 256 + SixthNum;

        System.out.println("[System]> PASV: " + ip + ":" + port);
        return new Socket(ip, port);
    }

    /**
     * Helper to list files in current directory, it will create a data socket, 
     * send LIST command and print out the respond from data socket until it is closed by server, 
     * then read the final respond from control socket
     * @param in
     * @param out
     * @throws Exception
     */
    static public void listDirectory(BufferedReader in, BufferedWriter out) throws Exception {
        try {
            Socket dataSocket = createDataSocket(in, out);
            sendCommand(out, "LIST");
            readReply(in);

            System.out.println("================================================================================================");
            try (BufferedReader dataIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {
                String directoryName;
                while ((directoryName = dataIn.readLine()) != null) {
                    System.out.println(directoryName);
                }
            } finally {
                dataSocket.close();
                
            }
            System.out.println("================================================================================================");

            readReply(in);
        } catch (Exception e) {
            System.out.println("[System]> Could not list files because something went wrong. " + e.getMessage());
        }
        
    }

    /**
     * Helper to download file, it will first check if there is a Download folder and create one if not, 
     * then it will get the file size, create a data socket, 
     * send RETR command and read from data socket and write to local file until server close the connection, 
     * at the same time it will print out the download progress. Finally it will read the final respond from control socket
     * @param in
     * @param out
     * @param fileName
     * @throws IOException
     */
    static public void getFile(BufferedReader in, BufferedWriter out, String fileName) throws IOException{
        File downloadFolder = new File("Download");
        if(!downloadFolder.exists()){
            downloadFolder.mkdir();
        }

        File locaFile = new File(downloadFolder, fileName);
        
        try {

            sendCommand(out, "TYPE I");
            readReply(in);

            try (Socket dataSocket = createDataSocket(in, out)) {
                sendCommand(out, "RETR " + fileName);
                String serverRespon = readReply(in);
                if(serverRespon != null && (serverRespon.startsWith("1") || serverRespon.startsWith("2"))){

                    try (InputStream dataIn = dataSocket.getInputStream();
                        FileOutputStream FileOut = new FileOutputStream(locaFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;

                        System.out.println("[System]> Downloading " + fileName + " from server...");

                        /* Print download progress */
                        while((bytesRead = dataIn.read(buffer)) != -1){
                            FileOut.write(buffer, 0, bytesRead);
            
                        }
                    }

                    readReply(in);
                    System.out.println("[System]> " + fileName + " should be in " + locaFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.out.println("[System]> Could not download file, either because file does not exist or because something went wrong. " + e.getMessage());
        }
    }

    /***
     * Helper to upload file, it will first check if the file exist in current directory or Upload folder,
     * @param in
     * @param out
     * @param fileName
     * @throws IOException
     */
    static public void uploadFile(BufferedReader in, BufferedWriter out, String fileName) throws IOException{
        File localFile = new File(fileName);
        if(!localFile.exists()){
            File uploadFolder = new File("Upload");
            localFile = new File(uploadFolder, fileName);
        }

        if(!localFile.exists()){
            System.out.println("[System]> " + fileName + " not found. Try placing it in the Upload folder or provide a full path.");
        }else{
            try {
                String remoteFileName = localFile.getName();
                sendCommand(out, "TYPE I");
                readReply(in);

                try (Socket dataSocket = createDataSocket(in, out)) {
                    sendCommand(out, "STOR " + remoteFileName);
                    String serverRespon = readReply(in);

                    if(serverRespon != null && (serverRespon.startsWith("1") || serverRespon.startsWith("2"))){

                        try (FileInputStream FileIn = new FileInputStream(localFile);
                            OutputStream dataOut = dataSocket.getOutputStream()) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;

                            System.out.println("[System]> Uploading " + localFile.getAbsolutePath() + " to server as " + remoteFileName + "...");
                            while((bytesRead = FileIn.read(buffer)) != -1){
                                dataOut.write(buffer, 0, bytesRead);
                            }
                        }

                        readReply(in);
                    }
                }
            } catch (Exception e) {
                System.out.println("[System]> Could not upload file, because something went wrong. " + e.getMessage());
            }
        }
    }

    /**
     * Helper to print out the list of available commands and their usage instructions to the console. 
     * This method is called when the user types "help" to provide guidance on how to use the FTP client.
     */
    public static void printHelp() {
        System.out.println("[System]> Available commands:");
        System.out.println("[System]>   help                 - Show this help message");
        System.out.println("[System]>   pwd                  - Print the current remote directory");
        System.out.println("[System]>   ls                   - List files in the current remote directory");
        System.out.println("[System]>   cd <dirName>         - Change the current remote directory");
        System.out.println("[System]>   get <fileName>       - Download a file from the server");
        System.out.println("[System]>   put <fileName>       - Upload a local file to the server");
        System.out.println("[System]>   delete <fileName>    - Delete a file on the server");
        System.out.println("[System]>   mkdir <dirName>      - Create a directory on the server");
        System.out.println("[System]>   rmdir <dirName>      - Remove a directory on the server");
        System.out.println("[System]>   quit                 - Close the FTP connection and exit");
    }
}

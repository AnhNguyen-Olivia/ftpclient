package ftpclient.com;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Main class for the FTP client application. This class handles user input, manages the connection to the FTP server, 
 * and processes FTP commands. It supports both anonymous and custom logins, and provides a command-line interface for users 
 * to interact with the FTP server. ฅ^•ﻌ•^ฅ
 */
public class Main {

    /**
     * Utility method to strip matching quotes from a string. 
     * This is used to allow users to input arguments with spaces by enclosing them in quotes.
     * @param value The input string to process.
     * @return The string with matching quotes removed, if applicable.
     */
    private static String stripMatchingQuotes(String value) {
        if (value == null || value.length() < 2) {
            return value;
        }

        char first = value.charAt(0);
        char last = value.charAt(value.length() - 1);
        if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        try{
            Scanner sc = new Scanner(System.in);
            System.out.println("[System]> Before we start, do you want to login as anoymous? [y/n]");
            System.out.print("> ");
            String choice = sc.nextLine();

            Socket socket;
            switch(choice){
                case "y":
                    socket = new Socket("ftp.gnu.org", 21);
                    break;
                case "n":
                    socket = new Socket("ftp.dlptest.com", 21);
                    break;
                default:
                    System.out.println("[System]> Invalid choice. Please enter y or n.");
                    return;
            }

            BufferedWriter Network_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader Network_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            

            switch(choice){
                case "y": helper.AnonymousLogin(Network_in, Network_out); break;
                case "n": helper.CustomLogin(Network_in, Network_out); break;
            }

            while(true){
                System.out.print("> ");
                String text = sc.nextLine().trim();
                if (text.isEmpty()) {
                    continue;
                }

                String[] inputParts = text.split("\\s+", 2);
                String command = inputParts[0].toLowerCase();
                String argument = inputParts.length > 1 ? stripMatchingQuotes(inputParts[1].trim()) : "";

                if(command.equalsIgnoreCase("quit")){
                    helper.sendCommand(Network_out, command.toUpperCase());
                    helper.readReply(Network_in);
                    break;

                }else if(command.equalsIgnoreCase("pwd")){
                    helper.sendCommand(Network_out, command.toUpperCase());
                    helper.readReply(Network_in);

                }else if(command.equalsIgnoreCase("cd")){
                    if(!argument.isEmpty()){
                        String folderName = argument;
                        helper.sendCommand(Network_out, "CWD " + folderName);
                        helper.readReply(Network_in);
                    }else{
                        System.out.println("[System]> Error: Please specify a directory [command: cd <folderName>]");
                    }

                }else if(command.equalsIgnoreCase("ls")){
                    helper.listDirectory(Network_in, Network_out);

                }else if(command.equalsIgnoreCase("help")){
                    helper.printHelp();

                }else if(command.equalsIgnoreCase("get")){
                    if(!argument.isEmpty()){
                        String fileName = argument;
                        helper.getFile(Network_in, Network_out, fileName);
                    }else{
                        System.out.println("[System]> Error: Please specify a file name [command: get <fileName>]");
                    }

                }else if(command.equalsIgnoreCase("put")){
                    if(!argument.isEmpty()){
                        String fileName = argument;
                        helper.uploadFile(Network_in, Network_out, fileName);
                    }else{
                        System.out.println("[System]> Error: Please specify a file name [command: put <fileName>]");
                    }

                }else if(command.equalsIgnoreCase("delete")){
                    if(!argument.isEmpty()){
                        String fileName = argument;
                        helper.sendCommand(Network_out, "DELE " + fileName);
                        helper.readReply(Network_in);
                    }else{
                        System.out.println("[System]> Error: Please specify a file [command: delete <fileName>]");
                    }
                }else if(command.equalsIgnoreCase("mkdir")){
                    if(!argument.isEmpty()){
                        String dirName = argument;
                        helper.sendCommand(Network_out, "MKD " + dirName);
                        helper.readReply(Network_in);
                    }else{
                        System.out.println("[System]> Error: Please specify a directory name [command: mkdir <fileName>]");
                    }
                }else if(command.equalsIgnoreCase("rmdir")){
                    if(!argument.isEmpty()){
                        String dirName = argument;
                        helper.sendCommand(Network_out, "RMD " + dirName);
                        helper.readReply(Network_in);
                    }else{
                        System.out.println("[System]> Error: Please specify a directory name [command: rmdir <fileName>]");
                    }
                }else{
                    helper.sendCommand(Network_out, text);
                    helper.readReply(Network_in);
                }
            }
            socket.close();
        }catch(Exception e){e.printStackTrace();}
    }
}
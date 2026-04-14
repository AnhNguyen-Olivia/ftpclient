package ftpclient.com;
import java.util.*;
import java.io.*;

public class FtpCLI {
    public void startCli(){
        FtpClient ftpClient = new FtpClient();
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("[System]> Before we start, do you want to login as anoymous? [y/n]");
            System.out.print("> ");
            String choice = sc.nextLine().trim().toLowerCase();

            switch(choice){
                case "y":
                    System.out.println("[System]> Connecting to anonymous FTP server...");
                    ftpClient.connect(FtpConfig.ANONYMOUS_HOST, FtpConfig.FTP_PORT);

                    System.out.println("[System]> Logging in as anonymous...");
                    if (!ftpClient.login(FtpConfig.DEFAULT_ANONYMOUS_USERNAME, FtpConfig.DEFAULT_ANONYMOUS_PASSWORD)) {
                        System.out.println("[System]> Anonymous login failed.");
                        return;
                    }
                    System.out.println("[System]> Anonymous login successful.");
                    break;
                case "n":
                    System.out.println("[System]> Connecting to custom FTP server...");
                    ftpClient.connect(FtpConfig.CUSTOM_HOST, FtpConfig.FTP_PORT);

                    System.out.println("[System]> Logging in with custom credentials...");
                    if (!ftpClient.login(FtpConfig.DEFAULT_CUSTOM_USERNAME, FtpConfig.DEFAULT_CUSTOM_PASSWORD)) {
                        System.out.println("[System]> Custom login failed.");
                        return;
                    }
                    System.out.println("[System]> Custom login successful.");
                    break;
                default:
                    System.out.println("[System]> Invalid choice. Please enter y or n.");
                    return;
            }

            while (true) {
                System.out.print("> ");
                String commandLine = sc.nextLine().trim();
                if (commandLine.isEmpty()) continue;
                String[] commandParts = commandLine.split("\\s+", 2);
                String command = commandParts[0].toLowerCase();
                String argument = commandParts.length > 1 ? commandParts[1] : "";

                try {
                    switch (command) {
                        case "ls":
                            List<String> directoryEntries = ftpClient.listDirectory();
                            System.out.println("================================================================================================");
                            if (directoryEntries.isEmpty()) {
                                System.out.println("[System]> Directory is empty.");
                            } else {
                                directoryEntries.forEach(System.out::println);
                            }
                            System.out.println("================================================================================================");
                            break;

                        case "cd":
                            if (argument.isEmpty()) {
                                System.out.println("[System]> Error: Please specify a directory [command: cd <folderName>]");
                            } else {
                                ftpClient.cd(argument);
                            }
                            break;

                        case "get":
                            if (argument.isEmpty()) {
                                System.out.println("[System]> Error: Please specify a file [command: get <fileName>]");
                            } else {
                                System.out.println("[Server]> Downloading file: " + argument);
                                File downloadedFile = ftpClient.downloadFile(argument);
                                System.out.println("[Server]> File downloaded successfully: " + downloadedFile.getAbsolutePath());
                            }
                            break;

                        case "put":
                            if (argument.isEmpty()) {
                                System.out.println("[System]> Error: Please specify a file [command: put <fileName>]");
                            } else {
                                System.out.println("[Server]> Uploading file: " + argument);
                                ftpClient.uploadFile(argument);
                                System.out.println("[Server]> File uploaded successfully.");
                            }
                            break;

                        case "size":
                            if (argument.isEmpty()) {
                                System.out.println("[System]> Error: Please specify a file [command: size <fileName>]");
                            } else {
                                long size = ftpClient.getSize(argument);
                                System.out.println("[Server]> Size: " + size + " bytes");
                            }
                            break;
                        
                        case "pwd":
                            ftpClient.pwd();
                            break;
                        
                        case "delete":
                            if (argument.isEmpty()) {
                                System.out.println("[System]> Error: Please specify a file [command: delete <fileName>]");
                            } else {
                                ftpClient.delete(argument);
                            }
                            break;
                        
                        case "mkdir":
                            if (argument.isEmpty()) {
                                System.out.println("[System]> Error: Please specify a directory name [command: mkdir <dirName>]");
                            } else {
                                ftpClient.mkdir(argument);
                            }
                            break;
                        
                        case "rmdir":
                            if (argument.isEmpty()) {
                                System.out.println("[System]> Error: Please specify a directory name [command: rmdir <dirName>]");
                            } else {
                                ftpClient.rmdir(argument);
                            }
                            break;
                        
                        case "quit":
                            ftpClient.quit();
                            return;
                        
                        case "help":
                            printHelp();
                            break;

                        default:
                            System.out.println("[System]> Unknown command, type 'help' for a list of available commands: " + command);
                    }
                } catch (Exception e) {
                    System.out.println("[System]> Error executing command: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                System.out.println("Error disconnecting: " + e.getMessage());
            }
        }
        
    }

    /**
     * Prints the help message with a list of available commands.
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
        System.out.println("[System]>   size <fileName>      - Get the size of a file on the server");
        System.out.println("[System]>   quit                 - Close the FTP connection and exit");
    }
}

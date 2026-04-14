package ftpclient.com;

public class FtpApp {
    public static void main(String[] args){
        try{
            String choice = args[0].trim().toLowerCase();
        switch(choice){
            case "cli":
                new FtpCLI().startCli();
                break;
            case "gui":
                new FtpUI().startUI();
                break;
            default:
                System.out.println("[System]> Invalid choice. Please run the program with 'cli' or 'gui' argument.");
        }
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("[System]> Please specify a mode ('cli' or 'gui').");
        }
    }
}
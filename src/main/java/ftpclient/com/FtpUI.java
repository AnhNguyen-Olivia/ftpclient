package ftpclient.com;

import ftpclient.com.UI_Compoments.LoginPage;
/* Main UI class that serves as the entry point for the graphical user interface of the FTP client application. 
 * It initializes and displays the login page when the startUI method is called.
 */

public class FtpUI {

    public void startUI() {
        javax.swing.SwingUtilities.invokeLater(() -> 
            new LoginPage().showWindow());
    }

}

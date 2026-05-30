package ftpclient.com;

import ftpclient.com.UI_Compoments.LoginPage;
/* Main UI class that serves as the entry point for the graphical user interface of the FTP client application. 
 * It initializes and displays the login page when the startUI method is called.
 */

public class FtpUI {
    /* Method to start the user interface by displaying the login page. 
     * This method is called from the main method of the application to launch the GUI.
     */
    public void startUI() {
        javax.swing.SwingUtilities.invokeLater(() -> 
            new LoginPage().showWindow());
    }

}

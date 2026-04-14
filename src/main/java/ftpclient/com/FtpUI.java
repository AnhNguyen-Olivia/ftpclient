package ftpclient.com;

import ftpclient.com.UI_Compoments.LoginPage;

public class FtpUI {

    public void startUI() {
        javax.swing.SwingUtilities.invokeLater(() -> 
            new LoginPage().showWindow());
    }

}

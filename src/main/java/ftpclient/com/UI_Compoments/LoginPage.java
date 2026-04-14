// package ftpclient.com.UI_Compoments;

// import ftpclient.com.FtpClient;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.io.IOException;
// import javax.swing.JButton;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JTextField;
// import javax.swing.SwingUtilities;

// public class LoginPage implements ActionListener {

//     private JTextField serverText;
//     private JTextField userText;
//     private JTextField passwordText;
//     private JLabel connectingToServer;
//     private JButton connectButton;
    
//     public static void main(String[] args){
//         new LoginPage().showWindow();
//     }

//     private void showWindow() {
//         JPanel panel = new JPanel();
//         JFrame frame = new JFrame();
//         frame.setSize(350, 300);
//         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         frame.add(panel);
//         frame.setLocationRelativeTo(null);

//         panel.setLayout(null);
//         JLabel label = new JLabel("FTP Client Login");
//         label.setFont(label.getFont().deriveFont(16.0f));
//         label.setBounds(110, 10, 150, 25);
//         panel.add(label);
        
//         serverText = new JTextField(20);
//         JLabel serverLabel = new JLabel("Server:");
//         serverLabel.setBounds(10, 50, 80, 25);
//         panel.add(serverLabel);
//         serverText.setBounds(100, 50, 220, 25);
//         panel.add(serverText);

//         userText = new JTextField(20);
//         JLabel userLabel = new JLabel("User:");
//         userLabel.setBounds(10, 90, 80, 25);
//         panel.add(userLabel);
//         userText.setBounds(100, 90, 220, 25);
//         panel.add(userText);

//         passwordText = new JTextField(20);
//         JLabel passwordLabel = new JLabel("Password:");
//         passwordLabel.setBounds(10, 130, 80, 25);
//         panel.add(passwordLabel);
//         passwordText.setBounds(100, 130, 220, 25);
//         panel.add(passwordText);

//         connectButton = new JButton("Login");
//         connectButton.setBounds(110, 180, 120, 25);
//         connectButton.setFocusPainted(false);
//         connectButton.addActionListener(this);
//         panel.add(connectButton);

//         connectingToServer = new JLabel("");
//         connectingToServer.setBounds(10, 220, 300, 25);
//         panel.add(connectingToServer);

//         frame.setVisible(true);
//     }
    
//     @Override
//     public void actionPerformed(ActionEvent e) {
//         connectButton.setEnabled(false);
//         connectingToServer.setText("Connecting to server...");

//         new Thread(() -> {
//             try {
//                 String server = serverText.getText().trim();
//                 String user = userText.getText().trim();
//                 String password = passwordText.getText().trim();

//                 //FtpClient.Session session = FtpClient.connect(server, user, password);
//                 //session.close();

//                 SwingUtilities.invokeLater(() -> connectingToServer.setText("Connected to " + server));
//             } catch (IOException ex) {
//                 SwingUtilities.invokeLater(() -> connectingToServer.setText("Login failed: " + ex.getMessage()));
//             } finally {
//                 SwingUtilities.invokeLater(() -> connectButton.setEnabled(true));
//             }
//         }).start();
//     }

// }

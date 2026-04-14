package ftpclient.com.UI_Compoments;

import ftpclient.com.FtpClient;
import ftpclient.com.FtpConfig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;

public class LoginPage implements ActionListener {

    private JTextField serverText;
    private JTextField userText;
    private JTextField passwordText;
    private JLabel connectingToServer;
    private JLabel sampleTextLabel;
    private JButton connectButton;
    private JCheckBox anonymousCheckBox;
    private JFrame frame;
    private final Runnable onLoginSuccess;

    public static void main(String[] args) {
        FtpClient ftpClient = new FtpClient();
        new LoginPage(() -> new MainWindow().showMainWindow()).showWindow();
    }

    public LoginPage() {
        this(null);
    }

    public LoginPage(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void showWindow() {
        JPanel panel = new JPanel();
        frame = new JFrame();
        frame.setSize(350, 330);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        panel.setLayout(null);

        JLabel label = new JLabel("FTP Client Login");
        label.setFont(label.getFont().deriveFont(16.0f));
        label.setBounds(110, 10, 150, 25);
        panel.add(label);

        sampleTextLabel = new JLabel("Example text");
        sampleTextLabel.setBounds(10, 35, 200, 20);
        panel.add(sampleTextLabel);

        serverText = new JTextField(20);
        serverText.setText(FtpConfig.ANONYMOUS_HOST);
        JLabel serverLabel = new JLabel("Server:");
        serverLabel.setBounds(10, 60, 80, 25);
        panel.add(serverLabel);
        serverText.setBounds(100, 60, 220, 25);
        panel.add(serverText);

        userText = new JTextField(20);
        userText.setText(FtpConfig.DEFAULT_ANONYMOUS_USERNAME);
        JLabel userLabel = new JLabel("User:");
        userLabel.setBounds(10, 95, 80, 25);
        panel.add(userLabel);
        userText.setBounds(100, 95, 220, 25);
        panel.add(userText);

        passwordText = new JPasswordField(20);
        passwordText.setText(FtpConfig.DEFAULT_ANONYMOUS_PASSWORD);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 130, 80, 25);
        panel.add(passwordLabel);
        passwordText.setBounds(100, 130, 220, 25);
        panel.add(passwordText);

        anonymousCheckBox = new JCheckBox("Login as Anonymous");
        anonymousCheckBox.setBounds(10, 165, 200, 25);
        anonymousCheckBox.setSelected(true);
        anonymousCheckBox.addActionListener(this);
        panel.add(anonymousCheckBox);

        connectButton = new JButton("Login");
        connectButton.setBounds(110, 200, 120, 25);
        connectButton.setFocusPainted(false);
        connectButton.addActionListener(this);
        panel.add(connectButton);

        connectingToServer = new JLabel("");
        connectingToServer.setBounds(10, 240, 300, 25);
        panel.add(connectingToServer);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == anonymousCheckBox) {
            boolean anonymousMode = anonymousCheckBox.isSelected();
            if (anonymousMode) {
                serverText.setText(FtpConfig.ANONYMOUS_HOST);
                userText.setText(FtpConfig.DEFAULT_ANONYMOUS_USERNAME);
                passwordText.setText(FtpConfig.DEFAULT_ANONYMOUS_PASSWORD);
            } else {
                serverText.setText(FtpConfig.CUSTOM_HOST);
                userText.setText(FtpConfig.DEFAULT_CUSTOM_USERNAME);
                passwordText.setText(FtpConfig.DEFAULT_CUSTOM_PASSWORD);
            }
            return;
        }

        if (event.getSource() == connectButton) {
            connectToServer();
        }
    }

    private void connectToServer() {
        connectButton.setEnabled(false);
        connectingToServer.setText("Connecting to server...");

        new Thread(() -> {
            try {
                String server = serverText.getText().trim();
                String user = userText.getText().trim();
                String password = passwordText.getText().trim();
                FtpClient ftpClient = new FtpClient();
                ftpClient.connect(server, FtpConfig.FTP_PORT);

                if (!ftpClient.login(user, password)) {
                    throw new IOException("Login failed: Invalid credentials");
                }

                SwingUtilities.invokeLater(() -> {
                    connectingToServer.setText("Connected to " + server);
                    frame.dispose();
                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    } else {
                        MainWindow mainWindow = new MainWindow();
                        mainWindow.showMainWindow();
                    }
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> connectingToServer.setText("Login failed: " + ex.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> connectButton.setEnabled(true));
            }
        }).start();
    }
}

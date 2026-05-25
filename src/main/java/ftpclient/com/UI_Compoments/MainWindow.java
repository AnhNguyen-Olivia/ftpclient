package ftpclient.com.UI_Compoments;
import ftpclient.com.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class MainWindow{
    private JFrame frame;
    private final FtpClient ftpClient;

    private LocalPanel localPanel;
    private RemotePanel remotePanel;
    private TopbarPanel topbarPanel;

    public MainWindow(FtpClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public void showMainWindow(){
        localPanel = new LocalPanel();
        remotePanel = new RemotePanel(ftpClient);
        topbarPanel = new TopbarPanel(
            this::uploadSelectedFile,
            this::downloadSelectedFile,
            this::deleteSelectedRemoteFile,
            this::createRemoteDirectory,
            this::removeRemoteDirectory,
            this::showRemoteWorkingDirectory,
            this::refreshAll,
            this::quitApplication
        );

        remotePanel.setOnSelectionChanged(topbarPanel::updateFileTypeLabel);
 
        frame = new JFrame("FTP Client Window");
        frame.setSize(1366, 768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
 
        JPanel root = new JPanel(new BorderLayout(5, 5));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildSplitPane(), BorderLayout.CENTER);
        frame.setContentPane(root);
 
        refreshAll();
        frame.setVisible(true);
    }

    private void uploadSelectedFile() {
        String selected = localPanel.getSelectedValue();
        if (selected == null || selected.endsWith("/")) {
            showError("Select a local file to upload.");
            return;
        }
 
        File file = new File(localPanel.getCurrentDirectory(), selected);
        if (!file.isFile()) {
            showError("Selected entry is not a file.");
            return;
        }
 
        try {
            ftpClient.uploadFile(file);
            remotePanel.refresh();
            showInfo("Uploaded: " + file.getName());
        } catch (Exception ex) {
            showError("Upload failed: " + ex.getMessage());
        }
    }
 
    private void downloadSelectedFile() {
        String selected = remotePanel.getSelectedValue();
        if (selected == null || selected.isBlank()) {
            showError("Select a remote file to download.");
            return;
        }
 
        try {
            File dest = ftpClient.downloadFile(selected, new File(FtpConfig.DEFAULT_DOWNLOAD_DIRECTORY));
            localPanel.refresh();
            showInfo("Downloaded: " + dest.getAbsolutePath());
        } catch (Exception ex) {
            showError("Download failed: " + ex.getMessage());
        }
    }
 
    private void deleteSelectedRemoteFile() {
        String selected = remotePanel.getSelectedValue();
        if (selected == null || selected.isBlank()) {
            showError("Select a remote file to delete.");
            return;
        }
 
        try {
            ftpClient.delete(selected);
            remotePanel.refresh();
        } catch (IOException ex) {
            showError("Delete failed: " + ex.getMessage());
        }
    }
 
    private void createRemoteDirectory() {
        String name = JOptionPane.showInputDialog(frame, "New remote directory name:");
        if (name == null || name.isBlank()) return;
 
        try {
            ftpClient.mkdir(name.trim());
            remotePanel.refresh();
        } catch (IOException ex) {
            showError("MKDIR failed: " + ex.getMessage());
        }
    }
 
    private void removeRemoteDirectory() {
        String selected = remotePanel.getSelectedValue();
        if (selected == null || selected.isBlank() || selected.startsWith("[System]>")) {
            showError("Select a directory to remove.");
            return;
        }
        if (!selected.endsWith("/")) {
            showError("Selected entry is not a directory.");
            return;
        }
 
        String dirName = selected.replaceAll("/$", "");
        int choice = JOptionPane.showConfirmDialog(
                frame, "Remove directory: " + dirName + "?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
 
        try {
            ftpClient.rmdir(dirName);
            remotePanel.refresh();
            showInfo("Directory removed: " + dirName);
        } catch (IOException ex) {
            showError("RMDIR failed: " + ex.getMessage());
        }
    }
 
    private void showRemoteWorkingDirectory() {
        try {
            showInfo(remotePanel.parseWorkingDirectory(ftpClient.pwd()));
        } catch (IOException ex) {
            showError("PWD failed: " + ex.getMessage());
        }
    }

    private void refreshAll() {
        localPanel.refresh();
        remotePanel.refresh();
    }

    private void quitApplication() {
        try {
            ftpClient.quit();
        } catch (IOException ex) {
            showError("Quit failed: " + ex.getMessage());
        } finally {
            if (frame != null) {
                frame.dispose();
            }
        }
    }
 
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
 
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel buildHeader() {
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
 
        JLabel title = new JLabel("FTP Client Manager");
        title.setFont(title.getFont().deriveFont(18.0f));
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(new JLabel("Connected"), BorderLayout.EAST);
 
        JPanel topBarRow = new JPanel(new BorderLayout(5, 5));
        topBarRow.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topBarRow.add(topbarPanel, BorderLayout.CENTER);
 
        JPanel header = new JPanel(new BorderLayout());
        header.add(titleRow,  BorderLayout.NORTH);
        header.add(topBarRow, BorderLayout.CENTER);
        return header;
    }
 
    private JSplitPane buildSplitPane() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(localPanel);
        split.setRightComponent(remotePanel);
        split.setResizeWeight(0.5);
        split.setDividerSize(8);
        return split;
    }
}

 
package ftpclient.com.UI_Compoments;

import ftpclient.com.FtpClient;
import ftpclient.com.FtpConfig;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;  
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class MainWindow {
    private JFrame frame;
    private final FtpClient ftpClient;
    private File currentLocalDirectory;
    private final DefaultListModel<String> localModel = new DefaultListModel<>();
    private final DefaultListModel<String> remoteModel = new DefaultListModel<>();
    private JList<String> localList;
    private JList<String> remoteList;
    private JLabel localPathLabel;
    private JLabel remotePathLabel;

    public MainWindow(FtpClient ftpClient) {
        this.ftpClient = ftpClient;
        this.currentLocalDirectory = new File(System.getProperty("user.home"));
    }

    public void showMainWindow() {
        frame = new JFrame("FTP Client Window");
        frame.setSize(1366, 768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(5,5));
        frame.setContentPane(root);

        root.add(createTopBar(), BorderLayout.NORTH);
        root.add(createCenterArea(), BorderLayout.CENTER);

        refreshLocalListing();
        refreshRemoteListing();

        frame.setVisible(true);
    }

    private JComponent createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        JButton uploadButton = new JButton("PUT");
        uploadButton.setFocusPainted(false);
        uploadButton.addActionListener(e -> uploadSelectedLocalFile());

        JButton downloadButton = new JButton("GET");
        downloadButton.setFocusPainted(false);
        downloadButton.addActionListener(e -> downloadSelectedRemoteFile());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedRemoteFile());

        JButton makeDirButton = new JButton("MKDIR");
        makeDirButton.setFocusPainted(false);
        makeDirButton.addActionListener(e -> createRemoteDirectory());
        
        JButton removeButton = new JButton("RMDIR");
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(e -> removeRemoteDirectory());

        JButton pwdButton = new JButton("PWD");
        pwdButton.setFocusPainted(false);
        pwdButton.addActionListener(e -> showRemoteWorkingDirectory());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshAllListings());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 20));

        topBar.add(uploadButton);
        topBar.add(downloadButton);
        topBar.add(deleteButton);
        topBar.add(makeDirButton);
        topBar.add(removeButton);
        topBar.add(pwdButton);
        topBar.add(refreshButton);
        topBar.add(progressBar);

        return topBar;
    }

    private JComponent createCenterArea() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLocalPanel(), createRemotePanel());
        split.setResizeWeight(0.5);
        split.setDividerSize(8);
        return split;
    }

    private JComponent createLocalPanel(){
        JPanel panel = new JPanel(new BorderLayout(5,5));
        localPathLabel = new JLabel();
        localPathLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        localList = new JList<>(localModel);
        localList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    openSelectedLocalEntry();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(localList);
        panel.add(localPathLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent createRemotePanel(){
        JPanel panel = new JPanel(new BorderLayout(5,5));
        remotePathLabel = new JLabel();
        remotePathLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        remoteList = new JList<>(remoteModel);

        JScrollPane scrollPane = new JScrollPane(remoteList);
        panel.add(remotePathLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void refreshAllListings() {
        refreshLocalListing();
        refreshRemoteListing();
    }

    private void refreshLocalListing() {
        localModel.clear();
        localPathLabel.setText("Local: " + currentLocalDirectory.getAbsolutePath());

        File parent = currentLocalDirectory.getParentFile();
        if (parent != null) {
            localModel.addElement("..");
        }

        File[] entries = currentLocalDirectory.listFiles();
        if (entries == null) {
            return;
        }

        Arrays.sort(entries, Comparator.comparing(File::isFile).thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        for (File entry : entries) {
            localModel.addElement(entry.isDirectory() ? entry.getName() + "/" : entry.getName());
        }
    }

    private void refreshRemoteListing() {
        try {
            remoteModel.clear();
            remotePathLabel.setText("Remote: " + extractWorkingDirectory(ftpClient.pwd()));
            for (String entry : ftpClient.listRemoteNames()) {
                remoteModel.addElement(entry);
            }
        } catch (IOException ex) {
            remotePathLabel.setText("Remote: unavailable");
            remoteModel.clear();
            remoteModel.addElement("[System]> " + ex.getMessage());
        }
    }

    private void openSelectedLocalEntry() {
        String selected = localList.getSelectedValue();
        if (selected == null) {
            return;
        }

        if ("..".equals(selected)) {
            File parent = currentLocalDirectory.getParentFile();
            if (parent != null) {
                currentLocalDirectory = parent;
                refreshLocalListing();
            }
            return;
        }

        File selectedFile = new File(currentLocalDirectory, selected.replaceAll("/$", ""));
        if (selectedFile.isDirectory()) {
            currentLocalDirectory = selectedFile;
            refreshLocalListing();
        }
    }

    private void uploadSelectedLocalFile() {
        String selected = localList.getSelectedValue();
        if (selected == null || selected.endsWith("/")) {
            JOptionPane.showMessageDialog(frame, "Select a local file to upload.");
            return;
        }

        File file = new File(currentLocalDirectory, selected);
        if (!file.isFile()) {
            JOptionPane.showMessageDialog(frame, "Selected entry is not a file.");
            return;
        }

        try {
            ftpClient.uploadFile(file);
            refreshRemoteListing();
            JOptionPane.showMessageDialog(frame, "Uploaded: " + file.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Upload failed: " + ex.getMessage());
        }
    }

    private void downloadSelectedRemoteFile() {
        String selected = remoteList.getSelectedValue();
        if (selected == null || selected.isBlank()) {
            JOptionPane.showMessageDialog(frame, "Select a remote file to download.");
            return;
        }

        try {
            File downloadedFile = ftpClient.downloadFile(selected, new File(FtpConfig.DEFAULT_DOWNLOAD_DIRECTORY));
            refreshLocalListing();
            JOptionPane.showMessageDialog(frame, "Downloaded: " + downloadedFile.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Download failed: " + ex.getMessage());
        }
    }

    private void deleteSelectedRemoteFile() {
        String selected = remoteList.getSelectedValue();
        if (selected == null || selected.isBlank()) {
            JOptionPane.showMessageDialog(frame, "Select a remote file to delete.");
            return;
        }

        try {
            ftpClient.delete(selected);
            refreshRemoteListing();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Delete failed: " + ex.getMessage());
        }
    }

    private void createRemoteDirectory() {
        String name = JOptionPane.showInputDialog(frame, "New remote directory name:");
        if (name == null || name.isBlank()) {
            return;
        }

        try {
            ftpClient.mkdir(name.trim());
            refreshRemoteListing();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "MKDIR failed: " + ex.getMessage());
        }
    }

    private void removeRemoteDirectory() {
        String name = JOptionPane.showInputDialog(frame, "Remote directory name to remove:");
        if (name == null || name.isBlank()) {
            return;
        }

        try {
            ftpClient.rmdir(name.trim());
            refreshRemoteListing();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "RMDIR failed: " + ex.getMessage());
        }
    }

    private void showRemoteWorkingDirectory() {
        try {
            JOptionPane.showMessageDialog(frame, extractWorkingDirectory(ftpClient.pwd()));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "PWD failed: " + ex.getMessage());
        }
    }

    private String extractWorkingDirectory(String response) {
        if (response == null || response.isBlank()) {
            return "unknown";
        }

        int firstQuote = response.indexOf('"');
        int secondQuote = firstQuote >= 0 ? response.indexOf('"', firstQuote + 1) : -1;
        if (firstQuote >= 0 && secondQuote > firstQuote) {
            return response.substring(firstQuote + 1, secondQuote);
        }

        return response.trim();
    }
}
    
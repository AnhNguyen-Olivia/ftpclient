package ftpclient.com.UI_Compoments;

import ftpclient.com.FtpClient;
 
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Consumer;
 
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 * A panel that displays the remote FTP directory contents and allows navigation.
 */
public class RemotePanel extends JPanel {
    private final FtpClient ftpClient;
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list;
    private final JLabel pathLabel;

    /**
     * Callback for when the selection changes, e.g., to update the local panel
     */
    private Consumer<String> onSelectionChanged = ignored -> {};
    
    /**
     * Constructs the RemotePanel with the given FtpClient.
     * @param ftpClient
     */
    public RemotePanel(FtpClient ftpClient) {
        super(new BorderLayout(5, 5));
        this.ftpClient = ftpClient;
 
        pathLabel = new JLabel();
        pathLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
 
        list = new JList<>(model);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedEntry();
                } else {
                    onSelectionChanged.accept(list.getSelectedValue());
                }
            }
        });
 
        add(pathLabel, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    /**
     * Refreshes the remote directory listing and updates the path label.
     */
    public void refresh() {
        try {
            model.clear();
            String currentPath = parseWorkingDirectory(ftpClient.pwd());
            pathLabel.setText("Remote: " + currentPath);

            if (!"/".equals(currentPath)) {
                model.addElement("..");
            }

            ftpClient.listRemoteWithTypes().forEach(model::addElement);
        } catch (IOException ex) {
            pathLabel.setText("Remote: unavailable");
            model.clear();
            model.addElement("[System]> " + ex.getMessage());
        }
    }

    /**
     * Returns the currently selected value from the list.
     * @return the selected value or null if none is selected
     */
    public String getSelectedValue() {
        return list.getSelectedValue();
    }

    /**
     * Sets a callback to be invoked when the selection changes.
     * @param callback a Consumer that accepts the selected value
     */
    public void setOnSelectionChanged(Consumer<String> callback) {
        this.onSelectionChanged = callback;
    }

    /**
     * Parses the working directory from the FTP PWD response.
     * @param pwdResponse the raw response from the PWD command
     * @return the extracted working directory or "unknown" if parsing fails
     */
    public String parseWorkingDirectory(String pwdResponse) {
        if (pwdResponse == null || pwdResponse.isBlank()) return "unknown";
 
        int first = pwdResponse.indexOf('"');
        int second = first >= 0 ? pwdResponse.indexOf('"', first + 1) : -1;
 
        return (first >= 0 && second > first)
                ? pwdResponse.substring(first + 1, second)
                : pwdResponse.trim();
    }
    
    /**
     * Handles opening the selected entry in the list. 
     * If it's a directory, it navigates into it; if it's "..", it goes up one level. Plain files are not opened!
     */
        private void openSelectedEntry() {
        String selected = list.getSelectedValue();
        if (selected == null || selected.startsWith("[System]>")) return;
 
        String target = "..".equals(selected) ? ".." : selected.replaceAll("/$", "");
 
        // Only navigate into directories; plain files are not opened here!
        if (!selected.endsWith("/") && !"..".equals(selected)) return;
 
        try {
            ftpClient.cd(target);
            refresh();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to navigate: " + ex.getMessage());
        }
    }
}

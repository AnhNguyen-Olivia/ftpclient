package ftpclient.com.UI_Compoments;
 
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
 
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
 
/**
 * LocalPanel is a GUI component that displays the local file system in a list format. 
 * It allows users to navigate through directories and select files for uploading to the FTP server. 
 * The panel maintains a base directory (user's home/FtpClient) to restrict navigation,
 * ensure that users do not access unintended parts of the file system.
 * Aka me being lazy and not implementing a full file explorer, sorry Dr. Huy :')
 */
public class LocalPanel extends JPanel {
 
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list;
    private final JLabel pathLabel;
 
    private File currentDirectory;
    private final File baseDirectory;
    
    /**
     * Constructor for LocalPanel. 
     * It initializes the base directory to "FtpClient" within the user's home directory 
     * and sets up the GUI components including the path label and file list.
     */
    public LocalPanel() {
        super(new BorderLayout(5, 5));
 
        this.baseDirectory = new File(System.getProperty("user.home"), "FtpClient");
        ensureDirectoryStructure();
        this.currentDirectory = baseDirectory;
 
        pathLabel = new JLabel();
        pathLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
 
        list = new JList<>(model);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedEntry();
                }
            }
        });
 
        add(pathLabel, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
    }
    
        /**
         * Refreshes the file list and updates the path label. 
         */
        public void refresh() {
        model.clear();
        pathLabel.setText("Local: " + currentDirectory.getAbsolutePath());
 
        if (currentDirectory.getParentFile() != null) {
            model.addElement("..");
        }
 
        File[] entries = currentDirectory.listFiles();
        if (entries == null) return;
        
        Arrays.sort(entries, Comparator.comparing(File::isFile)
                .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER));
 
        for (File entry : entries) {
            model.addElement(entry.isDirectory() ? entry.getName() + "/" : entry.getName());
        }
    }

    /**
     * Returns the name of the currently selected file or directory in the list. 
     * If no selection is made, it returns null.
     * @return The name of the selected file or directory, or null if no selection.
     */
    public String getSelectedValue() {
        return list.getSelectedValue();
    }

    /**
     * Returns the current directory as a File object. 
     * This method allows other components to access the current directory for operations like uploading files.
     * @return The current directory as a File object.
     */
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Ensures that the required directory structure exists within the base directory.
     */
    private void ensureDirectoryStructure() {
        baseDirectory.mkdirs();
        new File(baseDirectory, "Download").mkdirs();
        new File(baseDirectory, "Upload").mkdirs();
    }
    
    /**
     * Handles the logic for opening a selected entry in the file list. 
     * If the entry is "..", it navigates to the parent directory (if within base). 
     * If the entry is a directory, it navigates into that directory. 
     * Files are not opened but can be selected for upload.
     */
    private void openSelectedEntry() {
        String selected = list.getSelectedValue();
        if (selected == null) return;
 
        if ("..".equals(selected)) {
            File parent = currentDirectory.getParentFile();
            if (parent != null && isWithinBaseDirectory(parent)) {
                currentDirectory = parent;
                refresh();
            }
            return;
        }
 
        File target = new File(currentDirectory, selected.replaceAll("/$", ""));
        if (target.isDirectory()) {
            currentDirectory = target;
            refresh();
        }
    }
    
    /**
     * Checks if a given file is within the base directory to prevent unauthorized access. 
     * This method is used to ensure that users cannot navigate outside of the designated base directory.
     * @param file The file to check.
     * @return true if the file is within the base directory, false otherwise.
     */
    private boolean isWithinBaseDirectory(File file) {
        try {
            return file.getCanonicalPath().startsWith(baseDirectory.getCanonicalPath());
        } catch (IOException e) {
            return false;
        }
    }
}

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
 
public class LocalPanel extends JPanel {
 
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list;
    private final JLabel pathLabel;
 
    private File currentDirectory;
    private final File baseDirectory;
 
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

    public String getSelectedValue() {
        return list.getSelectedValue();
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    private void ensureDirectoryStructure() {
        baseDirectory.mkdirs();
        new File(baseDirectory, "Download").mkdirs();
        new File(baseDirectory, "Upload").mkdirs();
    }
 
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
 
    private boolean isWithinBaseDirectory(File file) {
        try {
            return file.getCanonicalPath().startsWith(baseDirectory.getCanonicalPath());
        } catch (IOException e) {
            return false;
        }
    }

}

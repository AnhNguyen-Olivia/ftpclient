package ftpclient.com.UI_Compoments;
import java.awt.Dimension;
import java.awt.FlowLayout;
 
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * TopbarPanel is a JPanel that contains buttons for FTP operations and a label to display the type of the selected file.
 * It provides methods to update the file type label based on the selected file.
 */
public class TopbarPanel extends JPanel {
    public final JLabel fileTypeLabel;

    /**
     * Constructor for TopbarPanel.
     * @param onUpload
     * @param onDownload
     * @param onDelete
     * @param onMkDir
     * @param onRmDir
     * @param onPwd
     * @param onRefresh
     * @param onQuit
     */

    public TopbarPanel(
        Runnable onUpload,
        Runnable onDownload,
        Runnable onDelete,
        Runnable onMkDir,
        Runnable onRmDir,
        Runnable onPwd,
        Runnable onRefresh,
        Runnable onQuit
    ){
        super(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        add(button("PUT", onUpload));
        add(button("GET", onDownload));
        add(button("DELETE", onDelete));
        add(button("MKDIR", onMkDir));
        add(button("RMDIR", onRmDir));
        add(button("PWD", onPwd));
        add(button("REFRESH", onRefresh));
        add(button("QUIT", onQuit));

        fileTypeLabel = new JLabel("Select a file to see its type");
        fileTypeLabel.setPreferredSize(new Dimension(200, 20));
        add(fileTypeLabel);
    }

        /**
         * Updates the file type label based on the selected file.
         * @param selected
         */
        public void updateFileTypeLabel(String selected) {
            if (selected == null || selected.isBlank() || selected.startsWith("[System]>") || "..".equals(selected)) {
            fileTypeLabel.setText("Select a file to see type");
        } else if (selected.endsWith("/")) {
            fileTypeLabel.setText("Type: Directory — " + selected);
        } else {
            fileTypeLabel.setText("Type: File — " + selected);
        }
    }
        /**
         * Helper method to create a JButton with a label and an action.
         * @param label
         * @param action
         * @return
         */
        private static JButton button(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> action.run());
        return btn;
    }
}
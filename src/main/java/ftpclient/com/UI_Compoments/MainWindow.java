package ftpclient.com.UI_Compoments;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MainWindow {
    private JFrame frame;

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
        //root.add(createConsolePanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JComponent createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton uploadButton = new JButton("Upload");
        JButton downloadButton = new JButton("Download");
        JButton deleteButton = new JButton("Delete");
        JButton makeDirButton = new JButton("Make Directory");
        JButton removeButton = new JButton("Remove Directory");
        JButton pwdButton = new JButton("PWD");
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 25));

        topBar.add(uploadButton);
        topBar.add(downloadButton);
        topBar.add(deleteButton);
        topBar.add(makeDirButton);
        topBar.add(removeButton);
        topBar.add(pwdButton);
        topBar.add(progressBar);

        return topBar;
    }

    private JComponent createCenterArea() {
        JPanel centerPanel = new JPanel(new BorderLayout(5 ,5));
        return centerPanel;
    }

}
    
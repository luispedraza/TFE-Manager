import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;



public class MainWindow extends JDialog {
    private TFEManager manager;
    static final String WORKING_DIRECTORY = "/Users/luispedraza/OneDrive - Universidad Internacional de La Rioja/TFE-MANAGER";
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton unzipProposals;
    private JButton loadProposals;
    private JTextArea logTextArea;

    public MainWindow() {
        manager = new TFEManager(MainWindow.WORKING_DIRECTORY);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        unzipProposals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unzipProposals();
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void unzipProposals() {
        final JFileChooser fc = new JFileChooser(MainWindow.WORKING_DIRECTORY);
        fc.setFileFilter(new FileNameExtensionFilter("Archivo zip", "zip"));
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = fc.getSelectedFile().getAbsolutePath();
            logInfo("Descromprimiendo el archivo de propuestas: " + filePath);
            manager.unzipProposals(filePath, MainWindow.WORKING_DIRECTORY);

        }
    }

    private void logInfo(String info) {
        logTextArea.setText(
                logTextArea.getText() + "\n" + info
        );
    }

    public static void main(String[] args) {
        MainWindow dialog = new MainWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}

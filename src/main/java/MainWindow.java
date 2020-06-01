import TFEManagerLib.MailManager;
import TFEManagerLib.TFEManager;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// Noa sobre manifest: https://stackoverflow.com/questions/20952713/wrong-manifest-mf-in-intellij-idea-created-jar

public class MainWindow extends JDialog {
    private TFEManager manager;
    static final String WORKING_DIRECTORY = "/Users/luispedraza/OneDrive - Universidad Internacional de La Rioja/TFE-MANAGER";
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton unzipProposals;
    private JButton loadProposals;
    private JTextArea logTextArea;
    private JButton saveProposals;
    private JButton createReviews;
    private JButton sendReviews;
    private JButton loadReviewsResults;
    private JButton saveReviewsResult;
    private JButton generateVerdict;
    private JButton generateGradings;
    private JButton loadProgress;

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
        loadProposals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProposals();
            }
        });
        saveProposals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProposals();
            }
        });
        createReviews.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createReviews();
            }
        });
        sendReviews.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendReviews();
            }
        });
        loadReviewsResults.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReviewsResults();
            }
        });
        generateVerdict.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateVerdict();
            }
        });
        generateGradings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateGradings();
            }
        });
        loadProgress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProgress();
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

    private void loadProposals() {
        // chooser.setCurrentDirectory(new java.io.File("."));
        final JFileChooser chooser = new JFileChooser(MainWindow.WORKING_DIRECTORY);
        chooser.setDialogTitle("Elija el directorio que contiene las propuestas");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String proposalsPath = chooser.getSelectedFile().getAbsolutePath();
            logInfo("Cargando las propuestas contenidas en : " + proposalsPath);
            try {
                manager.loadProposalsFromDisc(proposalsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /** Se guarda la información de las propuestas
     *
     */
    private void saveProposals() {
        try {
            manager.saveProposalsToExcel(null);
        } catch (IOException e) {
            logInfo(e.toString());
        }
    }

    /**
     * Generación de paquetes para los revisores
     */
    private void createReviews() {
        try {
            manager.createReviews();
        } catch (IOException e) {
            logInfo(e.toString());
        }

    }

    /**
     * Envío de paquetes a los revisores
     */
    private void sendReviews() {

        try {
            manager.sendReviews();
        } catch (Exception e){
            logInfo(e.toString());
        }
    }



    /**
     * Lectura de las revisiones recibidas desde el disco
     */
    private void loadReviewsResults() {
        try {
            manager.loadReviewsResults();
        } catch (IOException e) {
            logInfo(e.toString());
        }
    }

    /**
     * Generación de los veredictos y guardado en la lista maestra
     */
    private void generateVerdict() {

    }

    /**
     * Generación de la resolución en la lista maestra
     */
    private void generateGradings() {

    }

    /**
     * Carga de la información de progreso de revisión para una entrega
     */
    private void loadProgress() {

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

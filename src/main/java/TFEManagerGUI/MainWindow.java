package TFEManagerGUI;

import TFEManagerLib.Models.Student;
import TFEManagerLib.TFEManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
    private JButton createReviews;
    private JButton sendReviews;
    private JButton loadReviewsResults;
    private JButton generateGradings;
    private JButton loadProgress;
    private JPanel studentsPanel;
    private JTree studentsTree;
    private JPanel reviewersPanel;
    private JTree reviewersTree;
    private JPanel directorsPanel;
    private JTree directorsTree;
    private JPanel buttonsPanel;
    private JButton assignReviewers;
    private JButton assignDirectors;

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
        assignReviewers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignReviewers();
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
        generateGradings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateGradings();
            }
        });
        assignDirectors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignDirectors();
            }
        });
        loadProgress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProgress();
            }
        });
    }


    private void updateStudentsTree(ArrayList<Student> proposals) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) studentsTree.getModel().getRoot();
        DefaultMutableTreeNode child = null;
        for (Student p : proposals) {
            child = new DefaultMutableTreeNode(p);
            root.add(child);
        }
        for (int i = 0; i < studentsTree.getRowCount(); i++) {
            studentsTree.expandRow(i);
        }
        ((DefaultTreeModel) studentsTree.getModel()).nodeChanged(root);


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
            logInfo("Cargando propuestas desde : " + proposalsPath);
            try {
                ArrayList<Student> proposals = manager.loadProposalsFromDisc(proposalsPath);
                manager.saveProposalsToExcel(null);
                updateStudentsTree(proposals);

            } catch (Exception e) {
                logInfo(e.toString());
                e.printStackTrace();
            }

        }
    }

    /**
     * Asignación automática de revisores a las propuestas
     */
    private void assignReviewers() {
        try {
            manager.assignReviewers();
        } catch (Exception e) {
            logInfo(e.toString());
            e.printStackTrace();
        }

    }


    /**
     * Generación de paquetes para los revisores
     */
    private void createReviews() {
        try {
            manager.createReviewPacks();
        } catch (Exception e) {
            logInfo(e.toString());
            e.printStackTrace();
        }

    }

    /**
     * Envío de paquetes a los revisores
     */
    private void sendReviews() {

        try {
            manager.sendReviews();
        } catch (Exception e) {
            logInfo(e.toString());
            e.printStackTrace();

        }
    }


    /**
     * Lectura de las revisiones recibidas desde el disco
     */
    private void loadReviewsResults() {
        String pathToReviews = "/Users/luispedraza/OneDrive - Universidad Internacional de La Rioja/TFE-MANAGER/__Revisiones";
        try {
            manager.loadReviewsResults(pathToReviews);
        } catch (Exception e) {
            logInfo(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Generación de la resolución a partir de la lista maestra
     * Se genera la estructura de archivos y el zip ara subir a Sakai
     */
    private void generateGradings() {
        try {
            manager.generateGradings();
        } catch (Exception e) {
            logInfo(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Asignación de directores
     */
    private void assignDirectors() {
        try {
            OptimizerWindow optimWindow = new OptimizerWindow(manager);
            optimWindow.setSize(new Dimension(700, 500));
            optimWindow.setAlwaysOnTop(true);
            optimWindow.setResizable(false);
            optimWindow.setVisible(true);
        } catch (Exception e) {
            logInfo(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Carga de la información de progreso de revisión para una entrega
     */
    private void loadProgress() {
        try {
            ProgressTypeChooser typeChooser = new ProgressTypeChooser();
            typeChooser.setSize(new Dimension(400, 300));
            typeChooser.setAlwaysOnTop(true);
            typeChooser.setResizable(false);
            typeChooser.setVisible(true);
            int type = typeChooser.getOption();

            // SOLICITAMOS EL ARCHIVO
            String pathExcelFile = null;
            final JFileChooser fc = new JFileChooser(MainWindow.WORKING_DIRECTORY);
            fc.setFileFilter(new FileNameExtensionFilter("Elija el archivo Excel de calificaciones", "xls"));
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                pathExcelFile = fc.getSelectedFile().getAbsolutePath();
            }
            if (pathExcelFile != null) {
                manager.loadProgress(pathExcelFile, type);
            }

        } catch (Exception e) {
            logInfo(e.toString());
            e.printStackTrace();
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

    /**
     * Creación personalizada de componentes de interfaz
     */
    private void createUIComponents() {
        studentsTree = new JTree(new DefaultMutableTreeNode("Alumnos"));
        reviewersTree = new JTree(new DefaultMutableTreeNode("Revisores"));
        directorsTree = new JTree(new DefaultMutableTreeNode("Directores"));
    }
}

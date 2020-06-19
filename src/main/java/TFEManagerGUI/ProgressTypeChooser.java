package TFEManagerGUI;

import javax.swing.*;
import java.awt.event.*;

public class ProgressTypeChooser extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton draft1;
    private JRadioButton draft2;
    private JRadioButton draft3;

    public ProgressTypeChooser() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(draft1);
        buttonGroup.add(draft2);
        buttonGroup.add(draft3);

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
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        ProgressTypeChooser dialog = new ProgressTypeChooser();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public String getOption() {
        if (draft1.isSelected()) return "BORRADOR1";
        if (draft2.isSelected()) return "BORRADOR2";
        if (draft3.isSelected()) return "BORRADOR3";
        return null;
    }
}

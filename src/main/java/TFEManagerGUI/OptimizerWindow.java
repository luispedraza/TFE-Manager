package TFEManagerGUI;

import TFEManagerLib.Optimizers.OptimizerDirectorForStudent;
import TFEManagerLib.TFEManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class OptimizerWindow extends JDialog {

    private static final String[] RESULT_TABLE_HEADERS = {
            "ALUMNO",
            "Info. Alumno",
            "Info. Director",
            "DIRECTOR",
            "Asignación"
    };
    private DefaultTableModel tableModel = new DefaultTableModel(null, RESULT_TABLE_HEADERS);

    private JPanel contentPane;
    private JButton buttonGO;
    private JButton buttonCancel;
    private JCheckBox zoneCheck;
    private JSpinner zoneWeight;
    private JCheckBox typeCheck;
    private JSpinner typeWeight;
    private JCheckBox maxDirectorCheck;
    private JSpinner maxDirectorWeight;
    private JPanel chartPanelContainer;
    private JCheckBox skipAssigned;
    private JSpinner maxIter;
    private JLabel fitnessValue;
    private JTabbedPane tabbedPane1;
    private JTable tableResult;

    private TFEManager manager;
    private static XYSeries series;


    public OptimizerWindow(TFEManager manager) {
        this.manager = manager;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        typeCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                typeWeight.setEnabled(typeCheck.isSelected());
            }
        });
        zoneCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                zoneWeight.setEnabled(zoneCheck.isSelected());
            }
        });
        maxDirectorCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                maxDirectorWeight.setEnabled(maxDirectorCheck.isSelected());
            }
        });

        buttonGO.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                beginOptimization();
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

    /** Se actualiza la información en la pantlla (gráfica y mejor fitness) con cada iteración
     *
     * @param value: el mejor fitness obtenidos
     */
    public void updateGUI(Integer value) {
        if (OptimizerWindow.series.getItemCount() == 0) {
            OptimizerWindow.series.add(1, value);
        } else {
            OptimizerWindow.series.add(OptimizerWindow.series.getMaxX()+1, value);
        }
        // Mostramos el valor de fitness
        this.fitnessValue.setText(String.valueOf(value));
    }

    public void onEndOptimization(Integer value) {
        buttonCancel.setEnabled(true);

        for (int i=0; i<100; i++) {
            // Vector fila de prueba
            String[] row = {
                    "ESTEBA GONZALEZ, JUAN CARLOS",
                    "EUROPA\nTipo 1\n",
                    "EUROPA\nTipo 2\n",
                    "PEDRAZA, LUIS",
                    "13(+3)"
            };
            tableModel.addRow(row);
        }
        tableResult.setModel(tableModel);

    }

    private void beginOptimization() {
        OptimizerWindow.series.clear(); // Limpiamos la gráfica. Puede haber varias optimizaciones.
        buttonCancel.setEnabled(false);
        try {
            // Obtengo los parámetros de interfaz
            typeWeight.commitEdit();
            int wType = typeCheck.isSelected() ? (Integer) typeWeight.getValue() : 0;
            int wZone = zoneCheck.isSelected() ? (Integer) zoneWeight.getValue() : 0;
            int wMaxDirector = maxDirectorCheck.isSelected() ? (Integer) maxDirectorWeight.getValue() : 0;
            int maxIterations = (Integer) maxIter.getValue();

            boolean skipAssigned = this.skipAssigned.isSelected();

            manager.assignDirectors(new OptimizerDirectorForStudent.OptimizerConfiguration("GA",
                    wType,
                    wZone,
                    wMaxDirector,
                    0,
                    maxIterations
                    ),
                    skipAssigned,
                    this::updateGUI,
                    this::onEndOptimization);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        chartPanelContainer = new JPanel();
        chartPanelContainer.setLayout(new BoxLayout(chartPanelContainer, BoxLayout.PAGE_AXIS));
        chartPanelContainer.setSize(500, 300);

        // Los gráficos
        this.series = new XYSeries("Fitness");
        final XYSeriesCollection dataset = new XYSeriesCollection(this.series);
        final JFreeChart jChart = ChartFactory.createXYLineChart(
                null,
                "Iteración",
                "Valor de la función",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );
        final XYPlot plot = jChart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
//        axis.setAutoRange(true);
//        axis.setFixedAutoRange(50);
        axis.setRange(0, 1000);
        axis = plot.getRangeAxis();
        axis.setRange(-100-0, 100.0);

        ChartPanel chartPanel = new ChartPanel(jChart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        chartPanelContainer.add(chartPanel);

        // Ajustamos las características de los spinners:
        maxIter = new JSpinner(new SpinnerNumberModel(1000, 0, 10000, 200));
        zoneWeight = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        typeWeight = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        maxDirectorWeight = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        chartPanelContainer.updateUI();
    }


}

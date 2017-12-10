import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by clair on 11/28/2016.
 */
public class test1 extends JDialog{
    public static String PROCEEDING;
    public static String AUTHOR;
    public static String yearsDuration;
    private JPanel contentPanel;
    private JPanel westLabelPanel;
    private JPanel eastTextField;
    private JPanel southButtons;
    private JLabel yearsLabel;
    private JLabel proceedingLabel;
    private JTextField proceField;
    private JComboBox yearComboBox;
    private JButton showButton;
    private JPanel authorPanel;
    private JTextField authorField;
    private JButton showAuthorBtn;
    private JPanel charBar;

    //    default configuration files
    static String owlFile = "ontology/swrc_v0.3.owl";
    static String obdaFile = "ontology/swrc_v0.3.obda";

    //private OntopConnector connector;
    private ReadTransaction transaction;

    public test1(){
        setContentPane(contentPanel);
        setModal(true);
        //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE );
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        yearComboBox.setModel(new DefaultComboBoxModel(
                new String[] { "1995-2000","2001-2005", "2006-2016"}));


        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(proceField.getText()!=null){
                    PROCEEDING = proceField.getText();
                }

                yearsDuration = yearComboBox.getSelectedItem().toString();
                executeProceedingQuery(PROCEEDING,yearsDuration);
            }
        });

        showAuthorBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)  {
                if (authorField.getText() != null) {
                    AUTHOR = authorField.getText();
                }
                //executeAutorQuery(AUTHOR);
            }

        });
        transaction = new ReadTransaction();
    }

    //Query Proceedings
    private void executeProceedingQuery(String PROCEEDING,String yearsDuration) {
        try {
                //connector.executeProceedingQuery(PROCEEDING,yearsDuration);
            transaction.executeProceedingQuery(PROCEEDING);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // Query Author
//    private void executeAutorQuery(String author) {
//        try {
//            HashMap<String,String> authorInfo = new HashMap<String, String>();
//            authorInfo = connector.executeAutorQuery(author);
//            if(!authorInfo.isEmpty()){
//                AuthorGUI authorGUI = new AuthorGUI();
//                authorGUI.setFields(authorInfo);
//                authorGUI.pack();
//                authorGUI.setVisible(true);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        test1 dialog = new test1();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}

/**
 * Created by clair on 12/30/2016.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class SwingDisplay {
    public static String PROCEEDING;
    public static String AUTHOR;
    public static String yearsDuration;
    private JFrame mainFrame;
    private JLabel yearLabel;
    private JLabel preceedingLable;
    private JPanel controlPanel;
    private JComboBox yearComboBox;
    private JTextField proceField;
    private JButton showButton;
    private JButton similarConfBtn;
    private JPanel similarPanel;

    private LineChart_AWT chart;
    private BarChartSimiConf simiChart;
    private JTextArea commentTextArea;
    static String owlFile = "ontology/swrc_v0.3.owl";
    static String obdaFile = "ontology/swrc_v0.3.obda";

    //private OntopConnector connector;
    private ReadTransaction transaction;

    public SwingDisplay(){
        prepareGUI();
    }
    public static void main(String[] args){
        SwingDisplay  swingContainerDemo = new SwingDisplay();
        swingContainerDemo.showJPanelDemo();
        swingContainerDemo.showSimilarConf();
    }

    private void prepareGUI(){
        transaction = new ReadTransaction();
        mainFrame = new JFrame("DBLP Application");
        mainFrame.setSize(700,700);
        mainFrame.setLayout(null);
       // mainFrame.setLayout(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                transaction.close();
                System.exit(0);
            }
        });

        controlPanel = new JPanel();
       // controlPanel.setLayout(new FlowLayout());
        controlPanel.setLayout(null);

        yearLabel = new JLabel("Year:", JLabel.CENTER);
        preceedingLable = new JLabel("Proceeding:",JLabel.CENTER);
        yearLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        preceedingLable.setFont(new Font("Serif", Font.PLAIN, 18));

        yearComboBox = new JComboBox();
        yearComboBox.setModel(new DefaultComboBoxModel(
                new String[] { "1995-2000","2001-2005", "2006-2016"}));
        yearComboBox.setFont(new Font("Serif", Font.PLAIN, 18));

        proceField = new JTextField();
        showButton = new JButton();
        showButton.setText("Show");
        similarConfBtn = new JButton();
        similarConfBtn.setText("Similar Conf");
        controlPanel.add(yearLabel);
        controlPanel.add(preceedingLable);
        controlPanel.add(yearComboBox);
        controlPanel.add(proceField);
        controlPanel.add(showButton);
        controlPanel.add(similarConfBtn);

        yearLabel.setBounds(0,0,100,50);
        yearComboBox.setBounds(100,18,150,25);
        preceedingLable.setBounds(0,50,100,50);
        proceField.setBounds(100,68,150,25);
        showButton.setBounds(100,100,80,25);
        similarConfBtn.setBounds(200,100,120,25);
        mainFrame.add(controlPanel);
        controlPanel.setBounds(10,10,350,200);

        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean changed =false;
                if(proceField.getText()!=null && !proceField.getText().equals(PROCEEDING)){
                    System.out.println("=========="+proceField.getText().equals(PROCEEDING));
                    PROCEEDING = proceField.getText();
                    changed = true;
                }
                else{
                    changed = false;
                }
                long startTime = System.currentTimeMillis();
                yearsDuration = yearComboBox.getSelectedItem().toString();
                executeProceedingQuery(PROCEEDING,yearsDuration,changed);
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("elapsedTime:"+elapsedTime);
            }
        });
        similarConfBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(proceField.getText()!=null){
                    PROCEEDING = proceField.getText();
                }
                getSimilarConf();
            }
        });



    }
    //Query Proceedings
    private void executeProceedingQuery(String PROCEEDING,String yearsDuration,boolean changed) {
        try {
            //connector.executeProceedingQuery(PROCEEDING,yearsDuration);
            // Check if this the first execution,
            if(changed == true) {
                System.out.println("Changed");
                transaction.startTranscation();
                transaction.executeProceedingQuery(PROCEEDING);
                //chart = new BarChart_AWT("Conferences Statistics", PROCEEDING + " Conference Statistics",
                //transaction.map_year_numOfinPro, transaction.years);
            }

            chart = new LineChart_AWT(PROCEEDING + " Conference Statistics",
                        transaction.getYearNumberOfInProc(), transaction.getYears(yearsDuration));

            mainFrame.add(chart.chartPanel);
            chart.chartPanel.setBounds(25,250,600,400);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    private void showJPanelDemo(){
        mainFrame.setVisible(true);
    }

    private void showSimilarConf(){
        similarPanel = new JPanel();
        similarPanel.setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Similar Conference");
        border.setTitleFont(new Font("Serif", Font.PLAIN, 18));
        Border margin = new EmptyBorder(10,10,10,10);
        similarPanel.setBorder(new CompoundBorder(border, margin));

//        commentTextArea =
//                new JTextArea("",5,20);
//        JScrollPane scrollPane = new JScrollPane(commentTextArea);
//        similarPanel.add(scrollPane);
        //scrollPane.setBounds(7,25,185,170);

        mainFrame.add(similarPanel);
        similarPanel.setBounds(360,10,320,260);

    }

    private void getSimilarConf(){
        SimilarConference similarConference = new SimilarConference(PROCEEDING);
        Map sortedMap = similarConference.caculateSimilarConf();
        simiChart = new BarChartSimiConf("",sortedMap);

        similarPanel.add(simiChart.chartPanel);
        simiChart.chartPanel.setBounds(7,25,305,230);

//        mainFrame.add(simiChart.chartPanel);
//        simiChart.chartPanel.setBounds(370,20,310,250);


        //commentTextArea.setText();
    }
}

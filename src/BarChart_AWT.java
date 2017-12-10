
/**
 * Created by clair on 11/25/2016.
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.TreeMap;

public class BarChart_AWT
{
    public ChartPanel chartPanel;
    public BarChart_AWT( String chartTitle, TreeMap<String, Integer> map_year_numOfinPro, ArrayList<String> years )
    {
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "Year",
                "NumOfinProceedings",
                createDataset(map_year_numOfinPro, years),
                PlotOrientation.VERTICAL,
                true, true, false);
        chartPanel = new ChartPanel( barChart );
        chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );
    }

    private CategoryDataset createDataset(TreeMap<String, Integer> map_year_numOfinPro,ArrayList<String> years )
    {
        final DefaultCategoryDataset dataset =
                new DefaultCategoryDataset( );
        for(int i =0; i <  map_year_numOfinPro.size();i++) {
            dataset.addValue(map_year_numOfinPro.get(years.get(i)), "NumofInproceedings",years.get(i));
        }
        return dataset;
    }
}
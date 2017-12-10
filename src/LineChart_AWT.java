
/**
 * Created by clair on 11/25/2016.
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class LineChart_AWT
{
    public ChartPanel chartPanel;
    public LineChart_AWT( String chartTitle, TreeMap<String, Integer> map_year_numOfinPro,ArrayList<String> years)
    {
        JFreeChart lineChart  = ChartFactory.createLineChart(
                chartTitle,
                "Year",
                "NumOfinProceedings",
                createDataset(map_year_numOfinPro,years),
                PlotOrientation.VERTICAL,
                true, true, false);
        chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );
    }

    private CategoryDataset createDataset(TreeMap<String, Integer> map_year_numOfinPro,ArrayList<String> years)
    {
        final DefaultCategoryDataset dataset =
                new DefaultCategoryDataset( );

            for(int i =0; i< years.size();i++) {
                if(map_year_numOfinPro.containsKey(years.get(i))) {
                    dataset.addValue(map_year_numOfinPro.get(years.get(i)), "NumofInproceedings", years.get(i));
                }
                else
                {
                    dataset.addValue(0, "NumofInproceedings", years.get(i));
                }
            }
       // }
        return dataset;
    }
}
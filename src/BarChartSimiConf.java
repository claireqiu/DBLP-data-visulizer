
/**
 * Created by clair on 11/25/2016.
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.Color;
import java.awt.Paint;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BarChartSimiConf
{
    public ChartPanel chartPanel;
    public BarChartSimiConf(String chartTitle, Map <String, Double> sortedMap )
    {
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "Author",
                "Similarity Value",
                createDataset(sortedMap),
                PlotOrientation.HORIZONTAL,
                true, true, false);
        barChart.setBorderPaint(Color.BLUE);
        chartPanel = new ChartPanel( barChart );
        chartPanel.setPreferredSize(new java.awt.Dimension( 185 , 170 ) );
    }

    private CategoryDataset createDataset(Map<String, Double> sortedMap)
    {
        final DefaultCategoryDataset dataset =
                new DefaultCategoryDataset( );

         //Get a set of the entries on the sorted map
          Set set = sortedMap.entrySet();
        // Get an iterator
         Iterator i = set.iterator();
        int temp = 0;
        // Display elements
        while (i.hasNext() && temp < 10) {
            Map.Entry me = (Map.Entry) i.next();
            System.out.println(me.getValue()+":"+me.getKey().toString());
            dataset.addValue((Double) me.getValue(), "value",me.getKey().toString());
            temp++;
        }
        return dataset;
    }
}
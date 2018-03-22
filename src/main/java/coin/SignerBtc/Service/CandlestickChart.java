package coin.SignerBtc.Service;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.ApplicationFrame;

public class CandlestickChart extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public CandlestickChart(String title, DefaultHighLowDataset dataset) {
    super(title);
//    final DefaultHighLowDataset dataset = createDataset();
    final JFreeChart chart = createChart(title, dataset);
    chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
    XYPlot plot = chart.getXYPlot();
    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
    yAxis.setAutoRangeIncludesZero(false);
    final ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    setContentPane(chartPanel);
  }

  private JFreeChart createChart(String title, final DefaultHighLowDataset dataset) {
    final JFreeChart chart = ChartFactory.createCandlestickChart(title, "Time", "Value", dataset, true);
    return chart;
  }

}

package coin.SignerBtc.Controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import coin.SignerBtc.Service.CandlestickChart;
import coin.SignerBtc.Service.ScanAllSymbol;
import coin.SignerBtc.Service.TelegramMessage;
import coin.SignerBtc.Utils.Constants;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceCandlestick;
import com.webcerebrium.binance.datatype.BinanceSymbol;

@Controller
public class ScanPriceController {

  @Autowired
  private ScanAllSymbol scanService;

  @Autowired
  private TelegramMessage telegramMess;

  /* BTC or BTCUSDT or ... */
  private String symbolCompare = "BTC";

  private BinanceApi api = new BinanceApi();
  private Set<String> symbolWorking = new HashSet<String>();
  private Set<String> symbolToTrackDownTrend = new HashSet<String>();
  private Map<String, LinkedList<BigDecimal>> mapPrice = new HashMap<String, LinkedList<BigDecimal>>();
  private Map<String, Integer> signerPrice = new HashMap<String, Integer>();
  private Logger logger = LoggerFactory.getLogger(ScanPriceController.class);

  public void scanSigner() {
    try {
      logger.info("***");
      Map<String, BigDecimal> maps = scanService.scanAll(api);

      for (String key : maps.keySet()) {
        /* check price < 2$ and check name */
        if (symbolWorking.isEmpty()) {
          symbolWorking = scanService.filterPrice(maps, symbolCompare);
        }

        if (!symbolWorking.contains(key)) {
          continue;
        }

        /* not have value yet */
        if (mapPrice.get(key) == null) {
          LinkedList<BigDecimal> lstNew = new LinkedList<BigDecimal>();
          lstNew.addLast(maps.get(key));
          mapPrice.put(key, lstNew);
          /* Up */
        } else if (mapPrice.get(key).get(mapPrice.get(key).size() - 1).compareTo(maps.get(key)) < 0) {
          LinkedList<BigDecimal> lstNew = mapPrice.get(key);
          lstNew.addLast(maps.get(key));
          if (lstNew.size() >= 0) {
            lstNew.removeFirst();
            // if (signerPrice.containsKey(key) &&
            // scanService.IsUpTrend(lstNew)) {
            // int size = lstNew.size();
            List<BinanceCandlestick> kLine = scanService.getOldCandltick(new BinanceSymbol(key));
            if (signerPrice.containsKey(key) && kLine != null) {
              telegramMess.postMultipart(getImageChart(kLine, key));
              symbolToTrackDownTrend.add(key);
              // symbolWorking.remove(key);
              // facebookService.sendTextMessage("=> Coin đang tăng: " + key);
              telegramMess.sendToChannel("=> Up: " + key + " https://www.binance.com/trade.html?symbol=" + key.subSequence(0, key.length() - 3) + "_BTC");
              logger.info("Up: " + key);
            }
          }
          mapPrice.put(key, lstNew);
          /* Down */
        } else if (mapPrice.get(key).get(mapPrice.get(key).size() - 1).compareTo(maps.get(key)) > 0) {
          LinkedList<BigDecimal> lstNew = mapPrice.get(key);
          lstNew.addLast(maps.get(key));
          if (lstNew.size() >= 1) {
            lstNew.removeFirst();
          }
          mapPrice.put(key, lstNew);
          signerPrice.put(key, Constants.DOWN_TREND);
        }

        /* check if symbol down */

      }
    } catch (Exception e) {
      logger.error("ScanPriceController Error: {}", e);
    } catch (BinanceApiException e) {
      e.printStackTrace();
    }
  }

  public Boolean comparePriceChange(BigDecimal num1, BigDecimal num2, BigDecimal num3) {
    MathContext mc = new MathContext(2, RoundingMode.HALF_UP);
    if (num3.divide(num2.subtract(num1), mc).compareTo(new BigDecimal("4")) >= 0) {
      return true;
    }
    return false;
  }

  public File getImageChart(List<BinanceCandlestick> candlestickData, String symbol) throws IOException {
    CandlestickChart chart = new CandlestickChart(symbol, createDataset(candlestickData));
    chart.pack();
    RefineryUtilities.centerFrameOnScreen(chart);
    chart.setVisible(true);
    
    BufferedImage img = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_RGB);
    chart.paint(img.getGraphics());
    
    File outputfile = new File("D:/temp/saved.png");
    ImageIO.write(img, "png", outputfile);
    chart.dispose();
    return outputfile;
  }



  public DefaultHighLowDataset createDataset(List<BinanceCandlestick> candlestickData) {
    int serice = candlestickData.size();

    Date[] date = new Date[serice];
    double[] high = new double[serice];
    double[] low = new double[serice];
    double[] open = new double[serice];
    double[] close = new double[serice];
    double[] volume = new double[serice];

    for (int i = 0; i < serice; i++) {
      BinanceCandlestick can = candlestickData.get(i);
      date[i] = new Date(can.getOpenTime());
      high[i] = can.getHigh().doubleValue();
      low[i] = can.getLow().doubleValue();
      open[i] = can.getOpen().doubleValue();
      close[i] = can.getClose().doubleValue();
      volume[i] = can.getVolume().doubleValue();
    }

    DefaultHighLowDataset data = new DefaultHighLowDataset("", date, high, low, open, close, volume);
    return data;
  }
}

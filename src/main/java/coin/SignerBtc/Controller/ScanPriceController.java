package coin.SignerBtc.Controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
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
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.RefineryUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import coin.SignerBtc.Service.CandlestickChart;
import coin.SignerBtc.Service.ScanAllSymbol;
import coin.SignerBtc.Service.TelegramMessage;
import coin.SignerBtc.Utils.Constants;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceCandlestick;
import com.webcerebrium.binance.datatype.BinanceInterval;
import com.webcerebrium.binance.datatype.BinanceSymbol;

@Controller
@RequestMapping(value = "/scan")
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
  private Logger logger = Logger.getLogger(ScanPriceController.class.getName());

  @RequestMapping(value = "/signer")
  public Object scanSigner() {
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
      logger.warning("ScanPriceController Error: " + e);
      return new ResponseEntity<String>("error", HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (BinanceApiException e) {
      e.printStackTrace();
      return new ResponseEntity<String>("error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<String>("ok", HttpStatus.OK);
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
    
    chart.setBackground(Color.WHITE);
    chart.setUndecorated(true);
    Component c = chart.getContentPane();
    chart.pack();
    RefineryUtilities.centerFrameOnScreen(chart);
//    chart.setVisible(true);
    BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics g = img.getGraphics();
    c.print(g);
    g.dispose();
    File outputfile = new File("./saved.png");
    ImageIO.write(img, "png", outputfile);
    chart.dispose();
    return outputfile;
  }

  public void test() throws BinanceApiException, IOException {
    List<BinanceCandlestick> kLine = (new BinanceApi()).klines(new BinanceSymbol("BTCUSDT"), BinanceInterval.FIFTEEN_MIN, 7, null);
    telegramMess.postMultipart(getImageChart(kLine, "BTCUSDT"));
  }

  public static DefaultHighLowDataset createDataset(List<BinanceCandlestick> candlestickData) {
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

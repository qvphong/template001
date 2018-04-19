package coin.SignerBtc.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import coin.SignerBtc.Service.TelegramMessage;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceCandlestick;
import com.webcerebrium.binance.datatype.BinanceInterval;
import com.webcerebrium.binance.datatype.BinanceSymbol;

@Controller
public class PriceChangeController {
  @Autowired
  private TelegramMessage telegramMess;

  private Logger logger = LoggerFactory.getLogger(PriceChangeController.class);
  private int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
  private Map<String, BigDecimal> openPriceDay = null;
  private Map<String, Float> currentChange = new HashMap<String, Float>();
  private Set<String> symbolLst = new HashSet<String>();

  public void ScanChange() {
    BinanceApi api = new BinanceApi();
    try {
      int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
      if (openPriceDay == null || nowDay != day) {
        day = nowDay;
        /* Get price day before */
        openPriceDay = getOldPrice(api);
        currentChange = new HashMap<String, Float>();
        symbolLst = openPriceDay.keySet();
      }
      /* get list symbol */
      if (symbolLst == null) {
        symbolLst = openPriceDay.keySet();
      }
      /* Get current price per minute */
      Map<String, BigDecimal> currentPrice = getCurrentPrice(api);

      for (String symbol : symbolLst) {
        BigDecimal Beprice = openPriceDay.get(symbol);
        BigDecimal curPrice = currentPrice.get(symbol);
        Float change = curPrice.subtract(Beprice).divide(Beprice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
        Float currChange = currentChange.get(symbol);
        if (currentChange.containsKey(symbol)) {
          /* gui tin hieu */
          if (change > currChange) {
            if (Math.abs(change - currChange) >= 1) {
              System.out.println(change + " > " + currChange);
              telegramMess.sendToChannel("Binance " + symbol + " tăng so với giá mở cửa " + change + "% từ " + currChange + "%");
            }
          } 
//          else {
//            if (Math.abs(currChange - change) > 1) {
//              telegramMess.sendToChannel(symbol + " Down: " + change);
//            }
//          }
        }

        currentChange.put(symbol, change);
      }

    } catch (BinanceApiException be) {
      logger.error("BinanceApiException: {}", be);
    } catch (Exception e) {
      logger.error("PriceChangeController ERROR: {}", e);
    }
  }

  public Map<String, BigDecimal> getCurrentPrice(BinanceApi api) throws BinanceApiException {
    return api.pricesMap();
  }

  public Map<String, BigDecimal> getOldPrice(BinanceApi api) throws BinanceApiException {
    Map<String, BigDecimal> price = new HashMap<String, BigDecimal>();
    Set<String> symbols = api.allBookTickersMap().keySet();
    for (String symbol : symbols) {
      BinanceSymbol sym = new BinanceSymbol(symbol);
      List<BinanceCandlestick> klines = (new BinanceApi()).klines(sym, BinanceInterval.ONE_DAY, 1, null);
      if (!klines.isEmpty()) {
        price.put(symbol, klines.get(0).getOpen());
      }
    }
    return price;
  }
}

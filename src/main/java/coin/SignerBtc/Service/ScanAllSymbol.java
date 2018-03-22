package coin.SignerBtc.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceCandlestick;
import com.webcerebrium.binance.datatype.BinanceInterval;
import com.webcerebrium.binance.datatype.BinanceSymbol;

@Service
public class ScanAllSymbol {

	private Logger logger = LoggerFactory.getLogger(ScanAllSymbol.class);

	public Map<String, BigDecimal> scanAll(BinanceApi api) {
		Map<String, BigDecimal> mapPrice = null;
		try {
			mapPrice = api.pricesMap();
		} catch (BinanceApiException e) {
			logger.error("--> ERROR: Scan all price: {}", e);
		}
		return mapPrice;
	}

	public Set<String> filterPrice(Map<String, BigDecimal> maps, String symbol) {
		Set<String> symbols = new HashSet<String>();
		for (String key : maps.keySet()) {
			if (key.contains(symbol) && maps.get(key).compareTo(new BigDecimal("0.0002294")) < 0) {
				symbols.add(key);
			}
		}
		return symbols;
	}

	public Boolean filterName(String name, String symbolCompare) {
		if (name.contains(symbolCompare)) {
			return true;
		}
		return false;
	}

	public Boolean IsUpTrend(LinkedList<BigDecimal> lstNew) {
		BigDecimal elementBefore = null;
		Iterator<BigDecimal> itr = lstNew.iterator();
		while (itr.hasNext()) {
			BigDecimal val = itr.next();
			if (elementBefore != null) {
				if (elementBefore.compareTo(val) > 0) {
					return false;
				} else {
					elementBefore = val;
				}
			} else {
				elementBefore = val;
			}
		}
		return true;
	}
	
	public List<BinanceCandlestick> getOldCandltick(BinanceSymbol symbol) throws BinanceApiException {
	  BinanceApi api = new BinanceApi();
	  MathContext mc = new MathContext(2, RoundingMode.HALF_UP);
	  List<BinanceCandlestick> kLine = api.klines(symbol, BinanceInterval.FIVE_MIN, 5, null);
	  BinanceCandlestick candl1 = kLine.get(kLine.size() - 3);
      BinanceCandlestick candl2 = kLine.get(kLine.size() - 2);
      if (candl1.getClose().compareTo(candl1.getOpen()) < 0 || candl2.getClose().compareTo(candl2.getOpen()) < 0) {
        return null;
      }
	  BigDecimal sub1 = candl1.getClose().subtract(candl1.getOpen());
	  BigDecimal mid1 = candl1.getClose().subtract(candl1.getOpen()).divide(new BigDecimal("2"), mc);
	  BigDecimal sub2 = candl2.getClose().subtract(candl2.getOpen());
	  if (sub1.compareTo(new BigDecimal(0)) != 0 && sub2.divide(sub1, mc).intValue() >= 2 && candl2.getOpen().compareTo(mid1) > 0) {
	    return kLine;
	  }
	  return null;
	}
}

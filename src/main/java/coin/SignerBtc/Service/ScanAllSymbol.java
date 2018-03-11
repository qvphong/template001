package coin.SignerBtc.Service;

import java.math.BigDecimal;
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
}

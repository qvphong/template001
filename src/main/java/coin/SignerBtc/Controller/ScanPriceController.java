package coin.SignerBtc.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.webcerebrium.binance.api.BinanceApi;

import coin.SignerBtc.Service.FacebookService;
import coin.SignerBtc.Service.ScanAllSymbol;
import coin.SignerBtc.Utils.Constants;

@Controller
public class ScanPriceController {

	@Autowired
	private ScanAllSymbol scanService;

	@Autowired
	private FacebookService facebookService;
	
	/* BTC or BTCUSDT or ...*/
	private String symbolCompare = "BTC";

	private BinanceApi api = new BinanceApi();
	private Set<String> symbolWorking = new HashSet<String>();
	private Set<String> symbolToTrackDownTrend = new HashSet<String>(); 
	private Map<String, LinkedList<BigDecimal>> mapPrice = new HashMap<String, LinkedList<BigDecimal>>();
	private Map<String, Integer> signerPrice = new HashMap<String, Integer>();
	private Logger logger = LoggerFactory.getLogger(ScanPriceController.class);
	
	public void scanSigner() {
		try {
			logger.info("Scan signer is working ***********************");
			
			Map<String, BigDecimal> maps = scanService.scanAll(api);
			
			for (String key : maps.keySet()) {
				/* check price < 2$ and check name */
				symbolWorking.add("BCPTBTC");
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
					if (lstNew.size() >= 5) {
						lstNew.removeFirst();
						if (signerPrice.containsKey(key) && scanService.IsUpTrend(lstNew)) {
							symbolToTrackDownTrend.add(key);
							symbolWorking.remove(key);
							facebookService.sendTextMessage("=> Coin đang tăng: " + key);
						}
					}
					mapPrice.put(key, lstNew);
					/* Down */
				} else if (mapPrice.get(key).get(mapPrice.get(key).size() - 1).compareTo(maps.get(key)) > 0) {
					LinkedList<BigDecimal> lstNew = mapPrice.get(key);
					lstNew.addLast(maps.get(key));
					if (lstNew.size() >= 5) {
						lstNew.removeFirst();
					}
					mapPrice.put(key, lstNew);
					signerPrice.put(key, Constants.DOWN_TREND);
				}
				
				/* check if symbol down */
				
			}
		} catch (Exception e) {
			logger.error("ScanPriceController Error: {}", e);
		}
	}
	
	/*public void scanPrice() {
		try {
			Map<String, BigDecimal> maps = scanService.scanAll(api);
			for (String key : maps.keySet()) {
				 check name
				if (!scanService.filterName(key, symbolCompare)) {
					continue;
				}
				 check price < 2$
				if (!scanService.filterPrice(maps.get(key))) {
					continue;
				}
				 not have value yet 
				if (mapPrice.get(key) == null) {
					List<BigDecimal> lstNew = new ArrayList<>();
					lstNew.add(maps.get(key));
					mapPrice.put(key, lstNew);
				} else if (mapPrice.get(key).get(mapPrice.get(key).size() - 1).compareTo(maps.get(key)) < 0) {
					List<BigDecimal> lstNew = mapPrice.get(key);
					lstNew.add(maps.get(key));
					if (lstNew.size() >= 5) {
						facebookService.sendTextMessage("=> Coin đang tăng: " + key);
						mapPrice.put(key, null);
					} else {
						mapPrice.put(key, lstNew);
					}
				} else if (mapPrice.get(key).get(mapPrice.get(key).size() - 1).compareTo(maps.get(key)) > 0) {
					if (mapPrice.get(key).size() == 4) {
						facebookService.sendTextMessage("=> Coin đang giảm: " + key);
					}
					mapPrice.put(key, null);
				}
			}
		} catch (Exception e) {
			logger.error("ScanPriceController Error: {}", e);
		}
	}*/
	
}

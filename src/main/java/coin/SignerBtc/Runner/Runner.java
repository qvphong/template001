package coin.SignerBtc.Runner;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.webcerebrium.binance.api.BinanceApi;

import coin.SignerBtc.Controller.ScanPriceController;
import coin.SignerBtc.Service.CallApi;
import coin.SignerBtc.Service.FacebookService;
import coin.SignerBtc.Service.FollowPrice;

@Component
public class Runner implements CommandLineRunner {

	@Autowired
	ScanPriceController scanPriceController;

	@Autowired
	FacebookService faceService;

	@Autowired
	FollowPrice follow;

	@Autowired
	CallApi callApi;

	private Logger logger = LoggerFactory.getLogger(Runner.class);

	public void run(String... arg0) {
		try {
			while (true) {
//				Boolean isUp = follow.getAllOrder();
//				if (isUp != null) {
//					if (isUp) {
//						follow.sellLimit(api, follow.PriceToSellOrBuy);
//					} else {
//						follow.buyLimit(api, follow.PriceToSellOrBuy);
//					}
//				}
				 scanPriceController.scanSigner();
				Thread.sleep(60000*15);
			}
		} catch (Exception e) {
			logger.error("ERROR Runner: {}", e);
		}
	}

	@PostConstruct
	public void contruct() {
	}
}

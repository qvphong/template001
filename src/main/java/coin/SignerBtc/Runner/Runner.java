package coin.SignerBtc.Runner;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.webcerebrium.binance.api.BinanceApiException;
import coin.SignerBtc.Controller.PriceChangeController;
import coin.SignerBtc.Controller.ScanPriceController;

@Component
public class Runner implements CommandLineRunner {

  @Autowired
  ScanPriceController scanPriceController;
  
  @Autowired
  PriceChangeController priceScan;

  private Logger logger = LoggerFactory.getLogger(Runner.class);

  public void run(String... arg0) {

    try {
//      try {
//        scanPriceController.test();
//      } catch (BinanceApiException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//      }
      while (true) {
        // Boolean isUp = follow.getAllOrder();
        // if (isUp != null) {
        // if (isUp) {
        // follow.sellLimit(api, follow.PriceToSellOrBuy);
        // } else {
        // follow.buyLimit(api, follow.PriceToSellOrBuy);
        // }
        // }
        priceScan.ScanChange();
        Thread.sleep(60000);
      }
    } catch (Exception e) {
      logger.error("ERROR Runner: {}", e);
    }
  }

  @PostConstruct
  public void contruct() {}
}

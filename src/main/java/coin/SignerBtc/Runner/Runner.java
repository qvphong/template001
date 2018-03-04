package coin.SignerBtc.Runner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.webcerebrium.binance.api.BinanceApiException;

import coin.SignerBtc.Controller.SignerController;
import coin.SignerBtc.Service.CallApi;
import coin.SignerBtc.Service.DepthTrade;
import coin.SignerBtc.Utils.Constants;

@Component
public class Runner implements CommandLineRunner {

	@Autowired
	SignerController signerController;

	@Autowired
	CallApi callApi;

	List<Double> datas = new ArrayList<Double>(2);

	public void run(String... arg0) {
		try {
			while (true) {
				try {
					signerController.depthController(Constants.BTC_USDT);
//					Thread.sleep(700);
				} catch (BinanceApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/********************/
				// Double fl = callApi.callApi();
				// if (!datas.isEmpty()) {
				// if (datas.get(0).compareTo(fl) != 0) {
				// datas.set(1, datas.get(0));
				// datas.set(0, fl);
				// System.out.println("---------- " + fl + " -----------");
				// }
				// } else {
				// datas.add(fl);
				// datas.add(fl);
				// System.out.println("---------- " + fl + " -----------");
				// }
				// Thread.sleep(2000);
				/**********************/

			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}
}

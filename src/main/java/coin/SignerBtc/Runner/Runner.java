package coin.SignerBtc.Runner;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import coin.SignerBtc.Controller.ScanPriceController;
import coin.SignerBtc.Service.CallApi;
import coin.SignerBtc.Service.FacebookService;

@Component
public class Runner implements CommandLineRunner {
	
	@Autowired
	ScanPriceController scanPriceController;
	
	@Autowired
	FacebookService faceService;

	@Autowired
	CallApi callApi;
	
	private Logger logger = LoggerFactory.getLogger(Runner.class);

	public void run(String... arg0) {
		try {
			while (true) {
//				face.sendTextMessage("ok");
				scanPriceController.scanSigner();
				Thread.sleep(200000);
			}
		} catch (Exception e) {
			logger.error("ERROR Runner: {}", e);
		}
	}
	
	@PostConstruct
	public void contruct() {
		faceService.senderLst.add("1607108626069359");
	}
}

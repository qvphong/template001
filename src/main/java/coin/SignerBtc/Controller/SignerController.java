//package coin.SignerBtc.Controller;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import com.binance.api.client.BinanceApiClientFactory;
//import com.binance.api.client.BinanceApiRestClient;
//import com.binance.api.client.BinanceApiWebSocketClient;
//import com.binance.api.client.domain.account.Order;
//import com.binance.api.client.domain.account.request.OrderRequest;
//import com.binance.api.client.domain.market.CandlestickInterval;
//import com.webcerebrium.binance.api.BinanceApiException;
//
//import coin.SignerBtc.Service.DepthTrade;
//import coin.SignerBtc.Service.OrderService;
//import coin.SignerBtc.Utils.Constants;
//
//@Component
//public class SignerController {
//	
//	@Autowired
//	private DepthTrade depthTrade;
//
//	@Value("${current.trade.symbol}")
//	private String currentTrade;
//
//	@Value("${api.key}")
//	private String apiKey;
//
//	@Value("${api.secret}")
//	private String apiSecret;
//
//	@Value("${amount.btc}")
//	private String amountBtc;
//
//	@Value("${distand}")
//	private String distand;
//	
//	// private DecimalFormat df = new DecimalFormat("#.#");
//
//	@Autowired
//	private OrderService orderService;
//
//	public void getCurrentPrice() {
//		BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();
//		client.onCandlestickEvent(Constants.BTC_USDT, CandlestickInterval.ONE_MINUTE, response -> System.out.println(response));
//	}
//
//	public void order() {
//		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, apiSecret);
//		BinanceApiRestClient client = factory.newRestClient();
//		BigDecimal distandVal = new BigDecimal(distand);
//		
//		// Getting list of open orders
//		List<Order> openOrders = client.getOpenOrders(new OrderRequest(Constants.BTC_USDT));
//		System.out.println(openOrders);
//		if (!openOrders.isEmpty()) {
//			orderService.cancelAllOrder(client, openOrders);
//		}
//
//		BinanceApiWebSocketClient client1 = BinanceApiClientFactory.newInstance().newWebSocketClient();
//		client.ping();
//		orderService.buy(Constants.BTC_USDT, "0.001", client1, client, distandVal);
//	}
//
//	public void depthController(String symbol) throws BinanceApiException {
//		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, apiSecret);
//		BinanceApiRestClient client = factory.newRestClient();
//		depthTrade.depth(client,symbol);
//	}
//
//}

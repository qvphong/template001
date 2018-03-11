//package coin.SignerBtc.Service;
//
//import static com.binance.api.client.domain.account.NewOrder.limitBuy;
//import static com.binance.api.client.domain.account.NewOrder.marketBuy;
//
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.binance.api.client.BinanceApiRestClient;
//import com.binance.api.client.BinanceApiWebSocketClient;
//import com.binance.api.client.domain.TimeInForce;
//import com.binance.api.client.domain.account.NewOrderResponse;
//import com.binance.api.client.domain.account.Order;
//import com.binance.api.client.domain.account.request.CancelOrderRequest;
//import com.binance.api.client.domain.account.request.OrderStatusRequest;
//import com.binance.api.client.domain.market.CandlestickInterval;
//import com.binance.api.client.exception.BinanceApiException;
//import com.webcerebrium.binance.api.BinanceApi;
//import com.webcerebrium.binance.datatype.BinanceOrder;
//import com.webcerebrium.binance.datatype.BinanceOrderPlacement;
//import com.webcerebrium.binance.datatype.BinanceOrderSide;
//import com.webcerebrium.binance.datatype.BinanceSymbol;
//
//import coin.SignerBtc.Utils.Constants;
//
//@Service
//public class OrderService {
//
//	@Value("${current.trade.symbol}")
//	private String currentTrade;
//
//	private BigDecimal currentPrice;
//	private DecimalFormat df = new DecimalFormat("#.#");
//
//	public void cancelAllOrder(BinanceApiRestClient client, List<Order> openOrders) {
//		// Canceling an order
//		try {
//			for (Order or : openOrders) {
//				client.cancelOrder(new CancelOrderRequest(Constants.BTC_USDT, or.getOrderId()));
//			}
//		} catch (BinanceApiException e) {
//			System.out.println(e.getError().getMsg());
//		}
//	}
//
//	public Long stopLoss(BigDecimal currentPrice) {
//		Long id = null;
//		try {
//			BinanceApi api = new BinanceApi();
//			BinanceSymbol symbol = new BinanceSymbol(Constants.BTC_USDT);
//			BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.SELL);
//			placement.setPrice(currentPrice.add(BigDecimal.valueOf(new Long("100"))));
//			// placement.setStopPrice(currentPrice.add(BigDecimal.valueOf(new
//			// Long("100"))));
//			placement.setQuantity(BigDecimal.valueOf(0.001)); // sell 1 piece of asset
//			BinanceOrder order = api.getOrderById(symbol, api.createOrder(placement).get("orderId").getAsLong());
//			System.out.println(order.toString());
//			id = order.getOrderId();
//		} catch (com.webcerebrium.binance.api.BinanceApiException e) {
//			System.out.println(e.getMessage());
//		}
//		return id;
//	}
//
//	public void marketOrder(BinanceApiRestClient client, String amount) {
//		client.newOrderTest(marketBuy(Constants.BTC_USDT, "0.001"));
//	}
//	
//	public Long orderAmount(BinanceApiRestClient client, String symbol, String quanty, String price) {
//		NewOrderResponse newOrderResponse = client.newOrder(limitBuy(symbol, TimeInForce.GTC, quanty, price));
//		System.out.println("New order: " + newOrderResponse);
//		return newOrderResponse.getOrderId();
//	}
//	
//	public Boolean checkOrderStatusIsWorking(BinanceApiRestClient client, Long id, String symbol) {
//		Order order = client.getOrderStatus(new OrderStatusRequest(symbol, id));
//		return order.getIsWorking();
//	}
//
//	public void buy(String symbol, String amount, BinanceApiWebSocketClient client1, BinanceApiRestClient client, BigDecimal distandValue) {
//		client1.onCandlestickEvent(symbol, CandlestickInterval.ONE_MINUTE, response -> {
//			System.out.println(response);
//			Order order = client.getOrderStatus(new OrderStatusRequest(symbol, response.getLastTradeId()));
//			// try {
//			currentPrice = new BigDecimal(order.getPrice());
//			System.out.println("current price" + order.getPrice());
//			System.out.println(response);
//			BigDecimal close = new BigDecimal(response.getClose());
//			System.out.println("close: " + close);
//			BigDecimal open = new BigDecimal(response.getOpen());
//			System.out.println("open" + open);
//			if (symbol.equalsIgnoreCase(response.getSymbol()) && close.compareTo(open) > 0) {// response.getBarFinal()
//				System.out.println("==>close > open");
//				if (close.subtract(open).compareTo(distandValue) > 0) {
//					// marketOrder(client, amount);
//					System.out.println("====>close - open > 50");
//				}
//				System.out.println("Buy successfull");
//			} else if (close.compareTo(open) < 0 && close.subtract(open).compareTo(distandValue) < 0) {
//				// stopLoss(currentPrice);
//				System.out.println("==>close < open");
//				System.out.println("Downtrend: " + " Open: " + response.getOpen() + ", Close: " + response.getClose());
//			}
//
//			// } catch (ParseException e) {
//			// System.out.println("Parse BigDecimal error. " + e);
//			// }
//		});
//	}
//
//}

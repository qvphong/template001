package coin.SignerBtc.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceAggregatedTrades;
import com.webcerebrium.binance.datatype.BinanceOrder;
import com.webcerebrium.binance.datatype.BinanceOrderPlacement;
import com.webcerebrium.binance.datatype.BinanceOrderSide;
import com.webcerebrium.binance.datatype.BinanceOrderStatus;
import com.webcerebrium.binance.datatype.BinanceOrderType;
import com.webcerebrium.binance.datatype.BinanceSymbol;

@Component
public class FollowPrice {

	private BigDecimal lastQuanty = new BigDecimal(-1);
	private int count = 0;
	Long lastId = null;
	Map<String, Long> options = new HashMap<String, Long>();
	Boolean isUp = null;
	BigDecimal lastPrice;
	public BigDecimal PriceToSellOrBuy = null;
	

	public Boolean getAllOrder() {
		try {
			BinanceApi api = new BinanceApi();
			// api.setApiKey("QYvZH3DFYM9yIzUZD2MCPjZVRGbzNXhdKRGlpvxDZECjM6u0mAkgmNYGpCJHNG2W");
			// api.setSecretKey("Dj44z710CeXwaHx9wC1E2WY50N3CutVRRLs9Y4pJ2ZwfU8b3zXjhiX5ntb45Dr97");

			BinanceSymbol symbol = new BinanceSymbol("TNBBTC");
			List<BinanceAggregatedTrades> binanceAggregatedTrades = api.aggTrades(symbol, 10, options);
			if (!binanceAggregatedTrades.isEmpty() && binanceAggregatedTrades.size() > 1) {
				lastId = binanceAggregatedTrades.get(binanceAggregatedTrades.size() - 1).getTradeId();
				if (lastId != null) {
					options.put("fromId", lastId);
				}
				// System.out.println("TRADE=" + binanceAggregatedTrades);

				BinanceAggregatedTrades oldTrade = binanceAggregatedTrades.get(0);
				if (oldTrade.getPrice().compareTo(binanceAggregatedTrades.get(1).getPrice()) < 0) {
					isUp = false;
				} else {
					isUp = true;
				}
				lastPrice = oldTrade.getPrice();
				binanceAggregatedTrades.remove(0);
				List<BinanceAggregatedTrades> orderFilled = new ArrayList<BinanceAggregatedTrades>(binanceAggregatedTrades);

				// System.out.println(orderFilled);
				for (BinanceAggregatedTrades o : orderFilled) {
					if (o.getPrice().compareTo(lastPrice) < 0) {
						isUp = false;
					} else {
						isUp = true;
					}
					if (o.getQuantity().compareTo(lastQuanty) == 0) {
						count++;
						System.out.println(o);
					} else {
						count = 0;
						lastQuanty = o.getQuantity();
					}
					if (count == 5) {
						System.out.println(isUp);
						PriceToSellOrBuy = o.getPrice();
						return isUp;
					}
				}
			}
		} catch (BinanceApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	Long buyId = null;
	Long sellId = null;

	public void buyLimit(BinanceApi api, BigDecimal price) {
		try {
			BinanceSymbol symbol = new BinanceSymbol("TNBBTC");
			if (buyId != null && checkOrder(api, buyId, symbol)) {
				return;
			}
			BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.BUY);
			placement.setType(BinanceOrderType.LIMIT);
			placement.setPrice(price);
			placement.setQuantity(BigDecimal.valueOf(0.004)); // buy 10000 of asset
																												// for 0.00001 BTC
			BinanceOrder order = api.getOrderById(symbol, api.createOrder(placement).get("orderId").getAsLong());
			System.out.println(order.toString());
			buyId = order.getOrderId();
		} catch (BinanceApiException e) {
			e.printStackTrace();
		}
	}

	public void sellLimit(BinanceApi api, BigDecimal price) {
		try {
			BinanceSymbol symbol = new BinanceSymbol("TNBBTC");
			if (sellId != null && checkOrder(api, sellId, symbol)) {
				return;
			}
			BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.BUY);
			placement.setType(BinanceOrderType.LIMIT);
			placement.setPrice(price);
			placement.setQuantity(BigDecimal.valueOf(0.004)); // buy 10000 of asset
																												// for 0.00001 BTC
			BinanceOrder order = api.getOrderById(symbol, api.createOrder(placement).get("orderId").getAsLong());
			System.out.println(order.toString());
			sellId = order.getOrderId();
		} catch (BinanceApiException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean checkOrder(BinanceApi api, Long id, BinanceSymbol symbol) throws BinanceApiException {
		BinanceOrderStatus stt = api.getOrderById(symbol, id).getStatus();
		if(stt.equals(BinanceOrderStatus.FILLED)) {
			return true;
		}
		return false;
	}

}

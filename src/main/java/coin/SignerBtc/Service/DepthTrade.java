package coin.SignerBtc.Service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binance.api.client.BinanceApiRestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceSymbol;

import coin.SignerBtc.Utils.Constants;

@Service
public class DepthTrade {

	@Autowired
	private OrderService orderService;

	private BigDecimal distand = new BigDecimal("4");

	private boolean selling;

	@SuppressWarnings("unused")
	public void depth(BinanceApiRestClient client, String symbo) throws BinanceApiException {
		BinanceSymbol symbol = BinanceSymbol.valueOf(symbo);
		JsonObject depth = (new BinanceApi()).depth(symbol);
		// buy
		BigDecimal bidMax = depth.get("bids").getAsJsonArray().get(0).getAsJsonArray().get(0).getAsBigDecimal();
		// sell
		BigDecimal askMin = depth.get("asks").getAsJsonArray().get(0).getAsJsonArray().get(0).getAsBigDecimal();
		System.out.println("BIDS= " + bidMax + ", ASKS= " + askMin);
		if (askMin.subtract(bidMax).compareTo(distand) >= 0) {
			System.out.println(askMin.subtract(bidMax));
			Long buyOrderId = orderService.orderAmount(client, symbo, "0.004", bidMax.toString());
			Boolean buyed = orderService.checkOrderStatusIsWorking(client, buyOrderId, symbo);
			if (!buyed) {
				Long sellOrderId = orderService.stopLoss(askMin);
				Boolean selled = false;
				while (!selled) {
					if (!orderService.checkOrderStatusIsWorking(client, sellOrderId, symbo)) {
						selled = true;
					} else {
						
					}
				}
			}
		}
	}

	public boolean checkShark(JsonObject depth) {
		boolean result = false;
		JsonArray asks = depth.get("asks").getAsJsonArray();
		for (int i = 0; i < asks.size(); i++) {
			if (asks.get(i).getAsJsonArray().get(1).getAsBigDecimal().compareTo(new BigDecimal("0.1")) >= 0) {
				result = true;
			}
		}
		return result;
	}
}

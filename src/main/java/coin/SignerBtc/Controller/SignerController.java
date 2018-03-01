package coin.SignerBtc.Controller;

import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SignerController {
	RestTemplate restTemplate;
	private String url = "https://api.coindesk.com/v1/bpi/currentprice/btc.json";
	
	
	
	public Float callApi() {
		ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		JSONObject jsonObj = new JSONObject(res.getBody());
		JSONObject bpi = (JSONObject) jsonObj.get("bpi");
		Float rate = (Float) bpi.get("rate_float");
		return rate;
	}
}

package coin.SignerBtc.Service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class CallApi {
	private RestTemplate restTemplate = getRestTemplate();
	private String url = "https://api.coindesk.com/v1/bpi/currentprice/btc.json";
	public Double callApi() {
		ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		JSONObject jsonObj = new JSONObject(res.getBody());
		JSONObject bpi = (JSONObject) jsonObj.get("bpi");
		JSONObject usd = (JSONObject) bpi.get("USD");
		Double rate = (Double) usd.get("rate_float");
		return rate;
	}

	private RestTemplate getRestTemplate() {
		HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = null;
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}

			});
			httpClientFactory.setHttpClient(HttpClients.custom().setSSLSocketFactory(sslsf).build());
		} catch (Exception e1) {
		}

		restTemplate = new RestTemplate(httpClientFactory);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		return restTemplate;
	}
}

package coin.SignerBtc.Service;

import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;

@Service
public class CallApi {
	private static RestTemplate restTemplate = getRestTemplate();
	private String url = "https://api.coindesk.com/v1/bpi/currentprice/btc.json";

	public Double callApi() {
		ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		JSONObject jsonObj = new JSONObject(res.getBody());
		JSONObject bpi = (JSONObject) jsonObj.get("bpi");
		JSONObject usd = (JSONObject) bpi.get("USD");
		Double rate = (Double) usd.get("rate_float");
		return rate;
	}

	private static RestTemplate getRestTemplate() {
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

	public static String postMethod(String id, String text, String url) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("recipient", "{\"id\":\"t_100001067544868\"}");
		map.add("message", "test");

		// JsonObject mess = new JsonObject();
		// mess.addProperty("messaging_type", "String");
		// JsonObject idObj = new JsonObject();
		// idObj.addProperty("id", id);
		// mess.add("recipient", idObj);
		//
		// JsonObject textObj = new JsonObject();
		// textObj.addProperty("text", text);
		// mess.add("message", textObj);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(map, requestHeaders);
		RestTemplate rest = new RestTemplate();
		HttpEntity<String> response = null;
		try {
			response = rest.exchange(url, HttpMethod.POST, body, String.class);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMethod(String url) {
		RestTemplate rest = new RestTemplate();
		HttpEntity<String> response = null;
		try {
			response = rest.exchange(url, HttpMethod.GET, null, String.class);
			return response.getBody();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	static String user_access = "EAAIRss3YOh4BAP0NPT2bc1jzzIZCypZBJ0fnWptIzaGu3VhEmOZApYUkspSn9UdrNIXBP0eKVLhwNMLZBDYQYbiHRlaD1H18ybVuaOJuYZBpuOqDqKjacY7yKdPATqfyGZBCZB1sxMCJAoTuAcOX6fKXSsQwsqUvljF6ATCBi6uQWrRhefxEl3oFiFZAtV2oZAYUPH683JdO72wZDZD";
	static String page_token = "582409608772126|beyCrUVwMlsaG5RVyBrPXftX2TM";
	private static String urlpost = "https://graph.facebook.com/v2.12/me/messages?access_token=" + user_access;
	private static String urlGet = "https://graph.facebook.com/v2.6/100001067544868?fields=name,age_range,ids_for_apps,ids_for_pages&access_token=" + page_token;
	static String getMail = "https://graph.facebook.com/v2.12/me/inbox?access_token=EAAIRss3YOh4BADn5KnrQmGwkr2Q9ci3gaeybF0ZAyVnDwYZCpZBKMz3ZB1eKNuubMmCFVUVsKqtNhZBO4t3kOQb8hxGrYEEEkZAKBBvjAQS66Fx0Sp9kXlYcGwah9cYciauHLyVzj2sTMQRH2w60VwwklaFLvgohpvQAlNlG1Y3mgrT9bFsApu8lWhZCu95lAYJ2vlM3D1lgAZDZD";
	static String conversation_page = "https://graph.facebook.com/v2.12/2061886637161446/messages?access_token=EAACEdEose0cBACsbrLUPQSzVXq5TqbeMv9kGfeZAr2ZAPCWH2qpD7E2bzXZAN0m5vI34i1D1VUN1Xy4lyAbZB8jf6N7izqMbuZB6p4HLcoiQ7WK7bouHgdgtDiDO4iHbHgBBNUEJDiMFtCj34SSnlGDqZCdRsMryv82LqMSsNWUDbMXjrn4JwZCLLhGB5ODWG62Wi5cTIBbZAgZDZD";

	public static void main(String[] args) {
		// System.out.println(getMethod(conversation_page));
		// System.out.println(postMethod("t_100001067544868", "test", urlpost));
		callRestFb();
	}

	public static void callRestFb() {
		String pageAccessToken = "EAACEdEose0cBABakwhtWiClkhXzfohNPR0V8QsLEtOQbtQd3jnZAWDrdmQYbu60gqxnu9Q8clTbpvRCUIZBklt0tgNAqrMVrwyqlZCOPa7f8BZBNZC5hvCOobcmEl3ExMGvur5r9h19VNUBfxnCvgZAIW1vLDoGf95jICLRkYo3sNt0EDA6euyieoJU7PRvK2imrAhF08BmAZDZD";

		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_2_6);
		IdMessageRecipient recipient = new IdMessageRecipient("100001067544868");
		Message simpleTextMessage = new Message("Just a simple text");
		SendResponse resp = pageClient.publish("me/messages", SendResponse.class, Parameter.with("recipient", recipient),
				Parameter.with("message", simpleTextMessage)); // one of the messages from above
		System.out.println(resp);
	}
}

package coin.SignerBtc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;

import coin.SignerBtc.Service.FacebookService;

@Controller
public class FacebookController {

	@Autowired
	private FacebookService facebookService;

	private Logger logger = LoggerFactory.getLogger(FacebookController.class);

	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> webhookPost(HttpServletRequest request, HttpServletResponse response, @RequestBody String payload) {
		System.out.println(payload);
		sendMess(payload);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@RequestMapping(value = "/webhook", method = RequestMethod.GET)
	public ResponseEntity<String> verifyWebhook(@RequestParam("hub.mode") final String mode, @RequestParam("hub.verify_token") final String verifyToken,
			@RequestParam("hub.challenge") final String challenge) {

		logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode, verifyToken, challenge);
		if ("phongola".equals(verifyToken)) {
			return ResponseEntity.ok(challenge);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("error");
		}
	}

	public void sendMess(String payload) {
		try {
			JSONObject payloadObj = new JSONObject(payload);
			JSONObject messaging = payloadObj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0);
			String text = messaging.getJSONObject("message").getString("text");
			String sender = messaging.getJSONObject("sender").getString("id");
			if ("start".equals(text)) {
				facebookService.senderLst.add(sender);
				logger.info("List of active Message: " + facebookService.senderLst);
				facebookService.sendTextMessage("OK");
			} else if ("stop".equals(text)) {
				facebookService.senderLst.remove(sender);
				logger.info("List of active Message: " + facebookService.senderLst);
				facebookService.sendTextMessage("OK");
			}
		} catch (Exception e) {
			logger.error("Error FacebookController: {}", e);
		}
	}

}

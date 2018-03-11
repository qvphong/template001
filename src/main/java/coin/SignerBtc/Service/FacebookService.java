package coin.SignerBtc.Service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;

@Service
public class FacebookService {

	@Value("${signer.access.token}")
	private String pageAccessToken;

	// @Value("${signer.id}")
	// private static String appId;

	// @Value("${signer.secret}")
	// private static String appSecret;

	public Set<String> senderLst = new HashSet<>();

	private Logger logger = LoggerFactory.getLogger(FacebookService.class);

	private FacebookClient pageClient = new DefaultFacebookClient(
			"EAAWWzli3MmgBAKtMBkEN0uuJdoZAejOAuFqIwSMxRJBLxp0CFiLWMlxmdxZCajRQcBXittf3VpqmrZAfZBs5jpeZC90NmWXMQZCp78AZBWJRn97f0VAZCPI5dAchMLaSN7GoX0X4qDXSLNrkIgMYd6UB8shZA71LZBOtOTEynUvURigQZDZD",
			Version.VERSION_2_6);

	public void sendTextMessage(String text) {
		for (String sender : senderLst) {
			IdMessageRecipient recipient = new IdMessageRecipient(sender);
			Message simpleTextMessage = new Message(text);
			SendResponse resp = pageClient.publish("me/messages", SendResponse.class, Parameter.with("recipient", recipient),
					Parameter.with("message", simpleTextMessage));
			if (resp.isSuccessful()) {
				logger.info("--> send text message ok, " + text);
			} else {
				logger.info("--> Error send text message, " + text);
			}
		}
	}
}

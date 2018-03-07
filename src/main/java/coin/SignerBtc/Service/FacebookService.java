package coin.SignerBtc.Service;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.send.SendResponse;

public class FacebookService {

  public void sendMess() {
    // the access token can be found in your Facebook app in the messenger section
    String pageAccessToken = "MY PAGE ACCESS TOKEN";

    // create a version 2.6 client
    FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_2_6);

    SendResponse resp = pageclient.publish("me/messages", SendResponse.class, Parameter.with("recipient", recipient),
        Parameter.with("message", message)); // one of the messages from above
  }
}

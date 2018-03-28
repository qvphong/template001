package coin.SignerBtc.Service;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TelegramMessage {
  private final Logger logger = Logger.getLogger(TelegramMessage.class.getName());
  private final String token = "";
  private final String chatId = "";
  private final String url = "https://api.telegram.org/bot" + token + "/sendMessage?chat_id=" + chatId + "&text=";
  private final String urlPostPhoto = "https://api.telegram.org/bot" + token + "/sendPhoto";
  private RestTemplate rest = new RestTemplate();
  
  public Boolean sendToChannel(String mess) {
    logger.info("Method sendToChannel's message: " + mess);
    ResponseEntity<String> response = rest.getForEntity(url + mess, String.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return true;
    }
    return false;
  }

  public void postMultipart(File file) throws ClientProtocolException, IOException {
    HttpPost post = new HttpPost(urlPostPhoto);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    builder.addBinaryBody("photo", file, ContentType.DEFAULT_BINARY, file.getName());
    builder.addTextBody("chat_id", chatId, ContentType.DEFAULT_BINARY);

    HttpEntity entity = builder.build();
    post.setEntity(entity);
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpResponse response = httpclient.execute(post);
    if (response.getStatusLine().getStatusCode() == 200) {
      logger.info("Send photo oke. ");
    } else {
      logger.info("Send photo false. ");
    }
  }
  
  public static void main(String[] args) throws ClientProtocolException, IOException {
  }
}

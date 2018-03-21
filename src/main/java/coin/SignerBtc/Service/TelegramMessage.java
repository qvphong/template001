package coin.SignerBtc.Service;

import java.io.File;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
public class TelegramMessage extends TelegramLongPollingBot {
  private final Logger logger = LoggerFactory.getLogger(TelegramMessage.class);
  private final String token = "";
  private final String chatId = "";
  private final String url = "https://api.telegram.org/bot" + token + "/sendMessage?chat_id=" + chatId + "&text=";
  private RestTemplate rest = new RestTemplate();

  public Boolean sendToChannel(String mess) {
    logger.info("Method sendToChannel's message: " + mess);
    ResponseEntity<String> response = rest.getForEntity(url + mess, String.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return true;
    }
    return false;
  }

  @Override
  public String getBotToken() {
    return "";
  }

  public void sendImg(File img) {
    logger.info("Send photo to channel. ");
    SendPhoto msg = new SendPhoto().setChatId(chatId).setNewPhoto(img);
    try {
      sendPhoto(msg); // Call method to send the photo
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  public void onUpdateReceived(Update arg0) {
    // TODO Auto-generated method stub

  }

  public String getBotUsername() {
    return "@signershank_bot";
  }

  public void postMultipart(File file) throws ClientProtocolException, IOException {
    HttpPost post = new HttpPost("http://echo.200please.com");
    String message = "This is a multipart post";
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    builder.addBinaryBody("upfile", file, ContentType.DEFAULT_BINARY, file.getName());
    builder.addTextBody("text", message, ContentType.DEFAULT_BINARY);

    HttpEntity entity = builder.build();
    post.setEntity(entity);
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpResponse response = httpclient.execute(post);
  }
}

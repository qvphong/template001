package coin.SignerBtc.Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TelegramMessage {
  
  private final String token = "";
  private final String chatId = "";
  private final String url = "https://api.telegram.org/bot" + token + "/sendMessage?chat_id=" + chatId + "&text=";
  private RestTemplate rest = new RestTemplate();
  
  public Boolean sendToChannel(String mess) {
    ResponseEntity<String> response = rest.getForEntity(url + mess, String.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return true;
    }
    return false;
  }
}

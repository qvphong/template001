package coin.SignerBtc.Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TwitterMessage {
  
  private static final String token = "566961336:AAHTdQEg-WK1BvEMcU-zC2mplcdxStZyxek";
  private static final String chatId = "-1001371816162";
  private static final String url = "https://api.telegram.org/bot" + token + "/sendMessage?chat_id=" + chatId + "&text=";
  private static RestTemplate rest = new RestTemplate();
  
  public static Boolean sendToChannel(String mess) {
    ResponseEntity<String> response = rest.getForEntity(url + mess, String.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return true;
    }
    return false;
  }
  
  public static void main(String[] args) {
    sendToChannel("haaaaaaalo");
  }
}

package coin.SignerBtc.Runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import coin.SignerBtc.Controller.SignerController;

@Component
public class Runner implements CommandLineRunner {

  @Autowired
  SignerController signerController;
  
  public void run(String... arg0) throws Exception {
    Float fl = signerController.callApi();
    System.out.println("---------- " + fl + " -----------");
  }
}

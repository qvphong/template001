package coin.SignerBtc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App {

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}

	public static void main(String[] args) {
	  SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);
	  builder.headless(false).run(args);
//	  System.setProperty("java.awt.headless", "false");
	}
}

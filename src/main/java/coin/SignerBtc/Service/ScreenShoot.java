package coin.SignerBtc.Service;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ScreenShoot {

  private static WebDriver driver = null;

  public static void main(String[] args) {
    TestJavaS1();
  }

  public static void TestJavaS1() {
    System.setProperty("webdriver.chrome.driver", "c:/chromedriver.exe");
    if (driver == null) {
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("--headless");
      driver = new ChromeDriver(options);
      driver.manage().window().maximize();
    }
    driver.get("https://translate.google.com/");
    captureScreenShot(driver);
    // driver.close();
    System.out.println("*** Screenshot OK ");
  }

  public static void captureScreenShot(WebDriver ldriver) {
    File src = ((TakesScreenshot) ldriver).getScreenshotAs(OutputType.FILE);
    try {
      FileUtils.copyFile(src, new File("C:/selenium/" + System.currentTimeMillis() + ".png"));
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}

package coin.SignerBtc.Runner;

public abstract class Runner implements Runnable {
	
	public void run() {
        System.out.println("Hello from a thread!");
    }
}

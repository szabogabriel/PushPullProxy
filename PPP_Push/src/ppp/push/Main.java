package ppp.push;

import java.io.IOException;

public class Main {
	
	public static void main(String [] args) throws IOException {
		new PushServer(8080, 8081).start();
	}

}

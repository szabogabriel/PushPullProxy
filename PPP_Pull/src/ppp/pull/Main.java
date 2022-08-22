package ppp.pull;

import java.io.IOException;

import ppp.pull.data.Endpoint;

public class Main {
	
	public static void main(String [] args) throws IOException {
		new Thread(new PullServer(new Endpoint("localhost", 8081), new Endpoint("localhost", 65000))).start();
	}

}

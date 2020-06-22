package ppp.pull.data;

public class Endpoint {
	
	private final String HOST;
	private final int PORT;
	
	public Endpoint(String host, int port) {
		this.HOST = host;
		this.PORT = port;
	}
	
	public String getHost() {
		return HOST;
	}
	
	public int getPort() {
		return PORT;
	}

}

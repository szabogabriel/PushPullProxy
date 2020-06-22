package ppp.push.data;

import ppp.push.handlers.RequestHandler;

public class RequestHolder {
	
	private final RequestHandler HANDLER;
	private final byte[] DATA;
	
	public RequestHolder(RequestHandler handler, byte[] data) {
		HANDLER = handler;
		DATA = data;
	}

	public RequestHandler getHANDLER() {
		return HANDLER;
	}

	public byte[] getDATA() {
		return DATA;
	}
	
	
}

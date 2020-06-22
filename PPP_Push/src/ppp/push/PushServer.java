package ppp.push;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ppp.push.data.RequestHolder;
import ppp.push.handlers.ForwardHandler;
import ppp.push.handlers.RequestHandler;

public class PushServer {

	private final ServerSocket SS_request;
	private final ServerSocket SS_forward;
	
	private Queue<RequestHolder> requestQueue = new ConcurrentLinkedQueue<>();

	public PushServer(int portRequest, int portForward) throws IOException {
		SS_request = new ServerSocket(portRequest);
		SS_forward = new ServerSocket(portForward);
	}

	public void start() {
		new Thread(this::forwardHandler).start();
		new Thread(this::requestHandler).start();
	}
	
	private void requestHandler() {
		while (true) {
			try {
				Socket socket = SS_request.accept();
				new Thread(new RequestHandler(socket, requestQueue)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void forwardHandler() {
		while (true) {
			try {
				Socket socket = SS_forward.accept();
				new Thread(new ForwardHandler(socket, requestQueue)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

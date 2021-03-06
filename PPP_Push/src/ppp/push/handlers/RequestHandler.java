package ppp.push.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

import ppp.push.data.RequestHolder;
import ppp.push.util.StreamUtil;

public class RequestHandler implements Runnable {
	
	private InputStream IN;
	private OutputStream OUT;
	private Queue<RequestHolder> QUEUE;
	
	public RequestHandler(Socket socket, Queue<RequestHolder> queue) throws IOException {
		this.QUEUE = queue;
		
		IN = socket.getInputStream();
		OUT = socket.getOutputStream();
	}

	@Override
	public void run() {
		try {
			byte[] tmp = StreamUtil.readInputStream(IN);
			QUEUE.add(new RequestHolder(this, tmp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finished(byte[] response) {
		try {
			OUT.write(response);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				IN.close();
			} catch (Exception ex) {
			}
			try {
				OUT.close();
			} catch (Exception ex) {
			}
		}
	}

}

package ppp.push.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

import ppp.push.data.RequestHolder;
import ppp.push.util.StreamUtil;

public class ForwardHandler implements Runnable {

	private InputStream IN;
	private OutputStream OUT;
	private final Queue<RequestHolder> QUEUE;

	public ForwardHandler(Socket socket, Queue<RequestHolder> queue) throws IOException {
		this.IN = socket.getInputStream();
		this.OUT = socket.getOutputStream();
		this.QUEUE = queue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (!QUEUE.isEmpty()) {
					RequestHolder tmp = QUEUE.poll();
					handleRequestHolder(tmp);
				} else {
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void handleRequestHolder(RequestHolder holder) {
		try {
			System.out.println("[PushProxy - ForwardHandler] Found data in queue. Forwarding it.");
			OUT.write(holder.getDATA());
			byte[] tmp = StreamUtil.readInputStream(IN);
			System.out.println("[PushProxy - ForwardHandler] Received response.");
			holder.getHANDLER().finished(tmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

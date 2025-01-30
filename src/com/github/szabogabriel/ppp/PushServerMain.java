package com.github.szabogabriel.ppp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class PushServerMain extends AbstractServer {

	private static final int BUFFER_SIZE = 1024;

	private static final String ERROR_MESSAGE_NO_TARGET_PRESENT = "HTTP/1.1 504 OK\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Length: 36\r\nConnection: close\r\n\r\nError 504. No connection to gateway.";

	private BlockingQueue<Socket> forwardSocketList = new LinkedBlockingDeque<>();

	public static void main(String[] args) throws IOException {
		new PushServerMain(8080, 8081);
	}

	public PushServerMain(int requestPort, int forwardPort) {
		execute(() -> runRequestServerSocket(requestPort));
		execute(() -> runForwardServerSocket(forwardPort));
		execute(() -> runForwardSocketListCleanup());
	}

	private void runForwardSocketListCleanup() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<Socket> tmp = new ArrayList<>();
			for (Socket it : forwardSocketList) {
				if (it.isClosed()) {
					tmp.add(it);
				}
			}
			if (tmp.size() > 0) {
				for (Socket it : tmp) {
					forwardSocketList.remove(it);
				}
			}
		}
	}

	private void runForwardServerSocket(int port) {
		boolean finished = false;
		while (!finished) {
			try (ServerSocket forwardServerSocket = new ServerSocket(port)) {
				forwardSocketList.add(forwardServerSocket.accept());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void runRequestServerSocket(int port) {
		boolean finished = false;
		while (!finished) {
			try (ServerSocket requestServerSocket = new ServerSocket(port)) {
				Socket tmpSocket = requestServerSocket.accept();
				execute(() -> handleRequestSocket(tmpSocket));
			} catch (IOException e) {
				finished = true;
				e.printStackTrace();
			}
		}
	}

	private void handleRequestSocket(Socket s) {
		Optional<Socket> forwardSocket = getForwardSocket();
		
		try {
			InputStream in = s.getInputStream();
			OutputStream out = s.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			if (forwardSocket.isPresent()) {
				System.out.println("[PushServer] [" + System.currentTimeMillis() + "] Forwarding request");
				InputStream forwardIn = forwardSocket.get().getInputStream();
				OutputStream forwardOut = forwardSocket.get().getOutputStream();

				do {
					len = in.read(buffer);
					if (len > 0) {
						forwardOut.write(buffer, 0, len);
					}
				} while (in.available() > 0);

				do {
					len = forwardIn.read(buffer);
					if (len > 0) {
						out.write(buffer, 0, len);
					}
				} while (forwardIn.available() > 0);
				
				out.close();
				in.close();
			} else {
				while (in.available() > 0 && (len = in.read(buffer)) > 0) {
					System.out.println(">" + new String(buffer, 0, len));
				}

				out.write(ERROR_MESSAGE_NO_TARGET_PRESENT.getBytes());
				out.close();
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (s != null && !s.isClosed()) {
				try {
					s.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			if (forwardSocket.isPresent()) {
				returnSocket(forwardSocket.get());
			}
		}
	}
	
	private void returnSocket(Socket s) {
		forwardSocketList.add(s);
	}

	private Optional<Socket> getForwardSocket() {
		if (forwardSocketList.size() > 0) { 
			try {
				return Optional.of(forwardSocketList.poll(1000, TimeUnit.MILLISECONDS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return Optional.empty();
	}

}

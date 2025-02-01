package com.github.szabogabriel.ppp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class PullServerMain extends AbstractServer {

	private static final int BUFFER_SIZE = 1024;

	private String targetUrl;
	private int targetPort;

	public static void main(String[] args) {
		new PullServerMain("localhost", 8081, "localhost", 18080);
	}
	
	public PullServerMain(RuntimeArguments args) {
		this(args.getForwardHost(), args.getForwardPort(), args.getTargetHost(), args.getTargetPort());
	}

	public PullServerMain(String pushHost, int pushPort, String targetUrl, int targetPort) {
		this.targetUrl = targetUrl;
		this.targetPort = targetPort;

		execute(() -> createSocket(pushHost, pushPort));
	}

	private void createSocket(String pushHost, int pushPort) {
		while (true) {
			try (Socket s = new Socket(pushHost, pushPort)) {
				Socket targetSocket = null;
				InputStream targetInputStream = null;
				OutputStream targetOutputStream = null;
				
				while (s.isBound()) {
					InputStream in = s.getInputStream();
					OutputStream out = s.getOutputStream();

					try {
						byte[] buffer = new byte[BUFFER_SIZE];
						int len = 0;

						do {
							len = in.read(buffer);
							if (len > 0) {
								if (targetSocket == null) {
									System.out.println("[PullServer] [" + System.currentTimeMillis() + "] Forwarding request to " + targetUrl + ":" + targetPort);
									targetSocket = createTargetSocket();
									targetInputStream = targetSocket.getInputStream();
									targetOutputStream = targetSocket.getOutputStream();
								}
								targetOutputStream.write(buffer, 0, len);
							}
						} while (in.available() > 0);

						do {
							len = targetInputStream.read(buffer);
							if (len > 0) {
								out.write(buffer, 0, len);
							}
						} while (targetInputStream.available() > 0);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (targetSocket != null) {
								targetInputStream.close();
								targetOutputStream.close();
								targetSocket.close();
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						} finally {
							targetInputStream = null;
							targetOutputStream = null;
							targetSocket = null;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Socket createTargetSocket() throws UnknownHostException, IOException {
		return new Socket(targetUrl, targetPort);
	}

}

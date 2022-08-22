package ppp.pull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import ppp.pull.data.Endpoint;
import ppp.pull.util.StreamUtil;

public class PullServer implements Runnable {

	private InputStream pushServerIn;
	private OutputStream pushServerOut;
	private Endpoint forwardLocation;
	private InputStream forwardIn;
	private OutputStream forwardOut;

	public PullServer(Endpoint pushServer, Endpoint forwardLocation) throws IOException {
		Socket ps = new Socket(pushServer.getHost(), pushServer.getPort());
		pushServerIn = ps.getInputStream();
		pushServerOut = ps.getOutputStream();
		this.forwardLocation = forwardLocation;
	}

	@Override
	public void run() {
		byte[] data;
		byte[] resp;
		while (true) {
			try {
				data = StreamUtil.readInputStream(pushServerIn);
				
				Socket fl = new Socket(forwardLocation.getHost(), forwardLocation.getPort());
				forwardIn = fl.getInputStream();
				forwardOut = fl.getOutputStream();
				forwardOut.write(data);
				resp = StreamUtil.readInputStreamUntilDefinetlyFinished(forwardIn);
				try {
					forwardIn.close();
				} catch (Exception ex) 
				{
				}
				try {
					forwardOut.close();
				} catch (Exception ex) 
				{
				}
				
				pushServerOut.write(resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

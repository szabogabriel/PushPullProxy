package ppp.push.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class StreamUtil {
	
	private static final int BUFFER_SIZE = 4096;
	
	public static byte[] readInputStream(InputStream IN) throws IOException {
		byte[] BUFFER = new byte[BUFFER_SIZE];
		byte[] tmp = new byte[0];
		int origLength = 0;
		int read;
		do {
			read = IN.read(BUFFER);
			if (read >= 0) {
				tmp = Arrays.copyOf(tmp, origLength + read);
				System.arraycopy(BUFFER, 0, tmp, origLength, read);
				origLength += read;
			}
		} while (read >= 0 && IN.available() > 0);
		return tmp;
	}

}

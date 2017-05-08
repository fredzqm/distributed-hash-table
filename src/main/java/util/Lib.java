package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Lib {

	public static String readStr(InputStream input) throws IOException {
		int length = input.read();
		byte[] buffer = new byte[length];
		input.read(buffer);
		return new String(buffer);
	}

	public static void writeStr(OutputStream output, String str) throws IOException {
		byte[] bytes = str.getBytes();
		output.write(bytes.length);
		output.write(bytes);
	}

	public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
		long count = 0;
		final byte[] buffer = new byte[1024 * 4];
		int n;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void writeLong(long x, OutputStream output) throws IOException {
		output.write((int) x);
		output.write((int) (x >> 32));
	}

	public static long readLong(InputStream output) throws IOException {
		long x = output.read();
		long y = output.read();
		return x | (y << 32);
	}

}

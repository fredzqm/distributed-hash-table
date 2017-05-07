package dht_node;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LogInputStream extends FilterInputStream {

	public LogInputStream(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public int read() throws IOException {
		int x = super.read();
		System.out.println("In: "+x);
		return x;
	}
	
//	@Override
//	public int read(byte[] b, int off, int len) throws IOException {
//		return super.read(b, off, len);
//	}
}

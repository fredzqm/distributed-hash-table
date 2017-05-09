package dht_node;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LogOuputStream extends FilterOutputStream {

	public LogOuputStream(OutputStream outputStream) {
		super(outputStream);
	}

	@Override
	public void write(int b) throws IOException {
		System.out.println("out "+b);
		super.write(b);
	}

}

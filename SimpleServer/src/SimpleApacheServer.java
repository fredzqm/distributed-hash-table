import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.xml.ws.spi.http.HttpExchange;

import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.ssl.SSLContexts;

public class SimpleApacheServer {

	 public static void main(String[] args) throws Exception {

	        int port = 8080;
	        if (args.length >= 1) {
	            port = Integer.parseInt(args[0]);
	        }

	        SSLContext sslcontext = null;
//	        if (port == 8443) {
//	            // Initialize SSL context
//	            URL url = HttpServer.class.getResource("/my.keystore");
//	            if (url == null) {
//	                System.out.println("Keystore not found");
//	                System.exit(1);
//	            }
//	            sslcontext = SSLContexts.custom()
//	                    .loadKeyMaterial(url, "secret".toCharArray(), "secret".toCharArray())
//	                    .build();
//	        }

	        SocketConfig socketConfig = SocketConfig.custom()
	                .setSoTimeout(15000)
	                .setTcpNoDelay(true)
	                .build();

	        final HttpServer server = ServerBootstrap.bootstrap()
	                .setListenerPort(port)
	                .setServerInfo("Test/1.1")
	                .setSocketConfig(socketConfig)
	                .setSslContext(sslcontext)
	                .setExceptionLogger(new StdErrorExceptionLogger())
	                .registerHandler("*", new HttpHandler())
	                .create();

	        server.start();
	        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

	        Runtime.getRuntime().addShutdownHook(new Thread() {
	            @Override
	            public void run() {
	                server.shutdown(5, TimeUnit.SECONDS);
	            }
	        });
	    }
	    static class StdErrorExceptionLogger implements ExceptionLogger {

	        @Override
	        public void log(final Exception ex) {
	            if (ex instanceof SocketTimeoutException) {
	                System.err.println("Connection timed out");
	            } else if (ex instanceof ConnectionClosedException) {
	                System.err.println(ex.getMessage());
	            } else {
	                ex.printStackTrace();
	            }
	        }

	    }

	    static class HttpHandler implements HttpRequestHandler  {


	        public HttpHandler() {
	            super();
	        }

			@Override
			public void handle(HttpRequest arg0, HttpResponse arg1, HttpContext arg2)
					throws HttpException, IOException {
				// TODO Auto-generated method stub
				String response = "Hello";
				arg1.setHeader("h1", "hello");
	           // t.sendResponseHeaders(200, response.length());
	           // OutputStream os = arg1.getAllHeaders();
	           // os.write(response.getBytes());
	           // os.close();
			}
		
	    }

}

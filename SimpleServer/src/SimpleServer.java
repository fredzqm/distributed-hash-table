	import java.io.IOException;
	import java.io.OutputStream;
	import java.net.InetSocketAddress;

	import com.sun.net.httpserver.HttpExchange;
	import com.sun.net.httpserver.HttpHandler;
	import com.sun.net.httpserver.HttpServer;
	
	/*
	 * This is a simple server using the sun httpserver library that outputs to the web browser.
	 * Planning on moving to apache, as there is much more control.
	 */

	public class SimpleServer {

	    public static void main(String[] args) throws Exception {
	        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
	        server.createContext("/test", new MyHandler());
	        server.createContext("/python", new PythonHandler());

	        server.start();
	    }

	    static class MyHandler implements HttpHandler {
	        @Override
	        public void handle(HttpExchange t) throws IOException {
	            String response = "This is the response";
	            t.sendResponseHeaders(200, response.length());
	            OutputStream os = t.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
	        }
	    }
	    
	    //TODO: implement running python scripts
	    static class PythonHandler implements HttpHandler {
	    	@Override
	    	public void handle(HttpExchange t) throws IOException {
	    		String response = "Python Script Response";
	            t.sendResponseHeaders(200, response.length());
	            OutputStream os = t.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
	    	}
	    	
	    }

}

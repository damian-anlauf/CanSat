package groundStation;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender extends Thread {
	private static final String IP = "10.2.1.1";
	private int port;
	private String message;
	
	public Sender(int port, String message) {
		this.port = port;
		this.message = message;
	}
	
	@Override
	public void run() {
		try (Socket socket = new Socket(IP, port)) {
			OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			out.write(message);
			out.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
}

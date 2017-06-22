package groundStation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Listener extends Thread {
	public boolean running = false; 
	private int port;
	private Main main;
	private int packetsStart = -1;
	private int packetsRecieved, packetsLost, packetsLast, packetsTotal;
	private double errorPerc, timeDiff, timeLast, testDuration;
	private long timeStart;
	private Date date = new Date();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	private static final File DIR = new File("logs");
	private File file = new File(DIR+File.separator+dateFormat.format(date)+".log");
	private BufferedWriter bw = null;

	
	
	
	public Listener(Main main, int port) {
		this.port = port;
		this.main = main;
		running = true;
		DIR.mkdir();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		listen();
	}
	
	private void listen() {
		 try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] dataBytes = new byte[4096];
            while(running) {
            	DatagramPacket packet = new DatagramPacket(dataBytes, dataBytes.length);
            	socket.receive(packet);
            	byte[] bytes = packet.getData();
            	String dataString = "";
				for(int i = 0; i < bytes.length; i++) {
					if (bytes[i] != 0) {
						dataString += (char) bytes[i];
					}
				}
            	dataString += System.lineSeparator() + additionalData(dataString) + System.lineSeparator();
            	dataString = "Received data: " + dataString;
            	main.tARawData.append(dataString);
            	main.sound.play();
            	writeToFile(dataString);
            }
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getPacketNo(String data) {
		int i = data.indexOf(',');
		int packetNo = Integer.parseInt(data.substring(0, i));
		return packetNo;
	}
	
	private void calcStability(int packetNo) {
		packetsRecieved += 1;
		long timeRecieved = System.currentTimeMillis();
		if (packetsStart == -1) {
			packetsStart = packetNo;
			timeStart = System.currentTimeMillis();
		} else {
			int packetsDiff = packetNo - packetsLast;
			if (packetsDiff != 1) {
				int lost = packetsDiff - 1;
				packetsLost += lost;
			}
			timeDiff = timeRecieved - timeLast;
		}
		packetsTotal = packetNo - packetsStart + 1;
		errorPerc = (1-((double)packetsRecieved / (double)packetsTotal)) * 100;
		errorPerc = Double.parseDouble(new BigDecimal(String.valueOf(errorPerc)).setScale(2, BigDecimal.ROUND_HALF_UP)+"");
		packetsLast = packetNo;
		testDuration = (double)(timeRecieved - timeStart) / 1000;
		testDuration = Double.parseDouble(new BigDecimal(String.valueOf(testDuration)).setScale(3, BigDecimal.ROUND_HALF_UP)+"");
		timeLast = timeRecieved;
	}
	
	private String additionalData(String data) {
		calcStability(getPacketNo(data));
		String out = "Error rate: "+errorPerc+"% Total recieved packets: "+packetsRecieved+" Packets lost: "+packetsLost+" Time difference: "+timeDiff+" Test duration: "+testDuration;
		return out;
	}

//	private static long symmetricRound(double d) {
//	    return d < 0 ? - Math.round( -d ) : Math.round( d );
//	}
	
	private void writeToFile(String string) {
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath(), true),Charset.forName("UTF-8").newEncoder()));
			
			bw.write(string);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
}

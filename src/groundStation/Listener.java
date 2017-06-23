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
	private int packetNo, packetsRecieved, packetsLost, packetsLast, packetsTotal;
	private double errorPerc, timeDiff, timeLast, testDuration;
	private long timeStart;
	private Date date = new Date();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	private static final File DIR = new File("logs");
	private File file = new File(DIR+File.separator+dateFormat.format(date)+".log");
	private BufferedWriter bw = null;
	private String temp, pressure, altitude, pitch, roll, yaw, longtitude, latitude; 
	private static final int PACKETNO = 0;
	private static final int TEMP = 1;
	private static final int PRESSURE = 2;
	private static final int ALTITUDE = 3;
	private static final int PITCH = 4;
	private static final int ROLL = 5;
	private static final int YAW = 6;
	private static final int LONGTITUDE = 7;
	private static final int LATITUDE = 8;
	private static final int LASTDATA = LATITUDE;

	
	
	
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
            byte[] dataBytes = new byte[128];
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
				debug(dataString);
				extractData(dataString);
				updateCharts();
            	dataString += System.lineSeparator() + additionalData(dataString) + System.lineSeparator();
            	dataString = "Received data: " + dataString;
            	main.tARawData.append(dataString);
            	updateData();
            	main.sound.play();
            	writeToFile(dataString);
            }
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void extractData(String raw) {
		int state = 0; 
		int i;
		while (state <= LASTDATA | raw.indexOf(',') != -1) {
			switch(state) {
			case PACKETNO:
				i = raw.indexOf(',');
				packetNo = Integer.parseInt(raw.substring(0, i));
//				debug(raw);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case TEMP:
				i = raw.indexOf(',');
				temp = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case PRESSURE:
				i = raw.indexOf(',');
				pressure = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case ALTITUDE:
				i = raw.indexOf(',');
				altitude = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case PITCH:
				i = raw.indexOf(',');
				pitch = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case ROLL:
				i = raw.indexOf(',');
				roll = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case YAW:
				i = raw.indexOf(',');
				yaw = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case LONGTITUDE:
				i = raw.indexOf(',');
				longtitude = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
				break;
			case LATITUDE:
				i = raw.indexOf(',');
				latitude = raw.substring(0, i);
				raw = raw.substring(i+1, raw.length());
				debug(raw);
//				debug(i+"");
				state++;
			}
		}
	}
	
	private void calcStability() {
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
		calcStability();
		String out = "Error rate: "+errorPerc+"% Total recieved packets: "+packetsRecieved+" Packets lost: "+packetsLost+" Time difference: "+timeDiff+" Test duration: "+testDuration;
		return out;
	}
	
	private void updateData() {
		main.tFErrorRate.setText(errorPerc+"%");
		main.tFTotalPackets.setText(packetsRecieved+"");
		main.tFLostPackets.setText(packetsLost+"");
		main.tFTimeDiff.setText(timeDiff+"ms");
		main.tFTestDuration.setText(testDuration+"s");
		main.tFTemp.setText(temp+"°C");
		main.tFPressure.setText(pressure+"hPa");
		main.tFAltitude.setText(altitude+"m");
		main.tFPitch.setText(pitch+"°");
		main.tFRoll.setText(roll+"°");
		main.tFYaw.setText(yaw+"°");
		main.tFLongtitude.setText(longtitude);
		main.tFLatitude.setText(latitude);
	}
	
	private void updateCharts() {
		if (temp != null) {
			main.dSTemp.addValue(Double.parseDouble(temp), "°C", testDuration+"");
		} 
		if (pressure != null) {
			main.dSPressure.addValue(Double.parseDouble(pressure), "hPa", testDuration+"");
		}
		if (altitude != null) {
			main.dSAltitude.addValue(Double.parseDouble(altitude), "m", testDuration+"");
		}
	}
	
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
	
	private void debug(String string) {
		System.out.println(string);
	}
	
}

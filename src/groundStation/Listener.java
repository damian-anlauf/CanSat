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
	private int packetNo, status, packetsReceived, packetsLost, packetsLast, packetsTotal;
	private double errorPerc, timeDiff, timeLast, testDuration;
	private long timeStart;
	private Date date = new Date();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	private static final File DIR = new File("logs");
	private File file = new File(DIR+File.separator+dateFormat.format(date)+".log");
	private BufferedWriter bw = null;
	private String time, temp, pressure, altitude, pitch, roll, yaw, gpsTime, longtitude, latitude; 
	private static final int PACKETNO = 0;
	private static final int TIME = 1;
	private static final int TEMP = 2;
	private static final int PRESSURE = 3;
	private static final int ALTITUDE = 4;
	private static final int PITCH = 5;
	private static final int ROLL = 6;
	private static final int YAW = 7;
	private static final int GPSTIME = 8;
	private static final int LATITUDE = 9;
	private static final int LONGTITUDE = 10;
	private static final int STATUS = 11;
	
	private static final int PREFLIGHT = 0;
	private static final int FLIGHT = 1;
	private static final int POSTFLIGHT = 2;

	
	
	
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
				for (int i = 0; i < bytes.length; i++) {
					if (bytes[i] != 0) {
						dataString += (char) bytes[i];
					}
				}
//				System.out.println(dataString);
				extractData(dataString);
				dataString = packetNo+","+time+","+temp+","+pressure+","+altitude+","+pitch+","+roll+","+yaw+","+gpsTime+","+latitude+","+longtitude+","+status;
//				System.out.println(dataString);
            	dataString += System.lineSeparator() + additionalData(dataString) + System.lineSeparator();
            	dataString = "Received data: " + dataString;
            	
            	main.tARawData.append(dataString);
            	updateData();
            	updateCharts();
            	updateMap();
            	
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
		String[] data = raw.split(",");
		
		for (int i = 0; i < data.length; i++) {
			switch (i) {
			case PACKETNO:
				packetNo = Integer.parseInt(data[i].trim());
				break;
			case TIME:
				time = data[i].trim();
				break;
			case TEMP:
				temp = data[i].trim();
				break;
			case PRESSURE:
				pressure = data[i].trim();
				break;
			case ALTITUDE:
				altitude = data[i].trim();
				break;
			case PITCH:
				pitch = data[i].trim();
				break;
			case ROLL:
				roll = data[i].trim();
				break;
			case YAW:
				yaw = data[i].trim();
				break;
			case GPSTIME:
				gpsTime = data[i].trim();
				break;
			case LATITUDE:
				latitude = data[i].trim();
				break;
			case LONGTITUDE:
				longtitude = data[i].trim();
				break;
			case STATUS:
				status = Integer.parseInt(data[i].trim().charAt(0)+"");
			}
		}
	}
	
	private void calcStability() {
		packetsReceived += 1;
		long timeReceived = System.currentTimeMillis();
		if (packetsStart == -1) {
			packetsStart = packetNo;
			timeStart = System.currentTimeMillis();
		} else {
			int packetsDiff = packetNo - packetsLast;
			if (packetsDiff != 1) {
				int lost = packetsDiff - 1;
				packetsLost += lost;
			}
			timeDiff = timeReceived - timeLast;
		}
		packetsTotal = packetNo - packetsStart + 1;
		errorPerc = (1-((double)packetsReceived / (double)packetsTotal)) * 100;
		errorPerc = Double.parseDouble(new BigDecimal(String.valueOf(errorPerc)).setScale(2, BigDecimal.ROUND_HALF_UP)+"");
		packetsLast = packetNo;
		testDuration = (double)(timeReceived - timeStart) / 1000;
		testDuration = Double.parseDouble(new BigDecimal(String.valueOf(testDuration)).setScale(3, BigDecimal.ROUND_HALF_UP)+"");
		timeLast = timeReceived;
	}
	
	private String additionalData(String data) {
		calcStability();
		String out = "Error rate: "+errorPerc+"% Total received packets: "+packetsReceived+" Packets lost: "+packetsLost+" Time difference: "+timeDiff+" Test duration: "+testDuration;
		return out;
	}
	
	private void updateData() {
		main.tFErrorRate.setText(errorPerc+"%");
		main.tFTotalPackets.setText(packetsReceived+"");
		main.tFLostPackets.setText(packetsLost+"");
		main.tFTimeDiff.setText(timeDiff+"ms");
		main.tFTestDuration.setText(testDuration+"s");
		main.tFTime.setText(time+"s");
		main.tFTemp.setText(temp+"°C");
		main.tFPressure.setText(pressure+"hPa");
		main.tFAltitude.setText(altitude+"m");
		main.tFPitch.setText(pitch+"°");
		main.tFRoll.setText(roll+"°");
		main.tFYaw.setText(yaw+"°");
		main.tFGpsTime.setText(gpsTime.substring(0, gpsTime.indexOf("."))+" GMT");
		main.tFLatitude.setText(latitude+"N");
		main.tFLongtitude.setText(longtitude+"E");
		updateFlightStatus();
	}
	
	private void updateFlightStatus() {
		switch (status) {
		case PREFLIGHT:
			main.tFStatus.setText("Preflight");
			break;
		case FLIGHT:
			main.tFStatus.setText("Flight");
			break;
		case POSTFLIGHT:
			main.tFStatus.setText("Postflight");
		}
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
	
	private void updateMap() {
		double dLatitude = Double.parseDouble(latitude);
		double dLongtitude = Double.parseDouble(longtitude);
		if (dLatitude != 0 && dLongtitude != 0) {
			main.updateMarkerCanSat(dLatitude, dLongtitude);
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
	
}

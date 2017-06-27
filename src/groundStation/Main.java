package groundStation;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextField tFPort;
	private JButton btnConnect, btnEnforceFlight;
	private int port;
	private Listener listener;
	protected JTextArea tARawData;
	private JCheckBox chckbxEnableEnforceFlightButton;
	protected AudioClip sound = Applet.newAudioClip(getClass().getResource("beep.wav"));
	private JFreeChart tempChart, pressureChart, altitudeChart;
	protected DefaultCategoryDataset dSTemp = new DefaultCategoryDataset();
	protected DefaultCategoryDataset dSPressure = new DefaultCategoryDataset();
	protected DefaultCategoryDataset dSAltitude = new DefaultCategoryDataset();
	protected JTextField tFErrorRate, tFTotalPackets, tFLostPackets, tFTimeDiff, tFTestDuration, tFTime, tFTemp, tFPressure, tFAltitude, tFPitch, tFRoll, tFYaw, tFGpsTime, tFLatitude, tFLongtitude, tFGpsAltitude, tFStatus;
	private JPanel mapsPanel;
	private JFXPanel jfxPanel;
	private GoogleMapView mapComponent;
    private GoogleMap map;
    private Marker markerCanSat;
    private Marker markerBase;
    private Scene scene;
    private static final String C = "Connect";
    private static final String D = "Disconnect";
    private static final double BASELATITUDE = 53.131567;
    private static final double BASELONGTITUDE = 9.353921;
    

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("Team Recognize - Ground Station");
		createGUI();
		initMaps();
	}
	
	private void createGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		tempChart = ChartFactory.createLineChart("Temperature", "Seconds", "Temperature (°C)", dSTemp, PlotOrientation.VERTICAL, false, true, false);
		pressureChart = ChartFactory.createLineChart("Pressure", "Seconds", "Pressure (hPa)", dSPressure, PlotOrientation.VERTICAL, false, true, false);
		altitudeChart = ChartFactory.createLineChart("Altitude", "Seconds", "Altitude (m)", dSAltitude, PlotOrientation.VERTICAL, false, true, false);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbc_tabbedPane);
		
		JPanel analyticsPanel = new JPanel();
		tabbedPane.addTab("Analytics", null, analyticsPanel, null);
		GridBagLayout gbl_analyticsPanel = new GridBagLayout();
		gbl_analyticsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_analyticsPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_analyticsPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_analyticsPanel.rowWeights = new double[]{0.0, 0.0, 2.0, 2.0, 2.0, 1.0, Double.MIN_VALUE};
		analyticsPanel.setLayout(gbl_analyticsPanel);
		
		JPanel topPanel1 = new JPanel();
		GridBagConstraints gbc_topPanel1 = new GridBagConstraints();
		gbc_topPanel1.gridwidth = 2;
		gbc_topPanel1.insets = new Insets(0, 0, 5, 0);
		gbc_topPanel1.anchor = GridBagConstraints.NORTH;
		gbc_topPanel1.fill = GridBagConstraints.HORIZONTAL;
		gbc_topPanel1.gridx = 0;
		gbc_topPanel1.gridy = 0;
		analyticsPanel.add(topPanel1, gbc_topPanel1);
		GridBagLayout gbl_topPanel1 = new GridBagLayout();
		gbl_topPanel1.columnWidths = new int[]{0, 0, 0, 0};
		gbl_topPanel1.rowHeights = new int[]{0, 0};
		gbl_topPanel1.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_topPanel1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		topPanel1.setLayout(gbl_topPanel1);
		
		JLabel lblPort = new JLabel("Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 0, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 0;
		topPanel1.add(lblPort, gbc_lblPort);
		
		tFPort = new JTextField();
		tFPort.setText("44444");
		GridBagConstraints gbc_tFPort = new GridBagConstraints();
		gbc_tFPort.insets = new Insets(0, 0, 0, 5);
		gbc_tFPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_tFPort.gridx = 1;
		gbc_tFPort.gridy = 0;
		topPanel1.add(tFPort, gbc_tFPort);
		tFPort.setColumns(10);
		
		btnConnect = new JButton(C);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.gridx = 2;
		gbc_btnConnect.gridy = 0;
		topPanel1.add(btnConnect, gbc_btnConnect);
		
		JPanel topPanel2 = new JPanel();
		GridBagConstraints gbc_topPanel2 = new GridBagConstraints();
		gbc_topPanel2.anchor = GridBagConstraints.NORTH;
		gbc_topPanel2.gridwidth = 2;
		gbc_topPanel2.insets = new Insets(0, 0, 5, 5);
		gbc_topPanel2.fill = GridBagConstraints.HORIZONTAL;
		gbc_topPanel2.gridx = 0;
		gbc_topPanel2.gridy = 1;
		analyticsPanel.add(topPanel2, gbc_topPanel2);
		topPanel2.setLayout(new BoxLayout(topPanel2, BoxLayout.X_AXIS));
		
		JLabel lblErrorRate = new JLabel("Error rate:");
		topPanel2.add(lblErrorRate);
		
		tFErrorRate = new JTextField();
		topPanel2.add(tFErrorRate);
		tFErrorRate.setHorizontalAlignment(SwingConstants.LEFT);
		tFErrorRate.setEditable(false);
		tFErrorRate.setColumns(10);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		topPanel2.add(horizontalStrut);
		
		JLabel lblTotalReceivedPackets = new JLabel("Total received packets:");
		topPanel2.add(lblTotalReceivedPackets);
		
		tFTotalPackets = new JTextField();
		topPanel2.add(tFTotalPackets);
		tFTotalPackets.setHorizontalAlignment(SwingConstants.LEFT);
		tFTotalPackets.setEditable(false);
		tFTotalPackets.setColumns(10);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		topPanel2.add(horizontalStrut_1);
		
		JLabel lblPacketsLost = new JLabel("Packets lost:");
		topPanel2.add(lblPacketsLost);
		
		tFLostPackets = new JTextField();
		topPanel2.add(tFLostPackets);
		tFLostPackets.setHorizontalAlignment(SwingConstants.LEFT);
		tFLostPackets.setEditable(false);
		tFLostPackets.setColumns(10);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		topPanel2.add(horizontalStrut_2);
		
		JLabel lblTimeDifference = new JLabel("Time difference:");
		topPanel2.add(lblTimeDifference);
		
		tFTimeDiff = new JTextField();
		topPanel2.add(tFTimeDiff);
		tFTimeDiff.setHorizontalAlignment(SwingConstants.LEFT);
		tFTimeDiff.setEditable(false);
		tFTimeDiff.setColumns(10);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		topPanel2.add(horizontalStrut_3);
		
		JLabel lblTestDuration = new JLabel("Test duration:");
		topPanel2.add(lblTestDuration);
		
		tFTestDuration = new JTextField();
		topPanel2.add(tFTestDuration);
		tFTestDuration.setHorizontalAlignment(SwingConstants.LEFT);
		tFTestDuration.setEditable(false);
		tFTestDuration.setColumns(10);
		
		ChartPanel cPTemp = new ChartPanel(tempChart);
		GridBagConstraints gbc_cPTemp = new GridBagConstraints();
		gbc_cPTemp.insets = new Insets(0, 0, 5, 5);
		gbc_cPTemp.fill = GridBagConstraints.BOTH;
		gbc_cPTemp.gridx = 0;
		gbc_cPTemp.gridy = 2;
		analyticsPanel.add(cPTemp, gbc_cPTemp);
		
		JPanel dataPanel = new JPanel();
		GridBagConstraints gbc_dataPanel = new GridBagConstraints();
		gbc_dataPanel.gridheight = 4;
		gbc_dataPanel.fill = GridBagConstraints.BOTH;
		gbc_dataPanel.gridx = 1;
		gbc_dataPanel.gridy = 2;
		analyticsPanel.add(dataPanel, gbc_dataPanel);
		GridBagLayout gbl_dataPanel = new GridBagLayout();
		gbl_dataPanel.columnWidths = new int[]{74, 0};
		gbl_dataPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_dataPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_dataPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		dataPanel.setLayout(gbl_dataPanel);
		
		JLabel lblTime = new JLabel("Time:");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.anchor = GridBagConstraints.WEST;
		gbc_lblTime.insets = new Insets(0, 0, 5, 0);
		gbc_lblTime.gridx = 0;
		gbc_lblTime.gridy = 0;
		dataPanel.add(lblTime, gbc_lblTime);
		
		tFTime = new JTextField();
		tFTime.setHorizontalAlignment(SwingConstants.LEFT);
		tFTime.setEditable(false);
		GridBagConstraints gbc_tFTime = new GridBagConstraints();
		gbc_tFTime.insets = new Insets(0, 0, 5, 0);
		gbc_tFTime.fill = GridBagConstraints.BOTH;
		gbc_tFTime.gridx = 0;
		gbc_tFTime.gridy = 1;
		dataPanel.add(tFTime, gbc_tFTime);
		tFTime.setColumns(10);
		
		JLabel lblTemperature = new JLabel("Temperature:");
		GridBagConstraints gbc_lblTemperature = new GridBagConstraints();
		gbc_lblTemperature.anchor = GridBagConstraints.WEST;
		gbc_lblTemperature.insets = new Insets(0, 0, 5, 0);
		gbc_lblTemperature.gridx = 0;
		gbc_lblTemperature.gridy = 2;
		dataPanel.add(lblTemperature, gbc_lblTemperature);
		
		tFTemp = new JTextField();
		tFTemp.setHorizontalAlignment(SwingConstants.LEFT);
		tFTemp.setEditable(false);
		GridBagConstraints gbc_tFTemp = new GridBagConstraints();
		gbc_tFTemp.insets = new Insets(0, 0, 5, 0);
		gbc_tFTemp.fill = GridBagConstraints.BOTH;
		gbc_tFTemp.gridx = 0;
		gbc_tFTemp.gridy = 3;
		dataPanel.add(tFTemp, gbc_tFTemp);
		tFTemp.setColumns(10);
		
		JLabel lblPressure = new JLabel("Pressure:");
		GridBagConstraints gbc_lblPressure = new GridBagConstraints();
		gbc_lblPressure.insets = new Insets(0, 0, 5, 0);
		gbc_lblPressure.anchor = GridBagConstraints.WEST;
		gbc_lblPressure.gridx = 0;
		gbc_lblPressure.gridy = 4;
		dataPanel.add(lblPressure, gbc_lblPressure);
		
		tFPressure = new JTextField();
		tFPressure.setHorizontalAlignment(SwingConstants.LEFT);
		tFPressure.setEditable(false);
		GridBagConstraints gbc_tFPressure = new GridBagConstraints();
		gbc_tFPressure.insets = new Insets(0, 0, 5, 0);
		gbc_tFPressure.fill = GridBagConstraints.BOTH;
		gbc_tFPressure.gridx = 0;
		gbc_tFPressure.gridy = 5;
		dataPanel.add(tFPressure, gbc_tFPressure);
		tFPressure.setColumns(10);
		
		JLabel lblAltitude = new JLabel("Altitude:");
		GridBagConstraints gbc_lblAltitude = new GridBagConstraints();
		gbc_lblAltitude.insets = new Insets(0, 0, 5, 0);
		gbc_lblAltitude.anchor = GridBagConstraints.WEST;
		gbc_lblAltitude.gridx = 0;
		gbc_lblAltitude.gridy = 6;
		dataPanel.add(lblAltitude, gbc_lblAltitude);
		
		tFAltitude = new JTextField();
		tFAltitude.setHorizontalAlignment(SwingConstants.LEFT);
		tFAltitude.setEditable(false);
		GridBagConstraints gbc_tFAltitude = new GridBagConstraints();
		gbc_tFAltitude.insets = new Insets(0, 0, 5, 0);
		gbc_tFAltitude.fill = GridBagConstraints.BOTH;
		gbc_tFAltitude.gridx = 0;
		gbc_tFAltitude.gridy = 7;
		dataPanel.add(tFAltitude, gbc_tFAltitude);
		tFAltitude.setColumns(10);
		
		JLabel lblOrientationPitch = new JLabel("Orientation Pitch:");
		GridBagConstraints gbc_lblOrientationPitch = new GridBagConstraints();
		gbc_lblOrientationPitch.insets = new Insets(0, 0, 5, 0);
		gbc_lblOrientationPitch.anchor = GridBagConstraints.WEST;
		gbc_lblOrientationPitch.gridx = 0;
		gbc_lblOrientationPitch.gridy = 8;
		dataPanel.add(lblOrientationPitch, gbc_lblOrientationPitch);
		
		tFPitch = new JTextField();
		tFPitch.setHorizontalAlignment(SwingConstants.LEFT);
		tFPitch.setEditable(false);
		GridBagConstraints gbc_tFPitch = new GridBagConstraints();
		gbc_tFPitch.insets = new Insets(0, 0, 5, 0);
		gbc_tFPitch.fill = GridBagConstraints.BOTH;
		gbc_tFPitch.gridx = 0;
		gbc_tFPitch.gridy = 9;
		dataPanel.add(tFPitch, gbc_tFPitch);
		tFPitch.setColumns(10);
		
		JLabel lblOrientationRoll = new JLabel("Orientation Roll:");
		GridBagConstraints gbc_lblOrientationRoll = new GridBagConstraints();
		gbc_lblOrientationRoll.insets = new Insets(0, 0, 5, 0);
		gbc_lblOrientationRoll.anchor = GridBagConstraints.WEST;
		gbc_lblOrientationRoll.gridx = 0;
		gbc_lblOrientationRoll.gridy = 10;
		dataPanel.add(lblOrientationRoll, gbc_lblOrientationRoll);
		
		tFRoll = new JTextField();
		tFRoll.setHorizontalAlignment(SwingConstants.LEFT);
		tFRoll.setEditable(false);
		GridBagConstraints gbc_tFRoll = new GridBagConstraints();
		gbc_tFRoll.insets = new Insets(0, 0, 5, 0);
		gbc_tFRoll.fill = GridBagConstraints.BOTH;
		gbc_tFRoll.gridx = 0;
		gbc_tFRoll.gridy = 11;
		dataPanel.add(tFRoll, gbc_tFRoll);
		tFRoll.setColumns(10);
		
		JLabel lblOrientationYaw = new JLabel("Orientation Yaw:");
		GridBagConstraints gbc_lblOrientationYaw = new GridBagConstraints();
		gbc_lblOrientationYaw.insets = new Insets(0, 0, 5, 0);
		gbc_lblOrientationYaw.anchor = GridBagConstraints.WEST;
		gbc_lblOrientationYaw.gridx = 0;
		gbc_lblOrientationYaw.gridy = 12;
		dataPanel.add(lblOrientationYaw, gbc_lblOrientationYaw);
		
		tFYaw = new JTextField();
		tFYaw.setHorizontalAlignment(SwingConstants.LEFT);
		tFYaw.setEditable(false);
		GridBagConstraints gbc_tFYaw = new GridBagConstraints();
		gbc_tFYaw.insets = new Insets(0, 0, 5, 0);
		gbc_tFYaw.fill = GridBagConstraints.BOTH;
		gbc_tFYaw.gridx = 0;
		gbc_tFYaw.gridy = 13;
		dataPanel.add(tFYaw, gbc_tFYaw);
		tFYaw.setColumns(10);
		
		JLabel lblGpsTime = new JLabel("GPS Time:");
		GridBagConstraints gbc_lblGpsTime = new GridBagConstraints();
		gbc_lblGpsTime.anchor = GridBagConstraints.WEST;
		gbc_lblGpsTime.insets = new Insets(0, 0, 5, 0);
		gbc_lblGpsTime.gridx = 0;
		gbc_lblGpsTime.gridy = 14;
		dataPanel.add(lblGpsTime, gbc_lblGpsTime);
		
		tFGpsTime = new JTextField();
		tFGpsTime.setHorizontalAlignment(SwingConstants.LEFT);
		tFGpsTime.setEditable(false);
		GridBagConstraints gbc_tFGpsTime = new GridBagConstraints();
		gbc_tFGpsTime.insets = new Insets(0, 0, 5, 0);
		gbc_tFGpsTime.fill = GridBagConstraints.BOTH;
		gbc_tFGpsTime.gridx = 0;
		gbc_tFGpsTime.gridy = 15;
		dataPanel.add(tFGpsTime, gbc_tFGpsTime);
		tFGpsTime.setColumns(10);
		
		JLabel lblGpsLatitude = new JLabel("GPS Latitude:");
		GridBagConstraints gbc_lblGpsLatitude = new GridBagConstraints();
		gbc_lblGpsLatitude.insets = new Insets(0, 0, 5, 0);
		gbc_lblGpsLatitude.anchor = GridBagConstraints.WEST;
		gbc_lblGpsLatitude.gridx = 0;
		gbc_lblGpsLatitude.gridy = 16;
		dataPanel.add(lblGpsLatitude, gbc_lblGpsLatitude);
		
		tFLatitude = new JTextField();
		tFLatitude.setHorizontalAlignment(SwingConstants.LEFT);
		tFLatitude.setEditable(false);
		GridBagConstraints gbc_tFLatitude = new GridBagConstraints();
		gbc_tFLatitude.insets = new Insets(0, 0, 5, 0);
		gbc_tFLatitude.fill = GridBagConstraints.BOTH;
		gbc_tFLatitude.gridx = 0;
		gbc_tFLatitude.gridy = 17;
		dataPanel.add(tFLatitude, gbc_tFLatitude);
		tFLatitude.setColumns(10);
		
		JLabel lblGpsLongtitude = new JLabel("GPS Longtitude:");
		GridBagConstraints gbc_lblGpsLongtitude = new GridBagConstraints();
		gbc_lblGpsLongtitude.insets = new Insets(0, 0, 5, 0);
		gbc_lblGpsLongtitude.anchor = GridBagConstraints.WEST;
		gbc_lblGpsLongtitude.gridx = 0;
		gbc_lblGpsLongtitude.gridy = 18;
		dataPanel.add(lblGpsLongtitude, gbc_lblGpsLongtitude);
		
		tFLongtitude = new JTextField();
		tFLongtitude.setHorizontalAlignment(SwingConstants.LEFT);
		tFLongtitude.setEditable(false);
		GridBagConstraints gbc_tFLongtitude = new GridBagConstraints();
		gbc_tFLongtitude.insets = new Insets(0, 0, 5, 0);
		gbc_tFLongtitude.fill = GridBagConstraints.BOTH;
		gbc_tFLongtitude.gridx = 0;
		gbc_tFLongtitude.gridy = 19;
		dataPanel.add(tFLongtitude, gbc_tFLongtitude);
		tFLongtitude.setColumns(10);
		
		JLabel lblGpsAltitude = new JLabel("GPS Altitude:");
		GridBagConstraints gbc_lblGpsAltitude = new GridBagConstraints();
		gbc_lblGpsAltitude.anchor = GridBagConstraints.WEST;
		gbc_lblGpsAltitude.insets = new Insets(0, 0, 5, 0);
		gbc_lblGpsAltitude.gridx = 0;
		gbc_lblGpsAltitude.gridy = 20;
		dataPanel.add(lblGpsAltitude, gbc_lblGpsAltitude);
		
		tFGpsAltitude = new JTextField();
		tFGpsAltitude.setHorizontalAlignment(SwingConstants.LEFT);
		tFGpsAltitude.setEditable(false);
		GridBagConstraints gbc_tFGpsAltitude = new GridBagConstraints();
		gbc_tFGpsAltitude.insets = new Insets(0, 0, 5, 0);
		gbc_tFGpsAltitude.fill = GridBagConstraints.BOTH;
		gbc_tFGpsAltitude.gridx = 0;
		gbc_tFGpsAltitude.gridy = 21;
		dataPanel.add(tFGpsAltitude, gbc_tFGpsAltitude);
		tFGpsAltitude.setColumns(10);
		
		JLabel lblFlightStatus = new JLabel("Flight Status:");
		GridBagConstraints gbc_lblFlightStatus = new GridBagConstraints();
		gbc_lblFlightStatus.insets = new Insets(0, 0, 5, 0);
		gbc_lblFlightStatus.anchor = GridBagConstraints.WEST;
		gbc_lblFlightStatus.gridx = 0;
		gbc_lblFlightStatus.gridy = 22;
		dataPanel.add(lblFlightStatus, gbc_lblFlightStatus);
		
		tFStatus = new JTextField();
		tFStatus.setEditable(false);
		tFStatus.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_tFStatus = new GridBagConstraints();
		gbc_tFStatus.insets = new Insets(0, 0, 5, 0);
		gbc_tFStatus.fill = GridBagConstraints.BOTH;
		gbc_tFStatus.gridx = 0;
		gbc_tFStatus.gridy = 23;
		dataPanel.add(tFStatus, gbc_tFStatus);
		tFStatus.setColumns(10);
		
		btnEnforceFlight = new JButton("Enforce Flight");
		btnEnforceFlight.setEnabled(false);
		btnEnforceFlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enforceFlightMode();
			}
		});
		
		chckbxEnableEnforceFlightButton = new JCheckBox("Enable Button");
		chckbxEnableEnforceFlightButton.setEnabled(false);
		chckbxEnableEnforceFlightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableEFButton();
			}
		});
		GridBagConstraints gbc_chckbxEnableEnforceFlightButton = new GridBagConstraints();
		gbc_chckbxEnableEnforceFlightButton.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxEnableEnforceFlightButton.gridx = 0;
		gbc_chckbxEnableEnforceFlightButton.gridy = 25;
		dataPanel.add(chckbxEnableEnforceFlightButton, gbc_chckbxEnableEnforceFlightButton);
		GridBagConstraints gbc_btnEnforceFlight = new GridBagConstraints();
		gbc_btnEnforceFlight.gridx = 0;
		gbc_btnEnforceFlight.gridy = 26;
		dataPanel.add(btnEnforceFlight, gbc_btnEnforceFlight);
		
		ChartPanel cPPressure = new ChartPanel(pressureChart);
		GridBagConstraints gbc_cPPressure = new GridBagConstraints();
		gbc_cPPressure.fill = GridBagConstraints.BOTH;
		gbc_cPPressure.insets = new Insets(0, 0, 5, 5);
		gbc_cPPressure.gridx = 0;
		gbc_cPPressure.gridy = 3;
		analyticsPanel.add(cPPressure, gbc_cPPressure);
		
		ChartPanel cPAltitude = new ChartPanel(altitudeChart);
		GridBagConstraints gbc_cPAltitude = new GridBagConstraints();
		gbc_cPAltitude.fill = GridBagConstraints.BOTH;
		gbc_cPAltitude.insets = new Insets(0, 0, 5, 5);
		gbc_cPAltitude.gridx = 0;
		gbc_cPAltitude.gridy = 4;
		analyticsPanel.add(cPAltitude, gbc_cPAltitude);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 5;
		analyticsPanel.add(scrollPane, gbc_scrollPane);
		new SmartScroller(scrollPane);
		
		tARawData = new JTextArea();
		scrollPane.setViewportView(tARawData);
		tARawData.setEditable(false);
		
		mapsPanel = new JPanel();
		tabbedPane.addTab("Google Maps", null, mapsPanel, null);
		GridBagLayout gbl_mapsPanel = new GridBagLayout();
		gbl_mapsPanel.columnWidths = new int[]{0, 0};
		gbl_mapsPanel.rowHeights = new int[]{0, 0};
		gbl_mapsPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_mapsPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		mapsPanel.setLayout(gbl_mapsPanel);
		
	}
	
	private void initMaps() {
		jfxPanel = new JFXPanel();
		GridBagConstraints gbc_jfxPanel = new GridBagConstraints();
		gbc_jfxPanel.fill = GridBagConstraints.BOTH;
		gbc_jfxPanel.gridx = 0;
		gbc_jfxPanel.gridy = 0;
		mapsPanel.add(jfxPanel, gbc_jfxPanel);
		
		Platform.runLater(() -> {
            mapComponent = new GoogleMapView();
            mapComponent.addMapInializedListener(() -> {

                LatLong center = new LatLong(BASELATITUDE, BASELONGTITUDE);

                MapOptions options = new MapOptions()
                        .center(center)
                        .mapMarker(true)
                        .zoom(15)
                        .overviewMapControl(false)
                        .panControl(false)
                        .rotateControl(false)
                        .scaleControl(false)
                        .streetViewControl(false)
                        .zoomControl(false)
                        .mapType(MapTypeIdEnum.ROADMAP);

                map = mapComponent.createMap(options);
                mapsModifier();
            });

            mapComponent.setPrefSize(600, 600);
            scene = new Scene(mapComponent);

            jfxPanel.setScene(scene);
        });
	}
	
	private void clearData() {
		tARawData.setText("");
		dSTemp.clear();
		dSAltitude.clear();
		dSPressure.clear();
		tFErrorRate.setText("");
		tFTotalPackets.setText("");
		tFLostPackets.setText("");
		tFTimeDiff.setText("");
		tFTestDuration.setText("");
		tFTime.setText("");
		tFTemp.setText("");
		tFPressure.setText("");
		tFAltitude.setText("");
		tFPitch.setText("");
		tFRoll.setText("");
		tFYaw.setText("");
		tFGpsTime.setText("");
		tFLatitude.setText("");
		tFLongtitude.setText("");
		tFStatus.setText("");
	}
	
	private void connect() {
		try {
			if (btnConnect.getText().equals(C)) {
				clearData();
				port = Integer.parseInt(tFPort.getText().trim());
				listener = new Listener(this, port);
				listener.start();
				tFPort.setEditable(false);
				chckbxEnableEnforceFlightButton.setEnabled(true);
				btnConnect.setText(D);
			}
			else if (btnConnect.getText().equals(D)) {
				listener.running  = false;
				tFPort.setEditable(true);
				btnEnforceFlight.setEnabled(false);
				chckbxEnableEnforceFlightButton.setSelected(false);
				chckbxEnableEnforceFlightButton.setEnabled(false);
				btnConnect.setText(C);
			}
		} catch (NumberFormatException e) {
			String message = "Port contains invalid characters."+System.lineSeparator()+"Please try again.";
			JOptionPane.showMessageDialog(this, message);
		}
	}
	
	private void enforceFlightMode() {
		String message = "1";
		Sender sender = new Sender(port, message);
		sender.start();
	}
	
	private void enableEFButton() {
		btnEnforceFlight.setEnabled(chckbxEnableEnforceFlightButton.isSelected());
	}
	
	private void mapsModifier() {
		createMarkerBase();
	}
	
	private void createMarkerBase() {
		MarkerOptions markerOptions = new MarkerOptions();
		
		markerOptions.position(new LatLong(BASELATITUDE, BASELONGTITUDE))
        .visible(Boolean.TRUE)
        .title("Base");

		markerBase = new Marker(markerOptions);
		map.addMarker(markerBase);
	}
	
	protected void updateMarkerCanSat(double latitude, double longtitude) {
		Platform.runLater(() -> { 
			try {
				markerCanSat.toString();
				markerCanSat.setPosition(new LatLong(latitude, longtitude));
			} catch (NullPointerException e) {
				MarkerOptions markerOptions = new MarkerOptions();
				
				markerOptions.position(new LatLong(latitude, longtitude))
		        .visible(Boolean.TRUE)
		        .title("CanSat");

				markerCanSat = new Marker(markerOptions);
				map.addMarker(markerCanSat);
			}
        });
	}
	
}

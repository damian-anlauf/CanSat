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

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextField tFPort;
	private JButton btnConnect;
	private int port;
	private Listener listener;
	protected JTextArea tARawData;
	protected AudioClip sound = Applet.newAudioClip(getClass().getResource("beep.wav"));
	private JFreeChart tempChart, pressureChart, altitudeChart;
	protected DefaultCategoryDataset dSTemp = new DefaultCategoryDataset();
	protected DefaultCategoryDataset dSPressure = new DefaultCategoryDataset();
	protected DefaultCategoryDataset dSAltitude = new DefaultCategoryDataset();
	protected JTextField tFErrorRate, tFTotalPackets, tFLostPackets, tFTimeDiff, tFTestDuration, tFTemp, tFPressure, tFAltitude, tFPitch, tFRoll, tFYaw, tFLongtitude, tFLatitude;
	private JFXPanel jfxPanel;
	private GoogleMapView mapComponent;
    private GoogleMap map;
    private Scene scene;
    protected MarkerOptions markerOptions;
    

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
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 107, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		tempChart = ChartFactory.createLineChart("Temperature", "Seconds", "Temperature (°C)", dSTemp, PlotOrientation.VERTICAL, false, true, false);
		pressureChart = ChartFactory.createLineChart("Pressure", "Seconds", "Pressure (hPa)", dSPressure, PlotOrientation.VERTICAL, false, true, false);
		altitudeChart = ChartFactory.createLineChart("Altitude", "Seconds", "Altitude (m)", dSAltitude, PlotOrientation.VERTICAL, false, true, false);
		
		JPanel topRow1 = new JPanel();
		GridBagConstraints gbc_topRow1 = new GridBagConstraints();
		gbc_topRow1.gridwidth = 3;
		gbc_topRow1.insets = new Insets(0, 0, 5, 0);
		gbc_topRow1.fill = GridBagConstraints.BOTH;
		gbc_topRow1.gridx = 0;
		gbc_topRow1.gridy = 0;
		contentPane.add(topRow1, gbc_topRow1);
		GridBagLayout gbl_topRow1 = new GridBagLayout();
		gbl_topRow1.columnWidths = new int[]{0, 0, 0, 0};
		gbl_topRow1.rowHeights = new int[]{0, 0};
		gbl_topRow1.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_topRow1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		topRow1.setLayout(gbl_topRow1);
		
		JLabel lblPort = new JLabel("Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 0, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 0;
		topRow1.add(lblPort, gbc_lblPort);
		
		tFPort = new JTextField();
		tFPort.setText("44444");
		GridBagConstraints gbc_tFPort = new GridBagConstraints();
		gbc_tFPort.insets = new Insets(0, 0, 0, 5);
		gbc_tFPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_tFPort.gridx = 1;
		gbc_tFPort.gridy = 0;
		topRow1.add(tFPort, gbc_tFPort);
		tFPort.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.gridx = 2;
		gbc_btnConnect.gridy = 0;
		topRow1.add(btnConnect, gbc_btnConnect);
		
		ChartPanel cPTemp = new ChartPanel(tempChart);
		GridBagConstraints gbc_cPTemp = new GridBagConstraints();
		gbc_cPTemp.gridwidth = 2;
		gbc_cPTemp.fill = GridBagConstraints.BOTH;
		gbc_cPTemp.insets = new Insets(0, 0, 5, 5);
		gbc_cPTemp.gridx = 0;
		gbc_cPTemp.gridy = 1;
		contentPane.add(cPTemp, gbc_cPTemp);
		
		JPanel topRow2 = new JPanel();
		GridBagConstraints gbc_topRow2 = new GridBagConstraints();
		gbc_topRow2.gridheight = 4;
		gbc_topRow2.fill = GridBagConstraints.BOTH;
		gbc_topRow2.gridx = 2;
		gbc_topRow2.gridy = 1;
		contentPane.add(topRow2, gbc_topRow2);
		GridBagLayout gbl_topRow2 = new GridBagLayout();
		gbl_topRow2.columnWidths = new int[]{74, 0};
		gbl_topRow2.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_topRow2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_topRow2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		topRow2.setLayout(gbl_topRow2);
		
		JLabel lblErrorRate = new JLabel("Error rate:");
		GridBagConstraints gbc_lblErrorRate = new GridBagConstraints();
		gbc_lblErrorRate.anchor = GridBagConstraints.WEST;
		gbc_lblErrorRate.insets = new Insets(0, 0, 5, 0);
		gbc_lblErrorRate.gridx = 0;
		gbc_lblErrorRate.gridy = 0;
		topRow2.add(lblErrorRate, gbc_lblErrorRate);
		
		tFErrorRate = new JTextField();
		tFErrorRate.setHorizontalAlignment(SwingConstants.LEFT);
		tFErrorRate.setEditable(false);
		GridBagConstraints gbc_tFErrorRate = new GridBagConstraints();
		gbc_tFErrorRate.fill = GridBagConstraints.BOTH;
		gbc_tFErrorRate.insets = new Insets(0, 0, 5, 0);
		gbc_tFErrorRate.gridx = 0;
		gbc_tFErrorRate.gridy = 1;
		topRow2.add(tFErrorRate, gbc_tFErrorRate);
		tFErrorRate.setColumns(10);
		
		JLabel lblTotalRecievedPackets = new JLabel("Total received packets:");
		GridBagConstraints gbc_lblTotalRecievedPackets = new GridBagConstraints();
		gbc_lblTotalRecievedPackets.anchor = GridBagConstraints.WEST;
		gbc_lblTotalRecievedPackets.insets = new Insets(0, 0, 5, 0);
		gbc_lblTotalRecievedPackets.gridx = 0;
		gbc_lblTotalRecievedPackets.gridy = 2;
		topRow2.add(lblTotalRecievedPackets, gbc_lblTotalRecievedPackets);
		
		tFTotalPackets = new JTextField();
		tFTotalPackets.setHorizontalAlignment(SwingConstants.LEFT);
		tFTotalPackets.setEditable(false);
		GridBagConstraints gbc_tFTotalPackets = new GridBagConstraints();
		gbc_tFTotalPackets.fill = GridBagConstraints.BOTH;
		gbc_tFTotalPackets.insets = new Insets(0, 0, 5, 0);
		gbc_tFTotalPackets.gridx = 0;
		gbc_tFTotalPackets.gridy = 3;
		topRow2.add(tFTotalPackets, gbc_tFTotalPackets);
		tFTotalPackets.setColumns(10);
		
		JLabel lblPacketsLost = new JLabel("Packets lost:");
		GridBagConstraints gbc_lblPacketsLost = new GridBagConstraints();
		gbc_lblPacketsLost.anchor = GridBagConstraints.WEST;
		gbc_lblPacketsLost.insets = new Insets(0, 0, 5, 0);
		gbc_lblPacketsLost.gridx = 0;
		gbc_lblPacketsLost.gridy = 4;
		topRow2.add(lblPacketsLost, gbc_lblPacketsLost);
		
		tFLostPackets = new JTextField();
		tFLostPackets.setHorizontalAlignment(SwingConstants.LEFT);
		tFLostPackets.setEditable(false);
		GridBagConstraints gbc_tFLostPackets = new GridBagConstraints();
		gbc_tFLostPackets.fill = GridBagConstraints.BOTH;
		gbc_tFLostPackets.insets = new Insets(0, 0, 5, 0);
		gbc_tFLostPackets.gridx = 0;
		gbc_tFLostPackets.gridy = 5;
		topRow2.add(tFLostPackets, gbc_tFLostPackets);
		tFLostPackets.setColumns(10);
		
		JLabel lblTimeDifference = new JLabel("Time difference:");
		GridBagConstraints gbc_lblTimeDifference = new GridBagConstraints();
		gbc_lblTimeDifference.anchor = GridBagConstraints.WEST;
		gbc_lblTimeDifference.insets = new Insets(0, 0, 5, 0);
		gbc_lblTimeDifference.gridx = 0;
		gbc_lblTimeDifference.gridy = 6;
		topRow2.add(lblTimeDifference, gbc_lblTimeDifference);
		
		tFTimeDiff = new JTextField();
		tFTimeDiff.setHorizontalAlignment(SwingConstants.LEFT);
		tFTimeDiff.setEditable(false);
		GridBagConstraints gbc_tFTimeDiff = new GridBagConstraints();
		gbc_tFTimeDiff.fill = GridBagConstraints.BOTH;
		gbc_tFTimeDiff.insets = new Insets(0, 0, 5, 0);
		gbc_tFTimeDiff.gridx = 0;
		gbc_tFTimeDiff.gridy = 7;
		topRow2.add(tFTimeDiff, gbc_tFTimeDiff);
		tFTimeDiff.setColumns(10);
		
		JLabel lblTestDuration = new JLabel("Test duration:");
		GridBagConstraints gbc_lblTestDuration = new GridBagConstraints();
		gbc_lblTestDuration.anchor = GridBagConstraints.WEST;
		gbc_lblTestDuration.insets = new Insets(0, 0, 5, 0);
		gbc_lblTestDuration.gridx = 0;
		gbc_lblTestDuration.gridy = 8;
		topRow2.add(lblTestDuration, gbc_lblTestDuration);
		
		tFTestDuration = new JTextField();
		tFTestDuration.setHorizontalAlignment(SwingConstants.LEFT);
		tFTestDuration.setEditable(false);
		GridBagConstraints gbc_tFTestDuration = new GridBagConstraints();
		gbc_tFTestDuration.insets = new Insets(0, 0, 5, 0);
		gbc_tFTestDuration.fill = GridBagConstraints.BOTH;
		gbc_tFTestDuration.gridx = 0;
		gbc_tFTestDuration.gridy = 9;
		topRow2.add(tFTestDuration, gbc_tFTestDuration);
		tFTestDuration.setColumns(10);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 10;
		topRow2.add(verticalStrut, gbc_verticalStrut);
		
		JLabel lblTemperature = new JLabel("Temperature:");
		GridBagConstraints gbc_lblTemperature = new GridBagConstraints();
		gbc_lblTemperature.anchor = GridBagConstraints.WEST;
		gbc_lblTemperature.insets = new Insets(0, 0, 5, 0);
		gbc_lblTemperature.gridx = 0;
		gbc_lblTemperature.gridy = 11;
		topRow2.add(lblTemperature, gbc_lblTemperature);
		
		tFTemp = new JTextField();
		tFTemp.setHorizontalAlignment(SwingConstants.LEFT);
		tFTemp.setEditable(false);
		GridBagConstraints gbc_tFTemp = new GridBagConstraints();
		gbc_tFTemp.insets = new Insets(0, 0, 5, 0);
		gbc_tFTemp.fill = GridBagConstraints.BOTH;
		gbc_tFTemp.gridx = 0;
		gbc_tFTemp.gridy = 12;
		topRow2.add(tFTemp, gbc_tFTemp);
		tFTemp.setColumns(10);
		
		JLabel lblPressure = new JLabel("Pressure:");
		GridBagConstraints gbc_lblPressure = new GridBagConstraints();
		gbc_lblPressure.insets = new Insets(0, 0, 5, 0);
		gbc_lblPressure.anchor = GridBagConstraints.WEST;
		gbc_lblPressure.gridx = 0;
		gbc_lblPressure.gridy = 13;
		topRow2.add(lblPressure, gbc_lblPressure);
		
		tFPressure = new JTextField();
		tFPressure.setHorizontalAlignment(SwingConstants.LEFT);
		tFPressure.setEditable(false);
		GridBagConstraints gbc_tFPressure = new GridBagConstraints();
		gbc_tFPressure.insets = new Insets(0, 0, 5, 0);
		gbc_tFPressure.fill = GridBagConstraints.BOTH;
		gbc_tFPressure.gridx = 0;
		gbc_tFPressure.gridy = 14;
		topRow2.add(tFPressure, gbc_tFPressure);
		tFPressure.setColumns(10);
		
		JLabel lblAltitude = new JLabel("Altitude:");
		GridBagConstraints gbc_lblAltitude = new GridBagConstraints();
		gbc_lblAltitude.insets = new Insets(0, 0, 5, 0);
		gbc_lblAltitude.anchor = GridBagConstraints.WEST;
		gbc_lblAltitude.gridx = 0;
		gbc_lblAltitude.gridy = 15;
		topRow2.add(lblAltitude, gbc_lblAltitude);
		
		tFAltitude = new JTextField();
		tFAltitude.setHorizontalAlignment(SwingConstants.LEFT);
		tFAltitude.setEditable(false);
		GridBagConstraints gbc_tFAltitude = new GridBagConstraints();
		gbc_tFAltitude.insets = new Insets(0, 0, 5, 0);
		gbc_tFAltitude.fill = GridBagConstraints.BOTH;
		gbc_tFAltitude.gridx = 0;
		gbc_tFAltitude.gridy = 16;
		topRow2.add(tFAltitude, gbc_tFAltitude);
		tFAltitude.setColumns(10);
		
		JLabel lblOrientationPitch = new JLabel("Orientation Pitch:");
		GridBagConstraints gbc_lblOrientationPitch = new GridBagConstraints();
		gbc_lblOrientationPitch.insets = new Insets(0, 0, 5, 0);
		gbc_lblOrientationPitch.anchor = GridBagConstraints.WEST;
		gbc_lblOrientationPitch.gridx = 0;
		gbc_lblOrientationPitch.gridy = 17;
		topRow2.add(lblOrientationPitch, gbc_lblOrientationPitch);
		
		tFPitch = new JTextField();
		tFPitch.setHorizontalAlignment(SwingConstants.LEFT);
		tFPitch.setEditable(false);
		GridBagConstraints gbc_tFPitch = new GridBagConstraints();
		gbc_tFPitch.insets = new Insets(0, 0, 5, 0);
		gbc_tFPitch.fill = GridBagConstraints.BOTH;
		gbc_tFPitch.gridx = 0;
		gbc_tFPitch.gridy = 18;
		topRow2.add(tFPitch, gbc_tFPitch);
		tFPitch.setColumns(10);
		
		JLabel lblOrientationRoll = new JLabel("Orientation Roll:");
		GridBagConstraints gbc_lblOrientationRoll = new GridBagConstraints();
		gbc_lblOrientationRoll.insets = new Insets(0, 0, 5, 0);
		gbc_lblOrientationRoll.anchor = GridBagConstraints.WEST;
		gbc_lblOrientationRoll.gridx = 0;
		gbc_lblOrientationRoll.gridy = 19;
		topRow2.add(lblOrientationRoll, gbc_lblOrientationRoll);
		
		tFRoll = new JTextField();
		tFRoll.setHorizontalAlignment(SwingConstants.LEFT);
		tFRoll.setEditable(false);
		GridBagConstraints gbc_tFRoll = new GridBagConstraints();
		gbc_tFRoll.insets = new Insets(0, 0, 5, 0);
		gbc_tFRoll.fill = GridBagConstraints.BOTH;
		gbc_tFRoll.gridx = 0;
		gbc_tFRoll.gridy = 20;
		topRow2.add(tFRoll, gbc_tFRoll);
		tFRoll.setColumns(10);
		
		JLabel lblOrientationYaw = new JLabel("Orientation Yaw:");
		GridBagConstraints gbc_lblOrientationYaw = new GridBagConstraints();
		gbc_lblOrientationYaw.insets = new Insets(0, 0, 5, 0);
		gbc_lblOrientationYaw.anchor = GridBagConstraints.WEST;
		gbc_lblOrientationYaw.gridx = 0;
		gbc_lblOrientationYaw.gridy = 21;
		topRow2.add(lblOrientationYaw, gbc_lblOrientationYaw);
		
		tFYaw = new JTextField();
		tFYaw.setHorizontalAlignment(SwingConstants.LEFT);
		tFYaw.setEditable(false);
		GridBagConstraints gbc_tFYaw = new GridBagConstraints();
		gbc_tFYaw.insets = new Insets(0, 0, 5, 0);
		gbc_tFYaw.fill = GridBagConstraints.BOTH;
		gbc_tFYaw.gridx = 0;
		gbc_tFYaw.gridy = 22;
		topRow2.add(tFYaw, gbc_tFYaw);
		tFYaw.setColumns(10);
		
		JLabel lblGpsLongtitude = new JLabel("GPS Longtitude:");
		GridBagConstraints gbc_lblGpsLongtitude = new GridBagConstraints();
		gbc_lblGpsLongtitude.insets = new Insets(0, 0, 5, 0);
		gbc_lblGpsLongtitude.anchor = GridBagConstraints.WEST;
		gbc_lblGpsLongtitude.gridx = 0;
		gbc_lblGpsLongtitude.gridy = 23;
		topRow2.add(lblGpsLongtitude, gbc_lblGpsLongtitude);
		
		tFLongtitude = new JTextField();
		tFLongtitude.setHorizontalAlignment(SwingConstants.LEFT);
		tFLongtitude.setEditable(false);
		GridBagConstraints gbc_tFLongtitude = new GridBagConstraints();
		gbc_tFLongtitude.insets = new Insets(0, 0, 5, 0);
		gbc_tFLongtitude.fill = GridBagConstraints.BOTH;
		gbc_tFLongtitude.gridx = 0;
		gbc_tFLongtitude.gridy = 24;
		topRow2.add(tFLongtitude, gbc_tFLongtitude);
		tFLongtitude.setColumns(10);
		
		JLabel lblGpsLatitude = new JLabel("GPS Latitude:");
		GridBagConstraints gbc_lblGpsLatitude = new GridBagConstraints();
		gbc_lblGpsLatitude.insets = new Insets(0, 0, 5, 0);
		gbc_lblGpsLatitude.anchor = GridBagConstraints.WEST;
		gbc_lblGpsLatitude.gridx = 0;
		gbc_lblGpsLatitude.gridy = 25;
		topRow2.add(lblGpsLatitude, gbc_lblGpsLatitude);
		
		tFLatitude = new JTextField();
		tFLatitude.setHorizontalAlignment(SwingConstants.LEFT);
		tFLatitude.setEditable(false);
		GridBagConstraints gbc_tFLatitude = new GridBagConstraints();
		gbc_tFLatitude.fill = GridBagConstraints.BOTH;
		gbc_tFLatitude.gridx = 0;
		gbc_tFLatitude.gridy = 26;
		topRow2.add(tFLatitude, gbc_tFLatitude);
		tFLatitude.setColumns(10);
		
		ChartPanel cPPressure = new ChartPanel(pressureChart);
		GridBagConstraints gbc_cPPressure = new GridBagConstraints();
		gbc_cPPressure.gridwidth = 2;
		gbc_cPPressure.fill = GridBagConstraints.BOTH;
		gbc_cPPressure.insets = new Insets(0, 0, 5, 5);
		gbc_cPPressure.gridx = 0;
		gbc_cPPressure.gridy = 2;
		contentPane.add(cPPressure, gbc_cPPressure);
		
		ChartPanel cPAltitude = new ChartPanel(altitudeChart);
		GridBagConstraints gbc_cPAltitude = new GridBagConstraints();
		gbc_cPAltitude.gridwidth = 2;
		gbc_cPAltitude.fill = GridBagConstraints.BOTH;
		gbc_cPAltitude.insets = new Insets(0, 0, 5, 5);
		gbc_cPAltitude.gridx = 0;
		gbc_cPAltitude.gridy = 3;
		contentPane.add(cPAltitude, gbc_cPAltitude);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		contentPane.add(scrollPane, gbc_scrollPane);
		new SmartScroller(scrollPane);
		
		tARawData = new JTextArea();
		scrollPane.setViewportView(tARawData);
		tARawData.setEditable(false);
	}
	
	private void initMaps() {
		jfxPanel = new JFXPanel();
		GridBagConstraints gbc_jfxPanel = new GridBagConstraints();
		gbc_jfxPanel.insets = new Insets(0, 0, 0, 5);
		gbc_jfxPanel.fill = GridBagConstraints.BOTH;
		gbc_jfxPanel.gridx = 1;
		gbc_jfxPanel.gridy = 4;
		contentPane.add(jfxPanel, gbc_jfxPanel);
		
		Platform.runLater(() -> {
            mapComponent = new GoogleMapView();
            mapComponent.addMapInializedListener(() -> {

                LatLong center = new LatLong(53.131632, 9.353838);

                MapOptions options = new MapOptions()
                        .center(center)
                        .mapMarker(true)
                        .zoom(13)
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
		tFTemp.setText("");
		tFPressure.setText("");
		tFAltitude.setText("");
		tFPitch.setText("");
		tFRoll.setText("");
		tFYaw.setText("");
		tFLongtitude.setText("");
		tFLatitude.setText("");
	}
	
	private void connect() {
		String c = "Connect";
		String d = "Disconnect";
		try {
			if (btnConnect.getText().equals(c)) {
				clearData();
				port = Integer.parseInt(tFPort.getText().trim());
				listener = new Listener(this, port);
				listener.start();
				btnConnect.setText(d);
				tFPort.setEditable(false);
			}
			else if (btnConnect.getText().equals(d)) {
				listener.running  = false;
				btnConnect.setText(c);
				tFPort.setEditable(true);
			}
		} catch (NumberFormatException e) {
			String message = "Port contains invalid characters."+System.lineSeparator()+"Please try again.";
			JOptionPane.showMessageDialog(this, message);
		}
	}
	
	private void mapsModifier() {
//		addMarker(53.131632, 9.353838, "Home");
	}
	
	private void addMarker(double latitude, double longtitude, String name) {
		markerOptions = new MarkerOptions();
		
		markerOptions.position(new LatLong(latitude, longtitude))
        .visible(Boolean.TRUE)
        .title("name");

		Marker marker = new Marker(markerOptions);

		map.addMarker(marker);
	}
	
}

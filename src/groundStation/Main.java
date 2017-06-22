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
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;

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
	protected JTextField tFErrorRate, tFTotalPackets, tFLostPackets, tFTimeDiff, tFTestDuration;

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
	}
	
	private void createGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		tempChart = ChartFactory.createLineChart("Temperature", "Seconds", "Temperature (°C)", dSTemp, PlotOrientation.VERTICAL, false, true, false);
		pressureChart = ChartFactory.createLineChart("Pressure", "Seconds", "Pressure (hPa)", dSPressure, PlotOrientation.VERTICAL, false, true, false);
		altitudeChart = ChartFactory.createLineChart("Altitude", "Seconds", "Altitude (m)", dSAltitude, PlotOrientation.VERTICAL, false, true, false);
		
		JPanel topRow1 = new JPanel();
		GridBagConstraints gbc_topRow1 = new GridBagConstraints();
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
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		contentPane.add(tabbedPane, gbc_tabbedPane);
		
		JPanel panelTab1 = new JPanel();
		tabbedPane.addTab("Analytics", null, panelTab1, null);
		GridBagLayout gbl_panelTab1 = new GridBagLayout();
		gbl_panelTab1.columnWidths = new int[]{0, 0};
		gbl_panelTab1.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panelTab1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelTab1.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		panelTab1.setLayout(gbl_panelTab1);
		
		JPanel topRow2 = new JPanel();
		GridBagConstraints gbc_topRow2 = new GridBagConstraints();
		gbc_topRow2.fill = GridBagConstraints.HORIZONTAL;
		gbc_topRow2.insets = new Insets(0, 0, 5, 0);
		gbc_topRow2.gridx = 0;
		gbc_topRow2.gridy = 0;
		panelTab1.add(topRow2, gbc_topRow2);
		topRow2.setLayout(new BoxLayout(topRow2, BoxLayout.X_AXIS));
		
		JLabel lblErrorRate = new JLabel("Error rate:");
		topRow2.add(lblErrorRate);
		
		tFErrorRate = new JTextField();
		tFErrorRate.setHorizontalAlignment(SwingConstants.LEFT);
		tFErrorRate.setEditable(false);
		topRow2.add(tFErrorRate);
		tFErrorRate.setColumns(10);
		
		JLabel lblTotalRecievedPackets = new JLabel("Total received packets:");
		topRow2.add(lblTotalRecievedPackets);
		
		tFTotalPackets = new JTextField();
		tFTotalPackets.setHorizontalAlignment(SwingConstants.LEFT);
		tFTotalPackets.setEditable(false);
		topRow2.add(tFTotalPackets);
		tFTotalPackets.setColumns(10);
		
		JLabel lblPacketsLost = new JLabel("Packets lost:");
		topRow2.add(lblPacketsLost);
		
		tFLostPackets = new JTextField();
		tFLostPackets.setHorizontalAlignment(SwingConstants.LEFT);
		tFLostPackets.setEditable(false);
		topRow2.add(tFLostPackets);
		tFLostPackets.setColumns(10);
		
		JLabel lblTimeDifference = new JLabel("Time difference:");
		topRow2.add(lblTimeDifference);
		
		tFTimeDiff = new JTextField();
		tFTimeDiff.setHorizontalAlignment(SwingConstants.LEFT);
		tFTimeDiff.setEditable(false);
		topRow2.add(tFTimeDiff);
		tFTimeDiff.setColumns(10);
		
		JLabel lblTestDuration = new JLabel("Test duration:");
		topRow2.add(lblTestDuration);
		
		tFTestDuration = new JTextField();
		tFTestDuration.setHorizontalAlignment(SwingConstants.LEFT);
		tFTestDuration.setEditable(false);
		topRow2.add(tFTestDuration);
		tFTestDuration.setColumns(10);
		
		ChartPanel cPTemp = new ChartPanel(tempChart);
		GridBagConstraints gbc_cPTemp = new GridBagConstraints();
		gbc_cPTemp.insets = new Insets(0, 0, 5, 0);
		gbc_cPTemp.fill = GridBagConstraints.BOTH;
		gbc_cPTemp.gridx = 0;
		gbc_cPTemp.gridy = 1;
		panelTab1.add(cPTemp, gbc_cPTemp);
		
		ChartPanel cPPressure = new ChartPanel(pressureChart);
		GridBagConstraints gbc_cPPressure = new GridBagConstraints();
		gbc_cPPressure.insets = new Insets(0, 0, 5, 0);
		gbc_cPPressure.fill = GridBagConstraints.BOTH;
		gbc_cPPressure.gridx = 0;
		gbc_cPPressure.gridy = 2;
		panelTab1.add(cPPressure, gbc_cPPressure);
		
		ChartPanel cPAltitude = new ChartPanel(altitudeChart);
		GridBagConstraints gbc_cPAltitude = new GridBagConstraints();
		gbc_cPAltitude.fill = GridBagConstraints.BOTH;
		gbc_cPAltitude.gridx = 0;
		gbc_cPAltitude.gridy = 3;
		panelTab1.add(cPAltitude, gbc_cPAltitude);
		
		JPanel panelTab2 = new JPanel();
		tabbedPane.addTab("Raw Data", null, panelTab2, null);
		GridBagLayout gbl_panelTab2 = new GridBagLayout();
		gbl_panelTab2.columnWidths = new int[]{0, 0};
		gbl_panelTab2.rowHeights = new int[]{0, 0};
		gbl_panelTab2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelTab2.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelTab2.setLayout(gbl_panelTab2);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panelTab2.add(scrollPane, gbc_scrollPane);
		
		tARawData = new JTextArea();
		scrollPane.setViewportView(tARawData);
		new SmartScroller(scrollPane);
		tARawData.setEditable(false);
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
	}

}

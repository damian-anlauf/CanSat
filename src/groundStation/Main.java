package groundStation;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextField tFPort;
	private JButton btnConnect;
	private int port;
	private Listener listener;
	protected JTextArea tARawData;
	protected AudioClip sound = Applet.newAudioClip(getClass().getResource("beep.wav"));

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
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblPort = new JLabel("Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 0, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 0;
		panel.add(lblPort, gbc_lblPort);
		
		tFPort = new JTextField();
		tFPort.setText("44444");
		GridBagConstraints gbc_tFPort = new GridBagConstraints();
		gbc_tFPort.insets = new Insets(0, 0, 0, 5);
		gbc_tFPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_tFPort.gridx = 1;
		gbc_tFPort.gridy = 0;
		panel.add(tFPort, gbc_tFPort);
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
		panel.add(btnConnect, gbc_btnConnect);
		
		JLabel lblRawData = new JLabel("Raw Data:");
		GridBagConstraints gbc_lblRawData = new GridBagConstraints();
		gbc_lblRawData.insets = new Insets(0, 0, 5, 0);
		gbc_lblRawData.gridx = 0;
		gbc_lblRawData.gridy = 1;
		contentPane.add(lblRawData, gbc_lblRawData);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		tARawData = new JTextArea();
		tARawData.setEditable(false);
		scrollPane.setViewportView(tARawData);
		new SmartScroller(scrollPane);
	}
	
	private void connect() {
		String c = "Connect";
		String d = "Disconnect";
		try {
			if (btnConnect.getText().equals(c)) {
				tARawData.setText("");
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

}

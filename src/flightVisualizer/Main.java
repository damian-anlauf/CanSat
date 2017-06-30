package flightVisualizer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Main extends JFrame {

	private JPanel contentPane;
	private JPanel mapsPanel;
	private JFXPanel jfxPanel;
	private GoogleMapView mapComponent;
    private GoogleMap map;
    private Marker markerCanSat;
    private Marker markerBase;
    private Scene scene;
    private static final double BASELATITUDE = 53.131567;
    private static final double BASELONGTITUDE = 9.353921;
    private static final String FILENAME = "data.log";

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
		setTitle("Team Recognize - Flight Visualizer");
		createGUI();
		initMaps();
	}
	
	private void createGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
				mapsPanel = new JPanel();
				GridBagConstraints gbc_mapsPanel = new GridBagConstraints();
				gbc_mapsPanel.fill = GridBagConstraints.BOTH;
				gbc_mapsPanel.gridx = 0;
				gbc_mapsPanel.gridy = 0;
				contentPane.add(mapsPanel, gbc_mapsPanel);
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
                        .mapType(MapTypeIdEnum.SATELLITE);

                map = mapComponent.createMap(options);
                mapsModifier();
            });

            mapComponent.setPrefSize(600, 600);
            scene = new Scene(mapComponent);

            jfxPanel.setScene(scene);
        });
	}
	
	private void mapsModifier() {
		createMarkerBase();
		fileToMarkers(readFile());
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
	
	private String readFile() {
		String string = null;
		try {
			FileInputStream inFile = new FileInputStream(FILENAME);
			InputStreamReader in = new InputStreamReader(inFile, "UTF-8");
			int integer;
			while((integer = in.read()) != -1) {
				string += (char)integer;
			}
			in.close();
			inFile.close();
			return string;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "File "+FILENAME+" was not found!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return string;
	}
	
	private void fileToMarkers(String string) {
		String[] file = string.split("[\\r\\n]+");
		for (int i = 0; i < file.length; i++) {
			String[] line = file[i].split(",");
			createMarker(Double.parseDouble(line[9]), Double.parseDouble(line[10]), line[11]);
		}
	}
	
	private void createMarker(double latitude, double longtitude, String name) {
		Platform.runLater(() -> {
			MarkerOptions markerOptions = new MarkerOptions();
			
			markerOptions.position(new LatLong(latitude, longtitude))
	        .visible(Boolean.TRUE)
	        .title(name);

			Marker marker = new Marker(markerOptions);
			map.addMarker(marker);
        });
	}
	
}

package com.esgi.sslmanager.core.views;

import com.esgi.sslmanager.SSLManagerApp;
import com.esgi.sslmanager.commons.externalapps.arpspoof.ArpspoofDebian;
import com.esgi.sslmanager.commons.externalapps.arpspoof.IArpspoof;
import com.esgi.sslmanager.commons.externalapps.ipforwarding.IIPForwarding;
import com.esgi.sslmanager.commons.externalapps.ipforwarding.IPForwardingDebian;
import com.esgi.sslmanager.commons.externalapps.nmap.INmap;
import com.esgi.sslmanager.commons.externalapps.nmap.NmapDebian;
import com.esgi.sslmanager.commons.externalapps.sslstrip.ISSLstrip;
import com.esgi.sslmanager.commons.externalapps.sslstrip.SSLstripDebian;
import com.esgi.sslmanager.commons.handlers.LogHandler;
import com.esgi.sslmanager.commons.utils.DateUtils;
import com.esgi.sslmanager.commons.utils.OSUtils;
import com.esgi.sslmanager.core.models.Log;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class MainViewController {

	private SSLManagerApp SSLManagerApp;
	private Scene mainScene;

	private IIPForwarding ipForwarding;

	private INmap nmap;
	private Service<Void> nmapServiceFindGateways;
	private Service<Void> nmapServiceFindHosts;

	private IArpspoof arpspoof;
	private Service<Void> arpspoofServiceRunning;

	private ISSLstrip sslstrip;
	private Service<Void> sslstripServiceRunning;
	private ScheduledService<Void> sslstripServiceLog;

	private LogHandler logHandler;

	@FXML
	private Tab tabNetwork;

	@FXML
	private Tab tabArpSpoof;

	@FXML
	private Tab tabSSLstrip;

	// --------- Network Tab --------------------- //

	@FXML
	private Button buttonScanNetworkInterfaces;

	@FXML
	private ComboBox<String> chooseNetworkInterface;

	@FXML
	private TextField currentNetworkInterfaceName;

	@FXML
	private TextField currentAddressIP;

	@FXML
	private Button buttonScanGateways;

	@FXML
	private ComboBox<String> chooseGateways;

	@FXML
	private TextField currentGatewayIP;

	@FXML
	private Button buttonScanHosts;

	@FXML
	private ComboBox<String> chooseSubMasks;

	@FXML
	private Button buttonIPForwarding;

	private boolean isIPForwardingEnable = false;

	// --------- Arpspoof Tab --------------------- //

	@FXML
	private TextField ipTargetArpspoof;

	@FXML
	private TextField ipGatewayArpspoof;

	@FXML
	private Tab subTabHosts;

	@FXML
	private ListView<String> listViewHosts;

	@FXML
	private Tab subTabGateways;

	@FXML
	private ListView<String> listViewGateways;

	@FXML
	private Button buttonStartArpspoof;

	// --------- SSLstrip Tab --------------------- //

	@FXML
	private Button buttonStartSSLstrip;

	@FXML
	private TextField sslStripPort;

	// --------- Log Tab --------------------- //

	@FXML
    private TableView<Log> logTable;

	@FXML
    private TableColumn<Log, String> dateColumn;

    @FXML
    private TableColumn<Log, String> domainNameColumn;

    @FXML
    private TableColumn<Log, String> loginColumn;

    @FXML
    private TableColumn<Log, String> passwordColumn;

    @FXML
    private Button buttonExport;

    @FXML
    private Button buttonArchive;

	// --------- Console --------------------- //

	@FXML
	private TextArea consoleTextArea;

	// --------- END FXML --------------------- //

	public MainViewController() {
		// If Linux
		ipForwarding = new IPForwardingDebian();
		nmap = new NmapDebian();
		arpspoof = new ArpspoofDebian();
		arpspoof.close();
		sslstrip = new SSLstripDebian();
		sslstrip.close();

		logHandler = new LogHandler();
	}

	@FXML
	private void initialize() {
		initNmapServiceFindGateways();
		initNmapServiceFindHosts();
		initSubMaskChooser();

		chooseNetworkInterface.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateCurrentNetworkInterfaceName(newValue));

		chooseGateways.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> currentGatewayIP.setText(newValue));

		// Arpspoof //
		initArpspoofServiceRunning();

		listViewHosts.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateIPTargetArpspoof(newValue));

		listViewGateways.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateIPGatewayArpspoof(newValue));

		initLogTableColumns();
		initSSLstripServiceRunning();
		initSSLstripServiceLog();
		sslstripServiceLog.start();

		buttonIPForwarding.setTooltip(new Tooltip("Activer/Désactiver le routage IP (IP forwarding)"));
		clickOnButtonIPForwarding();
	}

	private void initNmapServiceFindGateways() {
		nmapServiceFindGateways = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						nmap.findGateways();
						consoleTextArea.appendText(SSLManagerApp.getConsoleText());
						return null;
					}
				};
			}
		};
		nmapServiceFindGateways.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue,
				Worker.State oldValue, Worker.State newValue) -> {
			switch (newValue) {
			case FAILED:
			case CANCELLED:
				resetServiceFindGateways();
				break;

			case SUCCEEDED:
				resetServiceFindGateways();
				if (!nmap.getGateways().isEmpty()) {
					ObservableList<String> gateways = FXCollections.observableArrayList(nmap.getGateways());
					listViewGateways.setItems(gateways);
					chooseGateways.setItems(gateways);
					chooseGateways.getSelectionModel().selectFirst();
					chooseGateways.setDisable(false);
					currentGatewayIP.setDisable(false);
					buttonScanHosts.setDisable(false);
					chooseSubMasks.setDisable(false);
				}
				break;

			case READY:
				break;
			case RUNNING:
				break;
			case SCHEDULED:
				break;
			default:
				break;
			}
		});
	}

	private void resetServiceFindGateways() {
		nmapServiceFindGateways.reset();
		buttonScanNetworkInterfaces.setDisable(false);
		buttonScanHosts.setDisable(false);
		buttonScanGateways.setDisable(false);
		chooseNetworkInterface.setDisable(false);
		mainScene.setCursor(Cursor.DEFAULT);
	}

	private void initNmapServiceFindHosts() {
		nmapServiceFindHosts = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						nmap.findHosts(currentGatewayIP.getText(),
								chooseSubMasks.getSelectionModel().getSelectedItem());
						consoleTextArea.appendText(SSLManagerApp.getConsoleText());
						return null;
					}
				};
			}
		};
		nmapServiceFindHosts.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue,
				Worker.State oldValue, Worker.State newValue) -> {
			switch (newValue) {
			case FAILED:
			case CANCELLED:
				resetServiceFindHosts();
				break;

			case SUCCEEDED:
				resetServiceFindHosts();

				ObservableList<String> hosts = FXCollections.observableArrayList(nmap.getHosts());
				listViewHosts.setItems(hosts);
				break;

			case READY:
				buttonScanHosts.setText("Scan Hôtes");
				break;
			case RUNNING:
				buttonScanHosts.setText("Arrêter Scan");
				break;
			case SCHEDULED:
				break;
			default:
				break;
			}
		});
	}

	private void initSubMaskChooser() {
		ObservableList<String> subMasks = FXCollections.observableArrayList();
		subMasks.add("/8");
		subMasks.add("/16");
		subMasks.add("/24");
		subMasks.add("/32");
		chooseSubMasks.setItems(subMasks);
		chooseSubMasks.getSelectionModel().selectFirst();
	}

	private void updateCurrentNetworkInterfaceName(String name) {
		currentNetworkInterfaceName.setText(name);
		listViewHosts.setItems(null);
		if (name != null) {
			String addressIP = OSUtils.findIpAdressByNetworkInterfaceName(name);
			currentAddressIP.setText(addressIP.substring(1));
		}
	}

	private void initArpspoofServiceRunning() {
		arpspoofServiceRunning = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						arpspoof.run(currentNetworkInterfaceName.getText(), ipTargetArpspoof.getText(),
								ipGatewayArpspoof.getText());
						return null;
					}
				};
			}
		};
		arpspoofServiceRunning.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue,
				Worker.State oldValue, Worker.State newValue) -> {
			switch (newValue) {
			case FAILED:
			case CANCELLED:
			case SUCCEEDED:
				arpspoof.close();
				arpspoofServiceRunning.reset();
				consoleTextArea.appendText("Arpspoof stopped." + System.lineSeparator());
				break;

			case READY:
				buttonStartArpspoof.setText("Démarrer");
				break;

			case RUNNING:
				buttonStartArpspoof.setText("Arrêter");
				consoleTextArea.appendText("Arpspoof running..." + System.lineSeparator());
				break;

			case SCHEDULED:
				break;
			default:
				break;
			}
		});
	}

	private void initLogTableColumns() {
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateColumn"));
		domainNameColumn.setCellValueFactory(new PropertyValueFactory<>("domainNameColumn"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("loginColumn"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("passwordColumn"));
	}

	private void initSSLstripServiceRunning() {
		sslstripServiceRunning = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						String logFileName = DateUtils.getDateNow(DateUtils.FILE_DATE_PATTERN)
								.replaceFirst(":", "h").replaceFirst(":", "m");
						sslstrip.run(sslStripPort.getText(), logFileName);
						return null;
					}
				};
			}
		};
		sslstripServiceRunning.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue,
				Worker.State oldValue, Worker.State newValue) -> {
			switch (newValue) {
			case FAILED:
			case CANCELLED:
			case SUCCEEDED:
				if (!arpspoofServiceRunning.isRunning()) {
					tabNetwork.setDisable(false);
				}
				sslstrip.close();
				sslstripServiceRunning.reset();
				consoleTextArea.appendText("SSLstrip stopped." + System.lineSeparator());
				break;

			case READY:
				buttonStartSSLstrip.setText("Démarrer");
				break;
			case RUNNING:
				tabNetwork.setDisable(true);
				buttonStartSSLstrip.setText("Arrêter");
				consoleTextArea.appendText("SSLstrip running..." + System.lineSeparator());
				break;

			case SCHEDULED:
				break;
			default:
				break;
			}
		});
	}

	private void initSSLstripServiceLog() {
		sslstripServiceLog = new ScheduledService<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						logHandler.readFiles();
//						ObservableList<Log> logData = FXCollections.observableArrayList(logHandler.getLogList());
//				        logTable.setItems(logData);
						return null;
					}
				};
			}
		};
		sslstripServiceLog.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue,
				Worker.State oldValue, Worker.State newValue) -> {
			switch (newValue) {

			case READY:
				break;

			case RUNNING:
				break;

			case SCHEDULED:
				dateColumn.setSortable(true);
				ObservableList<Log> logData = FXCollections.observableArrayList(logHandler.getLogList());
		        logTable.setItems(logData);
		        logTable.getSortOrder().add(dateColumn);
		        dateColumn.setSortable(false);
				break;

			default:
				break;
			}
		});
		sslstripServiceLog.setPeriod(Duration.seconds(1));
		sslstripServiceLog.setRestartOnFailure(true);
		sslstripServiceLog.setMaximumFailureCount(100);
	}

	private void updateIPTargetArpspoof(String name) {
		ipTargetArpspoof.setText(name);
		if (ipGatewayArpspoof.getText() != null && !ipGatewayArpspoof.getText().isEmpty()
				&& ipTargetArpspoof.getText() != null && !ipTargetArpspoof.getText().isEmpty()) {
			buttonStartArpspoof.setDisable(false);
		} else {
			buttonStartArpspoof.setDisable(true);
		}
	}

	private void updateIPGatewayArpspoof(String name) {
		ipGatewayArpspoof.setText(name);
		if (ipGatewayArpspoof.getText() != null && !ipGatewayArpspoof.getText().isEmpty()
				&& ipTargetArpspoof.getText() != null && !ipTargetArpspoof.getText().isEmpty()) {
			buttonStartArpspoof.setDisable(false);
		} else {
			buttonStartArpspoof.setDisable(true);
		}
	}

	private void resetServiceFindHosts() {
		nmapServiceFindHosts.reset();
		buttonScanNetworkInterfaces.setDisable(false);
		buttonScanHosts.setDisable(false);
		buttonScanGateways.setDisable(false);
		chooseNetworkInterface.setDisable(false);
		mainScene.setCursor(Cursor.DEFAULT);
	}

	public void setSSLManagerApp(SSLManagerApp SSLManagerApp) {
		this.SSLManagerApp = SSLManagerApp;
		this.mainScene = this.SSLManagerApp.getMainStage().getScene();
	}

	@FXML
	private void clickOnButtonIPForwarding() {
		if (!isIPForwardingEnable) {
			ipForwarding.enable();
			isIPForwardingEnable = true;
		} else {
			ipForwarding.disable();
			isIPForwardingEnable = false;
		}
		buttonIPForwarding.setText(isIPForwardingEnable ?
				IIPForwarding.ENABLED_TEXT : IIPForwarding.DISABLED_TEXT);
		consoleTextArea.appendText(buttonIPForwarding.getText());
	}

	@FXML
	private void clickOnScanNetworkInterfaces() {
		ObservableList<String> networkInterfaces = FXCollections
				.observableArrayList(OSUtils.getNetworkInterfaceNames());
		chooseNetworkInterface.setItems(networkInterfaces);
		if (!chooseNetworkInterface.getItems().isEmpty()) {
			chooseNetworkInterface.getSelectionModel().selectFirst();
			chooseNetworkInterface.setDisable(false);
			tabArpSpoof.setDisable(false);
			tabSSLstrip.setDisable(false);
			buttonScanGateways.setDisable(false);
		} else {
			System.out.println("0 Network Interface found !");
		}
	}

	@FXML
	private void clickOnScanGateways() {
		mainScene.setCursor(Cursor.WAIT);
		buttonScanNetworkInterfaces.setDisable(true);
		buttonScanHosts.setDisable(true);
		buttonScanGateways.setDisable(true);
		chooseNetworkInterface.setDisable(true);
		nmapServiceFindGateways.start();
	}

	@FXML
	private void clickOnScanHosts() {
		mainScene.setCursor(Cursor.WAIT);
		buttonScanNetworkInterfaces.setDisable(true);
		buttonScanGateways.setDisable(true);
		chooseNetworkInterface.setDisable(true);
		if (!nmapServiceFindHosts.isRunning()) {
			nmapServiceFindHosts.start();
		} else {
			nmapServiceFindHosts.cancel();
		}
	}

	@FXML
	private void clickOnStartArpspoof() {
		if (!arpspoofServiceRunning.isRunning()) {
			arpspoofServiceRunning.start();
			subTabHosts.setDisable(true);
			subTabGateways.setDisable(true);
			tabNetwork.setDisable(true);
		} else {
			arpspoofServiceRunning.cancel();
			subTabHosts.setDisable(false);
			subTabGateways.setDisable(false);
			tabNetwork.setDisable(sslstripServiceRunning.isRunning());
		}
	}

	@FXML
	private void clickOnStartSSLstrip() {
		if (!sslstripServiceRunning.isRunning()) {
			logHandler.isSSLstripRunning(true);
			sslstripServiceRunning.start();
			tabNetwork.setDisable(true);
		} else {
			logHandler.isSSLstripRunning(false);
			sslstripServiceRunning.cancel();
			tabNetwork.setDisable(arpspoofServiceRunning.isRunning());
			sslstrip.flushIPTables();
			consoleTextArea.appendText("Flush IP table" + System.lineSeparator());
		}
	}

	@FXML
	private void clickOnButtonExport() {

	}

	@FXML
	private void clickOnButtonArchive() {
		logHandler.clearLog();
		logTable.setItems(null);
	}

	public TextField getCurrentNetworkInterfaceName() {
		return currentNetworkInterfaceName;
	}

}

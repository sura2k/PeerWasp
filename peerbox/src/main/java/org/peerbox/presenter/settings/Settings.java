package org.peerbox.presenter.settings;

import java.net.URL;
import java.util.ResourceBundle;

import org.peerbox.presenter.settings.synchronization.Synchronization;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class Settings implements Initializable {

	@FXML
	private Account accountController;

	@FXML
	private Network networkController;

	@FXML
	private Synchronization synchronizationController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}

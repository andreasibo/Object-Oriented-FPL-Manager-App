package FPLManager.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import FPLManager.model.DataManager;
import FPLManager.model.Manager;
import FPLManager.model.Player;

public class FPLManagerController {

    @FXML private TextField savedTeamNameField, teamIdField, saveTeamNameField, saveTeamIdField;
    @FXML private Button getSavedStatsButton, getStatsButton, saveUserButton;
    @FXML private TreeTableView<Player> statsTableView;
    @FXML private TreeTableColumn<Player, String> playerColumn, teamColumn, pointAverageColumn, selectedPercentageColumn;
    @FXML private TreeTableColumn<Player, Integer> fitPercentageColumn, pointsLastRoundColumn, transfersColumn;
    @FXML private TreeTableColumn<Player, Double> priceColumn, costChangeColumn, xgPer90Column, xaPer90Column, csPer90Column;
    @FXML private Label deadlineLabel, wildcardLabel, benchboostLabel, triplecaptainLabel, freehitLabel, assistantmanagerLabel, availableTransfersLabel;

    private Manager manager;
    private DataManager dataManager;

    @FXML
    public void initialize() {
        dataManager = new DataManager();
        setupTableViewColumns();
    }

    private void setupTableViewColumns() {
        playerColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        teamColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("team"));
        fitPercentageColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("chanceOfPlaying"));
        pointsLastRoundColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("pointsLastRound"));
        priceColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        costChangeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("costChange"));
        pointAverageColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("avgPoints"));
        selectedPercentageColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("selectedBy"));
        transfersColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transferBalance"));
        xgPer90Column.setCellValueFactory(new TreeItemPropertyValueFactory<>("xG"));
        xaPer90Column.setCellValueFactory(new TreeItemPropertyValueFactory<>("xA"));
        csPer90Column.setCellValueFactory(new TreeItemPropertyValueFactory<>("cleanSheetsPerGame"));
    }

    @FXML
    public void handleButtonClickSaved() {
        String teamName = savedTeamNameField.getText();
        int teamId = dataManager.findUser(teamName);
        if (teamId != -1) {
            fetchAndDisplayManagerData(teamId);
        } else {
            showAlert("Team not found", "Could not find team with name: " + teamName);
        }
    }

    @FXML
    public void handleButtonClickID() {
        try {
            int teamId = Integer.parseInt(teamIdField.getText());
            fetchAndDisplayManagerData(teamId);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid Team ID.");
        }

    }

    @FXML
    public void handleSaveUser() {
        try {
            int teamId = Integer.parseInt(saveTeamIdField.getText());
            dataManager.addUser(saveTeamNameField.getText(), teamId);
            showAlert("Saved", "Team data saved successfully.");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid Team ID.");
        }
    }

    private void fetchAndDisplayManagerData(int teamId) {
        try {
            manager = new Manager(teamId);
            displayManagerData(manager);
            displayGWinfo(manager);
            displayChips(manager);
            displayTransfers(manager);
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve manager data: " + e.getMessage());
        }
    }

    private void displayGWinfo(Manager manager) {
        if (manager != null && manager.getGWDeadline() != null) {
            deadlineLabel.setText(manager.getGWDeadline());
        } else {
            deadlineLabel.setText("N/A");
        }
    }

    private void displayTransfers(Manager manager) {
        if (manager != null) {
            availableTransfersLabel.setText(String.valueOf(manager.getAvailableTransfers()));
        } else {
            availableTransfersLabel.setText("N/A");
        }
    }

    private void displayChips(Manager manager) {
        if (manager != null && manager.getChipsAvailable() != null) {
            Map<String, Object> chips = manager.getChipsAvailable();
            wildcardLabel.setText(String.valueOf(chips.get("wildcard")));
            benchboostLabel.setText(String.valueOf(chips.get("bboost")));
            freehitLabel.setText(String.valueOf(chips.get("freehit")));
            triplecaptainLabel.setText(String.valueOf(chips.get("3xc")));
            assistantmanagerLabel.setText(String.valueOf(chips.get("manager")));
        } else {
            wildcardLabel.setText("N/A");
            benchboostLabel.setText("N/A");
            freehitLabel.setText("N/A");
            triplecaptainLabel.setText("N/A");
            assistantmanagerLabel.setText("N/A");
        }
    }

    private void displayManagerData(Manager manager) {
        if (manager == null) {
            statsTableView.setRoot(null);
            return;
        }
        ArrayList<Player> players = manager.getTeamPlayers();
        ObservableList<TreeItem<Player>> treeItems = FXCollections.observableArrayList();

        for (Player player : players) {
            TreeItem<Player> treeItem = new TreeItem<>(player);
            treeItems.add(treeItem);
        }

        TreeItem<Player> root = new TreeItem<>(new Player(-1, -1, null, null, null));
        root.getChildren().addAll(treeItems);
        statsTableView.setRoot(root);
        statsTableView.setShowRoot(false);

        addFixtureColumns(players);
    }

    private void addFixtureColumns(ArrayList<Player> players) {
        statsTableView.getColumns().removeIf(column -> column.getText().startsWith("GW"));
        if (players != null && !players.isEmpty()) {
            if (players.get(0).getFixtures() != null) {
                Map<Integer, List<String>> firstPlayerFixtures = players.get(0).getFixtures();
                for (int gameweek : firstPlayerFixtures.keySet()) {
                    TreeTableColumn<Player, String> gameweekColumn = new TreeTableColumn<>("GW " + gameweek);
                    gameweekColumn.setCellValueFactory(cellData -> {
                        String fixture = cellData.getValue().getValue().getFixtureForGameweek(gameweek);
                        return new SimpleStringProperty(fixture.isEmpty() ? "N/A" : fixture); // Handle empty string
                    });
                    statsTableView.getColumns().add(gameweekColumn);
                }
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

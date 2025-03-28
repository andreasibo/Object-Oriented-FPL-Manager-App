package FPLManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        dataManager = new DataManager();
    }

    @FXML
    public void handleButtonClickSaved() {
        String teamName = savedTeamNameField.getText();
        try {
            int teamId = dataManager.findUser(teamName);

            if (teamId != -1) {
                fetchAndDisplayManagerData(teamId);
            } else {
                showAlert("Team not found", "Could not find team with name: " + teamName);
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid Team ID");
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
        String teamName = saveTeamNameField.getText();
        String teamIdStr = saveTeamIdField.getText();

        try {
            int teamId = Integer.parseInt(teamIdStr);
            dataManager.addUser(teamName, teamId);
            showAlert("Saved", "Team data saved successfully.");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid Team ID.");
        }
    }

    private void fetchAndDisplayManagerData(int teamId) {
        manager = new Manager(teamId);

        if (manager != null) {
            displayManagerData(manager);
            displayGWinfo(manager);
            displayChips(manager);
            displayTransfers(manager);
        } else {
            showAlert("Error", "Failed to retrieve manager data.");
        }
    }

    private void displayGWinfo(Manager manager) {
        deadlineLabel.setText(manager.getGWDeadline());
    }

    private void displayTransfers(Manager manager) {
        availableTransfersLabel.setText(manager.getAvailableTransfers() + "");
    }

    private void displayChips(Manager manager) {
        wildcardLabel.setText(manager.getChipsAvailable().get("wildcard").toString());
        benchboostLabel.setText(manager.getChipsAvailable().get("bboost").toString());
        freehitLabel.setText(manager.getChipsAvailable().get("freehit").toString());
        triplecaptainLabel.setText(manager.getChipsAvailable().get("3xc").toString());
        assistantmanagerLabel.setText(manager.getChipsAvailable().get("manager").toString());
    }

    private void displayManagerData(Manager manager) {
        ArrayList<Player> players = manager.getTeamPlayers();
        ObservableList<TreeItem<Player>> treeItems = FXCollections.observableArrayList();

        for (Player player : players) {
            TreeItem<Player> treeItem = new TreeItem<>(player);
            treeItems.add(treeItem);
        }

        TreeItem<Player> root = new TreeItem<>(new Player(-1, -1, null, null));
        root.getChildren().addAll(treeItems);
        statsTableView.setRoot(root);
        statsTableView.setShowRoot(false);

        playerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue().getName()));
        teamColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue().getTeam()));
        fitPercentageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getValue().getChanceOfPlaying()).asObject());
        pointsLastRoundColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getValue().getPointsLastRound()).asObject());
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getPrice()).asObject());
        costChangeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getCostChange()).asObject());
        pointAverageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue().getAvgPoints()));
        selectedPercentageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue().getSelectedBy()));
        transfersColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getValue().getTransferBalance()).asObject());
        xgPer90Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getxG()).asObject());
        xaPer90Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getxA()).asObject());
        csPer90Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getCleanSheetsPerGame()).asObject());
        
        if (!players.isEmpty() && players.get(0).getFixtures() != null) {
                Map<Integer, List<String>> firstPlayerFixtures = players.get(0).getFixtures();
                List<Integer> gameweeks = new ArrayList<>(firstPlayerFixtures.keySet());

                for (int gameweek : gameweeks) {
                    TreeTableColumn<Player, String> gameweekColumn = new TreeTableColumn<>("GW " + gameweek);
                    gameweekColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getFixtureForGameweek(gameweek)));
                    statsTableView.getColumns().add(gameweekColumn);
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

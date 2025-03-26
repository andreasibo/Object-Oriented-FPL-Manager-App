package FPLManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FPLManagerController {

    @FXML
    private TextField savedTeamNameField;

    @FXML
    private TextField GWFieldforName;

    @FXML
    private Button getSavedStatsButton;

    @FXML
    private TextField teamIdField;

    @FXML
    private TextField GWFieldforID;

    @FXML
    private Button getStatsButton;

    @FXML
    private TextField saveTeamNameField;

    @FXML
    private TextField saveTeamIdField;

    @FXML
    private Button saveUserButton;

    @FXML
    private TreeTableView<Player> statsTableView;

    @FXML
    private TreeTableColumn<Player, String> playerColumn;

    @FXML
    private TreeTableColumn<Player, Integer> teamColumn;

    @FXML
    private TreeTableColumn<Player, Map<Integer, List<Integer>>> nextGwColumn;

    @FXML
    private TreeTableColumn<Player, Double> fitPercentageColumn;

    @FXML
    private TreeTableColumn<Player, Integer> pointsLastRoundColumn;

    @FXML
    private TreeTableColumn<Player, Double> priceColumn;

    @FXML
    private TreeTableColumn<Player, Double> costChangeColumn;

    @FXML
    private TreeTableColumn<Player, String> pointAverageColumn;

    @FXML
    private TreeTableColumn<Player, String> selectedPercentageColumn;

    @FXML
    private TreeTableColumn<Player, Integer> transfersColumn;

    @FXML
    private TreeTableColumn<Player, Double> xgPer90Column;

    @FXML
    private TreeTableColumn<Player, Double> xaPer90Column;

    @FXML
    private TreeTableColumn<Player, Double> csPer90Column;

    private Manager manager;
    private DataManager dataManager;

    @FXML
    public void initialize() {
        playerColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        teamColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("team"));
        nextGwColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("fixtures"));
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
        String gwText = GWFieldforName.getText();
        try {
            int teamId = dataManager.findUser(teamName);
            int gw = Integer.parseInt(gwText);

            if (teamId != -1) {
                fetchAndDisplayManagerData(teamId, gw);
            } else {
                showAlert("Team not found", "Could not find team with name: " + teamName);
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid Team ID and Game week.");
        }

    }

    @FXML
    public void handleButtonClickID() {
        try {
            int teamId = Integer.parseInt(teamIdField.getText());
            int gw = Integer.parseInt(GWFieldforID.getText());
            fetchAndDisplayManagerData(teamId, gw);
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

    private void fetchAndDisplayManagerData(int teamId, int gw) {
        manager = new Manager(teamId, gw);

        if (manager != null) {
            displayManagerData(manager);
        } else {
            showAlert("Error", "Failed to retrieve manager data.");
        }
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
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

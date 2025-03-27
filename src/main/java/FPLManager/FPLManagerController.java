package FPLManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FPLManagerController {

    @FXML private TextField savedTeamNameField, teamIdField, saveTeamNameField, saveTeamIdField;
    @FXML private Button getSavedStatsButton, getStatsButton, saveUserButton;

    @FXML private TreeTableView<Player> statsTableView;
    @FXML private TreeTableColumn<Player, String> playerColumn, teamColumn, pointAverageColumn, selectedPercentageColumn;
    @FXML private TreeTableColumn<Player, Integer> pointsLastRoundColumn, transfersColumn;
    @FXML private TreeTableColumn<Player, Map<Integer, List<String>>> nextGwColumn;
    @FXML private TreeTableColumn<Player, Double> fitPercentageColumn, priceColumn, costChangeColumn, xgPer90Column, xaPer90Column, csPer90Column;
    @FXML private Label deadlineLabel, wildcardLabel, benchboostLabel, triplecaptainLabel, freehitLabel, assistantmanagerLabel;

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
        try {
            int teamId = dataManager.findUser(teamName);

            if (teamId != -1) {
                fetchAndDisplayManagerData(teamId);
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
        } else {
            showAlert("Error", "Failed to retrieve manager data.");
        }
    }

    private void displayGWinfo(Manager manager) {
        deadlineLabel.setText(manager.getGWDeadline());
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
        nextGwColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getValue().getFixtures()));
        fitPercentageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getChanceOfPlaying()).asObject());
        pointsLastRoundColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getValue().getPointsLastRound()).asObject());
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getPrice()).asObject());
        costChangeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getCostChange()).asObject());
        pointAverageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue().getAvgPoints()));
        selectedPercentageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue().getSelectedBy()));
        transfersColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getValue().getTransferBalance()).asObject());
        xgPer90Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getxG()).asObject());
        xaPer90Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getxA()).asObject());
        csPer90Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValue().getCleanSheetsPerGame()).asObject());

        nextGwColumn.setCellFactory(new Callback<TreeTableColumn<Player, Map<Integer, List<String>>>, TreeTableCell<Player, Map<Integer, List<String>>>>() {
            @Override
            public TreeTableCell<Player, Map<Integer, List<String>>> call(TreeTableColumn<Player, Map<Integer, List<String>>> param) {
                return new TreeTableCell<Player, Map<Integer, List<String>>>() {
                    @Override
                    protected void updateItem(Map<Integer, List<String>> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            TreeItem<Player> treeItem = getTreeTableView().getTreeItem(getIndex());
                            if (treeItem != null) {
                                Player player = treeItem.getValue();
                                String playersTeam = player.getTeam();
    
                                for (Map.Entry<Integer, List<String>> entry : item.entrySet()) {
                                    int gw = entry.getKey();
                                    List<String> fixtureList = entry.getValue();
                                    if(fixtureList != null && fixtureList.size() > 0) {
                                        sb.append("GW ").append(gw).append(": ");
                                        for(int i = 0; i < fixtureList.size(); i++){
                                            if (playersTeam.equals(fixtureList.get(i))) {
                                                if (i % 2 == 0) {
                                                    if (i + 1 < fixtureList.size()) {
                                                        sb.append(fixtureList.get(i + 1)).append(" (H)");
                                                    }
                                                } else {
                                                    if (i - 1 >= 0) {
                                                        sb.append(fixtureList.get(i - 1)).append(" (A)");
                                                    }
                                                }
                                            }
                                            if (i < fixtureList.size() -1 && playersTeam.equals(fixtureList.get(i))) {
                                                sb.append(", ");
                                            }
                                        }
                                        sb.append("\n");
                                    }
                                }
                                setText(sb.toString());
                            }
                        }
                    }
                };
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package FPLManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Player class represents a player in the Fantasy Premier League (FPL) game.
 * It contains various attributes of the player and methods to access these attributes.
 */
public class Player implements ID{
    private int ID;
    private int nextGW;
    private String name;
    private int chanceOfPlaying;
    private double costChange;
    private int pointsLastRound;
    private double price;
    private String avgPoints;
    private String selectedBy;
    private int transferBalance;
    private double xG;
    private double xA;
    private double cleanSheets;
    private int teamID;
    private String team;
    private Map<Integer, List<String>> fixtures;

    /**
     * Constructs a new Player object with the specified ID, next game week, player data, and fixture data.
     *
     * @param ID the unique identifier of the player
     * @param nextGW the next game week
     * @param playerData a map containing the player's data
     * @param fixtureData a map containing the fixture data
     */
    public Player(int ID, int nextGW, Map<String, Object> playerData, Map<Integer, List<Map<String, Object>>> fixtureData) {
        this.ID = ID;
        this.nextGW = nextGW;
        DataManager data = new DataManager();
        ArrayList<ArrayList<String>> teams = data.getTeams();
        setAttributes(playerData);
        setTeam(teams);
        setFixtures(fixtureData, teams);
        sortFixtures();
    }   

    /**
     * Sets the attributes of the player using the provided player data.
     *
     * @param playerData a map containing the player's data, provided by the FPLAPI class through the Manager class.
     */
    private void setAttributes(Map<String, Object> playerData) {
        if (playerData != null) {
            String firstName = (String) playerData.get("first_name");
            String secondName = (String) playerData.get("second_name");
    
            this.name = (firstName != null ? firstName : "") + " " + (secondName != null ? secondName : "");
    
            this.chanceOfPlaying = playerData.get("chance_of_playing_next_round") != null ? (int) playerData.get("chance_of_playing_next_round") : 0;
            this.costChange = playerData.get("cost_change_event") != null ? ((Integer) playerData.get("cost_change_event")).doubleValue() / 10 : 0.0;
            this.pointsLastRound = playerData.get("event_points") != null ? (int) playerData.get("event_points") : 0;
            this.price = playerData.get("now_cost") != null ? ((Integer) playerData.get("now_cost")).doubleValue() / 10.0 : 0.0;
            this.avgPoints = playerData.get("points_per_game") != null ? (String) playerData.get("points_per_game") : "0";
            this.selectedBy = playerData.get("selected_by_percent") != null ? (String) playerData.get("selected_by_percent") : "0";
            this.transferBalance = playerData.get("transfers_in_event") != null && playerData.get("transfers_out_event") != null ?
                    (int) playerData.get("transfers_in_event") - (int) playerData.get("transfers_out_event") : 0;
            this.xG = playerData.get("expected_goals_per_90") != null ? (double) playerData.get("expected_goals_per_90") : 0.0;
            this.xA = playerData.get("expected_assists_per_90") != null ? (double) playerData.get("expected_assists_per_90") : 0.0;
            this.cleanSheets = playerData.get("clean_sheets_per_90") != null ? (double) playerData.get("clean_sheets_per_90") : 0.0;
            this.teamID = playerData.get("team") != null ? (int) playerData.get("team") : 0;
        } else {
            this.name = "";
            this.chanceOfPlaying = 0;
            this.costChange = 0.0;
            this.pointsLastRound = 0;
            this.price = 0.0;
            this.avgPoints = "0";
            this.selectedBy = "0";
            this.transferBalance = 0;
            this.xG = 0.0;
            this.xA = 0.0;
            this.cleanSheets = 0.0;
        }
    }

    /**
     * Sets the fixtures of the player using the provided fixture data.
     *
     * @param fixtureData a map containing the fixture data.
     * The keys are the GW number and the values is a list where the first index is the opponent, and the second integer represent 1 for home and 0 for away.
     */
    private void setFixtures(Map<Integer, List<Map<String, Object>>> fixtureData, ArrayList<ArrayList<String>> teams) {
        if (fixtureData != null) {
            Map<Integer, List<String>> fixtures = new HashMap<>();
            for (int gw = this.nextGW; gw < 39; gw++) {
                if (fixtureData.containsKey(gw)) { 
                    List<String> fixture = new ArrayList<>();
                    List<Map<String, Object>> fixtureList = fixtureData.get(gw);
                    if (fixtureList != null) {
                        for (int matches = 0; matches < fixtureList.size(); matches++) {
                            Map<String, Object> match = fixtureList.get(matches);
                            if (match != null && match.get("HomeTeam") != null && match.get("AwayTeam") != null) {
                                if (match.get("HomeTeam").equals(this.teamID)) {
                                    int awayTeam = (int) match.get("AwayTeam");
                                    String fixtureOutput = teams.get(awayTeam - 1).get(1) + "(H)";
                                    fixture.add(fixtureOutput);
                                } else if (match.get("AwayTeam").equals(this.teamID)) {
                                    int homeTeam = (int) match.get("HomeTeam");
                                    String fixtureOutput = teams.get(homeTeam - 1).get(1) + "(A)";
                                    fixture.add(fixtureOutput);
                                }
                            }
                        }
                    }
                    fixtures.put(gw, fixture);
                }
            }
            this.fixtures = fixtures;
        } else {
            this.fixtures = new HashMap<>(); 
        }
    }

    private void sortFixtures() {
        Map<Integer, List<String>> sortedFixtures = new TreeMap<>();
        sortedFixtures.putAll(this.fixtures);
        this.fixtures = sortedFixtures;
    }

    private void setTeam(ArrayList<ArrayList<String>> teams) {
        if (this.teamID == 0) {
            this.team = "Not found";
        } else {
            this.team = teams.get(this.teamID - 1).get(0);
        }
    }

    public String getFixtureForGameweek(int gameweek) {
        if (fixtures != null && fixtures.containsKey(gameweek)) {
            return String.join(", ", fixtures.get(gameweek));
        }
        return "";
    }

    // Getters
    public String getName() {return name;}
    public int getChanceOfPlaying() {return chanceOfPlaying;}
    public double getCostChange() {return costChange;}
    public int getPointsLastRound() {return pointsLastRound;}
    public double getPrice() {return price;}
    public String getAvgPoints() {return avgPoints;}
    public String getSelectedBy() {return selectedBy;}
    public int getTransferBalance() {return transferBalance;}
    public double getxG() {return xG;}
    public double getxA() {return xA;}
    public double getCleanSheetsPerGame() {return cleanSheets;}
    public String getTeam() {return team;}
    public Map<Integer, List<String>> getFixtures() {return fixtures;}

    @Override
    public int getID() {
        return this.ID;
    }

}

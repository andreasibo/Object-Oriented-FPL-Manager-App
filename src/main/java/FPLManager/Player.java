package FPLManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int team;
    private Map<Integer, List<Integer>> fixtures;

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
        setAttributes(playerData);
        setFixtures(fixtureData);
    }   

    /**
     * Sets the attributes of the player using the provided player data.
     *
     * @param playerData a map containing the player's data, provided by the FPLAPI class through the Manager class.
     */
    private void setAttributes(Map<String, Object> playerData) {
        this.name = (String) playerData.get("first_name") + " " + (String) playerData.get("second_name");
        this.chanceOfPlaying = playerData.get("chance_of_playing_next_round") != null ? (int) playerData.get("chance_of_playing_next_round") : 0;
        this.costChange = ((Integer) playerData.get("cost_change_event")).doubleValue();
        this.pointsLastRound = playerData.get("event_points") != null ? (int) playerData.get("event_points") : 0;
        this.price = ((Integer) playerData.get("now_cost")).doubleValue() / 10.0 ;
        this.avgPoints = (String) playerData.get("points_per_game");
        this.selectedBy = (String) playerData.get("selected_by_percent");
        this.transferBalance = (int) playerData.get("transfers_in_event") - (int) playerData.get("transfers_out_event") ;
        this.xG = (double) playerData.get("expected_goals_per_90");
        this.xA = (double) playerData.get("expected_assists_per_90");
        this.cleanSheets = (double) playerData.get("clean_sheets_per_90");
        this.team = (int) playerData.get("team");
    }

    /**
     * Sets the fixtures of the player using the provided fixture data.
     *
     * @param fixtureData a map containing the fixture data.
     * The keys are the GW number and the values is a list where the first index is the opponent, and the second integer represent 1 for home and 0 for away.
     */
    private void setFixtures(Map<Integer, List<Map<String, Object>>> fixtureData) {
        Map<Integer, List<Integer>> fixtures = new HashMap<>();
        for (int gw = this.nextGW; gw < 39; gw++) {
            List<Integer> fixture = new ArrayList<>();
            List<Map<String, Object>> fixtureList = fixtureData.get(gw);
            for (int matches = 0; matches < fixtureList.size(); matches ++) {
                Map<String, Object> match = fixtureList.get(matches);
                if (match.get("HomeTeam").equals(this.team)) {
                    int awayTeam = (int) match.get("AwayTeam");
                    fixture.add(awayTeam);
                    fixture.add(1);
                } else if (match.get("AwayTeam").equals(this.team)) {
                    int homeTeam = (int) match.get("HomeTeam");
                    fixture.add(homeTeam);
                    fixture.add(0);
                }
            }
            fixtures.put(gw, fixture);
        }
        this.fixtures = fixtures;
    }

    // Getters
    public String getName() {return name;}
    public double getChanceOfPlaying() {return chanceOfPlaying;}
    public double getCostChange() {return costChange;}
    public int getPointsLastRound() {return pointsLastRound;}
    public double getPrice() {return price;}
    public String getAvgPoints() {return avgPoints;}
    public String getSelectedBy() {return selectedBy;}
    public int getTransferBalance() {return transferBalance;}
    public double getxG() {return xG;}
    public double getxA() {return xA;}
    public double getCleanSheetsPerGame() {return cleanSheets;}
    public int getTeam() {return team;}
    public Map<Integer, List<Integer>> getFixtures() {return fixtures;}

    @Override
    public int getID() {
        return this.ID;
    }

}

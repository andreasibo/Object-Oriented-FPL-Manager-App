package FPLManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Player class represents a player in the Fantasy Premier League (FPL) game.
 * It contains various attributes of the player and methods to access these attributes.
 */
public abstract class Player {
    
    private final int ID;
    private final int nextGW;
    private final String name;
    private final int chanceOfPlaying;
    private final double costChange;
    private final int pointsLastRound;
    private final double price;
    private final String avgPoints;
    private final String selectedBy;
    private final int transferBalance;
    private final double xG;
    private final double xA;
    private final double cleanSheets;
    private final int teamID;
    private final String team;
    private final Map<Integer, List<String>> fixtures;

    /**
     * Constructs a new Player object with the given attributes.
     * Handles the dummy root, where the ID is -1
     * 
     * @param ID the ID of the player
     * @param nextGW the next game week
     * @param playerData a map containing player data
     * @param fixtureData a map containing fixture data
     * @param data the DataManager instance
     */
    public Player(int ID, int nextGW, Map<String, Object> playerData, Map<Integer, List<Map<String, Object>>> fixtureData, DataManager data) {
        this.ID = ID;
        this.nextGW = nextGW;

        if (ID == -1) {
            this.name = "Root";
            this.chanceOfPlaying = 0;
            this.costChange = 0;
            this.pointsLastRound = 0;
            this.price = 0;
            this.avgPoints = "0";
            this.selectedBy = "0";
            this.transferBalance = 0;
            this.xG = 0;
            this.xA = 0;
            this.cleanSheets = 0;
            this.teamID = 0;
            this.team = "Root Team"; 
            this.fixtures = new TreeMap<>();
        } else {
            this.name = getNameFromPlayerData(playerData);
            this.chanceOfPlaying = getIntFromPlayerData(playerData, "chance_of_playing_next_round");
            this.costChange = getDoubleFromPlayerData(playerData, "cost_change_event", 10.0);
            this.pointsLastRound = getIntFromPlayerData(playerData, "event_points");
            this.price = getDoubleFromPlayerData(playerData, "now_cost", 10.0);
            this.avgPoints = getStringFromPlayerData(playerData, "points_per_game");
            this.selectedBy = getStringFromPlayerData(playerData, "selected_by_percent");
            this.transferBalance = getTransferBalance(playerData);
            this.xG = getDoubleFromPlayerData(playerData, "expected_goals_per_90", 1.0);
            this.xA = getDoubleFromPlayerData(playerData, "expected_assists_per_90", 1.0);
            this.cleanSheets = getDoubleFromPlayerData(playerData, "clean_sheets_per_90", 1.0);
            this.teamID = getIntFromPlayerData(playerData, "team");
            this.team = getTeamName(data.getTeams());
            this.fixtures = processFixtures(fixtureData, data.getTeams());
        }
    }



    /**
     * Retrieves the player's name from the player data map.
     *
     * @param playerData a map containing player data
     * @return the player's name
     */
    private String getNameFromPlayerData(Map<String, Object> playerData) {
        String firstName = (String) playerData.get("first_name");
        String secondName = (String) playerData.get("second_name");
        return (firstName != null ? firstName : "") + " " + (secondName != null ? secondName : "");
    }

    /**
     * Retrieves an integer value from the player data map.
     *
     * @param playerData a map containing player data
     * @param key the key to retrieve the value
     * @return the integer value
     */
    private int getIntFromPlayerData(Map<String, Object> playerData, String key) {
        return playerData.get(key) != null ? (int) playerData.get(key) : 0;
    }

    /**
     * Retrieves a double value from the player data map and divides it by the given divisor.
     *
     * @param playerData a map containing player data
     * @param key the key to retrieve the value
     * @param divisor the divisor to divide the value
     * @return the double value
     */
    private double getDoubleFromPlayerData(Map<String, Object> playerData, String key, double divisor) {
        return playerData.get(key) != null ? ((Number) playerData.get(key)).doubleValue() / divisor : 0.0;
    }

    /**
     * Retrieves a string value from the player data map.
     *
     * @param playerData a map containing player data
     * @param key the key to retrieve the value
     * @return the string value
     */
    private String getStringFromPlayerData(Map<String, Object> playerData, String key) {
        return playerData.get(key) != null ? (String) playerData.get(key) : "0";
    }

    /**
     * Calculates the transfer balance from the player data map.
     *
     * @param playerData a map containing player data
     * @return the transfer balance
     */
    private int getTransferBalance(Map<String, Object> playerData) {
        if (playerData.get("transfers_in_event") != null && playerData.get("transfers_out_event") != null) {
            return (int) playerData.get("transfers_in_event") - (int) playerData.get("transfers_out_event");
        }
        return 0;
    }

    /**
     * Retrieves the team name based on the team ID.
     *
     * @param teams a list of teams
     * @return the team name
     */
    private String getTeamName(ArrayList<ArrayList<String>> teams) {
        if (this.teamID == 0) {
            return "Not found";
        }
        return teams.get(this.teamID - 1).get(0);
    }

    /**
     * Processes the fixture data and maps it to the player's fixtures.
     *
     * @param fixtureData a map containing fixture data
     * @param teams a list of teams
     * @return a map of fixtures
     */
    private Map<Integer, List<String>> processFixtures(Map<Integer, List<Map<String, Object>>> fixtureData, ArrayList<ArrayList<String>> teams) {
        Map<Integer, List<String>> fixtures = new TreeMap<>();
        if (fixtureData == null) {
            return fixtures;
        }

        for (int gw = this.nextGW; gw < 39; gw++) {
            List<String> fixture = new ArrayList<>();
            List<Map<String, Object>> fixtureList = fixtureData.get(gw);
            if (fixtureList != null) {
                for (Map<String, Object> match : fixtureList) {
                    if (match != null && match.get("HomeTeam") != null && match.get("AwayTeam") != null) {
                        if (match.get("HomeTeam").equals(this.teamID)) {
                            fixture.add(teams.get((int) match.get("AwayTeam") - 1).get(1) + "(H)");
                        } else if (match.get("AwayTeam").equals(this.teamID)) {
                            fixture.add(teams.get((int) match.get("HomeTeam") - 1).get(1) + "(A)");
                        }
                    }
                }
            }
            if (!fixture.isEmpty()) {
                fixtures.put(gw, fixture);
            }
        }
        return fixtures;
    }

    /**
     * Retrieves the fixture for a specific game week.
     *
     * @param gameweek the game week
     * @return the fixture for the game week
     */
    public String getFixtureForGameweek(int gameweek) {
        if (fixtures != null && fixtures.containsKey(gameweek)) {
            return String.join(", ", fixtures.get(gameweek));
        }
        return "";
    }

    /**
     * Retrieves the player's position from the player data map.
     *
     * @param playerData a map containing player data
     * @param key        the key to retrieve the value
     * @return the player's position
     */
    public static String getPositionFromPlayerData(Map<String, Object> playerData, String key) {
        if (playerData == null || !playerData.containsKey(key)) {
            return "Unknown";
        }
        Integer pos = (Integer) playerData.get(key);
        if (pos == null) {
            return "Unknown";
        }

        switch (pos) {
            case 1:
                return "Goalkeeper";
            case 2:
                return "Defender";
            case 3:
                return "Midfielder";
            case 4:
                return "Forward";
            default:
                return "Unknown";
        }
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
    public double getXG() {return xG;}
    public double getXA() {return xA;}
    public double getCleanSheetsPerGame() {return cleanSheets;}
    public String getTeam() {return team;}
    public Map<Integer, List<String>> getFixtures() {return fixtures;}
    public int getID() { return this.ID; }


}

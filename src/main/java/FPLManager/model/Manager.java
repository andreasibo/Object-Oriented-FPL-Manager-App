package FPLManager.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Manager class represents a manager in the Fantasy Premier League (FPL) game.
 * It contains various attributes of the manager and methods to access these attributes.
 */
public class Manager {
    private final int ID;
    private final int nextGW;
    private final String teamName;
    private final ArrayList<Integer> teamPlayersID;
    private final ArrayList<Player> teamPlayers;
    private final Map<String, Object> chipsAvailable;
    private final int availableTransfers;
    private final String gwDeadline;

    /**
     * Constructs a new Manager object with the specified manager ID and next game week.
     *
     * @param managerID the unique identifier of the manager
     * @param nextGW the next game week
     */
    public Manager(int managerID) {
        this.ID = managerID;
        FPLAPI managerData = new FPLAPI(this.ID);
        DeadlineHandler deadline = new DeadlineHandler(managerData.getNextGWInfo());
        this.nextGW = managerData.getNextGW();
        this.teamName = managerData.getTeamName();
        this.teamPlayersID = managerData.getTeamPlayers();
        this.gwDeadline = deadline.getGWDeadLine();
        this.chipsAvailable = processChipsAvailable(managerData.getChips());
        this.availableTransfers = calculateAvailableTransfers(managerData.getTransferHistory());
        this.teamPlayers = createTeamPlayers(managerData.getPlayerData(), managerData.getRemainingFixtures());
    }

    /**
     * Sets the available transfers for the manager using the provided transfer history data.
     *
     * @param transferHistoryData a list containing the transfer history data
     */
    private int calculateAvailableTransfers(ArrayList<Integer> transferHistoryData) {
        int availableTransfers = 1;
        int wildcardGW = -1;

        if (this.chipsAvailable != null && this.chipsAvailable.get("wildcard") != null &&this.chipsAvailable.get("wildcard").getClass().isInstance(String.class)) {
            wildcardGW = (Integer) this.chipsAvailable.get("wildcard");
        }

        for (int currentGW = 1; currentGW <= this.nextGW; currentGW++) {
            if (availableTransfers < 5) {
                availableTransfers ++;
            }
            for (int gw : transferHistoryData) {
                if (gw == currentGW && gw != wildcardGW) {
                    availableTransfers--;
                }
            }
            if (availableTransfers < 0) {
                availableTransfers = 0;
            }
        }
        return availableTransfers;
    }

    /**
     * Sets the chips available for the manager using the provided chips data.
     *
     * @param chipsUsed a map containing the chips data
     */
    private Map<String, Object> processChipsAvailable(Map<String, Object> chipsUsed) {
        Map<String, Object> chipsAvailable = new HashMap<String, Object>();
        List<String> chips = Arrays.asList("wildcard", "3xc", "bboost", "manager", "freehit");
        for (String chip : chips) {
            if (chipsUsed != null && chipsUsed.containsKey(chip)) {
                    int usedGW = (Integer) chipsUsed.get(chip);
                    if (chip.equals("wildcard")) {
                        if (this.nextGW < 18 && usedGW < 18) {
                            chipsAvailable.put(chip, usedGW);
                        } else if (this.nextGW > 18 && usedGW > 18) {
                            chipsAvailable.put(chip, usedGW);
                        } else {
                            chipsAvailable.put(chip, "Available");
                        }  
                    } else {
                        chipsAvailable.put(chip, usedGW);
                    }
            } else {
                chipsAvailable.put(chip, "Available");
            }
        }
        return chipsAvailable;
    }

    /**
     * Sets the team players for the manager using the provided player data and fixture data.
     *
     * @param allPlayerData a map containing all player data
     * @param fixtureData a map containing the fixture data
     */
    private ArrayList<Player> createTeamPlayers(Map<Integer, Map<String, Object>> allPlayerData, Map<Integer, List<Map<String, Object>>> fixtureData) {
        ArrayList<Player> teamPlayers = new ArrayList<>();
        DataManager dataManager = new DataManager();

        for (int playerID : this.teamPlayersID) {
            Map<String, Object> playerData = allPlayerData.get(playerID);
            if (playerData != null) {
                String position = Player.getPositionFromPlayerData(playerData, "element_type");

                Player player;
                switch (position) {
                    case "Goalkeeper":
                        player = new Goalkeeper(playerID, this.nextGW, playerData, fixtureData, dataManager);
                        break;
                    case "Defender":
                        player = new Defender(playerID, this.nextGW, playerData, fixtureData, dataManager);
                        break;
                    case "Midfielder":
                        player = new Midfielder(playerID, this.nextGW, playerData, fixtureData, dataManager);
                        break;
                    case "Forward":
                        player = new Forward(playerID, this.nextGW, playerData, fixtureData, dataManager);
                        break;
                    default:
                        System.err.println("Warning: Unknown Player Position for player ID: " + playerID);
                        continue;
                }
                teamPlayers.add(player);
            }
        }
        Collections.sort(teamPlayers, new PlayerPositionComparator());
        
        return teamPlayers;
    }

    // Getters
    public String getName() { return this.teamName; }
    public ArrayList<Integer> getTeamPlayersID() { return this.teamPlayersID; }
    public int getAvailableTransfers() { return this.availableTransfers; }
    public int getNextGW() { return this.nextGW; }
    public Map<String, Object> getChipsAvailable() { return this.chipsAvailable; }
    public ArrayList<Player> getTeamPlayers() { return this.teamPlayers; }
    public String getGWDeadline() { return this.gwDeadline; }
    public int getID() { return this.ID; }
}

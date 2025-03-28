package FPLManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Manager class represents a manager in the Fantasy Premier League (FPL) game.
 * It contains various attributes of the manager and methods to access these attributes.
 */
public class Manager implements ID{
    private int ID;
    private int nextGW;
    private String teamName;
    private ArrayList<Integer> teamPlayersID;
    private ArrayList<Player> teamPlayers;
    private HashMap<String, Object> chipsAvailable;
    private int availableTransfers;
    private String gwDeadline;

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
        setChipsAvailable(managerData.getChips());
        setAvailableTransfers(managerData.getTransferHistory());
        setTeamPlayers(managerData.getPlayerData(),managerData.getRemainingFixtures());
    }

    /**
     * Sets the available transfers for the manager using the provided transfer history data.
     *
     * @param transferHistoryData a list containing the transfer history data
     */
    private void setAvailableTransfers(ArrayList<Integer> transferHistoryData) {
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
        this.availableTransfers = availableTransfers;
    }

    /**
     * Sets the chips available for the manager using the provided chips data.
     *
     * @param chipsUsed a map containing the chips data
     */
    private void setChipsAvailable(Map<String, Object> chipsUsed) {
        this.chipsAvailable = new HashMap<String, Object>();
        List<String> chips = Arrays.asList("wildcard", "3xc", "bboost", "manager", "freehit");
        for (String chip : chips) {
            if (chipsUsed != null && chipsUsed.containsKey(chip)) {
                    int usedGW = (Integer) chipsUsed.get(chip);
                    if (chip.equals("wildcard")) {
                        if (this.nextGW < 18 && usedGW < 18) {
                            this.chipsAvailable.put(chip, usedGW);
                        } else if (this.nextGW > 18 && usedGW > 18) {
                            this.chipsAvailable.put(chip, usedGW);
                        } else {
                            this.chipsAvailable.put(chip, "Available");
                        }  
                    } else {
                        this.chipsAvailable.put(chip, usedGW);
                    }
            } else {
                this.chipsAvailable.put(chip, "Available");
            }
        }
    }

    /**
     * Sets the team players for the manager using the provided player data and fixture data.
     *
     * @param allPlayerData a map containing all player data
     * @param fixtureData a map containing the fixture data
     */
    private void setTeamPlayers(Map<Integer, Map<String, Object>> allPlayerData, Map<Integer, List<Map<String, Object>>> fixtureData) {
        this.teamPlayers = new ArrayList<>();
        for (int playerID : this.teamPlayersID) {
            Map<String, Object> playerData = allPlayerData.get(playerID);
            Player player = new Player(playerID, this.nextGW, playerData, fixtureData);
            this.teamPlayers.add(player);
        }
    }

    // Getters
    public String getName() { return this.teamName; }
    public ArrayList<Integer> getTeamPlayersID() { return this.teamPlayersID; }
    public int getAvailableTransfers() { return this.availableTransfers; }
    public int getNextGW() { return this.nextGW; }
    public HashMap<String, Object> getChipsAvailable() { return this.chipsAvailable; }
    public ArrayList<Player> getTeamPlayers() { return this.teamPlayers; }
    public String getGWDeadline() { return this.gwDeadline; }

    @Override
    public int getID() {
        return this.ID;
    }
}

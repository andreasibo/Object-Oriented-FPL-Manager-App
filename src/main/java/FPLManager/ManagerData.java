package FPLManager;

import java.util.HashMap;

public class ManagerData {
    private int managerID;
    private String teamName;
    private HashMap<Player, PlayerData> teamPlayers;

    private ManagerData(int managerID) {
        this.managerID = managerID;
    }

    public int getManagerID() {
        return managerID;
    }

    public String getName() {
        return teamName;
    }

    public HashMap<Player, PlayerData> getTeam() {
        return teamPlayers;
    }

    
}

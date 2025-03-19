package FPLManager;

import java.util.ArrayList;
import java.util.Map;

public class Manager implements ID{
    private int ID;
    private int nextGW;
    private String teamName;
    private ArrayList<Integer> teamPlayers;
    private Map<String, Integer> chipsUsed;
    private ArrayList<Integer> transferHistory;
    private int availableTransfers;

    public Manager(int managerID, int nextGW) {
        this.ID = managerID;
        this.nextGW = nextGW;
        FPLAPI managerData = new FPLAPI(this.ID, this.nextGW);
        this.teamName = managerData.getTeamName();
        this.teamPlayers = managerData.getTeamPlayers();
        this.chipsUsed = managerData.getChips();
        this.transferHistory = managerData.getTransferHistory();
        setAvailableTransfers();
    }

    private void setAvailableTransfers() {
        int availableTransfers = 1;
        int wildcardGW = -1;

        if (this.chipsUsed != null && this.chipsUsed.containsKey("wildcard")) {
            wildcardGW = this.chipsUsed.get("wildcard");
        }

        for (int currentGW = 1; currentGW <= this.nextGW; currentGW++) {
            if (availableTransfers < 5) {
                availableTransfers ++;
            }
            for (int gw : this.transferHistory) {
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

    public String getName() {
        return this.teamName;
    }

    public ArrayList<Integer> getTeam() {
        return this.teamPlayers;
    }

    public int getAvailableTransfers() {
        return this.availableTransfers;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    public static void main(String[] args) {
        Manager me = new Manager(3907402, 30);
        System.out.println(me.getName());
        System.out.println(me.getTeam());
        System.out.println(me.getAvailableTransfers());
    }
    
}

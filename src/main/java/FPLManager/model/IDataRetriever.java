package FPLManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface for retrieving data related to Fantasy Premier League (FPL) management.
 */
public interface IDataRetriever {

    String getTeamName();
    ArrayList<Integer> getTeamPlayers();
    int getNextGW();
    Map<Integer, Map<String, Object>> getPlayerData();
    List<Map<String, Object>> getNextGWInfo();
    ArrayList<Integer> getTransferHistory();
    Map<String, Object> getChips();
    Map<Integer, List<Map<String, Object>>> getRemainingFixtures();
}

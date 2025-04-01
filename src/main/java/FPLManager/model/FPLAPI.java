package FPLManager.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class for interacting with the Fantasy Premier League (FPL) API.
 * This class provides methods to retrieve manager data, team information, player data, and upcoming fixtures.
 */
public class FPLAPI implements IDataRetriever{
    private int managerID;
    private String teamName;
    private ArrayList<Integer> teamPlayers;
    private int nextGW;
    private Map<Integer, Map<String, Object>> playerData;
    private List<Map<String, Object>> nextGWInfo;
    private ArrayList<Integer> transferHistory;
    private Map<String, Object> chips;
    private Map<Integer, List<Map<String, Object>>> remainingFixtures;

    /**
     * Constructs an FPLAPI object with the specified manager ID and next gameweek.
     *
     * @param managerID The manager ID.
     */
    public FPLAPI(int managerID) {
        this.managerID = managerID;
        setNextGW();
        setTeamName();
        setTeamPlayers();
        setPlayerData();
        setNextGWInfo();
        setTransferHistory();
        setChips();
        setRemainingFixtures();
    }

    /**
     * Builds the API request URL.
     *
     * @param requestString The API endpoint.
     * @return The complete API request URL.
     */
    private final String requestBuilder(String requestString) {
        return "https://fantasy.premierleague.com/api/" + requestString;
    }

    /**
     * Sends an HTTP GET request to the specified URL and returns the response.
     *
     * @param requestString The request URL.
     * @return The HTTP response, or null if an error occurs.
     */
    private HttpResponse<String> getResponse(String requestString) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestString))
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Error with HTTP request in getResponse(): " + requestString + "-" + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves JSON data from the specified API endpoint and parses it into the specified type.
     *
     * @param <T> The type to parse the JSON data into.
     * @param requestString The API endpoint.
     * @param typeReference The type reference for parsing the JSON data.
     * @return The parsed JSON data, or null if an error occurs.
     * @throws IllegalStateException If the JSON body is null or empty.
     */
    private <T> T getJsonBody(String requestString, TypeReference<T> typeReference) {
        String finalRequestString = requestBuilder(requestString);
        try {
            HttpResponse<String> response = getResponse(finalRequestString);
            String jsonBody = response.body();
            if (jsonBody == null || jsonBody.isEmpty()) {
                throw new IllegalStateException("Empty or null JSON body recieved for request: " + finalRequestString);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonBody, typeReference);
        } catch (Exception e) {
            System.err.println("Error with HTTP request in getJsonBody(): " + finalRequestString + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Sets the next gameweek from the FPL API.
     */
    private void setNextGW() {
        List<Map<String, Object>> events = getJsonBody("events/", new TypeReference<List<Map<String, Object>>>() {});
        if (events != null) {
            for (Map<String, Object> event : events) {
                if ((Boolean) event.get("is_next")) {
                    this.nextGW = (Integer) event.get("id");
                    return;
                }
            }
        }
        this.nextGW = -1;
    }

    /**
     * Sets the team name for the manager.
     *
     * @throws IllegalStateException If the team name cannot be found.
     */
    private void setTeamName() {
        Map<String, Object> map = getJsonBody("entry/" + this.managerID + "/", new TypeReference<Map<String, Object>>() {});
        Object nameObj = map.get("name");
        if (nameObj == null) {
            throw new IllegalStateException("Cant find name");
        }
        this.teamName = (String) nameObj;
    }

    /**
     * Sets the team players for the manager based on the previous gameweek's picks.
     *
     * @throws IllegalArgumentException If the 'picks' data cannot be found.
     * @throws IllegalStateException If the team data cannot be found.
     */
    private void setTeamPlayers() {
        String previousGW = Integer.toString(this.nextGW - 1);
        Map<String, Object> teamMap = getJsonBody("entry/" + this.managerID + "/event/" + previousGW + "/picks/", new TypeReference<Map<String, Object>>() {});
        if (teamMap == null) {
            throw new IllegalStateException("Cant find team");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> picks = (List<Map<String, Object>>) teamMap.get("picks");

        if (picks == null) {
            throw new IllegalArgumentException("Cannot find 'picks' data for manager ID: " + this.managerID);
        }
        this.teamPlayers = new ArrayList<>();
        for (Map<String, Object> playerPick : picks) {
            Integer playerID = (Integer) playerPick.get("element");
            this.teamPlayers.add(playerID);
        }
    }

    /**
     * Sets player data for the manager's team.
     */
    private void setPlayerData() {
        this.playerData = new HashMap<>();
        Map<String, Object> responseMap = getJsonBody("bootstrap-static/", new TypeReference<Map<String, Object>>() {});

        if (responseMap != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> elements = (List<Map<String, Object>>) responseMap.get("elements");

            if (elements != null) {
                for (Map<String, Object> player : elements) {
                    Integer playerId = (Integer) player.get("id");
                    if (this.teamPlayers.contains(playerId)) {
                        this.playerData.put(playerId, player);
                    }
                }
            }
        }
    }

    /**
     * Sets information about the upcoming gameweek's fixtures.
     */
    public void setNextGWInfo() {
        List<Map<String, Object>> responseList = getJsonBody("fixtures/?event=" + this.nextGW, new TypeReference<List<Map<String, Object>>>() {});

        if (responseList == null) {
            System.err.println("Could not find upcoming fixtures for: " + this.nextGW);
        }
        this.nextGWInfo = responseList;
    }


    /**
     * Sets the transfer history for the manager.
     */
    public void setTransferHistory() {
        this.transferHistory = new ArrayList<>();
        List<Map<String, Object>> transferMap = getJsonBody("entry/" + this.managerID + "/transfers/", new TypeReference<List<Map<String, Object>>>() {});
        
        if (transferMap != null) {
            for (Map<String, Object> transfer : transferMap) {
                Integer eventID = (Integer) transfer.get("event");
                this.transferHistory.add(eventID);
            }
        }
    }

    /**
     * Sets the chips used by the manager.
     */
    public void setChips() {
        this.chips = new HashMap<>();
        Map<String, Object> responseMap = getJsonBody("entry/" + this.managerID + "/history/", new TypeReference<Map<String, Object>>() {});

        if (responseMap != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> chipsArray = (List<Map<String, Object>>) responseMap.get("chips");

            if (chipsArray != null) {
                for (Map<String, Object> chip : chipsArray) {
                    String chipName = (String) chip.get("name");
                    Integer gw = (Integer) chip.get("event");

                    if (chipName != null && gw != null) {
                        this.chips.put(chipName, gw);
                    }
                }
            }
        }
    }

    /**
     * Constructs the remaining fixture list for the season.
     *
     * @return A map where the key is the Gameweek number, and the values are a list of home and away team,
     */
    private void setRemainingFixtures() {
        List<Map<String, Object>> allFixtures = getJsonBody("fixtures/", new TypeReference<List<Map<String, Object>>>() {});
        Map<Integer, List<Map<String, Object>>> remainingFixtures = new HashMap<>();
        if (allFixtures != null) {
            for (Map<String, Object> fixture : allFixtures) {
                int gameweek = (fixture.get("event") != null) ? (Integer) fixture.get("event") : -1;
                int homeTeamId = (fixture.get("team_h") != null) ? (Integer) fixture.get("team_h") : -1;
                int awayTeamId = (fixture.get("team_a") != null) ? (Integer) fixture.get("team_a") : -1; 
    
                if (gameweek >= this.nextGW) {
                    Map<String, Object> fixtureData = new HashMap<>();
                    fixtureData.put("HomeTeam", homeTeamId);
                    fixtureData.put("AwayTeam", awayTeamId);
    
                    remainingFixtures.computeIfAbsent(gameweek, k -> new ArrayList<>()).add(fixtureData);
                }
            }
        }
        this.remainingFixtures = remainingFixtures;
    }


    // Getters
    public int getManagerID() { return managerID; }
    @Override
    public String getTeamName() { return teamName; }
    @Override
    public ArrayList<Integer> getTeamPlayers() { return teamPlayers; }
    @Override
    public int getNextGW() { return nextGW; }
    @Override
    public Map<Integer, Map<String, Object>> getPlayerData() { return playerData; }
    @Override
    public List<Map<String, Object>> getNextGWInfo() { return nextGWInfo; }
    @Override
    public ArrayList<Integer> getTransferHistory() { return transferHistory; }
    @Override
    public Map<String, Object> getChips() { return chips; }
    @Override
    public Map<Integer, List<Map<String, Object>>> getRemainingFixtures() {return remainingFixtures; }

}

package FPLManager;

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

public class FPLAPI {
    private int managerID;
    private String teamName;
    private ArrayList<Integer> teamPlayers;

    private FPLAPI(int managerID) {
        this.managerID = managerID;
    }

    private Map<String, Object> requestBuilder(String requestString) {
        String finalRequestString = "https://fantasy.premierleague.com/api/" + requestString;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(finalRequestString))
                                .GET()
                                .header(Integer.toString(managerID), "application/json")
                                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonBody = response.body();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Object> map = objectMapper.readValue(jsonBody, new TypeReference<HashMap<String, Object>>() {});
                return map;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setTeamName() {
        Map<String, Object> map = this.requestBuilder("entry/" + this.managerID + "/");
        Object nameObj = map.get("name");
        if (nameObj == null) {
            throw new IllegalArgumentException("Cant find name");
        }
        this.teamName = (String) nameObj;
    }

    private void setTeamPlayers() {
        Map<String, Object> teamMap = this.requestBuilder("entry/" + this.managerID + "/" + "event/28/picks/");
        if (teamMap == null) {
            throw new IllegalArgumentException("Cant find team");
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
        System.out.println(this.teamPlayers);
    }

    public static void main(String[] args) {
        FPLAPI meg = new FPLAPI(3907402);
        meg.setTeamName();
        System.out.println(meg.teamName);
        meg.setTeamPlayers();
    }
}

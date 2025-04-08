package FPLManager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    
    private DataManager testDataManager;
    private Map<String, Object> testPlayerData;
    private Map<Integer, List<Map<String, Object>>> testFixtureData;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testDataManager = new DataManager() {
            @Override
            public ArrayList<ArrayList<String>> getTeams() {
                ArrayList<ArrayList<String>> teams = new ArrayList<>();
                teams.add(new ArrayList<>(List.of("Test Team", "TT" )));
                teams.add(new ArrayList<>(List.of("Test Team", "TT" )));
                return teams;
            }
        };

        testPlayerData = new HashMap<>();
        testFixtureData = new TreeMap<>();

        testPlayerData.put("first_name", "John");
        testPlayerData.put("second_name", "Doe");
        testPlayerData.put("chance_of_playing_next_round", 75);
        testPlayerData.put("cost_change_event", 5);
        testPlayerData.put("event_points", 10);
        testPlayerData.put("now_cost", 80);
        testPlayerData.put("points_per_game", "5.5");
        testPlayerData.put("selected_by_percent", "10.0");
        testPlayerData.put("transfers_in_event", 100);
        testPlayerData.put("transfers_out_event", 50);
        testPlayerData.put("expected_goals_per_90", 0.5);
        testPlayerData.put("expected_assists_per_90", 0.3);
        testPlayerData.put("clean_sheets_per_90", 0.2);
        testPlayerData.put("team", 1);

        List<Map<String, Object>> fixturesGW38 = new ArrayList<>();
        Map<String, Object> fixture1 = new HashMap<>();
        fixture1.put("HomeTeam", 1);
        fixture1.put("AwayTeam", 2);
        fixturesGW38.add(fixture1);
        testFixtureData.put(38, fixturesGW38);

        testPlayer = new Defender(1, 37, testPlayerData, testFixtureData, testDataManager) {
        };
    }

    @Test
    void testPlayerConstructor() {
        assertNotNull(testPlayer);
        assertEquals("John Doe", testPlayer.getName());
        assertEquals(75, testPlayer.getChanceOfPlaying());
        assertEquals(0.5, testPlayer.getCostChange());
        assertEquals(10, testPlayer.getPointsLastRound());
        assertEquals(8.0, testPlayer.getPrice());
        assertEquals("5.5", testPlayer.getAvgPoints());
        assertEquals("10.0", testPlayer.getSelectedBy());
        assertEquals(50, testPlayer.getTransferBalance());
        assertEquals(0.5, testPlayer.getXG());
        assertEquals(0.3, testPlayer.getXA());
        assertEquals(0.2, testPlayer.getCleanSheetsPerGame());
        assertEquals("Test Team", testPlayer.getTeam());
        assertNotNull(testPlayer.getFixtures());
    }

    @Test
    void testGetFixtureForGameweek() {
        assertEquals("TT(H)", testPlayer.getFixtureForGameweek(38));
        assertEquals("", testPlayer.getFixtureForGameweek(37));
    }

    @Test
    void testGetPositionFromPlayerData() {
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("element_type", 1);
        assertEquals("Goalkeeper", Player.getPositionFromPlayerData(playerData, "element_type"));

        playerData.put("element_type", 2);
        assertEquals("Defender", Player.getPositionFromPlayerData(playerData, "element_type"));

        playerData.put("element_type", 3);
        assertEquals("Midfielder", Player.getPositionFromPlayerData(playerData, "element_type"));

        playerData.put("element_type", 4);
        assertEquals("Forward", Player.getPositionFromPlayerData(playerData, "element_type"));

        playerData.put("element_type", 5);
        assertEquals("Unknown", Player.getPositionFromPlayerData(playerData, "element_type"));

        assertEquals("Unknown", Player.getPositionFromPlayerData(new HashMap<>(), "element_type"));
    }

    @Test
    void testDummyRootPlayer() {
        Player rootPlayer = new Player(-1, 0, new HashMap<>(), new TreeMap<>(), testDataManager) {
        };
        assertEquals("Root", rootPlayer.getName());
        assertEquals(0, rootPlayer.getChanceOfPlaying());
    }
}

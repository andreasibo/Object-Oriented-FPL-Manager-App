package FPLManager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataManagerTest {
    private DataManager dataManager;

    @BeforeEach
    void setUp() {
        dataManager = new DataManager();
    }

    @Test
    void testLoadTeams() {
        ArrayList<ArrayList<String>> teams = dataManager.getTeams();
        assertNotNull(teams);
        assertFalse(teams.isEmpty());
        assertTrue(teams.stream().anyMatch(team -> team.get(0).equals("Arsenal")));
    }

    @Test
    void testAddUser() {
        String teamName = "TestTeam";
        int teamId = 123;
        dataManager.addUser(teamName, teamId);

        Path usersFilePath = Paths.get("src/main/resources/FPLManager/data/users.txt");
        try {
            String fileContent = new String(Files.readAllBytes(usersFilePath));
            assertTrue(fileContent.contains(teamName + "," + teamId));
        } catch (IOException e) {
            fail("Could not read users.txt: " + e.getMessage());
        }
    }

    @Test
    void testFindUser() {
        String teamName = "TestTeam";
        int teamId = 123;
        dataManager.addUser(teamName, teamId);

        int foundId = dataManager.findUser(teamName);
        assertEquals(teamId, foundId);

        int notFoundId = dataManager.findUser("NonExistentTeam");
        assertEquals(-1, notFoundId);
    }

    @Test
    void testGetTeams() {
        ArrayList<ArrayList<String>> teams = dataManager.getTeams();
        assertNotNull(teams);
    }
}

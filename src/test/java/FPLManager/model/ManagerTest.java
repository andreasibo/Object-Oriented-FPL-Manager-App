package FPLManager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ManagerTest {
    
    private Manager manager;

    @BeforeEach
    void setUp() {
        manager = new Manager(3907402);
    }

    @Test
    void testManagerConstructor() {
        assertNotNull(manager);
        assertEquals(manager.getID(), 3907402); 
        assertEquals(manager.getName(), "Aibo FC"); 
        assertNotNull(manager.getTeamPlayersID()); 
        assertNotNull(manager.getChipsAvailable()); 
        assertTrue(manager.getAvailableTransfers() >= 0); 
        assertNotNull(manager.getGWDeadline()); 
        assertNotNull(manager.getTeamPlayers()); 
    }
}

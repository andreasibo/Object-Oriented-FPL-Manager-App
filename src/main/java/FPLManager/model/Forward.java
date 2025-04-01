package FPLManager.model;

import java.util.List;
import java.util.Map;

public class Forward extends Player{

    public Forward(int ID, int nextGW, Map<String, Object> playerData, Map<Integer, List<Map<String, Object>>> fixtureData, DataManager data) {
        super(ID, nextGW, playerData, fixtureData, data);
    }
    
}

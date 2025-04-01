package FPLManager.model;

import java.util.Comparator;

/**
 * Comparator for comparing players based on their positions.
 */
public class PlayerPositionComparator implements Comparator<Player> {

    /**
     * Compares two players based on their positions.
     *
     * @param player1 the first player to be compared.
     * @param player2 the second player to be compared.
     * @return a negative integer, zero, or a positive integer as the first player is less than, equal to, or greater than the second player.
     */
    @Override
    public int compare(Player player1, Player player2) {
        String position1 = player1.getClass().getSimpleName();
        String position2 = player2.getClass().getSimpleName();

        int order1 = getPositionOrder(position1);
        int order2 = getPositionOrder(position2);

        return Integer.compare(order1, order2);
    }

    /**
     * Retrieves the order value for a given position.
     *
     * @param position the position of the player.
     * @return the order value corresponding to the position.
     */
    private int getPositionOrder(String position) {
        switch (position) {
            case "Goalkeeper":
                return 1;
            case "Defender":
                return 2;
            case "Midfielder":
                return 3;
            case "Forward":
                return 4;
            default:
                return 5;
        }
    }
}


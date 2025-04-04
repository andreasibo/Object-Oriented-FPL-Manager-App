package FPLManager.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The DataManager class handles the management of team data and user data for the Fantasy Premier League (FPL) game.
 */
public class DataManager {
    private final ArrayList<ArrayList<String>> teams;

    /**
     * Constructs a new DataManager object and initializes the teams.
     */
    public DataManager() {
        this.teams = loadTeams();
    }

    /**
     * Sets the teams by reading from the 'teams.csv' file.
     * 
     * @throws IllegalStateException if file not found
     */
    private ArrayList<ArrayList<String>> loadTeams() {
        ArrayList<ArrayList<String>> teams = new ArrayList<>();
        try (InputStream inputStream = getClass().getResourceAsStream("/FPLManager/data/teams.csv");
        Scanner scanner = new Scanner(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (inputStream == null) {
                throw new IllegalStateException("Error: 'teams.csv' resource not found");
            }

            boolean firstLine = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(";");
                ArrayList<String> team =  new ArrayList<>();
                if (parts.length == 3) {
                    String teamName = parts[1].trim();
                    String teamShort = parts[2].trim();
                    team.add(teamName);
                    team.add(teamShort);
                    teams.add(team);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: file 'teams.csv' could not be opened. Does it exist?");
        }
        return teams;
    }

    /**
     * Adds a user to the 'users.txt' file.
     *
     * @param teamName the name of the team
     * @param teamID the ID of the team
     */
    public void addUser(String teamName, int teamID) {
        Path currentPath = Paths.get(".");
        Path filePath = currentPath.resolve("src/main/resources/FPLManager/data").resolve("users.txt").normalize();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toString(), true))) {

            writer.println(teamName + "," + teamID);

        } catch (IOException e) {
            System.err.println("Error: file 'user.txt' could not be opened. Does it exist?");
        }
    }

    /**
     * Finds a user by team name in the 'users.txt' file.
     *
     * @param teamName the name of the team
     * @return the ID of the team, or 0 if not found
     */
    public int findUser(String teamName) {
        try (InputStream inputStream = getClass().getResourceAsStream("/FPLManager/data/users.txt");
             Scanner scanner = new Scanner(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            if (inputStream == null) {
                throw new IllegalStateException("Error: 'users.txt' resource not found");
            }

            boolean firstLine = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String teamNameFile = parts[0].trim();
                    int teamIDFile = Integer.parseInt(parts[1].trim());
                    if (teamNameFile.equals(teamName)) {
                        return teamIDFile;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error: file 'users.txt' could not be opened. Does it exist?");
        }
        return -1;
    }

    // Getter
    public ArrayList<ArrayList<String>> getTeams() { return this.teams; }

}

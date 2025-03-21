package FPLManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The DataManager class handles the management of team data and user data for the Fantasy Premier League (FPL) game.
 */
public class DataManager {
    private Collection<String> teams;

     /**
     * Constructs a new DataManager object and initializes the teams.
     */
    public DataManager() {
        setTeams();
    }

    /**
     * Sets the teams by reading from the 'teams.csv' file.
     */
    private void setTeams() {
        this.teams = new ArrayList<>();
        try (InputStream inputStream = getClass().getResourceAsStream("/FPLManager/teams.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (inputStream == null) {
                throw new IllegalStateException("Error: 'teams.csv' resource not found");
            }
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String teamName = parts[1].trim();
                    this.teams.add(teamName);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: file 'teams.csv' could not be opened. Does it exist?");
        }
    }

    /**
     * Adds a user to the 'users.txt' file.
     *
     * @param teamName the name of the team
     * @param teamID the ID of the team
     */
    public void addUser(String teamName, int teamID) {
        Path currentPath = Paths.get(".");
        Path filePath = currentPath.resolve("src/main/resources/FPLManager/").resolve("users.txt").normalize();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString(), true))) {

            writer.write(teamName + "," + teamID);
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Error: file 'teams.csv' could not be opened. Does it exist?");
        }
    }

    /**
     * Finds a user by team name in the 'users.txt' file.
     *
     * @param teamName the name of the team
     * @return the ID of the team, or 0 if not found
     */
    public int findUser(String teamName) {
        try (InputStream inputStream = getClass().getResourceAsStream("/FPLManager/users.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            if (inputStream == null) {
                throw new IllegalStateException("Error: 'users.txt' resource not found");
            }

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
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
        return 0;
    }

    // Getter
    public Collection<String> getTeams() { return this.teams; }

}

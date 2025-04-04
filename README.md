# FPL Manager Application

This is a JavaFX application designed to help Fantasy Premier League (FPL) managers analyze player data and manage their teams.

## Features

* **Team Data Retrieval:** Fetches and displays FPL team data, including player statistics.
* **Player Analysis:** Provides key player metrics such as points, cost, expected goals (xG), and expected assists (xA).
* **Game Week Information:** Displays upcoming game week fixtures and deadlines.
* **Chip Management:** Tracks and displays available and used FPL chips (e.g., Wildcard, Triple Captain).
* **User Management:** Allows users to save and retrieve their team data.
* **Data Persistence:** Saves user data to a local file.
* **Data Retrieval:** Retrieves data from the official FPL API.
* **Fixture data:** Displays the upcoming fixtures for each player.

## Technologies Used

* **JavaFX:** For building the graphical user interface.
* **Java:** The primary programming language.
* **Maven:** For build automation and dependency management.
* **JUnit 5:** For unit testing.
* **Jackson:** For JSON processing.

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 21 or later
* Maven

### Installation

1.  **Clone the repository:**

    ```bash
    git clone [repository URL]
    cd [repository directory]
    ```

2.  **Build the project using Maven:**

    ```bash
    mvn clean install
    ```

3.  **Run the application:**

    ```bash
    mvn javafx:run
    ```

### Running Tests

To run the JUnit 5 tests, execute the following Maven command:

```bash
mvn test
```
### Project Structure
```bash
FPLManager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── FPLManager/
│   │   │   │   ├── controller/
│   │   │   │   │   ├── FPLManagerApp.java
│   │   │   │   │   └── FPLManagerController.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Manager.java
│   │   │   │   │   ├── Player.java
│   │   │   │   │   ├── DataManager.java
│   │   │   │   │   └── ... (other model classes)
│   │   ├── resources/
│   │   │   ├── FPLManager/
│   │   │   │   ├── data/
│   │   │   │   │   ├── teams.csv
│   │   │   │   │   └── users.txt
│   │   │   │   ├── documentation/
│   │   │   │   │   ├── class_diagram.png
│   │   │   │   │   ├── class_diagram.puml
│   │   │   │   │   └── Dokumentasjon.pdf
│   │   │   │   ├──  model/
│   │   │   │   │   └── App.fxml
│   ├── test/
│   │   ├── java/
│   │   │   ├── FPLManager/model/
│   │   │   │   └── DataManagerTest.java
│   │   │   │   └── PlayerTest.java
│   │   │   │   └── ManagerTest.java
├── pom.xml
└── README.md
```

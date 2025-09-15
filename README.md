# Battleship (Java GUI)

A complete graphical implementation of the classic strategy game Battleship, built with Java and Swing. This project features a full user interface for placing ships and tracking attacks against a computer opponent, showcasing core Java programming and GUI development skills.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-6DB33F?style=for-the-badge&logo=java&logoColor=white)

## 🎮 Features

-   **Graphical User Interface (GUI):** Built with Java Swing for an intuitive and interactive experience.
-   **Classic Battleship Rules:** Sink all of your opponent's ships before they sink yours.
-   **Ship Placement:** click and drop your fleet on your board before battle begins.
-   **Visual Feedback:** Boards clearly show hits, misses, and the location of sunk ships.
-   **Game State Management:** Tracks turns, scores, and win conditions seamlessly.
-   **Play with local wifi:** Using socket play with another player in the same network.

## 🛠️ Tech Stack

-   **Language:** Java (JDK 11+)
-   **GUI Framework:** Swing (AWT)
-   **Build Tool:** Standard Java Compiler (`javac`)
-   **IDE:** Any Java-compatible IDE (IntelliJ IDEA, Eclipse, VSCode) recommended.

## 📦 Installation & Run

### Prerequisites
To run this game, you only need to have **Java** installed on your machine.
-   **Download Java:** Ensure you have the [Java JDK](https://www.oracle.com/java/technologies/downloads/) (version 11 or above) installed.
-   **Check Installation:** You can verify by opening a terminal/command prompt and typing:
    ```bash
    java -version
    javac -version
    ```

### Running the Game (Two Methods)

#### Method 1: Using an IDE (Recommended for Development)
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Mahdiyar-Monemian/java-battleship-gui.git
    cd java-battleship-gui
    ```
2.  **Open the project** in your favorite Java IDE (e.g., IntelliJ IDEA, Eclipse).
3.  **Locate the app class:** The entry point is in a file called App.java
4.  **Run the project** from within the IDE by clicking the "Run" button.

#### Method 2: From the Command Line
1.  **Clone the repository** (as shown above) and navigate into it.
2.  **Compile the Java source files:**
    ```bash
    javac -d out/src/**/*.java
    ```
    *(Note: The exact path to source files may vary. If this doesn't work, check the project structure below).*
3.  **Run the compiled game:**
    ```bash
    java -cp out App
    ```

## 🕹️ How to Play

1.  **Setup:** The game will start by asking you to place your fleet (Carrier, Battleship, Cruiser, Submarine, Destroyer) on your grid.
2.  **Place Ships:** Click on a ship and then click on your grid to place it. You can rotate the ship using a button.
3.  **Begin Battle:** After placing all your ships, the game begins. You and the other player take turns.
4.  **Take Your Turn:** Click on a cell in the opponent's (right) grid to launch an attack onthat coordinate.
5.  **Feedback:** The grid will show:
    -   💧 **White (Miss):** Your shot hit empty water.
    -   🔴 **Red (Hit):** Your shot struck an enemy ship!
6.  **Win the Game:** Sink all of the computer's ships before it sinks yours!

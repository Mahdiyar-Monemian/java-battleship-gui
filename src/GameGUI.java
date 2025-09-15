import java.awt.Color;
import java.awt.Font;
// import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
// import javax.swing.border.Border;

public class GameGUI extends JFrame implements ActionListener{

    // Global UI elements
    JPanel mapPanel;
    JPanel enemyMapPanel;
    JPanel endGamePanel;
    JPanel leftPanel;
    JButton[][] mapButtons;
    JButton[][] enemyMapButtons;
    JButton[] shipButtons;
    JButton selectedButton;
    JButton doneButton;
    JButton orientationButton;
    JButton mapResetButton;
    JLabel backgroundLable;
    JLabel turnLable;

    // Setting up images
    ImageIcon angryFacePng = new ImageIcon("icon.png");
    ImageIcon waterPng = new ImageIcon("water.png");
    ImageIcon shipPng = new ImageIcon("ship.png");
    ImageIcon brokenShipPng = new ImageIcon("brokenship.png");
    ImageIcon missPng = new ImageIcon("miss.png");
    ImageIcon wifiPng = new ImageIcon("wifi.png");
    ImageIcon gameIconPng = new ImageIcon("gameIcon.png");
    ImageIcon losepanelPng = new ImageIcon("lostpanel.png");
    ImageIcon winpanelPng = new ImageIcon("winpanel.png");
    ImageIcon mapResetButtonPng = new ImageIcon("mapresetbutton.png");
    ImageIcon orientationButtonVPng = new ImageIcon("oriantationbuttonV.png");
    ImageIcon orientationButtonHPng = new ImageIcon("oriantationbuttonH.png");
    ImageIcon donebuttonEnablePng = new ImageIcon("donebuttonenable.png");
    ImageIcon donebuttonDisablePng = new ImageIcon("donebuttondisable.png");
    ImageIcon gameBackground1Png = new ImageIcon("gamebackground1.png");
    ImageIcon gameBackground2Png = new ImageIcon("gamebackground2.png");
    ImageIcon turnLableEnablePng = new ImageIcon("turnlableenable.png");
    ImageIcon turnLableDisablePng = new ImageIcon("turnlabledisable.png");

    // Global variables
    boolean placingShips = false;
    boolean attacking = false;
    boolean myTurn = false;
    int shipLength;
    Orientation orientation;

    void SetMyTurn(boolean b){
        myTurn = b;
        if(myTurn)
            turnLable.setIcon(turnLableEnablePng);
        else
            turnLable.setIcon(turnLableDisablePng);
    }
    
    // Diables a single tile in GUI map (used only for the enemy GUI map)
    void disableTileButtonEnemyMap(int x, int y){
        enemyMapButtons[y][x].setEnabled(false);
    }

    // Set a single button image Icon
    void setTileButtonIcon(int x, int y, ImageIcon img, JButton[][] map){
        map[y][x].setIcon(img);
        map[y][x].setDisabledIcon(img);
    }
    
    // Chnage the Icon of a tile with a State
    void setTileButton(int x, int y, State s){
        if(s == State.water)
            setTileButtonIcon(x, y, waterPng, mapButtons);
        else if(s == State.ship)
            setTileButtonIcon(x, y, shipPng, mapButtons);
        else if(s == State.miss)
            setTileButtonIcon(x, y, missPng, mapButtons);
        else if(s == State.brokenShip)
            setTileButtonIcon(x, y, brokenShipPng, mapButtons);
        
    }

    // Set Activation for all of the buttons in the map
    void SetEnableMap(JButton[][] map, boolean active){
        for(JButton[] row: map)
            for(JButton tile: row)
                if(tile.getIcon() == missPng || tile.getIcon() == brokenShipPng)
                    tile.setEnabled(false); // We never need to click on a broken ship or a miss tile
                else
                    tile.setEnabled(active);
    }

    // Generates a GUI map with a 2D array out of JButton and put them all in a JPanel
    void GenerateMap(JButton[][] mapButtons, JPanel mapPanel){
        mapPanel.setLayout(new GridLayout(10, 10, 0, 0)); // A 10x10 GridLayout
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                JButton b = new JButton(); // Create new button
                b.addActionListener(this); // Set its action listener
                mapPanel.add(b); // Add the button the the pannel
                mapButtons[i][j] = b; // Add the button the the array
            }
        }
    }

    // Set selected ship button
    void selectShip(int l, JButton self){
        placingShips = true;
        SetEnableMap(mapButtons, true); // Activates the map so we can place ships
        shipLength = l; // Length of the ship
        self.setEnabled(false); // Disables itself so we know we selected this

        // Diables the privious selected button
        if(selectedButton != null){
            selectedButton.setEnabled(true);
        }

        selectedButton = self; // Now we are the selected button
    }

    // If we placed all the ships then all of the buttons should be disabled
    boolean isPlacedAllShips(){
        for(JButton b: shipButtons)
            if(b.isEnabled())
                return false;
        return true;
    }

    // Find the cordinates of a pressed button
    Cordinate findCordinates(JButton[][] map, ActionEvent e){
        for(int i = 0; i < 10; i++)
            for(int j = 0; j < 10; j++)
                if(e.getSource() == map[i][j])
                    return new Cordinate(j, i);
        System.out.println("WE DIDNT FOUND");
        return null;
    }

    // In enemy turn, waits for enemy hit request and sends the results of the hit
    void WaitForOtherPlayer(){
        Thread t1 = new Thread(){
            public void run(){
                // Split the input into two number, then convert thoes two from string to int and sends them to App.map
                String[] cordinates = App.online.waitForOtherPlayer().split(" ", 0); //
                boolean hurt = App.map.Hit(Integer.parseInt(cordinates[0]) , Integer.parseInt(cordinates[1]));

                updateMapGUI(); // Update our own GUI map
                App.online.AnswerToPlayer(hurt); // Sends the resualts to the enemy

                if(hurt){
                    // Check to see if when we got hit, the App.map detacted that we are lost or not
                    if(App.lost){
                        GameOver(false);
                        return;
                    }

                    // If they hit a ship, they get rewarded by and extra turn
                    SetMyTurn(false);
                    WaitForOtherPlayer();
                }else{
                    // If they miss, then it's our turn
                    SetMyTurn(true);
                    SetEnableMap(enemyMapButtons, true);
                }
            }
        };
        t1.start();
    }

    // Shows wait penel until we connect in a Thread
    void WaitUntilConnection(JPanel waitJPanel){
        System.out.println("waiting for connection");
        Thread t1 = new Thread(){
            public void run(){

                // Every 0.1 seconds checks for connection, if connected breakes the loop
                while(!App.online.connected){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                waitJPanel.setVisible(false); // Hides connection panel

                // Starts the game, always client has the first turn
                if(App.online.isServer){
                    WaitForOtherPlayer();
                }
                else{
                    SetMyTurn(true);
                    SetEnableMap(enemyMapButtons, true);
                }
            }
        };
        t1.start();
    }

    // Update our map so it be a match with App.map
    void updateMapGUI(){
        for(int i = 0; i < 10; i++)
            for(int j = 0; j < 10; j++)
                setTileButton(i, j, App.map.GetTile(i, j));
    }

    // Game over
    void GameOver(boolean won){
        App.online.Disconnect();
        SetEnableMap(enemyMapButtons, false);
        mapPanel.setVisible(false);
        enemyMapPanel.setVisible(false);
        JLabel backgroundImage = new JLabel();
        backgroundImage.setBounds(0,0, 1280, 720);
        if(won){
            backgroundImage.setIcon(winpanelPng);
        }else{
            backgroundImage.setIcon(losepanelPng);
        }
        endGamePanel.add(backgroundImage);
        endGamePanel.setVisible(true);
    }

    void resetEnemyMapButtons(){
        for(JButton[] row: enemyMapButtons){
            for(JButton b: row){
                b.setIcon(waterPng);
                b.setDisabledIcon(waterPng);
            }
        }
    }

    public void Restart(){
        attacking = false;
        placingShips = false;
        SetMyTurn(false);
        SetEnableMap(mapButtons, false);
        resetEnemyMapButtons();
        enemyMapPanel.setVisible(false);
        turnLable.setVisible(false);
        doneButton.setEnabled(false);
        mapPanel.setVisible(true);
        doneButton.setVisible(true);
        orientationButton.setVisible(true);
        mapResetButton.setVisible(true);
        backgroundLable.setIcon(gameBackground1Png);
        for(JButton b: shipButtons)
            b.setEnabled(true);
        leftPanel.setVisible(true);
        updateMapGUI();
        endGamePanel.setVisible(false);
    }

    GameGUI(){
        // Setting default values
        final int frameWidth = 1280, frameHeight = 720, mapWidth = 450, frameTopHeight = 31;
        orientation = Orientation.vertical;

        // SETTING UP GUI

        // Map Panel
        mapPanel = new JPanel();
        mapPanel.setBounds(200, (frameHeight - mapWidth - frameTopHeight) / 2, mapWidth, mapWidth);
        mapPanel.setBackground(Color.darkGray);
        mapButtons = new JButton[10][10];
        GenerateMap(mapButtons, mapPanel);
        SetEnableMap(mapButtons, false);

        // Enemy Map Panel
        enemyMapPanel = new JPanel();
        enemyMapPanel.setBounds(750, (frameHeight - mapWidth - frameTopHeight) / 2, mapWidth, mapWidth);
        enemyMapPanel.setBackground(Color.darkGray);
        enemyMapButtons = new JButton[10][10];
        GenerateMap(enemyMapButtons, enemyMapPanel);
        resetEnemyMapButtons();
        SetEnableMap(enemyMapButtons, false);
                
        // End Game Panel
        endGamePanel = new JPanel();
        endGamePanel.setBounds(0,0,frameWidth,frameHeight);;
        endGamePanel.setLayout(null);
        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(500, 450, 250, 150);
        restartButton.addActionListener(e -> {
            App.Restart();
        });
        endGamePanel.add(restartButton);

        // Left Panel
        leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setBounds(20, 5, 150, 720);
        leftPanel.setLayout(new GridLayout(6, 1,0,5));
        shipButtons = new JButton[5];
        shipButtons[0] = new JButton();
        shipButtons[1] = new JButton();
        shipButtons[2] = new JButton();
        shipButtons[3] = new JButton();
        shipButtons[4] = new JButton();
        shipButtons[0].addActionListener(e -> selectShip(5, shipButtons[0]));
        shipButtons[1].addActionListener(e -> selectShip(4, shipButtons[1]));
        shipButtons[2].addActionListener(e -> selectShip(3, shipButtons[2]));
        shipButtons[3].addActionListener(e -> selectShip(3, shipButtons[3]));
        shipButtons[4].addActionListener(e -> selectShip(2, shipButtons[4]));
        for(int i = 0; i < 5; i++)
            shipButtons[i].setIcon(new ImageIcon("shipbutton" + i + ".png"));
        for(JButton b: shipButtons){
            // b.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));;
            b.setBorderPainted(false);
            leftPanel.add(b);
        }
        
        // background lable
        backgroundLable = new JLabel();
        backgroundLable.setBounds(0,0,1280, 720 - 37);
        backgroundLable.setIcon(gameBackground1Png);
        
        // Orentation Button
        orientationButton = new JButton();
        orientationButton.setIcon(orientationButtonVPng);
        orientationButton.setBounds(192, 585, 450, 90);
        orientationButton.addActionListener(e -> {
            if(orientation == Orientation.vertical){
                orientationButton.setIcon(orientationButtonHPng);;
                orientation = Orientation.horizontal;
            }else{
                orientationButton.setIcon(orientationButtonVPng);
                orientation = Orientation.vertical;    
            }
        });

        // Map Reset button
        mapResetButton = new JButton();
        mapResetButton.setBounds(192, 14, 95, 95);
        mapResetButton.setIcon(mapResetButtonPng);
        mapResetButton.addActionListener(e ->{
            App.Restart();
        });

        // waiting for connection panel
        JPanel waitJPanel = new JPanel();
        waitJPanel.setOpaque(false);
        waitJPanel.setBounds(0,0,frameWidth,frameHeight - 37);
        JLabel wifiLable = new JLabel();
        wifiLable.setVerticalAlignment(JLabel.TOP);
        wifiLable.setIcon(wifiPng);
        waitJPanel.add(wifiLable);

        // Done Button
        doneButton = new JButton();
        doneButton.setIcon(donebuttonEnablePng);
        doneButton.setDisabledIcon(donebuttonDisablePng);
        doneButton.setBounds(750, (frameHeight - mapWidth - frameTopHeight) / 2, 450, 450);
        doneButton.addActionListener(e -> {
                leftPanel.setVisible(false);
                doneButton.setVisible(false);
                waitJPanel.setVisible(true);
                orientationButton.setVisible(false);
                mapResetButton.setVisible(false);
                enemyMapPanel.setVisible(true);
                backgroundLable.setIcon(gameBackground2Png);
                turnLable.setVisible(true);
                attacking = true;
                SetEnableMap(mapButtons, false);
                App.online.Connect();
                WaitUntilConnection(waitJPanel);
        });
        doneButton.setEnabled(false);

        // Turn Lable
        turnLable = new JLabel();
        turnLable.setBounds(750, 594, 450, 60);
        turnLable.setVisible(false);
        turnLable.setIcon(turnLableDisablePng);

        // Main Frame
        this.add(endGamePanel);
        this.add(waitJPanel);
        this.add(mapPanel);
        this.add(enemyMapPanel);
        this.add(leftPanel);
        this.add(orientationButton);
        this.add(doneButton);
        this.add(mapResetButton);
        this.add(turnLable);
        this.add(backgroundLable);
        waitJPanel.setVisible(false);
        endGamePanel.setVisible(false);
        enemyMapPanel.setVisible(false);
        this.setTitle("My Game");
        this.setSize(frameWidth, frameHeight);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(gameIconPng.getImage());
        this.setVisible(true);

    }

    // Map buttons action listener
    @Override
    public void actionPerformed(ActionEvent e) {
        // Are we Attacking or Placing ships?
        if(attacking){
            // If it's not our turn then there is bug the buttons should be disabled
            if(!myTurn)
                return;

            Cordinate c = findCordinates(enemyMapButtons, e); // Find cordinates of the button in the enemy map
            int x = c.x, y = c.y;

            // Sends a hit request to the enemy
            if(App.online.hitOtherPlayer(x, y)){
                // If the hit was successful, then it's still our turn, so we don't change myTurn
                setTileButtonIcon(x, y, brokenShipPng, enemyMapButtons); // set the targeted button icon to brokenship
                if(App.won)
                    GameOver(true); // Check if we won the game
            }
            else{
                SetMyTurn(false);; // If we miss, then it's the enemy's turn
                setTileButtonIcon(x, y, missPng, enemyMapButtons); // set the targeted button icon to miss
                SetEnableMap(enemyMapButtons, false); // Disable the enemy map
                WaitForOtherPlayer(); // Wait for the enemy's hit request
            }

            disableTileButtonEnemyMap(x, y); // we eather hit of miss, eatherway we shouldn't be able to click it again
        }else{
            if(!placingShips)
                return; // Then we haven't selected any ships

            Cordinate c = findCordinates(mapButtons, e); // Find cordinates of the button in our map
            
            // Tries to put the selected ship in the map with selected orientation
            if(App.map.putShip(c, shipLength, orientation)){
                placingShips = false;
                selectedButton = null;
                updateMapGUI();
                SetEnableMap(mapButtons, false);
                if(isPlacedAllShips())
                    doneButton.setEnabled(true);
            }
        }

        
    }
}

        // JLabel lable = new JLabel();
        // ImageIcon image = new ImageIcon("icon.png");
        // Border border = BorderFactory.createLineBorder(Color.green, 10);
        // lable.setIcon(image);
        // lable.setBorder(border);
        // lable.setHorizontalAlignment(JLabel.RIGHT);

        // JPanel redPanel = new JPanel();
        // redPanel.setBackground(Color.red);
        // redPanel.setBounds(0,0, 300, 300);
        // redPanel.setLayout(new BorderLayout());
        // JPanel bluePanel = new JPanel();
        // bluePanel.setBackground(Color.blue);
        // bluePanel.setBounds(300,0, 300, 300);
        // bluePanel.setLayout(new BorderLayout());
        // JPanel greenPanel = new JPanel();
        // greenPanel.setBackground(Color.green);
        // greenPanel.setBounds(0,300, 600, 300);
        // greenPanel.setLayout(new BorderLayout());

        // bluePanel.add(lable);
        // this.setTitle("My game.");
        // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // this.setResizable(false);
        // this.setSize(1280,720);
        // this.setVisible(true);
        // this.setLayout(null);
        // this.add(redPanel);
        // this.add(bluePanel);
        // this.add(greenPanel);
        // this.setIconImage(image.getImage());
        
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Online {
    public boolean connected = false;

    int port = 5000;
    String address = "127.0.0.1";
    boolean isServer;
    boolean reciving;

    Socket socket = null;
    ServerSocket server = null;
    DataInputStream socketIn = null;
    DataOutputStream socketOut = null;

    // Tries to connect
    public void ConnectRaw(){
        // if there is no server, we make a server, if there is, we connect to it
        try{
            // Client
            socket = new Socket(address, port);
            System.out.println("Connected to the server.");
            isServer = false;
            reciving = false;
            connected = true;
        }catch(Exception e){
            try{
                // Server
                isServer = true;
                server = new ServerSocket(port);
                System.out.println("There were no other severs, server started");
                socket = server.accept();
                System.out.println("Client Acepted.");
                connected = true;
                reciving = true;
            }catch(IOException i){
                System.out.println(i);
            }
        }

        // setting up Socket input and Socket output
        try {
            socketIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            socketOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tries to connect in a Thread
    public void Connect(){
        Thread t1 = new Thread(){
            public void run(){
                ConnectRaw();
            }
        };
        t1.start();
    }

    // Tries to hit the Enemy
    public boolean hitOtherPlayer(int x, int y){
        try {
            // We send the cordinates
            socketOut.writeUTF(x + " " + y);
            String answer = socketIn.readUTF();

            // If other Client sends "true" that means we hit the targen but there are still more ships
            if(answer.equals("true")){
                return true;
            }

            // If other Client sends "won" that means that we already hit all the ships and won the game
            else if(answer.equals("won")){
                App.won = true;
                return true;
            }

            // If other Client sends "false" that means we hit the water
            else{
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Wait for the enemy hit request, returns the raw request in a string
    public String waitForOtherPlayer(){
        try {
            return socketIn.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Answer to the enemy hit request;
    public void AnswerToPlayer(boolean hit){
        try {
            // If hit is true, checks to see if we lost the game, that mean the enemy won, else returns false
            if(hit)
                if(App.lost)
                    socketOut.writeUTF("won");
                else
                    socketOut.writeUTF("true");
            else
                socketOut.writeUTF("false");
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void Disconnect(){
        try {
            socket.close();
            System.out.println("Disconnected");
            if(isServer){
                System.out.println("Server closed");
                server.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        connected = false;
    }

    Online(){
        // At first we are not connected, they shoul call the Connect or RawConnect method.
        connected = false;
    }
}

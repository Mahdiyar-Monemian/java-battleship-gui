public class App{
	static final int mapWidth = 10;
	static final int mapHeight = 10;

	public static Map map;
	public static Online online;
	public static GameGUI gui;

	public static boolean won = false;
	public static boolean lost = false;

	public static void Restart(){
		System.out.println("Game restated");
		map.ResetMap();
		gui.Restart();
	}

	public static void main(String[] args){
		App.map = new Map();
		App.gui = new GameGUI();
		App.online = new Online();
		gui.updateMapGUI();
	}
}
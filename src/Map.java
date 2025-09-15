public class Map {
    State[][] map;
    int width;
    int height;

    // Generating Map
    Map(int w, int h){
        width = w;
        height = h;
        CreateMap();
    }
    
    // Generating Map with size of 10
    Map(){
        this(10,10);
    }

    // Check to see if there is no unbrocken ship, if there isn't we lost
    boolean checkForLost(){
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                if(map[i][j] == State.ship)
                    return false;
        return true;
    }

    // Create a 2D array with each member being a State, at first all of them are State.water
    void CreateMap(){
        map = new State[height][width];
        ResetMap();
    }

    void ResetMap(){
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                map[i][j] = State.water;
    }

    // Check if a cordinate is not bigger than map
    boolean inBound(int x, int y){
		if(x < width && y < height){
			return true;
		}else{
			return false;
		}
	}

    // Set a member of map to a State (x and y are swaped)
    public void SetTile(int x, int y, State s){
        map[y][x] = s;
    }

    // Get the state of a tile in map (x and y are swaped)
    public State GetTile(int x, int y){
        // If someone tries to get a tile out of map, returns water (because of putShip methond)
        if(x < 0 || y < 0 || x >= width || y >= height)
            return State.water;
        return map[y][x];
    }

    // Tries to put a ship in the map, if it is successful returns true, else false
    public boolean putShip(int x, int y, int length, Orientation o){
		// Check to make sure the cordinates aren't bigger than map
		if(!inBound(o == Orientation.horizontal ? x+length-1 : x, o == Orientation.vertical ? y+length-1 : y)){
			System.out.println("out of bound ship");
			return false;
		}

        // Check for ships in the way (For checking surroundings we may be checking out of bounds, but that's okay)
        for(int i = x-1; i < (o == Orientation.horizontal ? x+length : x+2); i++){
            for(int j = y-1; j < (o == Orientation.vertical ? y+length : y+2); j++){
                if(GetTile(i, j) == State.ship)
                    return false;
            }
        }

        // Setting the tiles
		for(int i = 0; i < length; i++){
			if(o == Orientation.horizontal){
                SetTile(x+i, y, State.ship);
			}else{
                SetTile(x, y+i, State.ship);
			}
		}
        return true;
	}
    
    // Hit a tile in the map, if it was a ship returns true, else false;
    public boolean Hit(int x, int y){

        // Checks for being in bounds (We don't need it, it's just to make sure)
		if(!inBound(x, y)){
			System.out.println("out of bound hit");
			return false;
		}

        // Checks to see if they already hit the target (We don't need it, it's just to make sure)
        if(GetTile(x, y) == State.brokenShip || GetTile(x, y) == State.miss){
            return false;
        }

        // If they hit a ship tile, checks to see if it lost the game and returns true, else it returns false
		if(GetTile(x, y) == State.ship){
            SetTile(x, y, State.brokenShip);
            App.lost = checkForLost();
            return true;
		}else{
            SetTile(x, y, State.miss);
            return false;
		}
	}
    
    public void Restart(){
        ResetMap();
    }

    // Cordinate support
    public void SetTile(Cordinate c, State s){
        SetTile(c.x, c.y, s);
    }
    public State GetTile(Cordinate c){
        return GetTile(c.x, c.y);
    }
    boolean inBound(Cordinate c){
        return inBound(c.x, c.y);
    }
    public boolean putShip(Cordinate c, int length, Orientation o){
        return putShip(c.x, c.y, length, o);
    }
    public boolean Hit(Cordinate c){
        return Hit(c.x, c.y);
    }
}

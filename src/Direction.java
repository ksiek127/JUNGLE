import java.util.Map;

public enum Direction {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public Vector2D toUnitVector(){ //zwraca wektor jednostkowy zgodny ze zwrotem
        return switch (this){
            case NORTH -> new Vector2D(0,1);
            case NORTHEAST -> new Vector2D(1,1);
            case EAST -> new Vector2D(1,0);
            case SOUTHEAST -> new Vector2D(1,-1);
            case SOUTH -> new Vector2D(0,-1);
            case SOUTHWEST -> new Vector2D(-1,-1);
            case WEST -> new Vector2D(-1,0);
            case NORTHWEST -> new Vector2D(-1, 1);
        };
    }

    public static Direction intToDirection(int dir){ //1 - NORTH
        return switch (dir){
            case 1 -> NORTH;
            case 2 -> NORTHEAST;
            case 3 -> EAST;
            case 4 -> SOUTHEAST;
            case 5 -> SOUTH;
            case 6 -> SOUTHWEST;
            case 7 -> WEST;
            case 8 -> NORTHWEST;
            default -> throw new IllegalArgumentException();
        };
    }

    public int directionToInt(){
        return switch (this){
            case NORTH -> 1;
            case NORTHEAST -> 2;
            case EAST -> 3;
            case SOUTHEAST -> 4;
            case SOUTH -> 5;
            case SOUTHWEST -> 6;
            case WEST -> 7;
            case NORTHWEST -> 8;
        };
    }
}

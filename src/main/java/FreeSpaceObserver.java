import java.util.ArrayList;

public class FreeSpaceObserver {
    protected final ArrayList<Vector2D> freeSpace; //lista wolnych miejsc

    public FreeSpaceObserver(int mapWidth, int mapHeight){
        freeSpace = new ArrayList<>();
        for(int i=0; i< mapWidth; i++){ //dodaje wszystkie wolne miejsca do listy
            for(int j=0; j< mapHeight; j++){
                freeSpace.add(new Vector2D(i, j));
            }
        }
    }

    public void addSpace(Vector2D position){
        freeSpace.add(position);
    }

    public void removeSpace(Vector2D position){
        for(Vector2D space: freeSpace){
            if(space.equals(position)){
                freeSpace.remove(space);
                return;
            }
        }
    }

    public ArrayList<Vector2D> getFreeSpace() {
        return freeSpace;
    }

    public Boolean isFreeSpace(){
        return freeSpace.size() > 0;
    }

    public Boolean isTheSpaceFree(Vector2D position){
        for(Vector2D space: freeSpace){
            if(space.equals(position))
                return true;
        }
        return false;
    }
}

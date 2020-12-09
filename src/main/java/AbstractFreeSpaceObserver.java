import java.util.ArrayList;

public abstract class AbstractFreeSpaceObserver {
    protected final ArrayList<Vector2D> freeSpace; //lista wolnych miejsc

    public AbstractFreeSpaceObserver(){
        freeSpace = new ArrayList<>();
    }

    public void addSpace(Vector2D position){
        freeSpace.add(position);
    }

    public void removeSpace(Vector2D position){
        freeSpace.remove(position);
    }

    public ArrayList<Vector2D> getFreeSpace() {
        return freeSpace;
    }

    public Boolean isFreeSpace(){
        return freeSpace.size() > 0;
    }
}

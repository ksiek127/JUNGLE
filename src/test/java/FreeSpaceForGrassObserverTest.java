import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class FreeSpaceForGrassObserverTest {
    @Test
    public void isFreeSpaceShouldReturnTrueIfThereIsFreeSpace(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        assertTrue(testMap.grassObserver.isFreeSpace());
    }

    @Test
    public void getFreeSpaceShouldReturnFreeSpace(){
        WorldMap testMap = new WorldMap(1,1,0.5,2);
        FreeSpaceForGrassObserver observer = new FreeSpaceForGrassObserver(testMap);
        ArrayList<Vector2D> space = new ArrayList<>();
        space.add(new Vector2D(0,0));
        assertTrue(space.get(0).equals(observer.getFreeSpace().get(0)));
    }
}
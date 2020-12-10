import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class FreeSpaceObserverTest {
    @Test
    public void freeSpaceForGrassObserverShouldInitFreeSpace(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        assertEquals(16, testMap.getGrassObserver().getFreeSpace().size());
    }

    @Test
    public void isFreeSpaceShouldReturnTrueIfThereIsFreeSpace(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        assertTrue(testMap.getGrassObserver().isFreeSpace());
    }

    @Test
    public void getFreeSpaceShouldReturnFreeSpace(){
        WorldMap testMap = new WorldMap(1,1,0.5,2);
        FreeSpaceObserver observer = testMap.getGrassObserver();
        ArrayList<Vector2D> space = new ArrayList<>();
        space.add(new Vector2D(0,0));
        assertTrue(space.get(0).equals(observer.getFreeSpace().get(0)));
    }

    @Test
    public void isTheSpaceFreeShouldReturnFalseIfTheSpaceIsNotFree(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        FreeSpaceObserver observer = testMap.getAnimalsObserver();
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        assertFalse(observer.isTheSpaceFree(new Vector2D(1,1)));
    }

    @Test
    public void removeSpaceShouldRemoveFreeSpace(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        FreeSpaceObserver observer = new FreeSpaceObserver(4, 4);
        observer.removeSpace(new Vector2D(1,1));
        assertFalse(observer.isTheSpaceFree(new Vector2D(1,1)));
    }
}
import java.util.ArrayList;

public class FreeSpaceForGrassObserver extends AbstractFreeSpaceObserver{ //obserwuje wolne miejsce na mapie, na ktorym moze urosnac trawa

    public FreeSpaceForGrassObserver(WorldMap map){
        for(int i=0; i< map.getWidth(); i++){ //dodaje wszystkie wolne miejsca do listy
            for(int j=0; j< map.getHeight(); j++){
                Vector2D space = new Vector2D(i, j);
                if(!map.isOccupied(space))
                    freeSpace.add(space);
            }
        }
    }
}

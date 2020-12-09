public class FreeSpaceForAnimalsObserver extends AbstractFreeSpaceObserver{

    public FreeSpaceForAnimalsObserver(WorldMap map){
        for(int i=0; i< map.getWidth(); i++){ //dodaje wszystkie wolne miejsca do listy
            for(int j=0; j< map.getHeight(); j++){
                Vector2D space = new Vector2D(i, j);
                if(!map.isOccupiedByLivingEntity(space))
                    freeSpace.add(space);
            }
        }
    }
}

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

public abstract class AbstractWorldMap implements IWorldMap{
    protected final LinkedHashMap<Vector2D, ArrayList<Animal>> animals = new LinkedHashMap<>(); //lista zwierzat
    protected final LinkedHashMap<Vector2D, IMapElement> environmentElements = new LinkedHashMap<>(); //lista roslin
    protected final LinkedHashMap<Vector2D, ArrayList<IMapElement>> mapElements = new LinkedHashMap<>(); //lista wszystkich obiektow na mapie

    @Override
    public LinkedHashMap<Vector2D, ArrayList<Animal>> getAnimals() { //zwracam kopie zwierzakow
        return new LinkedHashMap<>(animals);
    }

    public LinkedHashMap<Vector2D, IMapElement> getEnvironmentElements() { //zwraca kopie roslin
        return new LinkedHashMap<>(environmentElements);
    }

    @Override
    public void placeMapElement(IMapElement element){
        if(!mapElements.containsKey(element.getPosition())){ //dodaje do elementow na mapie
            mapElements.put(element.getPosition(), new ArrayList<IMapElement>());
        }
        mapElements.get(element.getPosition()).add(element);
        if(!element.getIsEnvironmentElement()){
            //dodaje do zwierzakow na mapie
            if(!animals.containsKey(element.getPosition())){ //jesli tam nie ma innych zwierzat
                animals.put(element.getPosition(), new ArrayList<Animal>());
            }
            animals.get(element.getPosition()).add((Animal) element);
        }else{
            //dodaje do elementow otoczenia
            environmentElements.put(element.getPosition(), element);
        }
    }

    @Override
    public boolean isOccupied(Vector2D position){
        return mapElements.containsKey(position);
    }

    @Override
    public abstract Optional<Object> objectsAt(Vector2D position);

    @Override
    public boolean isOccupiedByLivingEntity(Vector2D position) {
        return animals.containsKey(position);
    }

}

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

public interface IWorldMap {

    void placeMapElement(IMapElement element); //umieszczam obiekt na mapie

    boolean isOccupied(Vector2D position); //czy na danym polu cos sie znajduje

    boolean isOccupiedByLivingEntity(Vector2D position); //czy na danym polu znajduje sie zywa istota

    Optional<Object> objectsAt(Vector2D position); //zwraca obiekt na danej pozycji

    LinkedHashMap<Vector2D, ArrayList<Animal>> getAnimals(); //zwraca kopie zwierzat

    boolean isAnyAdjacentPositionFree(Vector2D position); //sprawdza, czy jakiekolwiek sasiednie pole jest wolne (tzn, czy nie ma tam zywej istoty, trawa moze byc)

    void moveAnimal(Animal animal, Vector2D oldPosition); //aktualizuje pozycje zwierzecia na mapie
}

public class Plant extends AbstractMapElement implements IMapElement{

    public Plant(Vector2D position) {
        this.position = position;
        this.isEnvironmentElement = true;
    }

}

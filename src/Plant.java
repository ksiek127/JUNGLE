public class Plant extends AbstractMapElement implements IMapElement{
    private Vector2D position;

    public Plant(Vector2D position) {
        this.position = position;
        this.isEnvironmentElement = true;
    }

}

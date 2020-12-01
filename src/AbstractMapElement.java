public abstract class AbstractMapElement  implements IMapElement {
    protected Vector2D position; //pozycja na mapie
    protected boolean isEnvironmentElement; //jesli to jest element srodowiska -> true, jesli to jest zwierze lub inna zywa istota tego typu -> false

    @Override
    public boolean getIsEnvironmentElement() {
        return isEnvironmentElement;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }
}

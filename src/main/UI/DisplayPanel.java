import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DisplayPanel extends JPanel {
    private WorldMap map; //mapa swiata
    private Simulation simulation;

    public DisplayPanel(WorldMap map, Simulation simulation){
        this.map = map;
        this.simulation = simulation;
    }

    @Override
    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        this.setSize(map.getWidth(), map.getHeight());
        this.setLocation(0, 0);
        graphics.setColor(new Color( 251, 227, 153)); //kolor stepu
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setColor(new Color(133, 144, 79)); //kolor dzungli
        graphics.fillRect(map.getJgBottomLeft().getX(), map.getJgBottomLeft().getY(), map.getJgTopRight().getX() - map.getJgBottomLeft().getX(), map.getJgTopRight().getY() - map.getJgBottomLeft().getY());
        for(IMapElement plant: map.getEnvironmentElementsList()){ //wyswietlam trawe
            graphics.setColor(new Color(205, 255, 176));
            graphics.fillRect(plant.getPosition().getX(), plant.getPosition().getY(), getWidth() / map.getWidth() + 1, (getHeight() / map.getHeight()) + 1);
        }
        for(Animal animal: map.getAnimalsList()){ //wyswietlam zwierzeta
            graphics.setColor(new Color(0,0,0));
            graphics.fillRect(animal.getPosition().getX(), animal.getPosition().getY(), getWidth() / map.getWidth() + 1, getHeight() / map.getHeight() + 1);
        }
    }
}

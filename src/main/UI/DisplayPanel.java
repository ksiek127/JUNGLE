import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DisplayPanel extends JPanel {
    private final WorldMap map; //mapa swiata
    //private WorldMap secondMap; //druga mapa z identycznymi parametrami jak pierwsza, ale niezaleznie losowanymi decyzjami
    private final Simulation simulation;
    private final int scale;

    public DisplayPanel(WorldMap map, Simulation simulation, int scale){
        this.map = map;
        this.simulation = simulation;
        this.scale = scale;
    }

    @Override
    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        this.setLayout(null);
        this.setSize(map.getWidth() * scale, map.getHeight() * scale);
        this.setLocation(0,0);
        graphics.setColor(new Color( 251, 227, 153)); //kolor stepu
        graphics.fillRect(0, 0, getWidth() * scale, getHeight() * scale);
        graphics.setColor(new Color(133, 144, 79)); //kolor dzungli
        graphics.fillRect(map.getJgBottomLeft().getX() * scale, map.getJgBottomLeft().getY() * scale, map.getJgTopRight().getX() * scale - map.getJgBottomLeft().getX() * scale, map.getJgTopRight().getY() * scale - map.getJgBottomLeft().getY() * scale);
        for(IMapElement plant: map.getEnvironmentElementsList()){ //wyswietlam trawe
            graphics.setColor(new Color(205, 255, 176));
            graphics.fillRect(plant.getPosition().getX() * scale, plant.getPosition().getY() * scale, scale, scale);
        }
        for(Animal animal: map.getAnimalsList()){ //wyswietlam zwierzeta
            graphics.setColor(animal.getColor());
            graphics.fillRect(animal.getPosition().getX() * scale, animal.getPosition().getY() * scale, scale, scale);
        }
    }
}

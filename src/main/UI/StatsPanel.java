import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {
    private final WorldMap map;
    private final Vector2D position;

    public StatsPanel(WorldMap map, Vector2D position){
        this.map = map;
        this.position = position;
    }

    @Override
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        this.setLayout(null);
        this.setSize(map.getWidth() * 10,350);
        this.setLocation(0,0);
//        graphics.setColor(new Color( 0, 227, 0)); //OUT
//        graphics.fillRect(0,0, map.getWidth(), 350); //OUT
        graphics.setColor(new Color( 0,0,0));
//        graphics.drawString("Current era: " + map.getEra(), position.getX(), position.getY());
//        graphics.drawString("Animals still alive: " + map.getAnimalsList().size(), position.getX(), position.getY() + 50);
//        graphics.drawString("Plants on the map: " + map.getEnvironmentElementsList().size(), position.getX(), position.getY() + 50);
//        graphics.drawString("Dominating genotype: " + map.getDominatingGenotype(), position.getX(), position.getY() + 50);
//        graphics.drawString("Average animal's energy level: " + map.getAverageEnergyLevel(), position.getX(), position.getY() + 50); //dla zyjacych zwierzat
//        graphics.drawString("Average longevity: " + map.getAverageLongevityForDeadAnimals(), position.getX(), position.getY() + 50); //dla martwych zwierzat
//        graphics.drawString("Average number of children: " + map.getAverageNrOfChildrenForAliveAnimals(), position.getX(), position.getY() + 50); //dla zyjacych zwierzat
        graphics.drawString("Current era: " + map.getEra(), 0, 20);
        graphics.drawString("Animals still alive: " + map.getAnimalsList().size(), 0, 50);
        graphics.drawString("Plants on the map: " + map.getEnvironmentElementsList().size(), 0, 100);
        graphics.drawString("Dominating genotype: " + map.getDominatingGenotype(), 0, 150);
        graphics.drawString("Average animal's energy level: " + map.getAverageEnergyLevel(), 0, 200); //dla zyjacych zwierzat
        graphics.drawString("Average longevity: " + map.getAverageLongevityForDeadAnimals(), 0, 250); //dla martwych zwierzat
        graphics.drawString("Average number of children: " + map.getAverageNrOfChildrenForAliveAnimals(), 0, 300); //dla zyjacych zwierzat
    }
}

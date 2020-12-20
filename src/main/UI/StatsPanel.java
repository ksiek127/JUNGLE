import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {
    private final WorldMap map;

    public StatsPanel(WorldMap map, Vector2D position){
        this.map = map;
    }

    @Override
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        this.setLayout(null);
        this.setSize(map.getWidth() * 10,350);
        this.setLocation(0,0);
        graphics.setColor(new Color( 0,0,0));
        graphics.drawString("Current era: " + map.getEra(), 0, 20);
        graphics.drawString("Animals still alive: " + map.getAnimalsList().size(), 0, 50);
        graphics.drawString("Plants on the map: " + map.getEnvironmentElementsList().size(), 0, 100);
        graphics.drawString("Dominating genotype: " + map.getDominatingGenotype(), 0, 150);
        graphics.drawString("Average animal's energy level: " + map.getAverageEnergyLevel(), 0, 200); //dla zyjacych zwierzat
        graphics.drawString("Average longevity: " + map.getAverageLongevityForDeadAnimals(), 0, 250); //dla martwych zwierzat
        graphics.drawString("Average number of children: " + map.getAverageNrOfChildrenForAliveAnimals(), 0, 300); //dla zyjacych zwierzat
    }
}

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

import org.json.simple.JSONObject;

import javax.swing.*;

public class World {
    private static int energyRequiredToBreed; //ilosc energii potrzebnej do rozmnazania

    public static int getEnergyRequiredToBreed() {
        return energyRequiredToBreed;
    }

    public static void main(String[] args) {
        try{
            LoadJson parameters = LoadJson.loadParameters();
            JFrame menuFrame = new JFrame();
            menuFrame.setPreferredSize(new Dimension(400,400));
            menuFrame.pack();
            menuFrame.add(new ConfigPanel(parameters));
            menuFrame.setLocationRelativeTo(null);
            menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menuFrame.setVisible(true);
        }catch (IllegalArgumentException e){
            System.out.println(e);
        }
    }
}

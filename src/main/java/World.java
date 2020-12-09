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
        JFrame menu = new JFrame();

        try{
            LoadJson parameters = LoadJson.loadParameters();
            JFrame menuFrame = new JFrame();
            menu.setSize(parameters.getWidth(), parameters.getHeight());
            menuFrame.add(new ConfigPanel(parameters));
            menuFrame.setVisible(true);
        }catch (IllegalArgumentException e){
            System.out.println(e);
        }
    }
}

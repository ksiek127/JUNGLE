import com.google.gson.Gson;

import java.io.*;

public class LoadJson {
    private int width;
    private int height;
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private double jungleRatio;
    private int nrOfAnimalsInTheBeginning;
    private int delay;

    public LoadJson() {
    }

    public static LoadJson loadParameters() {
        Gson gson = new Gson();
        LoadJson parameters = new LoadJson();
        try (Reader reader = new FileReader("src\\main\\java\\parameters.json")){
            parameters = gson.fromJson(reader, LoadJson.class); //zamiana parametrow na obiekt
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Something went wrong while trying to open the parameters file");
        }
        parameters.validate(); //sprawdzam poprawnosc danych
        return parameters;
    }

    public void validate() throws IllegalArgumentException{
        if(width <= 0)
            throw new IllegalArgumentException("Invalid map width");
        if(height <= 0)
            throw new IllegalArgumentException("Invalid map height");
        if(startEnergy <= 0)
            throw new IllegalArgumentException("Animals are dying instantly, give them more start energy");
        if(moveEnergy <= 0 || moveEnergy > startEnergy) //jesli jest ujemna lub rowna 0 to sie tworzy perpetuum mobile, a jesli wieksza, niz start energy, to zwierzaki nie moga sie poruszac
            throw new IllegalArgumentException("Invalid move energy");
        if(plantEnergy <= 0)
            throw new IllegalArgumentException("Poisonous plants (plant energy cannot be <= 0)");
        if(jungleRatio > 1 || jungleRatio < 0)
            throw new IllegalArgumentException("Jungle ratio has to be between 0 and 1");
        if(nrOfAnimalsInTheBeginning <= 0)
            throw new IllegalArgumentException("Invalid nr of animals in the beginning");
        if(delay < 0)
            throw new IllegalArgumentException("Delay cannot be negative");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

    public double
    getJungleRatio() {
        return jungleRatio;
    }

    public int getNrOfAnimalsInTheBeginning() {
        return nrOfAnimalsInTheBeginning;
    }

    public int getDelay() {
        return delay;
    }
}

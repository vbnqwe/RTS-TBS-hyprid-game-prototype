package map.planets;

import java.util.Random;

public class Planet {

    private String planetType;
    private int size; //0 = small, 1 = med, 2 = large
    private int population;
    private int maxPopulation; //3 = small, 6 = med, 9 = large
    private boolean ifSettled;

    private int industry;
    private int science;
    private double food;
    private double cash;
    private double rawMaterialExport;

    private int industryMultiplierForPlanet;
    private int scienceMultiplierForPlanet;
    private double foodMultiplierForPlanet;
    private double cashMultiplierForPlanet;
    private double rawMaterialExportMultiplierForPlanet;

    private String specialProperties; //***********WORK ON THIS LATER*************************

    public Planet(String type){
        planetType = type;
        Random rand = new Random();
        size = rand.nextInt(3);
        population = 0;
        decideCharacteristics();
        calculateOutput();
        if(size == 0){
            maxPopulation = 3;
        } else if(size == 1){
            maxPopulation = 6;
        } else if(size == 2){
            maxPopulation = 9;
        }

    }

    public Planet(String type, int size,  boolean settled, int population){
        planetType = type;
        this.size = size;
        this.population = population;
        ifSettled = settled;
        if(size == 0){
            maxPopulation = 3;
        } else if(size == 1){
            maxPopulation = 6;
        } else if(size == 2){
            maxPopulation = 9;
        }
        decideCharacteristics();
        calculateOutput();
    }

    public void decideCharacteristics(){
        if(planetType.compareTo("ammoniaIceGiant") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("chthonian") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("coreless") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("gasGiant") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("ice") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("methaneIceGiant") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("artificial") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("iron") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("ocean") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("telluric") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 2;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("arid") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("desert") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else if(planetType.compareTo("lava") == 0){
            industryMultiplierForPlanet = 1;
            scienceMultiplierForPlanet = 1;
            foodMultiplierForPlanet = 1;
            cashMultiplierForPlanet = 1;
            rawMaterialExportMultiplierForPlanet = 1;
        } else {
            System.out.println("Planet generation has gone doodoo");
        }
    }

    public int getSize(){
        return size;
    }

    public String getSpecialProperties(){
        return specialProperties;
    }

    public void nextTurn(){
        calculateOutput();
    }

    private void changePop(){
        population++;
    }

    public void calculateOutput(){
        industry = population * industryMultiplierForPlanet;
        science = population * scienceMultiplierForPlanet + 20;
        food = population * foodMultiplierForPlanet;
        cash = population * cashMultiplierForPlanet;
        rawMaterialExport = population * rawMaterialExportMultiplierForPlanet;
    }

    public String getPlanetType(){ return planetType; }

    public int getIndustry(){ return industry; }
    public int getScience(){ return science; }
    public double getFood(){ return food; }
    public double getCash(){ return cash; }
    public double getRawMaterialExport(){ return rawMaterialExport; }
    public int getPopulation(){ return population; }
    public void colonize(){
        population = 1;
    }
    public int getMaxPopulation(){ return maxPopulation; }
    public void increasePop(){ population++; }


}

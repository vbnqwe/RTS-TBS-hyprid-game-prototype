package map.systems;

import devTools.Coordinate;
import map.planets.*;
import planetUpgrades.Resource;
import planetUpgrades.Upgrade;
import players.Player;
import ships.Fleet;
import ships.Ship;
import ships.chassises.ShipTemplate;
import ships.tradeRoutes.tradeships.TradeFleet;

import java.util.ArrayList;
import java.util.Random;

public class SolarSystem {
    private Coordinate location;
    private int owner = -1;
    private Random rand;
    private Planet[] planets;
    private ArrayList<String> namesOfConnections = new ArrayList<>();
    private int numConnections;
    private int numAvailableConnections;

    private int numPlanets;
    private String starType;
    private double heat;
    private int age; //1-3
    private double size; //1 or 2

    private String name;

    private int industry;
    private double rawMaterialOutput;
    private int science;
    private double cash;
    private double food;
    private int manpowerSupported;
    private double viewDistance;

    private double industryPercentage = 1;
    private double sciencePercentage = 1;
    private double foodPercentage = 1;
    private double cashPercentage = 1;
    private double viewDistancePercentage = 1;
    private double rawMaterialOutputPercentage = 1;
    private ArrayList<Integer> queueForUpdatePop = new ArrayList<>();
    private int pointsUntilPop = 0;

    private int bonusIndustry;
    private double bonusRawMaterialOutput;
    private int bonusScience;
    private double bonusCash;
    private int bonusManPowerSupported;
    private double bonusViewDistance;
    private double bonusFoodOutput;

    //data storage objects
    private ArrayList<Upgrade> built = new ArrayList<>();
    private ArrayList<Upgrade> unLockable = new ArrayList<Upgrade>();
    private ArrayList<Upgrade> upgradeQueue = new ArrayList<Upgrade>();
    private ArrayList<ShipTemplate> shipQueue = new ArrayList<>();
    private ArrayList<String> queue = new ArrayList<>();

    private ArrayList<Ship> hanger = new ArrayList<>();
    private ArrayList<Fleet> garrison = new ArrayList<>();
    private ArrayList<TradeFleet> tradeGarrison = new ArrayList<>();


    //private int numShipsMadeHere = 0;


    public SolarSystem(int x, int y, String name){
        location = new Coordinate(x, y);
        viewDistance = 0;
        this.name = name;
        rand = new Random();
        setUpPlanets();
        calculateOutput();
    }

    public SolarSystem(int x, int y, int owner, String name, Player player){
        location = new Coordinate(x, y);
        this.owner = owner;
        viewDistance = 25;
        this.name = name;
        rand = new Random();
        setUpPlanetsCustom(player);
        calculateOutput();
        for(int i = 0; i < planets.length; i++){
            if(planets[i].getPopulation() > 0){
                queueForUpdatePop.add(i);
            }
        }
        pointsUntilPop = getPopulation() * 5;
    }

    public void setNumConnections(int numConnections){
        this.numConnections = numConnections;
        numAvailableConnections = numConnections;
    }

    public void addConnection(String newConnection){
        for(int i = 0; i < namesOfConnections.size(); i++){
            if(namesOfConnections.get(i).compareTo(newConnection) == 0){
                System.out.println("Clone exists error in connections");
                break;
            }
        }
        namesOfConnections.add(newConnection);
    }


    public boolean getIfConnectionAlreadyAdded(String conn){
        for(String str : namesOfConnections){
            if(str.compareTo(conn) == 0){
                return true;
            }
        }
        return false;
    }

    public int getNumConnections(){ return numConnections; }

    public int getNumAvailableConnections(){ return numConnections - namesOfConnections.size(); }

    public ArrayList<String> getConnections(){ return namesOfConnections; }

    public int getQueueSize(){
        return queue.size(); }

    public int getX(){
        return location.getX();
    }

    public int getY(){
        return location.getY();
    }

    public int getOwner(){ return owner; }

    public void setOwner(int newOwner){ owner = newOwner; }

    public double getViewDistance(){ return viewDistance; }

    public String getName(){ return name; }

    public void setViewDistance(double deltaViewDis){ viewDistance += deltaViewDis; }

    public void addToGarrison(Fleet newFleet){
        garrison.add(newFleet);
    }
    public void addToGarrision(TradeFleet newTrade){ tradeGarrison.add(newTrade); }
    public void removeFromGarrison(String oldFleet){
        for(int i = 0; i < garrison.size(); i++){
            if(oldFleet.compareTo(garrison.get(i).getID()) == 0){
                garrison.remove(i);
                break;
            }
        }
        for(int i = 0; i < tradeGarrison.size(); i++){
            if(oldFleet.compareTo(tradeGarrison.get(i).getId()) == 0){
                tradeGarrison.remove(i);
                break;
            }
        }
    }


    public void nextTurn(int numShipsMade){
        hanger.clear();
        this.setViewDistance(2);

        //gives upgrade
        if(queue.size() > 0){
            if(queue.get(0).compareTo("upgrade") == 0){
                upgradeQueue.get(0).nextTurn(this.getIndustry(), 0);
                if(upgradeQueue.get(0).ifBuilt()){
                    Resource upgrade = upgradeQueue.get(0).build();


                    bonusIndustry += upgrade.getIndustry();
                    bonusScience += upgrade.getScience();
                    bonusCash += upgrade.getCash();
                    bonusViewDistance += upgrade.getViewDistance();
                    bonusRawMaterialOutput += upgrade.getRawMaterialExport();
                    bonusFoodOutput += upgrade.getFood();

                    industryPercentage += upgrade.getIndustryPercentage();
                    sciencePercentage += upgrade.getSciencePercentage();
                    cashPercentage += upgrade.getCashPercentage();
                    viewDistancePercentage += upgrade.getViewDistancePercentage();
                    rawMaterialOutputPercentage += upgrade.getRawMaterialExportPercentage();

                    for(int j = 0; j < unLockable.size(); j++){
                        if(unLockable.get(j).getName().compareTo(upgradeQueue.get(0).getName()) == 0){
                            unLockable.remove(j);
                            break;
                        }
                    }

                    built.add(upgradeQueue.get(0));
                    upgradeQueue.remove(0);
                    queue.remove(0);

                }
            } else if(queue.get(0).compareTo("ship") == 0){
                shipQueue.get(0).build(industry);
                if(shipQueue.get(0).ifBuilt() || shipQueue.get(0).getTotalProductionNeeded() <= 0){
                    hanger.add(new Ship("ship__" + owner + "" + numShipsMade, shipQueue.get(0)));
                    shipQueue.remove(0);
                    queue.remove(0);
                }
            }
        }
        for(Planet p : planets){
            p.calculateOutput();
        }
        calculateOutput();
        popGrowthFunction();
    }

    public void popGrowthFunction(){
        double foodToSupportPop = 0;
        for(Planet p : planets){
            if(p.getPopulation() > p.getMaxPopulation()){
                foodToSupportPop += p.getPopulation() * 2;
            }else{
                foodToSupportPop += p.getPopulation();
            }
        }
        pointsUntilPop -= (food - foodToSupportPop);
        if(pointsUntilPop <= 0){
            for(int i = 0; i < planets.length; i++){
                if(i == queueForUpdatePop.get(0)){
                    planets[queueForUpdatePop.get(0)].increasePop();
                    queueForUpdatePop.add(queueForUpdatePop.get(0));
                    queueForUpdatePop.remove(0);
                    pointsUntilPop = getPopulation() * 5;
                    break;
                }
            }
        }
        if(this.getName().compareTo("System0") == 0){

        }
    }

    public Ship getHangerAt(int index){
        return hanger.get(index);
    }

    public int getHangerSize(){
        return hanger.size();
    }

    public void clearHanger(){
        hanger.clear();
    }

    private void calculateOutput(){
        for(Planet p : planets){
            p.calculateOutput();
        }

        int newIndustry = 0; int newScience = 0; int newCash = 0; double newRawMaterialExport = 0; double newFood = 0;
        for(int i = 0; i < planets.length; i++){
            newIndustry += planets[i].getIndustry();
            newScience += planets[i].getScience();
            newCash += planets[i].getCash();
            newRawMaterialExport += planets[i].getRawMaterialExport();
            newFood += planets[i].getFood();
        }
        newIndustry += bonusIndustry;
        newScience += bonusScience;
        newCash += bonusCash;
        newRawMaterialExport += bonusRawMaterialOutput;
        newFood += bonusFoodOutput;

        newIndustry *= industryPercentage;
        newScience *= sciencePercentage;
        newCash *= cashPercentage;
        newRawMaterialExport *= rawMaterialOutputPercentage;
        newFood *= foodPercentage;

        industry = newIndustry;
        science = newScience;
        cash = newCash;
        rawMaterialOutput = newRawMaterialExport;
        food = newFood;
    }


    public int compareTo(SolarSystem other){
        if(other.getY() == this.getY() && other.getX() == this.getX()){
            return 0;
        } else {
            return -1;
        }
    }

    public int getIndustry(){ return industry; }

    public int getScience(){
        return science;
    }

    public double getCash(){ return cash; }

    public double getManpowerSupported(){ return manpowerSupported; }

    public String getStarType(){
        return starType;
    }

    public double getStarSize(){ return size; }

    public void setUpPlanets(){
        int x = rand.nextInt(11);
        if(x == 0 || x == 1){
            //yellow dwarf
            starType = "yellow";
            heat = 4;
            size = 1;
            age = 1;
        }else if(x == 2 || x == 3){
            //red dwarf
            starType = "red";
            heat = 1;
            size = 1;
            age = 1;
        }else if(x == 4 || x == 5){
            //red giant
            starType = "red";
            heat = 2;
            size = 2;
            age = 2;
        }else if(x == 6 || x == 7){
            //blue giant
            starType = "blue";
            heat = 6;
            size = 2;
            age = 2;
        }else if(x == 8){
            //yellow giant? not sure if this really exists but niether does this game
            starType = "yellow";
            heat = 4;
            size = 2;
            age = 1;
        }else if(x == 9){
            //white dwarf
            starType = "white";
            heat = 5;
            size = 1.5;
            age = 3;
        }else if(x == 10){
            //neutron star
            starType = "neutron";
            heat = 3;
            size = 1;
            age = 3;
        } else {
            starType = "yellow";
            size = 1;
            heat = 3;
            age = 3;
        }

        numPlanets = rand.nextInt(3) + 3;
        if(age == 2){
            numPlanets += 2;
        } else if(age == 3){
            numPlanets -= 2;
        }
        planets = new Planet[numPlanets];
        for(int i = 0; i < numPlanets; i++){
            int y = rand.nextInt(15) + (int)heat;
            if(y <= 6){
                //creates colder planet
                //includes: all Giants, ice, chthonian, coreless (6)
                y = rand.nextInt(6);
                if(y == 0){
                    planets[i] = new Planet("ammoniaIceGiant");
                } else if(y == 1){
                    planets[i] = new Planet("ammoniaIceGiant");
                } else if(y == 2){
                    planets[i] = new Planet("coreless");
                } else if(y == 3){
                    planets[i] = new Planet("gasGiant");
                } else if(y == 4 && i == 0) {
                    planets[i] = new Planet("chthonian");
                } else if(y == 4 && i != 0){
                    planets[i] = new Planet("ice");
                } else if(y == 5){
                    planets[i] = new Planet("ice");
                }
            } else if(y > 6 && y <= 12){
                //creates moderate temp planet
                //includes: Artificial, Iron, Ocean, Telluric (4)
                y = rand.nextInt(4);
                if(y == 0){
                    planets[i] = new Planet("artificial");
                } else if(y == 1){
                    planets[i] = new Planet("iron");
                } else if(y == 2){
                    planets[i] = new Planet("ocean");
                } else if(y == 3){
                    planets[i] = new Planet("telluric");
                }
            } else if(y > 12){
                //creates warm planet
                //includes: Arid, Desert, Lava (3)
                y = rand.nextInt(3);
                if(y == 0){
                    planets[i] = new Planet("arid");
                } else if(y == 1){
                    planets[i] = new Planet("desert");
                } else if(y == 2){
                    planets[i] = new Planet("lava");
                }
            } else {
                System.out.println("PLANET GENERATION GONE DOODOO");
            }
        }
    }

    public Planet[] getPlanets(){ return planets; }

    public Planet[] setUpPlanetsCustom(Player player){
        planets = player.getCivilization().getHomeSystem();
        starType = player.getCivilization().getStarName();
        size = player.getCivilization().getStarSize();
        return planets;
    }

    public int getNumUpgrades(){ return unLockable.size(); }

    public void addUpgrade(Upgrade upgrade){
        unLockable.add(upgrade);
    }

    public Upgrade getUpgradeAt(int index){
        return unLockable.get(index);
    }


    public void addToQueue(Upgrade upgrade){
        boolean ifBuilt = false;
        for(int i = 0; i < built.size(); i++){
            if(upgrade.getName().compareTo(built.get(i).getName()) == 0){
                ifBuilt = true;
            }
        }
        if(!ifBuilt) {
            queue.add(0, "upgrade");
            upgradeQueue.add(0, upgrade);
        }
    }

    public void addToQueue(ShipTemplate ship){
        queue.add(0, "ship");
        shipQueue.add(0, ship);
    }


    /********************************************************
    -------------Accessors for displaying queue--------------
     ********************************************************/

    public String getQueueAt(int index){ return queue.get(0); }
    public Upgrade getUpgradeQueueAt(int index){ return upgradeQueue.get(0); }
    public ShipTemplate getShipQueueAt(int index){ return shipQueue.get(0); }

    public ArrayList<String> getQueue(){ return queue; }

    public void colonize(int newOwner, int indexOfPlanet){
        if(owner == -1){
            owner = newOwner;
        }
        planets[indexOfPlanet].colonize();
        System.out.println("Colonized");
        queueForUpdatePop.add(indexOfPlanet);
    }

    private void colonize(int newOwner){
        int highestScore = 0;
        int index = 0;
        for (int i = 0; i < planets.length; i++) {
            int score = 0;
            if (planets[i].getPlanetType().compareTo("ammoniaIceGiant") == 0) {
                score = 1;
            } else if (planets[i].getPlanetType().compareTo("chthonian") == 0) {
                score = 3;
            } else if (planets[i].getPlanetType().compareTo("coreless") == 0) {
                score = 2;
            } else if (planets[i].getPlanetType().compareTo("gasGiant") == 0) {
                score = 2;
            } else if (planets[i].getPlanetType().compareTo("ice") == 0) {
                score = 1;
            } else if (planets[i].getPlanetType().compareTo("methaneIceGiant") == 0) {
                score = 1;
            } else if (planets[i].getPlanetType().compareTo("artificial") == 0) {
                score = 15;
            } else if (planets[i].getPlanetType().compareTo("iron") == 0) {
                score = 5;
            } else if (planets[i].getPlanetType().compareTo("ocean") == 0) {
                score = 10;
            } else if (planets[i].getPlanetType().compareTo("telluric") == 0) {
                score = 20;
            } else if (planets[i].getPlanetType().compareTo("arid") == 0) {
                score = 5;
            } else if (planets[i].getPlanetType().compareTo("desert") == 0) {
                score = 3;
            } else if (planets[i].getPlanetType().compareTo("lava") == 0) {
                score = 4;
            }
            if(score > highestScore){
                highestScore = score;
                index = 0;
            }
        }
        if(owner == -1){
            owner = newOwner;
        }
        planets[index].colonize();
    }

    public int getPopulation(){
        int pop = 0;
        for(Planet p : planets){
            pop += p.getPopulation();
        }
        return pop;
    }

    public int getPopulationAtIndex(int index){
        return planets[index].getPopulation();
    }

    public void boostPopulation(int extraFood){
        pointsUntilPop -= extraFood;
        System.out.println("ADDED TO POPULATION " + pointsUntilPop);
        if(pointsUntilPop <= 0){
            for(int i = 0; i < planets.length; i++){
                if(i == queueForUpdatePop.get(0)){
                    planets[queueForUpdatePop.get(0)].increasePop();
                    queueForUpdatePop.add(queueForUpdatePop.get(0));
                    queueForUpdatePop.remove(0);
                    pointsUntilPop = getPopulation() * 5;
                    break;
                }
            }
        }
    }

    public double getFood(){
        return food;
    }

    public double getRawMaterialOutput(){ return rawMaterialOutput; }

    public void updateGarrison(ArrayList<Fleet> garrison){
        this.garrison = garrison;
    }

    public ArrayList<Fleet> getGarrison(){ return garrison; }
    public ArrayList<TradeFleet> getTradeGarrision(){ return tradeGarrison; }

    public ArrayList<TradeFleet> getTradeGarrison(){ return tradeGarrison; }

    public int getNumBuilt(){ return built.size(); }
    public Upgrade getBuiltAt(int index){ return built.get(index); }

    public int getNumUnlockable(){ return unLockable.size(); }
    public Upgrade getUnlockableAtIndex(int index){ return unLockable.get(index); }

    public void finalizeConnections(){
        numConnections = namesOfConnections.size();
    }
}

package players;

import players.civilizations.Civilization;
import devTools.DoubleCooridinate;
import devTools.Scores;
import javafx.scene.paint.Color;
import map.systems.*;
import planetUpgrades.Resource;
import planetUpgrades.Upgrade;
import players.aiComponents.Path;
import players.aiComponents.PathWithSystems;
import players.score.Score;
import ships.*;
import ships.chassises.ShipTemplate;
import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;
import ships.tradeRoutes.tradeships.TradeFleet;
import techTree.techs.Tech;
import techTree.TechTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class DefaultPlayer implements Player{

    private ArrayList<Integer> knownSystems = new ArrayList<>();
    private ArrayList<Integer> ownedSystemsIndex = new ArrayList<>();
    private ArrayList<SolarSystem> ownedSystems = new ArrayList<>();
    private ArrayList<Fleet> ownedFleets = new ArrayList<>();
    private ArrayList<Fleet> newlyConstructed = new ArrayList<>(); //used for next turn
    private HashMap<String, TradeFleet> theTraitors = new HashMap<>();
    private ArrayList<TradeFleet> newlyConstructedTraders = new ArrayList<>();
    private boolean isBot;
    private String color;
    private int playerIndex;
    private int numberOfShipsMade = 0;
    private int numberOfFleetsMade = 0;
    private int numberOfTradeFleetsMade = 0;
    private Civilization civ;

    int totalScience = 0;
    private TechTree tech;
    private ArrayList<String> unlockedAbilities = new ArrayList<>();
    private int population = 0;

    //importance values for resource to civilization
    private final float foodImpOG = 2.0f;
    private final float industryImpOG = 2.0f;
    private final float scienceImpOG = 1.5f;
    private final float cashImpOG = 1.25f;
    private final float manPowerImpOG = 1.0f;
    private final float rawMatExpImpOG = 1.25f;
    private final float miltaryImpOG = 1.5f;
    private final float expanImpOG = 1.5f;

    //importance values that change up
    private float foodImp = foodImpOG;
    private float industryImp = industryImpOG;
    private float scienceImp = scienceImpOG;
    private float cashImp = cashImpOG;
    private float militaryImp = miltaryImpOG;
    private float ManPowerImp = manPowerImpOG;
    private float rawMatExpImp = rawMatExpImpOG;
    private float expanImp = expanImpOG;

    //random number generator
    private Random rand = new Random();

    //importance values for actions
    private float happynessPriority = 1.5f; //on a 0 to 3.0f scale
    private float capitalismness = 1.5f;
    //private double

    //score values
    private double militaryScore = 0;
    private double colonizationScore = 1; //planets colonized / numturns

    //importance for different types of weaponry
    private float projImp = 1.5f;
    private float missileImp = 1.5f;
    private float hitScanImp = 1.5f;
    private float hullArmorImp = 1.5f;
    private float shieldImp = 1.5f;

    public DefaultPlayer(boolean isBot, String color, int playerIndex, Civilization civ){
        this.isBot = isBot;
        this.color = color;
        this.playerIndex = playerIndex;
        this.civ = civ;
        tech = new TechTree(civ.getCivID());
    }

    public void decideHomeSystem(int indexOfHomeSystem, SolarSystem homeSys){
        knownSystems.add(indexOfHomeSystem);
        ownedSystemsIndex.add(indexOfHomeSystem);
        ownedSystems.add(homeSys);
    }

    public Civilization getCivilization(){
        return civ;
    }

    public int getKnownSystem(int index){
        return knownSystems.get(index);
    }

    public int getOwnedSystemsIndex(int index){ return ownedSystemsIndex.get(index); }

    public void discoverSystem(int index){
        knownSystems.add(index);
    }

    public boolean getIfBot(){
        return isBot;
    }

    public String getColor(){
        return color;
    }

    public ArrayList<Integer> getAllKnownSystems(){
        return knownSystems;
    }

    public ArrayList<Integer> getAllOwnedSystemsIndex(){ return ownedSystemsIndex; }

    public ArrayList<SolarSystem> getAllOwnedSystems(){

        return ownedSystems;
    }


    public ArrayList<Fleet> getFleets(){
        return ownedFleets;
    }

    public HashMap<String, TradeFleet> getTradeFleets(){
        return theTraitors;
    }


    public Fleet getFleet(String iD){
        for(Fleet fleet : ownedFleets){
            if(iD.compareTo(fleet.getID()) == 0){
                return fleet;
            }
        }
        return null;
    }


    public void moveFleet(String id, SolarSystem target, SolarSystem[] map) {
        for(Fleet f : ownedFleets){
            if(id.compareTo(f.getID()) == 0){
                Path oldPath = new Path(f.getDepartedFrom());
                oldPath.addToPath(target);
                PathWithSystems path = new PathWithSystems(oldPath, map);
                f.move(path, map);
            }
        }
    }

    public int getNumFleets(){ return ownedFleets.size(); }

    public DoubleCooridinate getLocationOfFleetAt(int index){
        return ownedFleets.get(index).getPosition();
    }
    public int getNumShips(){
        return ownedFleets.size();
    }

    public String getIDOfFleetAtIndex(int index){
        return ownedFleets.get(index).getID();
    }


    public Upgrade sendNewlyDiscoveredUpgrade(){
        return null;
    }

    public void getScience(){
        int science = 0;
        for(SolarSystem sys : ownedSystems){
            science += sys.getScience();
        }
        totalScience = science;
    }

    public TechTree getTech(){
        return tech;
    }

    public void selectScience(String iD){
        tech.selectTech(iD);
    }

    public void initializeUpgrades(){
        civ.initializeUpgrades(tech);
    }

    private void updateSystems(){
        for(SolarSystem sys : ownedSystems){
            sys.nextTurn(numberOfShipsMade);
        }
    }

    private void updateScores(){
        militaryScore = 1.0 * this.getNumShips() / ownedSystems.size();
        for(SolarSystem sys : ownedSystems){
            population += sys.getPopulation();
        }
    }

    public void nextTurn(int numTurns, Scores scores, SolarSystem[] map){
        updateSystems();
        //updateFleets(map);
        uncoverSystems(map);
        this.getScience();
        tech.nextTurn(totalScience);
        Tech newlyCompleted = tech.getCompletedTech(playerIndex);
        /*if(newlyCompleted != null && newlyCompleted.getIfUpgradeExists()){
            discoveredUpgrades.add(newlyCompleted.getUpgrade());
            civ.unlockUpgrade(newlyCompleted);
            for(SolarSystem sys : ownedSystems){
                sys.addUpgrade(newlyCompleted.getUpgrade());
            }
            if(newlyCompleted.getUnlockedAbility().compareTo("") != 0){
                unlockedAbilities.add(newlyCompleted.getUnlockedAbility());
            }
        } else if(newlyCompleted != null && !newlyCompleted.getIfUpgradeExists()){
            unlockedAbilities.add(newlyCompleted.getUnlockedAbility());
        }*/

        //does the tech stuff
        if(newlyCompleted != null){
            //adds new upgrade if exists
            if(newlyCompleted.getIfUpgradeExists()){
                civ.unlockUpgrade(newlyCompleted);
                for(SolarSystem sys : ownedSystems){
                    sys.addUpgrade(newlyCompleted.getUpgrade());
                }
            }
            //adds weapon if exists
            if(newlyCompleted.getIfWeaponExists()){
                civ.unlockWeapon(newlyCompleted.getWeapon());
            }
            //adds defense if exists
            if(newlyCompleted.getIfDefenseExists()){
                civ.unlockDefense(newlyCompleted.getDefense());
            }

        }

        for(SolarSystem sys : ownedSystems){
            if(sys.getHangerSize() > 0) {
                if(sys.getHangerAt(0).getType().getPurpose().compareTo("trader") == 0){
                    TradeFleet tf = new TradeFleet(sys, "TraFle" + playerIndex + numberOfTradeFleetsMade, sys.
                            getHangerAt(0));
                    theTraitors.put(tf.getId(), tf);
                    numberOfTradeFleetsMade++;
                    numberOfShipsMade++;
                    System.out.println("HI1");
                    newlyConstructedTraders.add(tf);
                } else {
                    Fleet temp = new Fleet(sys, "Fleet_" + playerIndex + numberOfFleetsMade);
                    numberOfFleetsMade++;
                    numberOfShipsMade++;
                    temp.addShip(sys.getHangerAt(0), "vanguard");
                    ownedFleets.add(temp);
                    newlyConstructed.add(temp);
                    sys.addToGarrison(temp);
                }
                sys.clearHanger();
            }
        }

        updateBehaviorNextTurn(scores);
        /***************************************************************************************************************
         Will cause ai to be 1 turn behind the score values. Likely will not change much however a particularly eventful
         war could easily make the ai fall behind in a turn, could be solved by making updateScores() be called in main
         **************************************************************************************************************/

        updateScores();

        if(isBot){
            //decideWhatToDo(map);
        }
    }

    public void moveFleetAnim(String fleet, SolarSystem[] map){
        for(Fleet f : ownedFleets){
            if(f.getID().compareTo(fleet) == 0){
                f.travelAnim(map);
            }
        }


    }

    public void moveTradeFleetAnim(String name, SolarSystem[] map){
        theTraitors.get(name).nextTurnAnim();
    }

    private void updateBehaviorNextTurn(Scores scores){
        /***************************************************************************************************
        This will work by either a random generator triggering a specific action or reacting to one.
         Random number generator will be influenced by the specific civilization which will add to the chance
         Reactions will occur in 3 ways - either by an enemy sending a hostile message (will be implemented in diplomacy),
         enemy troops stationed on owned system, or by seeing that someones sci/indu/cash/rawMatEx/military score is much higher.
         Message - declaration of war, close borders, trade embargo, or denouncing
         Border Dispute - if troops stationed on your systems/borders will call a method which will trigger an increase in military and science
         Low resources - at the end of each turn will increase priority of a material if it is low relative to others
         Values will be between 3 and 0.5, all of which will be used for random number generators and score calculations
         **************************************************************************************************************/

        //increases importance values based off of civ importance
        //use (numPlayers - scores.getRankingOfPlayerIndex******()) / 10
        scienceImp = (scores.getNumPlayers() + 1 - scores.getPlacementOfPlayerScience(playerIndex)) / 10.0f + scienceImpOG;
        industryImp = (scores.getNumPlayers() + 1 - scores.getPlacementOfPlayerIndustry(playerIndex)) / 10.0f + industryImpOG;
        cashImp = (scores.getNumPlayers() + 1 - scores.getPlacementOfPlayerCash(playerIndex)) / 10.0f + cashImpOG;
        militaryImp = ((scores.getNumPlayers() + 1 - scores.getPlacementOfPlayerMilitary(playerIndex)) / 10.0f + miltaryImpOG);
        rawMatExpImp = (scores.getNumPlayers() + 1 - scores.getPlacementOfPlayerRME(playerIndex)) / 10.0f + rawMatExpImpOG;

    }

    public void updateBehaviorEvent(String event){

    }

    public void decideWhatToDo(SolarSystem[] map){
        //decide what tech to do
        if(tech.getQueue().size() == 0){
            //get potential techs
            ArrayList<Tech> possibleTechs = new ArrayList<>();
            for(int i = 0; i < tech.getNumTech(); i++){
                if(!tech.getTech(i).getIfUnlocked() && tech.getTech(i).getIfPrequisitesUnlocked()){
                    possibleTechs.add(tech.getTech(i));
                }
            }

            float industry = 0;
            float food = 0;
            float rme = 0;
            float cash = 0;
            float sci = 0;
            float mil = 0;
            float manP = 0;

            for(SolarSystem sys : ownedSystems){
                industry += sys.getIndustry();
                food += sys.getFood();
                rme += sys.getRawMaterialOutput();
                cash += sys.getCash();
                sci += sys.getScience();
                manP += sys.getManpowerSupported();
            }
            mil = (int)militaryScore;

            //get score values and choose highest - each score is the benefit/importance of a tech
            double highestScore = -1;
            String bestTech = "";
            if(possibleTechs.size() > 0){
                for(int i = 0; i < possibleTechs.size(); i++){
                    if(possibleTechs.get(i).getIfUpgradeExists()){ //only checks if upgrade exists
                        Resource r = possibleTechs.get(i).getUpgrade().getStats();
                        double score = (foodImp * r.getFood() + foodImp * population * r.getFoodPopulation() + foodImp
                                * r.getFoodPercentage() * food + industryImp * r.getIndustry() + industryImp * population
                                * r.getIndustryPopulation() + industryImp * industry * r.
                                getIndustryPopulation() + scienceImp * r.getScience() + scienceImp * population * r
                                .getSciencePopulation() + scienceImp * sci * r.getSciencePercentage() + cashImp
                                * r.getCash() + cashImp * population * r.getCashPopulation() + cashImp * r.
                                getCashPercentage() * cash + rawMatExpImp * r.getRawMaterialExport() + rawMatExpImp
                                * population * r.getRawMaterialExportPopualtion() + rawMatExpImp * r.
                                getRawMaterialExportPercentage() * rme) / possibleTechs.get(i).getScienceNeeded();
                        if(possibleTechs.get(i).getUnlockedAbility().compareTo("") != 0){
                            //temporary
                            score =+ 1;
                            score *= 2;
                        }
                        if(score > highestScore){
                            highestScore = score;
                            bestTech = possibleTechs.get(i).getID();
                        }
                    } else {
                        //different branch meant to handle ones that only contain an ability.
                        double score = 2;
                        if(score > highestScore){
                            highestScore = score;
                            bestTech = possibleTechs.get(i).getID();
                        }
                    }
                }
            }
            if(bestTech.compareTo("") != 0){
                tech.selectTech(bestTech);
            }

        }




        //decide each system queue
        for(SolarSystem sys : ownedSystems){
            double chanceOfBuildShip = militaryImp /*- difficulty*/;
            //if needs to queue new thing
            if(sys.getQueue().size() <= 0){
                //chance of building a ship based off faction values
                int y = rand.nextInt(2); //at the very most will make a ship every other possible chance

                //builds something
                if(chanceOfBuildShip > y || sys.getNumUnlockable() == 0){
                    //build ship
                    //choose between colonizer, scout, and warship
                    //first, if no scout exists, it will have a 50% chance of scout
                    boolean ifScoutExists = false;
                    for(Fleet f : ownedFleets){
                        for(int i = 0; i < 4; i++){
                            for(int j = 0; j < f.getSection(i).getNumShips(); j++){
                                if(f.getSection(i).getShipAtIndex(j).getType().getPurpose().compareTo("scout") == 0){
                                    ifScoutExists = true; //chance is 1 in 2, otherwise is 1 in 20
                                    //NOTE: This only works with one scout, so it will need to be consistently upgraded
                                }
                            }
                        }
                    }
                    if(!ifScoutExists){
                        int scoutChance = rand.nextInt(2);
                        if(scoutChance == 1) {
                            //build scout
                            for (int k = 0; k < civ.getNumShipTemplates(); k++) {
                                if (civ.getShipTemplateAt(k).getPurpose().compareTo("scout") == 0) {
                                    sys.addToQueue(civ.getShipTemplateAt(k).getClone());
                                    break;
                                }
                            }
                        }
                    }else{
                        int i = rand.nextInt(20);
                        if(i != 0){
                            //builds either a warship or colonizer based off of milImp and expansionism
                            float totalChanceOriginal = (militaryImp + expanImp) * 100;
                            int totalChance = (int)totalChanceOriginal; //casted version meant to keep decimal pointage
                            int z = rand.nextInt(totalChance);
                            if(z <= (int)(militaryImp * 100) && false){
                                //first militaryImp * 100 are equal to military chance
                                //will look for 3 kinds, overallCombat, carrier, and glass cannon with
                                //gives a random number and between 0 and 5, at max, which will correspond to a type of
                                //ship type, while will then be added to the system queue
                                /***************************************************************************************
                                                 COULD COMBINE BOTH FOR LOOPS, IDK, TOO TIRED RIGHT NOW
                                 **************************************************************************************/

                                //gathers data for ship type decision
                                int numOverall = 0;
                                int numCarrier = 0;
                                int numGlass = 0;
                                for(int v = 0; v < civ.getNumShipTemplates(); v++){
                                    if(civ.getShipTemplateAt(v).getPurpose().compareTo("overallCombat") == 0){
                                        numOverall++;
                                    } else if(civ.getShipTemplateAt(v).getPurpose().compareTo("carrier") == 0){
                                        numCarrier++;
                                    } else if(civ.getShipTemplateAt(v).getPurpose().compareTo("glass") == 0){
                                        numGlass++;
                                    }
                                }

                                //decides ship type built
                                int shipChance = rand.nextInt(numOverall + numCarrier + numGlass);
                                if(shipChance < numOverall){
                                    //builds an overallCombat type ship

                                    //gets overallCombat type ships
                                    ShipTemplate[] overallCombat = new ShipTemplate[numOverall]; //each of these ships has lots of range
                                    int indexOfTemplates = 0;
                                    for(int overallIndex = 0; overallIndex < civ.getNumShipTemplates(); overallIndex++){
                                        if(civ.getShipTemplateAt(overallIndex).getPurpose().compareTo("overallCombat") == 0){
                                            overallCombat[indexOfTemplates] = civ.getShipTemplateAt(overallIndex).getClone();
                                            indexOfTemplates++;
                                        }
                                    }

                                    //compares values based off of importance
                                    //uses a weighted power system, where it adds all the values
                                    float highestScore = -1;
                                    String nameOfBest = "";
                                    for(ShipTemplate st : overallCombat){
                                        float tempScore = 0;
                                        for(Weapon weapon : st.getWeapons()){
                                            tempScore += 1.0f * weapon.getDamagePerShot() / weapon.getFireRate();
                                        }
                                        for(Defense defense : st.getDefenses()){
                                            tempScore += defense.getHitPoints() + defense.getRegenRate() + defense.getRegenTime();
                                        }
                                        if(tempScore > highestScore){
                                            nameOfBest = st.getModelName();
                                            highestScore = tempScore;
                                        }
                                    }

                                    //queues up specific ship
                                    for(ShipTemplate st : overallCombat){
                                        if(st.getModelName().compareTo(nameOfBest) == 0){
                                            sys.addToQueue(st);
                                        }
                                    }
                                } else if(shipChance >= numOverall && shipChance <= numOverall + numGlass){
                                    //builds a glass cannon

                                    //gets glass cannon type ships
                                    ShipTemplate[] glassCannon = new ShipTemplate[numGlass]; //each of these ships has lots of range
                                    int indexOfTemplates = 0;
                                    for(int glassIndex = 0; glassIndex < civ.getNumShipTemplates(); glassIndex++){
                                        if(civ.getShipTemplateAt(glassIndex).getPurpose().compareTo("glass") == 0){
                                            glassCannon[indexOfTemplates] = civ.getShipTemplateAt(glassIndex).getClone();
                                            indexOfTemplates++;
                                        }
                                    }

                                    //compares values based off of importance
                                    //uses a weighted power system, where it adds all the values
                                    float highestScore = -1;
                                    String nameOfBest = "";
                                    if(glassCannon.length != 0) {
                                        for (ShipTemplate st : glassCannon) {
                                            float tempScore = 0;
                                            for(Weapon weapon : st.getWeapons()){
                                                tempScore += 1.0f * weapon.getDamagePerShot() / weapon.getFireRate();
                                            }
                                            for(Defense defense : st.getDefenses()){
                                                tempScore += defense.getHitPoints() + defense.getRegenRate() + defense.getRegenTime();
                                            }
                                            if (tempScore > highestScore) {
                                                nameOfBest = st.getModelName();
                                                highestScore = tempScore;
                                            }
                                        }

                                        //queues up specific ship
                                        for (ShipTemplate st : glassCannon) {
                                            if (st.getModelName().compareTo(nameOfBest) == 0) {
                                                sys.addToQueue(st);
                                            }
                                        }
                                    }
                                } else {
                                    //builds a carrier
                                    for(int carrierIndex = 0; carrierIndex < civ.getNumShipTemplates(); carrierIndex++){
                                        if(civ.getShipTemplateAt(carrierIndex).getPurpose().compareTo("carrier") == 0){
                                            sys.addToQueue(civ.getShipTemplateAt(carrierIndex).getClone());
                                            break;
                                        }
                                    }
                                }

                            } else { //rest are for colonizer chance
                                for(int j = 0; j < civ.getNumShipTemplates(); j++){
                                    if(civ.getShipTemplateAt(j).getPurpose().compareTo("colonizer") == 0) {
                                        sys.addToQueue(civ.getShipTemplateAt(j).getClone());
                                        break;
                                    }
                                }
                            }
                        } else {
                            //build scout
                            for (int k = 0; k < civ.getNumShipTemplates(); k++) {
                                if (civ.getShipTemplateAt(k).getPurpose().compareTo("scout") == 0) {
                                    sys.addToQueue(civ.getShipTemplateAt(k).getClone());
                                    break;
                                }
                            }
                        }
                    }

                    //will build an upgrade
                } else if(chanceOfBuildShip <= y && sys.getNumUnlockable() != 0) {
                    ArrayList<Upgrade> possibleUpgrades = new ArrayList<>();
                    for(int i = 0; i < sys.getNumUnlockable(); i++){
                        possibleUpgrades.add(sys.getUnlockableAtIndex(i));
                    }

                    //calculate scores
                    double highestValue = -1;
                    Upgrade bestUpg = new Upgrade("empty");
                    if(possibleUpgrades.size() > 0){
                        for(Upgrade upg : possibleUpgrades){
                            Resource r = upg.getStats();
                            /**************************************************************************
                             MAN POWER HAS NOT BEEN IMPLEMENTED PROPERLY
                             **************************************************************************/
                            double score = (foodImp * r.getFood() + foodImp * sys.getPopulation() * r.getFoodPopulation() + foodImp
                                    * r.getFoodPercentage() * sys.getFood() + industryImp * r.getIndustry() + industryImp * sys.
                                    getPopulation() * r.getIndustryPopulation() + industryImp * sys.getIndustry() * r.
                                    getIndustryPopulation() + scienceImp * r.getScience() + scienceImp * sys.getPopulation() * r
                                    .getSciencePopulation() + scienceImp * sys.getScience() * r.getSciencePercentage() + cashImp
                                    * r.getCash() + cashImp * sys.getPopulation() * r.getCashPopulation() + cashImp * r.
                                    getCashPercentage() * sys.getCash() + rawMatExpImp * r.getRawMaterialExport() + rawMatExpImp
                                    * sys.getPopulation() * r.getRawMaterialExportPopualtion() + rawMatExpImp * r.
                                    getRawMaterialExportPercentage() * sys.getRawMaterialOutput()) / upg.getTotalIndustryNeeded();
                            if(score > highestValue){
                                highestValue = score;
                                bestUpg = upg;
                            }
                        }
                    }
                    if(possibleUpgrades.size() > 0){
                        if(bestUpg.getName().compareTo("empty") != 0){
                            sys.addToQueue(bestUpg);
                        }
                    }
                }
            }
            //what to prioritize
        }


        //decide to move ships
        for(Fleet f : ownedFleets) {
            //decides what type of shit it does
            boolean ifColonizer = false;
            boolean ifCombat = true;
            boolean ifScout = true;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < f.getSection(i).getNumShips(); j++) {
                    if (f.getSection(i).getShipAtIndex(j).getType().getPurpose().compareTo("colonizer") == 0) {
                        ifScout = false;
                        ifColonizer = true;
                        ifCombat = false;
                    } else if(f.getSection(i).getShipAtIndex(j).getType().getPurpose().compareTo("scout") == 0 && !ifColonizer){
                        ifCombat = false;
                        ifScout = true;
                    }
                }
            }

            //does the shit that it is supposed to shit
            if (!f.getIfMoving()) {
                if (ifColonizer){
                    //look for suitable planets in known systems and chooses it
                    short highestScore = -1;
                    boolean ifBestFound = false;
                    String nameOfBestSystem = "";
                    SolarSystem bestSys = null;
                    for (Integer i : knownSystems) {
                        short score = 0;
                        for (int j = 0; j < map[i].getPlanets().length; j++) {
                            if (map[i].getPlanets()[j].getPlanetType().compareTo("ammoniaIceGiant") == 0) {
                                score += 1;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("chthonian") == 0) {
                                score += 3;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("coreless") == 0) {
                                score += 2;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("gasGiant") == 0) {
                                score += 2;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("ice") == 0) {
                                score += 1;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("methaneIceGiant") == 0) {
                                score += 1;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("artificial") == 0) {
                                score += 15;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("iron") == 0) {
                                score += 5;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("ocean") == 0) {
                                score += 10;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("telluric") == 0) {
                                score += 20;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("arid") == 0) {
                                score += 5;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("desert") == 0) {
                                score += 3;
                            } else if (map[i].getPlanets()[j].getPlanetType().compareTo("lava") == 0) {
                                score += 4;
                            }
                        }
                        if (score > highestScore && map[i].getOwner() == -1) {
                            highestScore = score;
                            ifBestFound = true;
                            nameOfBestSystem = map[i].getName();
                            bestSys = map[i];
                        }
                    }

                    /*******************************************************************************************************
                     Will use the A* type of pathfinding algorithm. Will view all adjescent nodes and if they are closer to
                     end goal than original node, will replace old node to use for the next round of finding distance
                     ******************************************************************************************************/
                    if(!f.getIfWantsToColonize() && ifBestFound) {
                        Path bestPath = getPath(f, bestSys, map);
                        f.move(new PathWithSystems(bestPath, map), map);
                    }
                } else if(ifScout){
                    ArrayList<Integer> possibleLocations = new ArrayList<>();
                    //finds nearest unknown node and makes path to it
                   // for(int i = 0; i < ownedSystemsIndex.get())
                    if(knownSystems.size() != map.length){ //no need to waste cpu if there is no possible scouting
                        for(Integer inte : knownSystems) {
                            for (int i = 0; i < map[inte].getConnections().size(); i++) {
                                int index = Integer.parseInt(map[inte].getConnections().get(i).substring(6));
                                boolean ifIndexIsFound = false;
                                for(int j = 0; j < knownSystems.size(); j++){
                                    if(knownSystems.get(j) == index){
                                        ifIndexIsFound = true;
                                    }
                                }
                                if(!ifIndexIsFound){
                                    //checks to make sure it doesnt already exist
                                    boolean ifAlreadySelected = false;
                                    for(int k = 0; k < possibleLocations.size(); k++) {
                                        if(possibleLocations.get(k) == index){
                                            ifAlreadySelected = true;
                                        }
                                    }
                                    if(!ifAlreadySelected && !f.getIfWantsToColonize()) {
                                        //System.out.println("DO NOT COLONIZE");
                                        //adds to possible locations
                                        possibleLocations.add(index);
                                    }
                                }
                            }
                        }
                    }
                    double distanceToClosest = 1000000;
                    String nameOfClosest = "";
                    for(int i = 0; i < possibleLocations.size(); i++){
                        for(int j = 0; j < map.length; j++){
                            if(possibleLocations.get(i) == j){
                                //compares
                                Path p = getPath(f, map[j], map);
                                if(p.getDistance(map) < distanceToClosest){
                                    nameOfClosest = map[j].getName();
                                    distanceToClosest = p.getDistance(map);
                                }
                            }
                        }
                    }
                    if(nameOfClosest.compareTo("") != 0){
                        for(int i = 0; i < map.length; i++){
                            if(map[i].getName().compareTo(nameOfClosest) == 0){
                                Path p = getPath(f, map[i], map);
                                PathWithSystems p2 = new PathWithSystems(p, map);
                                f.move(p2, map);
                            }
                        }
                    }
                }
            }
        }
    }

    private Path getPath(Fleet f, SolarSystem bestSys, SolarSystem[] map){
        Path bestPath = new Path(f.getDepartedFrom());
        boolean ifAllFound = false;
        int planetsGoneThroughPath = 0;
        int x = 0;
        int exceptionCounter = 0;
        while (!ifAllFound) {
            double g = 0; //distance from start node
            double h = 0; //distance from end node

            double lowestScore = 1000;
            boolean ifNextSystemExists = false;
            SolarSystem nextSystem = null;
            //get connections of latest in path
            for (int i = 0; i < bestPath.getSysLastInPath(map).getConnections().size(); i++) {
                //get connection system
                SolarSystem conn = null;
                //boolean ifConnKnown = false;
                for (int k = 0; k < map.length; k++) {
                    if (map[k].getName().compareTo(bestPath.getSysLastInPath(map).getConnections().get(i)) == 0) {
                        conn = map[k];
                    }
                }

                g = Math.sqrt(Math.pow(conn.getX() - bestPath.getSysAtInPath(map, x).getX(), 2) + Math.
                        pow(conn.getY() - bestPath.getSysAtInPath(map, x).getY(), 2));
                h = Math.sqrt(Math.pow(conn.getX() - bestSys.getX(), 2) + Math.pow(conn.getY() - bestSys.
                        getY(), 2));
                //rank them on the lowest g+h values
                if ((h + g) < lowestScore) {
                    nextSystem = conn;
                    lowestScore = h + g;
                }

            }
            exceptionCounter++;
            //add lowest one to path
            if(!bestPath.getIfAlreadyAdded(nextSystem.getName())){
                bestPath.addToPath(nextSystem);
                x++;
            }


            if(exceptionCounter > 30){
                bestPath.addToPath(bestSys);
                ifAllFound = true;
            }
            //check if found the best system
            if (nextSystem.getName().compareTo(bestSys.getName()) == 0) {
                ifAllFound = true;
            }
        }
        return bestPath;
    }

    private Path getPath(TradeFleet t, SolarSystem bestSys, SolarSystem[] map){
        Path bestPath = new Path(t.getDepartedFrom());
        boolean ifAllFound = false;
        int planetsGoneThroughPath = 0;
        int x = 0;
        int exceptionCounter = 0;
        while (!ifAllFound) {
            double g = 0; //distance from start node
            double h = 0; //distance from end node

            double lowestScore = 1000;
            boolean ifNextSystemExists = false;
            SolarSystem nextSystem = null;
            //get connections of latest in path
            for (int i = 0; i < bestPath.getSysLastInPath(map).getConnections().size(); i++) {
                //get connection system
                SolarSystem conn = null;
                //boolean ifConnKnown = false;
                for (int k = 0; k < map.length; k++) {
                    if (map[k].getName().compareTo(bestPath.getSysLastInPath(map).getConnections().get(i)) == 0) {
                        conn = map[k];
                    }
                }

                g = Math.sqrt(Math.pow(conn.getX() - bestPath.getSysAtInPath(map, x).getX(), 2) + Math.
                        pow(conn.getY() - bestPath.getSysAtInPath(map, x).getY(), 2));
                h = Math.sqrt(Math.pow(conn.getX() - bestSys.getX(), 2) + Math.pow(conn.getY() - bestSys.
                        getY(), 2));
                //rank them on the lowest g+h values
                if ((h + g) < lowestScore) {
                    nextSystem = conn;
                    lowestScore = h + g;
                }

            }
            exceptionCounter++;
            //add lowest one to path
            if(!bestPath.getIfAlreadyAdded(nextSystem.getName())){
                bestPath.addToPath(nextSystem);
                x++;
            }


            if(exceptionCounter > 30){
                bestPath.addToPath(bestSys);
                ifAllFound = true;
            }
            //check if found the best system
            if (nextSystem.getName().compareTo(bestSys.getName()) == 0) {
                ifAllFound = true;
            }
        }
        return bestPath;
    }


    private void uncoverSystems(SolarSystem[] map){
        ArrayList<Integer> tempKnown = new ArrayList<>();
        for(int j = 0; j < map.length; j++){
            for(SolarSystem ownedSys : ownedSystems){
                //checks if sys is within ownedSys view distance
                double distanceX, distanceY, distance;
                distanceX = map[j].getX() - ownedSys.getX();
                distanceY = map[j].getY() - ownedSys.getY();
                distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                //if in range
                if(distance < ownedSys.getViewDistance()){
                    //checks if sys is already added
                    boolean ifAlreadyAdded = false;
                    for(int i = 0; i < tempKnown.size(); i++){
                        if(tempKnown.get(i) == j){
                            ifAlreadyAdded = true;
                        }
                    }
                    if(!ifAlreadyAdded){
                        tempKnown.add(j);
                    }
                }
            }
        }
        knownSystems = tempKnown;
    }

    public void updateFleets(){
        for(Fleet fleet : ownedFleets){
            fleet.nextTurn();
        }

        Set<String> keys = theTraitors.keySet();
        for(String str : keys){
            theTraitors.get(str).nextTurn();
        }
    }


    public ArrayList<Fleet> getNewlyConstructedFleets(){
        ArrayList<Fleet> temp = new ArrayList<>();
        for(Fleet fleet : newlyConstructed){
            temp.add(fleet);
        }

        newlyConstructed.clear();
        return temp;
    }

    public ArrayList<TradeFleet> getNewlyConstructedTraders(){
        if(newlyConstructedTraders.size() > 0) {
            ArrayList<TradeFleet> temp = new ArrayList<>();
            for(TradeFleet tf : newlyConstructedTraders){
                temp.add(tf);
            }
            newlyConstructedTraders.clear();
            return temp;
        }
        return null;
    }

    public SolarSystem getSystem(String name){
        for(SolarSystem sys : ownedSystems){
            if(name.compareTo(sys.getName()) == 0){
                return sys;
            }
        }
        return null;
    }


    public void colonizeSystem(SolarSystem sys, int index, int planetIndex){
        ownedSystems.add(sys);
        ownedSystemsIndex.add(index);
        ownedSystems.get(ownedSystems.size() - 1).colonize(playerIndex, planetIndex);
    }

    public void destroyFleet(Fleet fleet){
        for(int i = 0; i < ownedFleets.size(); i++){
            if(ownedFleets.get(i).compareTo(ownedFleets.get(i)) == 0){
                ownedFleets.remove(i);
            }
        }
    }

    public void addShip(ShipTemplate template){
        civ.addShipTemplate(template);
    }

    public ArrayList<String> getUnlockedAbilities(){ return unlockedAbilities; }

    public boolean getIfCanBuildShip(){
        for(int i = 0; i < unlockedAbilities.size(); i++){
            //if(unlockedAbilities.get(i).compareTo("buildShip") == 0){
                return true;
            //}
            /**********************************************************************************************************
                            Make sure to later implement something here, idk i am too tired
             *********************************************************************************************************/
        }
        return false;
    }

    public Score getScore(){
        int sciScore = tech.getTechScore();
        int indScore = 0;
        int casScore = 0;
        int milScore = 0;
        int rmeScore = 0;
        for(SolarSystem sys : ownedSystems){
            indScore += sys.getIndustry();
            casScore += sys.getCash();
            milScore += sys.getManpowerSupported();
            rmeScore += sys.getRawMaterialOutput();
        }
        for(Fleet f : ownedFleets){
            milScore += f.getPower();
        }

        return new Score(sciScore, indScore, casScore, milScore, rmeScore);
    }


    public boolean getIfWantsToColonize(){
        for(Fleet f : ownedFleets){
            if(f.getIfWantsToColonize()){
                return true;
            }
        }
        return false;
    }

    public String[] getNamesOfNewColonies(){
        ArrayList<String> names = new ArrayList<>();
        for(Fleet f : ownedFleets){
            if(f.getIfWantsToColonize()){
                names.add(f.getDepartedFrom().getName());
            }
        }

        //returns a regular array so that the arraylist will be removed to save some memory. Might want to take
        //this out as it saves next to no memory, while also adding a little bit of cpu usage
        String[] names2 = new String[names.size()];
        for(int i = 0; i < names.size(); i++){
            names2[i] = names.get(i);
        }
        return names2;
    }

    public boolean getIfWantToColonizeThisSystem(String sys) {
        for (Fleet f : ownedFleets) {
            if (f.getIfWantsToColonize()) {
                return true;
            }
        }
        return false;
    }

    public Color getColorAsColor(){
        if(color.compareTo("blue") == 0){
            return Color.BLUE;
        } else if(color.compareTo("red") == 0){
            return Color.RED;
        } else if(color.compareTo("yellow") == 0){
            return Color.YELLOW;
        } else if(color.compareTo("green") == 0){
            return Color.GREEN;
        } else if(color.compareTo("orange") == 0){
            return Color.ORANGE;
        } else if(color.compareTo("purple") == 0){
            return Color.PURPLE;
        } else if(color.compareTo("pink") == 0){
            return Color.PINK;
        } else if(color.compareTo("teal") == 0){
            return Color.TEAL;
        } else {
            return Color.BEIGE;
        }
    }

    public int getIndexOfBestPlanetToColonize(SolarSystem sys){
        int highestScore = -1;
        int index = -1;
        for (int i = 0; i < sys.getPlanets().length; i++) {
            int score = 0;
            if(sys.getPlanets()[i].getPlanetType().compareTo("ammoniaIceGiant") == 0) {
                score = 1;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("chthonian") == 0) {
                score = 3;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("coreless") == 0) {
                score = 2;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("gasGiant") == 0) {
                score = 2;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("ice") == 0) {
                score = 1;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("methaneIceGiant") == 0) {
                score = 1;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("artificial") == 0) {
                score = 15;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("iron") == 0) {
                score = 5;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("ocean") == 0) {
                score = 10;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("telluric") == 0) {
                score = 20;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("arid") == 0) {
                score = 5;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("desert") == 0) {
                score = 3;
            } else if (sys.getPlanets()[i].getPlanetType().compareTo("lava") == 0) {
                score = 4;
            }
            if(score > highestScore){
                highestScore = score;
                index = i;
            }
        }
        return index;
    }

    public void destroyColonizer(String nameOfSys){
        for(int f = 0; f < ownedFleets.size(); f++){
            if(ownedFleets.get(f).getDepartedFrom().getName().compareTo(nameOfSys) == 0 && !ownedFleets.get(f).getIfMoving()){
                for(int i = 0; i < 4; i++) { //goes through the sections
                    if (ownedFleets.get(f).getSection(i).getNumShips() > 0) {
                        for (int j = 0; j < ownedFleets.get(f).getSection(i).getNumShips(); j++) {
                            if (ownedFleets.get(f).getSection(i).getShipAtIndex(j).getType().getPurpose().compareTo("colonizer") == 0) {
                                ownedFleets.get(f).removeShip(ownedFleets.get(f).getSection(i).getShipAtIndex(j).getID());

                                //tests if fleet is empty
                                boolean ifEmpty = true;
                                for (int k = 0; k < 4; k++) {
                                    if (ownedFleets.get(f).getSection(k).getNumShips() > 0) {
                                        ifEmpty = false;
                                    }
                                }
                                if (ifEmpty) {
                                    ownedFleets.remove(f);
                                    f--;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<ShipTemplate> getShips(){
        ArrayList<ShipTemplate> ships = new ArrayList<>();
        for(int i = 0; i < civ.getNumShipTemplates(); i++){
            ships.add(civ.getShipTemplateAt(i));
        }
        return ships;
    }

    public void removeShip(String nameOfShip){
        civ.removeShip(nameOfShip);
    }

    public void setUpTradeRoute(String nameOfTradeShip, SolarSystem finalDestination, SolarSystem[] map){
        Path path = getPath(theTraitors.get(nameOfTradeShip), finalDestination, map);
        PathWithSystems pathWithSystems = new PathWithSystems(path, map);
        theTraitors.get(nameOfTradeShip).createTradeRoute(pathWithSystems);
    }

    public TradeFleet getTradeFleet(String iD){
        return theTraitors.get(iD);
    }

    private void checkForIfTradeArrived(){
        //goes through all trade fleets and will checks if it arrives. If it does, it will increase pop of system,
        //as well as return the ship back. If the ship arrives on its way back it will reset to a selectable position
        Set<String> keys = theTraitors.keySet();
        for(String str : keys){
            if(theTraitors.get(str).getIfArrivedAtAPlace()){
                //only if it has just arrived at the end of a turn
                if(theTraitors.get(str).getIfMovingTowardsTarget() == 1){
                    //if arrived at destination gives food
                    String nameOfSystem = theTraitors.get(str).getDepartedFrom().getName();
                    for(SolarSystem sys : ownedSystems){
                        if(sys.getName().compareTo(nameOfSystem) == 0){
                            sys.boostPopulation(theTraitors.get(str).getFoodStored());
                            break;
                        }
                    }

                    //resets path
                    theTraitors.get(str).turnAround();
                } else if(theTraitors.get(str).getIfMovingTowardsTarget() == -1){
                    //if returned
                    //GIVE A BUNCH OF CASH WHEN THIS IS SETUP
                }
            }
        }
    }

    public void doNextTurnCheck(){
        checkForIfTradeArrived();
    }
}

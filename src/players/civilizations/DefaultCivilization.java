package players.civilizations;

import map.planets.Planet;
import planetUpgrades.Upgrade;
import ships.chassises.*;
import ships.shipComponents.defense.Defense;
import ships.shipComponents.defense.HullArmorDefense;
import ships.shipComponents.weapons.ProjectileWeapon;
import ships.shipComponents.weapons.Weapon;
import ships.tradeRoutes.tradeships.TempTradeShip;
import techTree.techs.Tech;
import techTree.TechTree;

import java.util.ArrayList;

public class DefaultCivilization implements Civilization {
    //This class essentially sets up ships and upgrades, as well as stores them

    private ArrayList<Upgrade> undiscoveredUpgrades = new ArrayList<>();
    private ArrayList<Upgrade> discoveredUpgrades = new ArrayList<>();
    private ArrayList<ShipTemplate> buildableShipTemplates = new ArrayList<>();
    private String starName = "yellow";
    private int starSize = 1;
    private Planet[] planets = {new Planet("telluric", 2, true, 1), new Planet("telluric", 2, false, 0)};
    private ArrayList<Weapon> discoveredWeapons = new ArrayList<>();
    private ArrayList<Defense> discoveredDefense = new ArrayList<>();

    private ArrayList<ShipTemplate> chassis = new ArrayList<>();

    public DefaultCivilization(){
        discoveredWeapons.add(new ProjectileWeapon("projectileTest"));
        discoveredDefense.add(new HullArmorDefense("test"));
        ShipTemplate tempShip = new TestShipTemplate(3, 2, "tempFighter4");
        Weapon[] temp = new Weapon[tempShip.getNumWeapons()];
        for(int i = 0; i < temp.length; i++){
            temp[i] = discoveredWeapons.get(0);
        }
        Defense[] temp2 = new Defense[tempShip.getNumDefenses()];
        for(int i = 0; i < temp2.length; i++){
            temp2[i] = discoveredDefense.get(0);
        }
        tempShip.setStats(temp, temp2);
        addShipTemplate(tempShip);
        tempShip = new ColonizerTemplate(2, 2, "tempColonizer3");
        temp = new Weapon[tempShip.getNumWeapons()];
        for(int i = 0; i < temp.length; i++){
            temp[i] = discoveredWeapons.get(0);
        }
         temp2 = new Defense[tempShip.getNumDefenses()];
        for(int i = 0; i < temp2.length; i++){
            temp2[i] = discoveredDefense.get(0);
        }
        tempShip.setStats(temp, temp2);
        addShipTemplate(tempShip);
        tempShip = new BasicScoutTemplate(4, 2, "tempScout2");
        temp = new Weapon[tempShip.getNumWeapons()];
        for(int i = 0; i < temp.length; i++){
            temp[i] = discoveredWeapons.get(0);
        }
        temp2 = new Defense[tempShip.getNumDefenses()];
        for(int i = 0; i < temp2.length; i++){
            temp2[i] = discoveredDefense.get(0);
        }
        tempShip.setStats(temp, temp2);
        addShipTemplate(tempShip);
        tempShip = new TestGlass(2, 2, "tempGlass1");
        temp = new Weapon[tempShip.getNumWeapons()];
        for(int i = 0; i < temp.length; i++){
            temp[i] = discoveredWeapons.get(0);
        }
        temp2 = new Defense[tempShip.getNumDefenses()];
        for(int i = 0; i < temp2.length; i++){
            temp2[i] = discoveredDefense.get(0);
        }
        tempShip.setStats(temp, temp2);
        addShipTemplate(tempShip);


        tempShip = new TempTradeShip("TradeJoes", 2);
        addShipTemplate(tempShip);

        //sets up possible chassises to use
        chassis.add(new TestShipTemplate(2, 40));
        chassis.add(new TestGlass(2, 40));
        chassis.add(new ColonizerTemplate(2, 15));
        chassis.add(new BasicScoutTemplate(4, 5));
    }

    public Planet[] getHomeSystem(){ return planets; }
    public String getStarName(){ return starName; }
    public int getStarSize(){ return starSize; }

    public void initializeUpgrades(TechTree tree) {
        for(int i = 0; i < tree.getNumTech(); i++){
            if(tree.getTech(i).getIfUpgradeExists() && tree.getTech(i).getIfUnlocked()){
                undiscoveredUpgrades.add(tree.getTech(i).getUpgrade());
            }
        }
    }

    public ArrayList<Upgrade> getUpgrades(){
        return discoveredUpgrades;
    }

    public String getCivID(){
        return "defCiv";
    }

    public void unlockUpgrade(Tech tech){
        if(tech.getIfUpgradeExists()){
            discoveredUpgrades.add(tech.getUpgrade());
        }
    }

    public int getNumUpgrades(){ return discoveredUpgrades.size(); }
    public Upgrade getUpgradeAt(int index){ return discoveredUpgrades.get(index); }

    //ship stuff
    public int getNumShipTemplates(){ return buildableShipTemplates.size(); }
    public void addShipTemplate(ShipTemplate ship){
        buildableShipTemplates.add(ship);
    }
    public ShipTemplate getShipTemplateAt(int index){ return buildableShipTemplates.get(index); }
    public ArrayList<Weapon> getWeapons(){ return discoveredWeapons; }
    public ArrayList<Defense> getDefenses(){ return discoveredDefense; }


    public void unlockWeapon(Weapon newWeapon){
        discoveredWeapons.add(newWeapon);
    }

    public void unlockDefense(Defense newDefense){
        discoveredDefense.add(newDefense);
    }

    public int getNumShipChassis(){
        return chassis.size();
    }

    public ArrayList<ShipTemplate> getChassises(){ return chassis; }

    public void removeShip(String shipName){
        for(int i = 0; i < buildableShipTemplates.size(); i++){
            if(shipName.compareTo(buildableShipTemplates.get(i).getModelName()) == 0){
                buildableShipTemplates.remove(i);
                break;
            }
        }
    }
}

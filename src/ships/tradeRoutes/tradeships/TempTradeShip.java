package ships.tradeRoutes.tradeships;

import map.systems.SolarSystem;
import players.aiComponents.PathWithSystems;
import ships.chassises.ShipTemplate;
import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;
import ships.tradeRoutes.TradeRoute;

public class TempTradeShip implements ShipTemplate {

    private String id;
    private String modelName;

    private double movement = 5.0;
    private int productionNeeded;
    private int initialProductionNeeded;


    public TempTradeShip(String id, int initialProductionNeeded){
        this.id = id;
        modelName = id;
        this.initialProductionNeeded = initialProductionNeeded;
        productionNeeded = initialProductionNeeded;
    }



    public String getId(){ return id; }



    public double getMovement() {
        return movement;
    }

    public String getShipClass() {
        return "TempTradeShip";
    }

    public int getTotalProductionNeeded() {
        return productionNeeded;
    }

    public int getInitialProductionNeeded() {
        return initialProductionNeeded;
    }

    public String[] getActions() {
        String[] temp = new String[]{"createTradeRoute"};
        return temp;
    }

    public String getModelName() {
        return modelName;
    }

    public String getPurpose() {
        return "trader";
    }



    public void build(int production) {
        productionNeeded -= production;
    }

    public boolean ifBuilt() {
        if(productionNeeded <= 0){
            return true;
        }
        return false;
    }

    public ShipTemplate getClone() {
        return new TempTradeShip(id, initialProductionNeeded);
    }

    public void changeName(String newName) {
        this.modelName = newName;
    }

    //All of these are null cause i am too lazy to make a new type of entity for traders, and this will save a few days
    //of time
    public boolean getIfOnlyChassis() { return false; }
    public ShipTemplate getNonTemplateVersion(String newName) { return null; }
    public void addWeapon(Weapon newWeapon) {}
    public void addDefense(Defense newDefense) {}
    public void replaceWeapon(Weapon newWeapon, Weapon oldWeapon) {}
    public void replaceDefense(Defense newDefense, Defense oldDefense) {}
    public int getNumWeapons() { return 0; }
    public int getNumDefenses() { return 0; }
    public Weapon[] getWeapons() { return new Weapon[0]; }
    public Defense[] getDefenses() { return new Defense[0]; }
    public void setStats(Weapon[] weapons, Defense[] defenses) {}

    public double getHP(){ return 0.1; }
}

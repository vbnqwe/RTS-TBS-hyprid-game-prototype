package ships.chassises;

import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;

public class TestGlass implements ShipTemplate {

    //stores attack, defense, movement, as well as promotion multipliers

    private String name;
    private String shipClass;
    private String purpose;
    private double movement;
    private int totalProductionNeeded;
    private int initialProductionNeeded;
    private boolean ifOnlyChassis;
    private double baseHP = 10;

    private String[] actions;
    private Weapon[] weapons;
    private Defense[] defenses;


    private int projectile;
    private int missile;
    private int hitScan;
    private int hullArmor;
    private int shieldStrength;
    private int numProjectile;
    private int numMissile;
    private int numHitScan;

    public TestGlass(double movement, int totalProductionNeeded, String name){
        this.movement = movement;
        this.totalProductionNeeded = totalProductionNeeded;
        initialProductionNeeded = totalProductionNeeded;
        shipClass = "testGlass";
        String[] action = {"idle", "attack", "blockade/guard"};
        actions = action;
        this.name = name;
        purpose = "glass";
        ifOnlyChassis = false;

        weapons = new Weapon[5];
        defenses = new Defense[1];

    }

    public TestGlass(double movement, int totalProductionNeeded){
        ifOnlyChassis = true;
        shipClass = "glass";
        this.movement = movement;
        this.totalProductionNeeded = totalProductionNeeded;
        initialProductionNeeded = totalProductionNeeded;
        String[] action = {"idle", "attack", "blockade/guard"};
        actions = action;
        purpose = "glass";

        weapons = new Weapon[5];
        defenses = new Defense[1];
    }

    public void setStats(Weapon[] weapons, Defense[] defenses){
        for(Weapon wep: weapons){
            this.addWeapon(wep);
        }
        for(Defense def : defenses){
            this.addDefense(def);
        }
    }

    public String getModelName(){ return name; }

    public String getPurpose(){ return purpose; }


    public double getMovement(){ return movement; }

    public String getShipClass(){ return shipClass; }

    public int getTotalProductionNeeded(){ return totalProductionNeeded; }

    public String[] getActions(){ return actions; }

    public void build(int production){
        totalProductionNeeded -= production;
    }

    public boolean ifBuilt(){
        if(totalProductionNeeded <= 0){
            totalProductionNeeded = initialProductionNeeded;
            return true;
        }
        return false;
    }

    public int getPower(){
        return projectile + missile + hitScan + hullArmor + shieldStrength;
    }
    public int getProjectile(){
        return projectile;
    }
    public int getMissile(){
        return missile;
    }
    public int getHitScan(){
        return hitScan;
    }
    public int getHullArmor(){
        return hullArmor;
    }
    public int getShieldStrength(){
        return shieldStrength;
    }
    public int getNumProjectile(){
        return numProjectile;
    }
    public int getNumMissile(){
        return numMissile;
    }
    public int getNumHitScan(){
        return numHitScan;
    }

    public double getHP(){
        double hp = baseHP;
        for(Defense def : defenses){
            if(def != null) {
                if (def.getType().substring(0, 4).compareTo("hull") == 0) {
                    hp += def.getHitPoints();
                }
            }
        }
        return hp;
    }

    public int getInitialProductionNeeded(){
        return initialProductionNeeded;
    }

    public ShipTemplate getClone(){
        TestGlass temp = new TestGlass(movement, initialProductionNeeded, name);
        temp.setStats(weapons, defenses);
        return temp;
    }

    public ShipTemplate getNonTemplateVersion(String newName){
        return new TestGlass(movement, initialProductionNeeded, newName);
    }

    public void addWeapon(Weapon newWeapon){
        for(int i = 0; i < weapons.length; i++){
            if(weapons[i] == null){
                weapons[i] = newWeapon;
                break;
            }
        }
    }

    public void addDefense(Defense newDefense){
        for(int i = 0; i < defenses.length; i++){
            if(defenses[i] == null){
                defenses[i] = newDefense;
                break;
            }
        }
    }

    public void replaceWeapon(Weapon newWeapon, Weapon oldWeapon){
        for(int i = 0; i < weapons.length; i++){
            if(weapons[i].getType().compareTo(oldWeapon.getType()) == 0){
                weapons[i] = newWeapon;
                break;
            }
        }
    }

    public void replaceDefense(Defense newDefense, Defense oldDefense){
        for(int i = 0; i < defenses.length; i++){
            if(defenses[i].getType().compareTo(oldDefense.getType()) == 0){
                defenses[i] = newDefense;
                break;
            }
        }
    }

    public int getNumWeapons(){ return weapons.length; }
    public int getNumDefenses(){ return defenses.length; }

    public boolean getIfOnlyChassis(){ return ifOnlyChassis; }

    public Weapon[] getWeapons(){
        return weapons;
    }

    public Defense[] getDefenses(){
        return defenses;
    }

    public void changeName(String newName){
        name = newName;
    }
}

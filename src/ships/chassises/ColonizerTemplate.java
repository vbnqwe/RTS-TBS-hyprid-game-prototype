package ships.chassises;

import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;

public class ColonizerTemplate implements ShipTemplate {

    private String shipClass;
    private String purpose;
    private String name;
    private double movement;
    private int totalProductionNeeded;
    private int initialProductionNeeded;
    private double baseHP = 15;

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

    private boolean ifOnlyChassis;

    public ColonizerTemplate(double movement, int totalProductionNeeded, String name){
        shipClass = "colonizer";
        this.movement = movement;
        this.totalProductionNeeded = totalProductionNeeded;
        initialProductionNeeded = totalProductionNeeded;
        String[] action = {"idle", "attack", "blockade/guard", "colonize"};
        actions = action;
        this.name = name;
        purpose = "colonizer";
        ifOnlyChassis = false;

        weapons = new Weapon[0];
        defenses = new Defense[2];

    }

    public ColonizerTemplate(double movement, int totalProductionNeeded){
        ifOnlyChassis = true;
        shipClass = "colonizer";
        this.movement = movement;
        this.totalProductionNeeded = totalProductionNeeded;
        initialProductionNeeded = totalProductionNeeded;
        String[] action = {"idle", "attack", "blockade/guard", "colonize"};
        actions = action;
        purpose = "colonizer";

        weapons = new Weapon[0];
        defenses = new Defense[2];
    }

    public int getInitialProductionNeeded(){
        return initialProductionNeeded;
    }

    public void setStats(Weapon[] weapons, Defense[] defenses){
        for(Weapon wep: weapons){
            this.addWeapon(wep);
        }
        for(Defense def : defenses){
            this.addDefense(def);
        }
    }

    public double getMovement(){ return movement; }
    public String getShipClass(){ return shipClass; }
    public int getTotalProductionNeeded(){ return totalProductionNeeded; }
    public String[] getActions(){ return actions; }

    //building stuff
    public void build(int production){
        totalProductionNeeded -= production;
    }
    public boolean ifBuilt(){
        if(totalProductionNeeded <= 0){
            return true;
        }
        return false;
    }

    public String getModelName(){ return name; }
    public String getPurpose(){ return purpose; }

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

    public ShipTemplate getClone(){
        ColonizerTemplate temp = new ColonizerTemplate(movement, initialProductionNeeded, name);
        temp.setStats(weapons, defenses);
        return temp;
    }

    public ShipTemplate getNonTemplateVersion(String newName){
        return new ColonizerTemplate(movement, initialProductionNeeded, newName);
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

package ships.chassises;


import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;

public class BasicScoutTemplate implements ShipTemplate {

    private String shipClass;
    private String purpose;
    private String name;
    private double movement;
    private int totalProductionNeeded;
    private int initialProductionNeeded;
    private int productionFromChassis = 5;
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

    private boolean ifOnlyChassis;

    public BasicScoutTemplate(double movement, int totalProductionNeeded, String name){
        shipClass = "basicScout";
        this.movement = movement;
        this.totalProductionNeeded = productionFromChassis;
        initialProductionNeeded = totalProductionNeeded;
        String[] action = {"idle", "attack", "blockade/guard"};
        actions = action;
        this.name = name;
        purpose = "scout";
        ifOnlyChassis = false;

        //sets the maximum amount of the weapon and defense nodes
        weapons = new Weapon[1];
        defenses = new Defense[2];
    }

    public BasicScoutTemplate(double movement, int totalProductionNeeded){
        shipClass = "basicScout";
        this.movement = movement;
        this.totalProductionNeeded = productionFromChassis;
        String[] action = {"idle", "attack", "blockade/guard"};
        actions = action;
        purpose = "scout";
        ifOnlyChassis = true;

        weapons = new Weapon[1];
        defenses = new Defense[2];
    }

    public double getMovement() {
        return movement;
    }

    public String getShipClass() {
        return shipClass;
    }

    public int getTotalProductionNeeded() {
        return totalProductionNeeded;
    }

    public String[] getActions() {
        return actions;
    }

    public String getModelName() {
        return name;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setStats(Weapon[] weapons, Defense[] defenses) {
        for(Weapon wep: weapons){
            this.addWeapon(wep);
        }
        for(Defense def : defenses){
            this.addDefense(def);
        }
    }

    public void build(int production) {
        totalProductionNeeded -= production;
    }

    public boolean ifBuilt() {
        if(totalProductionNeeded <= 0){
            return true;
        }
        return false;
    }

    public int getPower() {
        return projectile + missile + hitScan + hullArmor + shieldStrength;
    }

    public int getProjectile() {
        return projectile;
    }

    public int getMissile() {
        return missile;
    }

    public int getHitScan() {
        return hitScan;
    }

    public int getHullArmor() {
        return hullArmor;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }

    public int getNumProjectile() {
        return numProjectile;
    }

    public int getNumMissile() {
        return numMissile;
    }

    public int getNumHitScan() {
        return numHitScan;
    }

    public int getInitialProductionNeeded(){
        return initialProductionNeeded;
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
        BasicScoutTemplate temp = new BasicScoutTemplate(movement, initialProductionNeeded, name);
        temp.setStats(weapons, defenses);
        return temp;
    }

    public ShipTemplate getNonTemplateVersion(String newName){
        return new BasicScoutTemplate(movement, initialProductionNeeded, newName);
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

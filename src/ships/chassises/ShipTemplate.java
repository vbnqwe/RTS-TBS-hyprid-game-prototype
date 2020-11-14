package ships.chassises;

import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;

public interface ShipTemplate {

    public double getMovement();
    public String getShipClass();
    public int getTotalProductionNeeded();
    public int getInitialProductionNeeded();
    public String[] getActions();
    public String getModelName();
    public String getPurpose();
    public void setStats(Weapon[] weapons, Defense[] defenses);
    public double getHP();

    //building stuff
    public void build(int production);
    public boolean ifBuilt();
    public ShipTemplate getClone();
    public boolean getIfOnlyChassis();
    public ShipTemplate getNonTemplateVersion(String newName);

    //add stuff for the thing(ship)
    public void addWeapon(Weapon newWeapon);
    public void addDefense(Defense newDefense);
    public void replaceWeapon(Weapon newWeapon, Weapon oldWeapon);
    public void replaceDefense(Defense newDefense, Defense oldDefense);
    public int getNumWeapons();
    public int getNumDefenses();
    public Weapon[] getWeapons();
    public Defense[] getDefenses();
    public void changeName(String newName);

}

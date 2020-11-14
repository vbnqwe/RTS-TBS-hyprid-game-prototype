package players.civilizations;

import map.planets.Planet;
import planetUpgrades.*;
import ships.chassises.ShipTemplate;
import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;
import techTree.techs.Tech;
import techTree.TechTree;

import java.util.ArrayList;

public interface Civilization {
    public void initializeUpgrades(TechTree tree);
    public String getStarName();
    public int getStarSize();
    public Planet[] getHomeSystem();
    public String getCivID();
    public void unlockUpgrade(Tech tech);
    public int getNumUpgrades();
    public Upgrade getUpgradeAt(int index);

    //ship stuff
    public int getNumShipTemplates();
    public void addShipTemplate(ShipTemplate ship);
    public ShipTemplate getShipTemplateAt(int index);
    public ArrayList<Weapon> getWeapons();
    public ArrayList<Defense> getDefenses();
    public void unlockWeapon(Weapon newWeapon);
    public void unlockDefense(Defense newDefense);
    public void removeShip(String shipName);

    //ship chassises
    public int getNumShipChassis();
    public ArrayList<ShipTemplate> getChassises();

}

package players;

import players.civilizations.Civilization;
import javafx.scene.paint.Color;
import map.systems.SolarSystem;
import players.score.Score;
import ships.Fleet;
import devTools.*;
import ships.chassises.ShipTemplate;
import ships.tradeRoutes.tradeships.TradeFleet;
import techTree.TechTree;

import java.util.ArrayList;
import java.util.HashMap;

public interface Player {
    //initialization
    public void decideHomeSystem(int indexOfHomeSystem, SolarSystem homeSys);

    public void initializeUpgrades();

    //accessors
    public int getKnownSystem(int index);
    public int getOwnedSystemsIndex(int index);
    public ArrayList<Integer> getAllKnownSystems();
    public ArrayList<Integer> getAllOwnedSystemsIndex();
    public ArrayList<SolarSystem> getAllOwnedSystems();
    public boolean getIfBot();
    public String getColor();
    public Color getColorAsColor();
    public DoubleCooridinate getLocationOfFleetAt(int index);
    public String getIDOfFleetAtIndex(int index);
    public int getNumFleets();
    public Civilization getCivilization();
    public TechTree getTech();
    public boolean getIfCanBuildShip();
    public Score getScore();
    public void updateFleets();
    public SolarSystem getSystem(String name);
    public int getIndexOfBestPlanetToColonize(SolarSystem sys);
    public ArrayList<ShipTemplate> getShips();

    //Game mechanics
    public void discoverSystem(int index);
    public ArrayList<Fleet> getFleets();
    public HashMap<String, TradeFleet> getTradeFleets();
    public Fleet getFleet(String iD);
    public TradeFleet getTradeFleet(String iD);
    public void moveFleet(String id, SolarSystem target, SolarSystem[] map);
    public void selectScience(String iD);
    public void colonizeSystem(SolarSystem sys, int index, int planetIndex);
    //public void setCivilization();
    public void destroyFleet(Fleet fleet);
    public void addShip(ShipTemplate template);
    public void destroyColonizer(String nameOfSys);
    public void removeShip(String nameOfShip);
    public void setUpTradeRoute(String nameOfTradeShip, SolarSystem finalDestination, SolarSystem[] map);

    //THE IMPORTANT METHOD
    public void nextTurn(int numTurns, Scores scores, SolarSystem[] map);
    public void doNextTurnCheck();
    public void moveFleetAnim(String fleet, SolarSystem[] map);
    public void moveTradeFleetAnim(String name, SolarSystem[] map);
    public ArrayList<Fleet> getNewlyConstructedFleets();
    public ArrayList<TradeFleet> getNewlyConstructedTraders();
    public ArrayList<String> getUnlockedAbilities();

    //ai stuff
    public boolean getIfWantsToColonize();
    public String[] getNamesOfNewColonies();
    public boolean getIfWantToColonizeThisSystem(String sys);
}

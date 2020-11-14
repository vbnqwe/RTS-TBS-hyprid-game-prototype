package map;

import java.lang.reflect.Array;
import java.security.SecurityPermission;
import java.util.Random;
import java.util.ArrayList;

import devTools.Coordinate;
import map.systems.*;
import players.*;

public class Map {
    private Random rand = new Random();
    private int numSystems;
    private SolarSystem[] galaxy;
    private ArrayList<Player> players;
    private StarLine[] connections;

    private static int DISTANCEFACTOR;

    public Map(String type, int size, ArrayList<Player> players){
        DISTANCEFACTOR = size;
        /*types include disk, spiral
        size: 1 = puny, 2 = small, 3 = medium, 4 = large, 5 = huge*/
        genNumSystems(size);
        galaxy = new SolarSystem[numSystems];
        this.players = players;
        if(type.equals("disk")){
            generateDisk();
        } else if(type.equals("spiral")){
            generateSpiral();
        }else{
            System.out.println("galaxy type not recognized");
        }
        System.out.println("Complete");
    }


    private void genNumSystems(int size){
        if(size == 1){
            numSystems = rand.nextInt(5) + 15; //15 to 19
        } else if(size == 2){
            numSystems = rand.nextInt(10) + 20; //20 to 29
        } else if(size == 3){
            numSystems = rand.nextInt(10) + 30; //30 to 39
        } else if(size == 4){
            numSystems = rand.nextInt(10) + 40; //40 to 49
        } else if(size == 5){
            numSystems = rand.nextInt(10) + 50; //50 to 59
        } else if(size == 0){
            numSystems = 5;
        }
    }


    private void generateSpiral(){
        for(int i = 0; i < numSystems; i++){

        }
    }

    private void generateDisk(){
        //generaters positions of systems, as well as the contents of each system
        for(int i = 0; i < numSystems; i++){
            boolean noSystemOverlap = false;
            while(!noSystemOverlap){
                int randomNumber = rand.nextInt(2);
                if(randomNumber == 1){
                    galaxy[i] = new SolarSystem((rand.nextInt(100) + 1) * DISTANCEFACTOR,
                            (rand.nextInt(100) + 1) * DISTANCEFACTOR, "System" + i);
                } else {
                    galaxy[i] = new SolarSystem((rand.nextInt(50) + 25) * DISTANCEFACTOR,
                            (rand.nextInt(50) + 25) * DISTANCEFACTOR, "System" + i);
                }

                //tests for overlap
                boolean currentTestOfOverlap = true;
                for(int j = 0; j < i; j++){
                    if(galaxy[j].getX() == galaxy[i].getX() && galaxy[j].getY() == galaxy[i].getX()){
                        currentTestOfOverlap = false;
                    }
                }
                if(currentTestOfOverlap){
                    noSystemOverlap = true;

                    if(i < players.size()){
                        Player p = players.get(i);
                        int x = galaxy[i].getX();
                        int y = galaxy[i].getY();
                        /***********************************************************************************************
                                        Store a location for each player system based off playerNumber
                                        Could also force them to spread apart if a player
                         **********************************************************************************************/
                        galaxy[i] = new SolarSystem(x, y, i, "System" + i, players.get(i));
                        p.decideHomeSystem(i, galaxy[i]);
                    } else {
                        galaxy[i].setUpPlanets();
                    }
                }
            }
        }

        //actual world generation that works
        //add some randomization to it to make it look nice
        for(int numberOfConn = 0; numberOfConn < 6; numberOfConn++){
            int chanceOutOfTen = -1;
            if(numberOfConn == 0){
                chanceOutOfTen = 10;
            } else if(numberOfConn == 1){
                chanceOutOfTen = 3;
            } else if(numberOfConn == 2){
                chanceOutOfTen = 2;
            } else if(numberOfConn == 3){
                chanceOutOfTen = 1;
            } else if(numberOfConn == 4){
                chanceOutOfTen = 1;
            } else {
                chanceOutOfTen = 0;
            }
            for(SolarSystem sys : galaxy) {
                int roll = rand.nextInt(10); //0-10
                if((sys.getX() >= 25 * DISTANCEFACTOR && sys.getX() <= 75 * DISTANCEFACTOR) && (sys.getY() >= 25 *
                        DISTANCEFACTOR && sys.getY() <= 75 * DISTANCEFACTOR)){
                    roll += 1;
                }
                if(roll >= 10 - chanceOutOfTen && sys.getConnections().size() <= 5){
                    //adds a new connection to a nearby system
                    int[] indexOfClosestAvailableSystems = new int[2];
                    double[] scoresOfClosestAvailableSystems = new double[2];
                    int numAdded = 0;
                    int indexOfClosestAvailableSystem = -1;
                    double distance = 1000000;
                    //finds the nearest system that is not already has a connection
                    for(int i = 0; i < galaxy.length; i++){
                        if(numAdded == 2){
                            if(galaxy[i].getName().compareTo(sys.getName()) != 0 && !galaxy[i].getIfConnectionAlreadyAdded(
                                    sys.getName()) && !sys.getIfConnectionAlreadyAdded(galaxy[i].getName()) && galaxy[i].
                            getConnections().size() <= 5) {
                                double deltaX = galaxy[i].getX() - sys.getX();
                                double deltaY = galaxy[i].getY() - sys.getY();
                                double tempDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                                int indexOfFarthest = -1;
                                //compares to the entire array
                                for(int j = 0; j < indexOfClosestAvailableSystems.length; j++){
                                    if(scoresOfClosestAvailableSystems[j] > tempDistance){
                                        indexOfFarthest = j;
                                    }
                                }
                                if(indexOfFarthest != -1){
                                    scoresOfClosestAvailableSystems[indexOfFarthest] = tempDistance;
                                    indexOfClosestAvailableSystems[indexOfFarthest] = i;
                                }
                            }
                        } else {
                            if(galaxy[i].getName().compareTo(sys.getName()) != 0 && !galaxy[i].getIfConnectionAlreadyAdded(
                                    sys.getName()) && !sys.getIfConnectionAlreadyAdded(galaxy[i].getName())) {
                                indexOfClosestAvailableSystems[numAdded] = i;
                                double deltaX = galaxy[i].getX() - sys.getX();
                                double deltaY = galaxy[i].getY() - sys.getY();
                                double tempDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                                scoresOfClosestAvailableSystems[numAdded] = tempDistance;
                                numAdded++;
                            }
                        }
                        /*
                        if(galaxy[i].getName().compareTo(sys.getName()) != 0 && !galaxy[i].getIfConnectionAlreadyAdded(
                                sys.getName()) && !sys.getIfConnectionAlreadyAdded(galaxy[i].getName())) {
                            double deltaX = galaxy[i].getX() - sys.getX();
                            double deltaY = galaxy[i].getY() - sys.getY();
                            double tempDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                            if(tempDistance < distance){
                                indexOfClosestAvailableSystem = i;
                                distance = tempDistance;
                            }
                        }*/
                    }
                    /*galaxy[indexOfClosestAvailableSystem].addConnection(sys.getName());
                    sys.addConnection(galaxy[indexOfClosestAvailableSystem].getName());*/

                    int x = rand.nextInt(2);
                    galaxy[indexOfClosestAvailableSystems[x]].addConnection(sys.getName());
                    sys.addConnection(galaxy[indexOfClosestAvailableSystems[x]].getName());
                }
            }
        }

        for(SolarSystem sys : galaxy){
            sys.finalizeConnections();
        }
    }

    //ACCESSORS
    public int getNumSystems(){
        return numSystems;
    }

    public SolarSystem getSystemAt(int index){
        return galaxy[index];
    }


    public ArrayList<Player> getPlayers(){ return players; }

    public int getDistancefactor(){ return DISTANCEFACTOR; }
}

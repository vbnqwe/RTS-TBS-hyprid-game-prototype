package ships.tradeRoutes.tradeships;

import devTools.DoubleCooridinate;
import map.systems.SolarSystem;
import players.aiComponents.PathWithSystems;
import ships.Ship;
import ships.tradeRoutes.TradeRoute;

import java.util.ArrayList;

public class TradeFleet {

    private String id;
    private ArrayList<Ship> tradeShips = new ArrayList<>();

    private SolarSystem departedFrom;
    private SolarSystem departedTowards;

    private int foodStored = 200;
    private int cashGotten = 50;

    private double movement = 20;

    private int ifMovingTowardsTarget = 0; //0 is doing nothing, 1 is moving towards, -1 is returning
    private boolean ifArrivedAtAPlace = false;

    //movement variables and objects
    private TradeRoute theRouteItself;
    private int indexOfTradeRouteSpot = 0;

    private boolean ifMoving = false;
    private double distanceFromDepartedFrom;
    private double distanceFromDestination;
    private double movementLeft = movement;


    public TradeFleet(SolarSystem intitialSystem, String id, Ship initialShip){
        theRouteItself = new TradeRoute();
        this.id = id;
        this.departedFrom = intitialSystem;
        tradeShips.add(initialShip);
        movementLeft = 5.0;
    }

    public String getId(){
        return id;
    }

    public SolarSystem getDepartedFrom(){
        return departedFrom;
    }

    public void createTradeRoute(PathWithSystems path){
        ifMovingTowardsTarget = 1;
        indexOfTradeRouteSpot = 0;
        theRouteItself.setPath(path);
        ifMoving = true;
        departedTowards = path.getPathAtIndex(indexOfTradeRouteSpot + 1);
        distanceFromDestination = getDistanceFromCurrentSystemToNext();
    }

    public void returnToOrigin(PathWithSystems path){
        ifMovingTowardsTarget = -1;
        indexOfTradeRouteSpot = 0;
        theRouteItself.setPath(path);
        ifMoving = true;
        departedTowards = path.getPathAtIndex(path.getPathLength() - 1);
        distanceFromDestination = getDistanceFromCurrentSystemToNext();
    }

    private double getDistanceFromCurrentSystemToNext(){
        System.out.println(indexOfTradeRouteSpot + " " + (theRouteItself.getPath().getPathLength() - 1));
        if(indexOfTradeRouteSpot < theRouteItself.getPath().getPathLength() - 1){ //check to avoid index out of bounds
            return Math.sqrt(Math.pow(theRouteItself.getPath().getPathAtIndex(indexOfTradeRouteSpot).getX() -
                    theRouteItself.getPath().getPathAtIndex(indexOfTradeRouteSpot + 1).getX(), 2) + Math.pow(
                            theRouteItself.getPath().getPathAtIndex(indexOfTradeRouteSpot).getY() - theRouteItself.
                                    getPath().getPathAtIndex(indexOfTradeRouteSpot + 1).getY(), 2));
        }
        return -1;
    }

    public void nextTurn(){
        movementLeft = movement;
    }

    public void nextTurnAnim(){
        if(ifMoving && movementLeft >= 0){
            movementLeft -= movement / 60.0;
            distanceFromDepartedFrom += movement / 60.0;


            if(distanceFromDepartedFrom >= distanceFromDestination){
                arrive();

                /*departedFrom = departedTowards;
                if(departedFrom.getName().compareTo(theRouteItself.getPath().getLastInPath()) == 0){
                    //arrive
                    arrive();

                } else {
                    indexOfTradeRouteSpot++;
                    distanceFromDepartedFrom = 0;
                    distanceFromDestination = getDistanceFromCurrentSystemToNext();
                    departedTowards = theRouteItself.getPath().getPathAtIndex(indexOfTradeRouteSpot + 1);
                }*/
            }
        }

    }

    public void arrive(){
        distanceFromDepartedFrom = 0;
        departedFrom = departedTowards;
        indexOfTradeRouteSpot++;
        if(theRouteItself.getPath().getLastSystemInPath().getName().compareTo(this.departedFrom.getName()) == 0){
            ifMoving = false;
            indexOfTradeRouteSpot = -1;
            System.out.println(ifMovingTowardsTarget + " " + ifArrivedAtAPlace);
            if(indexOfTradeRouteSpot == 1){
                ifArrivedAtAPlace = true;
            } else if(indexOfTradeRouteSpot == -1){
                ifArrivedAtAPlace = true;
            }
            ifArrivedAtAPlace = true;
        } else {
            departedTowards = theRouteItself.getPath().getPathAtIndex(indexOfTradeRouteSpot);
        }
        distanceFromDestination = Math.sqrt(Math.pow(departedTowards.getY() - departedFrom.getY(), 2)
                + Math.pow(departedTowards.getX() - departedFrom.getX(), 2));
    }

    public void turnAround(){
        //reverses the path
        SolarSystem[] temp = theRouteItself.getReturnPath();
        PathWithSystems temp2 = new PathWithSystems(temp);

        returnToOrigin(temp2);
    }

    public DoubleCooridinate getPosition(){
        if(!ifMoving){
            return new DoubleCooridinate(departedFrom.getX(), departedFrom.getY());
        } else {
            double x, y, deltaX, deltaY, theta;
            deltaY = departedTowards.getY() - departedFrom.getY();
            deltaX = departedTowards.getX() - departedFrom.getX();
            theta = Math.atan2(deltaY, deltaX);
            y = distanceFromDepartedFrom * Math.sin(theta);
            x = distanceFromDepartedFrom * Math.cos(theta);
            return new DoubleCooridinate(departedFrom.getX() + x, departedFrom.getY() + y);
        }
    }

    public double getDistanceFromDepartedFrom(){ return distanceFromDepartedFrom; }
    public double getDistanceFromDestination(){ return distanceFromDestination; }
    public int getIfMovingTowardsTarget(){ return ifMovingTowardsTarget; }
    public boolean getIfArrivedAtAPlace(){
        boolean temp = ifArrivedAtAPlace;
        ifArrivedAtAPlace = false; //if returns true, will add either cash or food to a system
        return temp;
    }
    public int getIfMovingTowards(){
        int temp = 2;
        if(ifMovingTowardsTarget == 1){
            ifMovingTowardsTarget = -1;
            temp = 1;
        } else if(ifMovingTowardsTarget == -1){
            ifMovingTowardsTarget = 0;
            temp = -1;
        }
        return temp;
    }

    public boolean getIfMoving(){ return ifMoving; }

    public String[] getActions(){
        ArrayList<String> actions = new ArrayList<>();
        for(int i = 0; i < tradeShips.size(); i++){
            for(int j = 0; j < tradeShips.get(i).getAction().length; j++){
                boolean ifActionAlreadyAdded = false;
                for(int k = 0; k < actions.size(); k++){
                    if(actions.get(k).compareTo(tradeShips.get(i).getAction()[j]) == 0){
                        ifActionAlreadyAdded = true;
                    }
                }

                if(!ifActionAlreadyAdded){
                    actions.add(tradeShips.get(i).getAction()[j]);
                }
            }
        }


        String[] actionsFinal = new String[actions.size()];
        for(int i = 0; i < actions.size(); i++){
            actionsFinal[i] = actions.get(i);
        }

        return actionsFinal;
    }

    public int getFoodStored(){ return foodStored; }
    public int getCashGotten(){ return cashGotten; }
}

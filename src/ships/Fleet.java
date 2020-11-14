package ships;

import devTools.DoubleCooridinate;
import devTools.Vector;
import map.systems.SolarSystem;
import players.aiComponents.Path;
import players.aiComponents.PathWithSystems;

import java.util.ArrayList;

public class Fleet {
    private int i = 0;
    private Section[] sections;
    private String iD;

    private double distanceFromTarget = 0.0;
    private double travelDistance = 0.0;

    private double distanceTraveledThisTurn = 0;
    private double spotPrevious = 0;

    private SolarSystem departedFrom;
    private SolarSystem departedTowards;
    private PathWithSystems path;
    private int spotOnPath = -1;

    private boolean ifMoving = false;
    private boolean ifUsingPath = false;

    private double movement = 10;
    private double movementThisTurn = movement;

    private String[] actions;

    private int power = 0;

    private boolean ifHasColonizer = false;
    private String nameOfColonizerGoal = "";

    public Fleet(SolarSystem systemWhereItsBuilt, String iD){
        sections = new Section[4];
        sections[0] = new Section(7); //********************CHANGE WITH UPGRADES LATER*****************************
        sections[1] = new Section(7);
        sections[2] = new Section(7);
        sections[3] = new Section(7);
        departedFrom = systemWhereItsBuilt;
        departedTowards = departedFrom;
        this.iD = iD;
        updateActions();
    }

    public void calcPower(){
        power = 0;
        for(Section s : sections){
            for(int i = 0; i < s.getNumShips(); i++){
                power += s.getShipPowerAtIndex(i);
            }
        }
    }

    public int getPower(){ return power; }

    public SolarSystem getGarrison(){ return departedFrom; }
    public boolean getIfGarrisoned(){
        if(ifMoving){
            return false;
        }
        return true;
    }

    public void addShip(Ship newShip, String position){
        if(position.compareTo("vanguard") == 0){
            sections[0].add(newShip);
        } else if(position.compareTo("left") == 0){
            sections[1].add(newShip);
        } else if(position.compareTo("right") == 0){
            sections[2].add(newShip);
        } else if(position.compareTo("reserve") == 0){
            sections[3].add(newShip);
        }
        updateActions();

        if(newShip.getType().getPurpose().compareTo("colonizer") == 0){
            ifHasColonizer = true;
        }
    }

    public void removeShip(String iD){
        for(Section s : sections){
            s.remove(iD);
        }
    }

    public void nextTurn(){
        movementThisTurn = movement;
    }


    public void travelAnim(SolarSystem[] map){
        if(departedFrom.getName().compareTo(departedTowards.getName()) != 0) {
            if (movementThisTurn >= 0) { //only moves if it has any movement left
                boolean ifOnStarLine = false;
                for (SolarSystem sys : map) {
                    if (sys.getName().compareTo(departedFrom.getName()) == 0) {
                        for (String str : sys.getConnections()) {
                            if (str.compareTo(departedTowards.getName()) == 0) {
                                ifOnStarLine = true;
                            }
                        }
                    }
                }
                if (ifOnStarLine) {
                    distanceFromTarget += 1.0 * movement * 2 / 60;
                } else {
                    distanceFromTarget += 1.0 * movement / 60;
                }

                if (distanceFromTarget >= travelDistance) {
                    arrival(map);
                }
                movementThisTurn -= movement / 60;
            }
        }
    }

    public boolean getIfMoving(){ return ifMoving; }

    public void arrival(SolarSystem[] map){
        if(ifUsingPath){
            distanceFromTarget = 0;
            departedFrom = departedTowards;
            spotOnPath++;
            if(departedFrom.getName().compareTo(path.getLastInPath()) == 0){
                //stop
                ifMoving = false;
                ifUsingPath = false;
                spotOnPath = -1;
            } else {
                departedTowards = path.getPathAtIndex(spotOnPath);
            }
        }else {
            distanceFromTarget = 0;
            departedFrom = departedTowards;
            ifMoving = false;
        }
        travelDistance = Math.sqrt(Math.pow(departedTowards.getY() - departedFrom.getY(), 2)
                + Math.pow(departedTowards.getX() - departedFrom.getX(), 2));

    }

    public double getDistanceFromTarget(){
        return distanceFromTarget;
    }

    public double getMovement(){
        if (ifUsingPath) {
            return movement * 2;
        }
        return movement;
    }

    public DoubleCooridinate getPosition(){
        if(!ifMoving){
            return new DoubleCooridinate(departedFrom.getX(), departedFrom.getY());
        } else {
            double x, y, deltaX, deltaY, theta;
            deltaY = departedTowards.getY() - departedFrom.getY();
            deltaX = departedTowards.getX() - departedFrom.getX();
            theta = Math.atan2(deltaY, deltaX);
            y = distanceFromTarget * Math.sin(theta);
            x = distanceFromTarget * Math.cos(theta);
            return new DoubleCooridinate(departedFrom.getX() + x, departedFrom.getY() + y);
        }
    }

    public double getTheta(){
        double x, y, deltaX, deltaY, theta;
        deltaY = departedTowards.getY() - departedFrom.getY();
        deltaX = departedTowards.getX() - departedFrom.getX();
        theta = Math.atan2(deltaY, deltaX);
        return theta;
    }

    public double getTravelDistance(){ return travelDistance; }

    public void move(PathWithSystems path, SolarSystem[] map){
        nameOfColonizerGoal = path.getLastInPath();
        ifMoving = true;
        ifUsingPath = true;
        if(path.getPathLength() > 1) {
            departedTowards = path.getPathAtIndex(1);
        } else {
            System.out.println("Should have no available things in range");
        }
        travelDistance = Math.sqrt(Math.pow(departedTowards.getY() - departedFrom.getY(), 2)
                + Math.pow(departedTowards.getX() - departedFrom.getX(), 2));
        this.path = path;
        spotOnPath = 1;
        //travel(map);
    }

    public SolarSystem getDepartedFrom(){
        return departedFrom;
    }

    public SolarSystem getDepartedTowards(){ return departedTowards; }

    public String getID(){
        return iD;
    }

    public int compareTo(Fleet other){
        if(other.getID().compareTo(iD) == 0){
            return 0;
        }
        return 1;
    }

    public void updateActions(){
        ArrayList<String> tempActions = new ArrayList<>();
        for(Section s : sections){
            for(int i = 0; i < s.getNumShips(); i++){
                for(String str : s.getShipAtIndex(i).getAction()){
                    boolean ifAdded = false;
                    for(String st : actions){
                        if(st.compareTo(str) == 0){
                            ifAdded = true;
                        }
                    }
                    if(!ifAdded){
                        tempActions.add(str);
                    }
                }
            }
        }
        String[] temp = new String[tempActions.size()];
        for(int i = 0; i < tempActions.size(); i++){
            temp[i] = tempActions.get(i);
        }
        this.actions = temp;
    }

    public String[] getActions(){
        return actions;
    }

    public Section getSection(int type){
        return sections[type];
        //0 = vanguard, 1 = left, 2 = right, 3 = reserve
    }

    public void addPath(Path path){

    }

    public Vector getPositionAsVector(){
        double opp = departedTowards.getY() - departedFrom.getY();
        double adj = departedTowards.getX() - departedFrom.getX();
        double theta = Math.atan2(opp, adj);
        double magnitude = distanceFromTarget;
        double x = departedFrom.getX();
        double y = departedFrom.getY();
        Vector vector = new Vector(theta, magnitude, (int)x, (int)y);
        return vector;
    }

    public boolean getIfWillArrive(double moveThisMuch){
        if(moveThisMuch + distanceFromTarget >= travelDistance){
            return true;
        }
        return false;
    }


    public boolean getIfWantsToColonize(){
        if(nameOfColonizerGoal.compareTo(departedFrom.getName()) == 0 && ifHasColonizer){
            return true;
        }
        return false;
    }

}

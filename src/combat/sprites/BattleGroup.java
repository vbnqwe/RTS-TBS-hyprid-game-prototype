package combat.sprites;

import javafx.scene.control.Button;

import java.util.Vector;

public class BattleGroup extends Button {
    private Vector<String> namesOfShips = new Vector<>();
    private float x;
    private float y;
    private float prevTargetX = 0;
    private float prevTargetY = 0;
    private String groupName = "";

    private double initialWidthOfButton = 40;
    private double initialHeightOfButton = 20;
    private double widthOfButton = initialWidthOfButton;
    private double heightOfButton = initialHeightOfButton;

    public BattleGroup(Vector<ShipSprite> ships, int position, int team){
        super();
        for(ShipSprite ship : ships){
            namesOfShips.add(ship.getID());
        }

        groupName += "group";
        if(position == 0){
            groupName += "Vanguard";
        } else if(position == 1){
            groupName += "LeftFlak";
        } else if(position == 2){
            groupName += "RghtFlak";
        } else if(position == 3){
            groupName += "Reserve_";
        }
        groupName += team;

        super.setWidth(widthOfButton);
        super.setHeight(heightOfButton);
    }

    public String getID(){ return groupName; }

    public void nextFrame(Vector<ShipSprite> shipsInGroup){
        int numShips = shipsInGroup.size();
        float xTotal = 0;
        float yTotal = 0;
        for(ShipSprite shipSprite : shipsInGroup){
            xTotal += shipSprite.getPhysics().getX();
            yTotal += shipSprite.getPhysics().getY();
        }
        x = 1.0f * xTotal / numShips;
        y = 1.0f * yTotal / numShips;
    }


    public Vector<Float>[] setWayPointForGroup(Vector<ShipSprite> shipsInGroup, float xTarget, float yTarget){
        Vector<Float>[] collectionOfWayPoints = new Vector[2];
        collectionOfWayPoints[0] = new Vector<>(); //x coordinates
        collectionOfWayPoints[1] = new Vector<>(); //y coordinates
        prevTargetX = shipsInGroup.get(0).getPhysics().getX();
        prevTargetY = shipsInGroup.get(0).getPhysics().getY();

        //adds to first ship
        collectionOfWayPoints[0].add(xTarget);
        collectionOfWayPoints[1].add(yTarget);

        double theta = Math.atan2(prevTargetY - yTarget, prevTargetX - xTarget);
        //x is 40, y is 20
        double hypotenuse = Math.sqrt(80 * 80 + 40 * 40);
        double thetaFormation = Math.atan2(40, 80);


        //determines the counting
        boolean ifGoingDown = false;
        if(theta >= 0 && theta < Math.PI){
            ifGoingDown = true;
        }

        for(int i = 1; i < shipsInGroup.size(); i++){
            boolean ifShipIsToTheRightOfCenter = false;
            ifShipIsToTheRightOfCenter = shipsInGroup.get(i).getIfIsRightOfCenterOnGroup();

            float thetaForThisShip;
            if(ifGoingDown){
                if(ifShipIsToTheRightOfCenter){
                    thetaForThisShip = (float)(theta + thetaFormation);
                } else {
                    thetaForThisShip = (float)(theta - thetaFormation);
                }
            }else{
                if(ifShipIsToTheRightOfCenter){
                    thetaForThisShip = (float)(theta + thetaFormation);
                } else {
                    thetaForThisShip = (float)(theta - thetaFormation);
                }
            }

            int hypotenuseMultiplier = shipsInGroup.get(i).getRankFromFront();

            float x = (float)(hypotenuseMultiplier * hypotenuse * Math.cos(thetaForThisShip)) + xTarget;
            float y = (float)(hypotenuseMultiplier * hypotenuse * Math.sin(thetaForThisShip)) + yTarget;
            collectionOfWayPoints[0].add(x);
            collectionOfWayPoints[1].add(y);
        }

        return collectionOfWayPoints;
    }

    public Vector<String> setTargetForGroup(Vector<ShipSprite> shipsInGroup, Vector<ShipSprite> targetedGroup){
        Vector<String> namesOfTargets = new Vector<>();
        for(ShipSprite shipsOnGroupSide : shipsInGroup){
            double shortestDistance = 10000000;
            String nameOfClosest = "";
            for(ShipSprite shipsOnOtherSide : targetedGroup){
                double distance = Math.sqrt(Math.pow(shipsOnGroupSide.getPhysics().getX() - shipsOnOtherSide.getPhysics()
                        .getX(), 2) + Math.pow(shipsOnGroupSide.getPhysics().getY() - shipsOnOtherSide.getPhysics().getY
                        (), 2));
                if(nameOfClosest.compareTo("") != 0){
                    if(shortestDistance > distance){
                        shortestDistance = distance;
                        nameOfClosest = shipsOnOtherSide.getID();
                    }
                } else {
                    shortestDistance = distance;
                    nameOfClosest = shipsOnOtherSide.getID();
                }
            }

            namesOfTargets.add(nameOfClosest);
        }

        return namesOfTargets;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public double getWidthOfButton(){
        return widthOfButton;
    }

    public double getHeightOfButton(){
        return heightOfButton;
    }

    public void adjustForZoom(double zoomFactor){
        widthOfButton = zoomFactor * initialWidthOfButton;
        heightOfButton = zoomFactor * initialHeightOfButton;
    }

    public Vector<String> getNamesOfShips(){ return namesOfShips; }

    public boolean removeShip(String nameOfShip){
        for(int i = 0; i < namesOfShips.size(); i++){
            if(nameOfShip.compareTo(namesOfShips.get(i)) == 0){
                namesOfShips.remove(i);
                return true;
            }
        }
        return false;
    }


}

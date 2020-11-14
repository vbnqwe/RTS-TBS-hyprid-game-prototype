package combat.sprites.weapons;

import combat.objects.MissileWithPhysics;
import combat.sprites.ShipSprite;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.concurrent.ConcurrentHashMap;

public class Missile {

    private CircMiss circ;
    private String target;
    private MissileWithPhysics physics;
    private double framesUntilRemove = 100;

    private class CircMiss extends Circle{
        private String id;

        public CircMiss(double radius, String id){
            super(radius);
            this.id = id;
        }

        public String toString(){ return id; }
    }

    public Missile(Group root, MissileWithPhysics data, float xTarget, float yTarget, int numMissilesMade){
        target = data.getTarget();
        physics = data;
        data.setID(data.getId() + numMissilesMade);
        circ = new CircMiss(2, data.getId());
        circ.setFill(Color.PURPLE);
        circ.setCenterY(-100);
        circ.setCenterX(-100);
        root.getChildren().add(circ);
        physics.createWayPoint(xTarget, yTarget);
    }

    public MissileWithPhysics getMovementHandler(){ return physics; }

    public void nextFrame(ConcurrentHashMap<String, ShipSprite> allShips){
        physics.nextFrame();
        framesUntilRemove--;
        if(allShips.get(target) != null) {
            physics.createWayPoint(allShips.get(target).getPhysics().getX(), allShips.get(target).getPhysics().getY());
        } else {
            /*
            Create a waypoint that extends from the current velocity vector
             */
        }
    }

    public void setXDisplay(double x){
        circ.setCenterX(x);
    }

    public void setYDisplay(double y){
        circ.setCenterY(y);
    }

    public boolean checkIfRemove(Group root){
        if(framesUntilRemove <= 0){
            //removes object
            for(int i = 0; i < root.getChildren().size(); i++){
                if(root.getChildren().get(i).toString().compareTo(circ.toString()) == 0){
                    root.getChildren().remove(i);
                    return true;
                }
            }
        }

        return false;
    }
}

package combat.sprites.weapons;

import combat.objects.ObjectWithPhysics;
import combat.objects.ProjectileWithPhysics;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class projectile {

    private ProjectileWithPhysics movementHandler;
    private Circ circ;
    private int framesUntiLRemove = 240;

    private double damage;

    private class Circ extends  Circle{
        private String id;

        public Circ(double radius, String id){
            super(radius);
            this.id = id;
        }

        public String toString(){ return id; }
    }

    public projectile(ProjectileWithPhysics proj, String id, Group root, int numProjectilesMade){
        circ = new Circ(2, id);
        movementHandler = proj;
        movementHandler.setID(movementHandler.getId() + numProjectilesMade);
        circ = new Circ(2, movementHandler.getId());
        circ.setFill(Color.WHITE);
        root.getChildren().add(circ);
        circ.setLayoutX(-100);
        circ.setLayoutY(-100);
    }

    public void nextFrame(){
        movementHandler.nextFrame();
        framesUntiLRemove--;
    }

    public void setXDisplay(double x){
        circ.setLayoutX(x);
    }

    public void setYDisplay(double y){
        circ.setLayoutY(y);
    }

    public ProjectileWithPhysics getMovementHandler(){ return movementHandler; }

    public boolean checkIfRemove(Group root){
        if(framesUntiLRemove < 0){
            for(int i = 0; i < root.getChildren().size(); i++){
                if(root.getChildren().get(i).toString().compareTo(circ.toString()) == 0){
                    root.getChildren().remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    public void remove(Group root){
        for(int i = 0; i < root.getChildren().size(); i++){
            if(root.getChildren().get(i).toString() == null){
                System.out.println(root.getChildren().get(i).getClass().toString());
            }
            if (root.getChildren().get(i).toString().compareTo(circ.toString()) == 0) {
                root.getChildren().remove(i);
                break;
            }
        }
    }

    public double getDamage(){ return damage; }

}

package combat.sprites;

import combat.objects.MissileWithPhysics;
import combat.objects.ObjectWithPhysics;
import combat.objects.ProjectileWithPhysics;
import combat.sprites.weapons.Missile;
import combat.sprites.weapons.projectile;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ships.Ship;
import ships.chassises.ShipTemplate;
import ships.shipComponents.weapons.Weapon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ShipSprite extends Button {

    //data storage
    private ObjectWithPhysics object;
    private String id;
    private int team;
    private ShipTemplate shipTemplate;
    private boolean ifRightOfCenterOnGroup;
    private int rankFromFront;

    //gui parts
    private File spriteLocation;
    private Image baseSprite;
    private TempImageView sprite;
    private TempCircle center;

    //gui info
    private double width;
    private double height;

    private double widthInitial;
    private double heightInitial;

    private float wayPointX;
    private float wayPointY;
    private boolean ifUsingWayPoint = false;


    //collection of objects that have collided with ship
    private Vector<String> keysOfProjectileHits = new Vector<>();
    private Vector<String> keysOfHitScanHits = new Vector<>();
    private Vector<String> keysOfMissilesHits = new Vector<>();



    //combat stuff
    private String target = "";
    private ArrayList<ProjectileWithPhysics> newlyMadeProjectiles = new ArrayList<>();
    private ArrayList<MissileWithPhysics> newlyMadeMissiles = new ArrayList<>();
    private Vector<Integer> projCoolDowns = new Vector<>();
    private Vector<Weapon> projectiles = new Vector<>();
    private Vector<Integer> lazeCoolDowns = new Vector<>();
    private Vector<Weapon> lasers = new Vector<>();
    private Vector<Integer> missCoolDowns = new Vector<>();
    private Vector<Weapon> missiles = new Vector<>();
    private int projectilesMade = 0;
    private int missilesMade = 0;

    //combat data storage
    private double health;

    private class TempImageView extends ImageView{

        private String id;

        public TempImageView(Image image, String id){
            super(image);

            this.id = "view" + id;
        }

        public String toString(){
            return id;
        }
    }

    private class TempCircle extends Circle {
        private String id;

        public TempCircle(double radius, String id){
            super(radius);
            this.id = "tempCir" + id;
        }

        public String toString(){ return id; }
    }


    public ShipSprite(Ship ship, Group root, float x, float y, float direction){
        width = 60;
        height = 40;
        widthInitial = width;
        heightInitial = height;
        super.setPrefWidth(width);
        super.setPrefHeight(height);
        super.setOpacity(0);
        id = ship.getID();
        center = new TempCircle(20, ship.getID());
        root.getChildren().add(center);

        object = new ObjectWithPhysics(x, y, "HI", 2, 20);
        object.setVelocity(direction, 0);
        spriteLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\human ships\\scout.png");
        baseSprite = new Image(spriteLocation.toURI().toString());
        sprite = new TempImageView(baseSprite, id);
        sprite.setFitHeight(height);
        sprite.setFitWidth(width);
        root.getChildren().add(sprite);
        sprite.toFront();

        team = Integer.parseInt(id.substring(6, 7));
        if(team == 0){
            center.setFill(Color.BLUE);
        } else if(team == 1){
            center.setFill(Color.RED);
        }

        center.toBack();


        //initializes weapons
        shipTemplate = ship.getType();
        for(Weapon weap : ship.getType().getWeapons()){
            if (weap != null) {
                if(weap.getType().substring(0, 4).compareTo("prjt") == 0){
                    //adds a projectile
                    projectiles.add(weap);
                } else if(weap.getType().substring(0, 4).compareTo("misl") == 0){
                    //adds a missile
                    missiles.add(weap);
                } else if(weap.getType().substring(0, 4).compareTo("htSn") == 0){
                    //adds a hitscan
                    lasers.add(weap);
                }
            }
        }

        health = shipTemplate.getHP();
    }


    public void setX(double x){
        object.setPosition((float)x, object.getY());
        super.setLayoutX(object.getX() - width / 2);
        sprite.setLayoutX(object.getX() - width / 2);
        center.setLayoutX(object.getX());
    }

    public void setX(){
        super.setLayoutX(object.getX() - width / 2);
        sprite.setLayoutX(object.getX() - width / 2);
        center.setLayoutX(object.getX());

    }

    public void setXDisplay(double x){
        super.setLayoutX(x - width / 2);
        sprite.setLayoutX(x - width / 2);
        center.setCenterX(x);
    }

    public void setY(double y){
        object.setPosition(object.getX(), (float)(y));
        super.setLayoutY(object.getY() - height / 2);
        sprite.setLayoutY(object.getY() - height / 2);
        center.setLayoutY(object.getY());
    }

    public void setY(){
        super.setLayoutY(object.getY() - height / 2);
        sprite.setLayoutY(object.getY() - height / 2);
        center.setLayoutY(object.getY());
    }

    public void setYDisplay(double y){
        super.setLayoutY(y - height / 2);
        sprite.setLayoutY(y - height / 2);
        center.setCenterY(y);
    }

    public void setZoom(double zoomFactor){
        width = zoomFactor * widthInitial;
        height = zoomFactor * heightInitial;
        super.setMinWidth(width);
        super.setMinHeight(height);
        super.setMaxWidth(width);
        super.setMaxHeight(height);
        sprite.setFitWidth(width);
        sprite.setFitHeight(height);
        center.setRadius(20 * zoomFactor);
    }

    public void setIfRightOfCenterOnGroup(boolean ifRightOfCenterOnGroup){
        this.ifRightOfCenterOnGroup = ifRightOfCenterOnGroup;
    }

    public boolean getIfIsRightOfCenterOnGroup(){ return ifRightOfCenterOnGroup;}

    public void setRankFromFront(int rankFromFront){ this.rankFromFront = rankFromFront; }

    public int getRankFromFront(){ return rankFromFront; }

    public ObjectWithPhysics getPhysics(){ return object; }

    public void nextFrame(ConcurrentHashMap<String, ShipSprite> allShips, HashMap<String, projectile> allProjectiles,
                          HashMap<String, Missile> allMissiles){
        keysOfProjectileHits.clear();
        keysOfHitScanHits.clear();
        keysOfMissilesHits.clear();

        newlyMadeProjectiles.clear();
        newlyMadeMissiles.clear();
        sprite.setRotate(object.getVelocityDirection() * 180 / Math.PI + 90);
        if(target != null){
            if(target.compareTo("") != 0){
                object.nextFrame(allShips.get(target).getPhysics());
            } else {
                object.nextFrame();
            }
        } else {
            object.nextFrame();
        }


            //does attack cooldown stuff
        if(projectiles.size() > 0) {
            for (int i = 0; i < projectiles.size(); i++) {
                if(target.compareTo("") != 0) {
                    if (projCoolDowns.get(i) > 0) {
                        projCoolDowns.set(i, projCoolDowns.get(i) - 1);
                    } else if (projCoolDowns.get(i) <= 0) {
                        //attacks with projectile
                        projCoolDowns.set(i, 60);
                        projectileAttack(allShips, projectiles.get(i));
                    }
                }
            }
        }
        if(missiles.size() > 0){
            for (int i = 0; i < missiles.size(); i++) {
                if(target.compareTo("") != 0) {
                    if (missCoolDowns.get(i) > 0) {
                        missCoolDowns.set(i, missCoolDowns.get(i) - 1);
                    } else if (missCoolDowns.get(i) <= 0) {
                        //attacks with projectile
                        missCoolDowns.set(i, 60);
                        missileAttack(allShips, missiles.get(i));
                        missilesMade++;
                    }
                }
            }
        }

        //collision detection
        //returns vector with all names of projectiles that collided with
        Set<String> projKeys = allProjectiles.keySet();
        for(String str : projKeys){
            //get projectile distance to ship
            if(Integer.parseInt(str.substring(10, 11)) != Integer.parseInt(id.substring(6, 7))){
                double distance = Math.sqrt(Math.pow(allProjectiles.get(str).getMovementHandler().getX() - object.getX(), 2)
                        + Math.pow(allProjectiles.get(str).getMovementHandler().getY() - object.getY(), 2));
                if (distance <= object.getRadiusOfCollision()) {
                    keysOfProjectileHits.add(str);

                    //does damage

                }
            }
        }

        Set<String> missKeys = allMissiles.keySet();
        for(String str : missKeys){
            //get projectile distance to ship
            if(Integer.parseInt(str.substring(10, 11)) != Integer.parseInt(id.substring(6, 7))){
                double distance = Math.sqrt(Math.pow(allMissiles.get(str).getMovementHandler().getX() - object.getX(), 2)
                        + Math.pow(allMissiles.get(str).getMovementHandler().getY() - object.getY(), 2));
                if (distance <= object.getRadiusOfCollision()) {
                    keysOfMissilesHits.add(str);

                    //does damage

                }
            }
        }
        //does damage with keysOfProjectiles
    }

    public Vector<String> getKeysOfProjectileHits(){
        return keysOfProjectileHits;
    }

    public Vector<String> getKeysOfMissilesHits(){ return keysOfMissilesHits; }

    public Vector<String> getKeysOfHitScanHits(){ return keysOfHitScanHits; }

    public void projectileAttack(ConcurrentHashMap<String, ShipSprite> allShips, Weapon projectileUsed){
        if(target.compareTo("") != 0) {
            //creates objects with physics, which will then be returned at end of nextFrame() where sprite created in environment
            float xTarget = allShips.get(target).getPhysics().getX();
            float yTarget = allShips.get(target).getPhysics().getY();
            float theta = (float) Math.atan2(yTarget - object.getY(), xTarget - object.getX());
            newlyMadeProjectiles.add(new ProjectileWithPhysics(object.getX(), object.getY(), "projectile" + id.substring(
                    6, 7), 1, 2, 200, theta, projectileUsed));
            projectilesMade++;
            projCoolDowns.add(60);
        }
    }

    public void missileAttack(ConcurrentHashMap<String, ShipSprite> allShips, Weapon missileUsed){
        if(target.compareTo("") != 0){
            float theta = (float)Math.atan2(allShips.get(target).getPhysics().getY() - object.getY(), allShips.get(target).
                    getPhysics().getX() - object.getX());
            newlyMadeMissiles.add(new MissileWithPhysics(object.getX(), object.getY(), "missile___" + id.substring(
                    6, 7),1, 3, missileUsed, target, theta));

        }
    }

    public ArrayList<ProjectileWithPhysics> getNewlyMadeProjectiles(){
        return newlyMadeProjectiles;
    }

    public ArrayList<MissileWithPhysics> getNewlyMadeMissiles(){
        return newlyMadeMissiles;
    }

    public void applyForce(float magnitude, float direction){
        object.applyForce(magnitude, direction);
    }


    public String getID(){
        return id;
    }

    public void createWayPoint(float x, float y){
        ifUsingWayPoint = true;
        wayPointX = x;
        wayPointY = y;
        object.createWayPoint(x, y);
    }

    public void setTarget(String nameOfTarget){
        target = nameOfTarget;
        for(int i = 0; i < projectiles.size(); i++){
            projCoolDowns.add(60);
        }
        for(int i = 0; i < missiles.size(); i++){
            missCoolDowns.add(60);
        }
        for(int i = 0; i < lasers.size(); i++){
            lazeCoolDowns.add(60);
        }
    }

    public boolean hit(ProjectileWithPhysics proj, Group root){
        //returns if it is destroyed
        health -= proj.getWeapon().getDamagePerShot();
        if(health <= 0){
            //remove object
            destroy(root);
            return true;
        }
        return false;
    }

    public boolean hit(MissileWithPhysics miss, Group root){
        health -= miss.getMissile().getDamagePerShot();
        if(health <= 0){
            //remove object
            destroy(root);
            return true;
        }
        return false;
    }

    public void destroy(Group root){
        resetTarget();
        for(int i = 0; i < root.getChildren().size(); i++){
            if(root.getChildren().get(i).toString().compareTo(sprite.toString()) == 0 || root.getChildren().get(i).
                    toString().compareTo(center.toString()) == 0){
                //removes i
                root.getChildren().remove(i);
                i--;
            }

        }
    }

    public String getTarget() {
        if(target != null) {
            return target;
        }
        return "";
    }

    public void resetTarget(){ target = ""; }
}

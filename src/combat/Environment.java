package combat;

import combat.objects.ObjectWithPhysics;
import combat.sprites.BattleGroup;
import combat.sprites.ShipSprite;
import combat.sprites.weapons.Missile;
import combat.sprites.weapons.projectile;
import devTools.Coordinate;
import devTools.DoubleCooridinate;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ships.Fleet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Environment {
    private ConcurrentHashMap<String, ShipSprite> objects = new ConcurrentHashMap<>();
    private HashMap<String, projectile> projectiles = new HashMap<>();
    private HashMap<String, Missile> missiles = new HashMap<>();
    private Vector<BattleGroup> groups = new Vector();


    private int numberMissilesMade = 0;
    private int numberProjectilesMade = 0;


    //used for camera movement
    private final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    private final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();
    private final double WORLD_WIDTH = 2000;
    private final double WORLD_HEIGHT = 2000;
    private double offSetX = -SCREEN_WIDTH / 2;
    private double offSetY = 0;
    private double panX = 0;
    private double panY = 0;
    private double startPanX = 0;
    private double startPanY = 0;
    private double mouseWorldBeforeZoomX;
    private double mouseWorldBeforeZoomY;
    private double mouseWorldAfterZoomX;
    private double mouseWorldAfterZoomY;
    private double zoomFactor = 1.0;
    private double mouseX = 0;
    private double mouseY = 0;



    private String selectedObjectID = "None__";

    private Group combat = new Group();


    private CombatTimer timer = new CombatTimer();

    public Environment() {

    }

    public void start(){
        timer.start();
    }

    public void stop(){
        timer.stop();
    }

    public void loadObjects(Fleet[] fleets, Stage stage, Group returnRoot){ //initializes all of the ships as well as their positions
        stage.getScene().setRoot(combat);

        setUpDefenders(fleets);
        setUpAttackers(fleets);


        mouseMovement(stage, combat.getScene());

        Button butt = new Button("back");
        butt.setLayoutX(600);
        combat.getChildren().add(butt);
        butt.setOnAction(value -> {
            stage.getScene().setRoot(returnRoot);
        });




    }

    private void setUpDefenders(Fleet[] fleets){
        //vanguard
        int xCount = 0;
        int yCount = 0;
        int xIncrement = 80;
        int yIncrement = 40;
        int counter = 1;
        boolean ifRight;
        Vector<String> nameOfVanguardDefense = new Vector<>();
        //System.out.println(fleets[0].getSection(0).getShips().get(0).getID());
        for(int i = 0; i < fleets[0].getSection(0).getShips().size(); i++){
            String id = fleets[0].getSection(0).getShips().get(i).getID();
            nameOfVanguardDefense.add(id);
            int x = xCount * xIncrement;
            int y = yCount * yIncrement * -1;
            if(counter == 1){
                xCount++;
                yCount++;
                counter = 0;
                ifRight = true;
            } else {
                x *= -1;
                counter++;
                ifRight = false;
            }

            objects.put(id, new ShipSprite(fleets[0].getSection(0).getShips().get(i), combat, x, y,
                    (float)Math.PI / 2));
            objects.get(id).setIfRightOfCenterOnGroup(ifRight);
            objects.get(id).setRankFromFront((i + 1) / 2);
            combat.getChildren().add(objects.get(id));
            objects.get(id).addEventHandler(MouseEvent.MOUSE_CLICKED, value -> {

                if(value.getButton().compareTo(MouseButton.PRIMARY) == 0){
                    //selected this ship
                    selectedObjectID = id;
                } else if(value.getButton().compareTo(MouseButton.SECONDARY) == 0 && selectedObjectID.substring(0, 6).
                        compareTo("Ship__") == 0){
                    //attacks ship or follows ship
                    int thisShipSide = Integer.parseInt(id.substring(6, 7));
                    int otherShipSide = Integer.parseInt(selectedObjectID.substring(6, 7));
                    if(thisShipSide != otherShipSide){
                        //attacks ship with id selectedObject
                        objects.get(selectedObjectID).setTarget(id);
                    }
                }
            });
        }

        Vector<ShipSprite> vanguardList = new Vector<>();
        for(String str : nameOfVanguardDefense){
            vanguardList.add(objects.get(str));
        }
        groups.add(new BattleGroup(vanguardList, 0, Integer.parseInt(fleets[0].getSection(0).getShips().get
                (0).getID().substring(6, 7))));
        String id = groups.get(groups.size() - 1).getID();
        combat.getChildren().add(groups.get(groups.size() - 1));
        groups.get(groups.size() - 1).addEventHandler(MouseEvent.MOUSE_CLICKED, value -> {
            if(selectedObjectID.substring(0, 5).compareTo("group") == 0 && value.getButton().compareTo(MouseButton.
                    SECONDARY) == 0){
                if(Integer.parseInt(selectedObjectID.substring(13)) != Integer.parseInt(id.substring(13))){ //if on different teams
                    if(selectedObjectID.compareTo(id) != 0){
                        for(int i = 0; i < groups.size(); i++){
                            if(groups.get(i).getID().compareTo(selectedObjectID) == 0){
                                Vector<ShipSprite> targeters = new Vector<>();
                                Vector<ShipSprite> targeted = new Vector<>();
                                for(String str : groups.get(i).getNamesOfShips()){
                                    targeters.add(objects.get(str));
                                }
                                int indexOfTargetedGroup = -1;
                                for(int j = 0; j < groups.size(); j++){
                                    if(groups.get(j).getID().compareTo(id) == 0){
                                        indexOfTargetedGroup = j;
                                        break;
                                    }
                                }
                                for(String str : groups.get(indexOfTargetedGroup).getNamesOfShips()){
                                    targeted.add(objects.get(str));
                                }
                                Vector<String> targets = groups.get(i).setTargetForGroup(targeters, targeted);
                                for(int j = 0; j < targets.size(); j++){
                                    objects.get(targeters.get(j).getID()).setTarget(targets.get(j));
                                }
                            }
                        }
                    }
                }
            }
            selectedObjectID = id;

        });


        //left
    }

    private void setUpAttackers(Fleet[] fleets){

        //vanguard
        int xCount = 0;
        int yCount = 0;
        int xIncrement = 80;
        int yIncrement = 40;
        int counter = 1;
        boolean ifRight;
        Vector<String> nameOfVanguardAttack = new Vector<>();
        //System.out.println(fleets[0].getSection(0).getShips().get(0).getID());
        for(int i = 0; i < fleets[1].getSection(0).getShips().size(); i++){
            String id = fleets[1].getSection(0).getShips().get(i).getID();
            nameOfVanguardAttack.add(id);
            int x = xCount * xIncrement;
            int y = yCount * yIncrement + 1000;
            if(counter == 1){
                xCount++;
                yCount++;
                counter = 0;
                ifRight = false;

            } else {
                counter++;
                ifRight = true;
                x *= -1;
            }

            objects.put(id, new ShipSprite(fleets[1].getSection(0).getShips().get(i), combat, x, y,
                    (float)Math.PI / 2 * 3));
            objects.get(id).setIfRightOfCenterOnGroup(ifRight);
            objects.get(id).setRankFromFront((i + 1) / 2);
            combat.getChildren().add(objects.get(id));
            objects.get(id).addEventHandler(MouseEvent.MOUSE_CLICKED, value -> {

                if(value.getButton().compareTo(MouseButton.PRIMARY) == 0){
                    //selected this ship
                    selectedObjectID = id;
                } else if(value.getButton().compareTo(MouseButton.SECONDARY) == 0 && selectedObjectID.substring(0, 6).
                        compareTo("Ship__") == 0){
                    //attacks ship or follows ship
                    int thisShipSide = Integer.parseInt(id.substring(6, 7));
                    int otherShipSide = Integer.parseInt(selectedObjectID.substring(6, 7));
                    if(thisShipSide != otherShipSide){
                        //attacks ship with id selectedObject
                        objects.get(selectedObjectID).setTarget(id);
                    }
                }
            });
        }

        Vector<ShipSprite> vanguardList = new Vector<>();
        for(String str : nameOfVanguardAttack){
            vanguardList.add(objects.get(str));
        }
        groups.add(new BattleGroup(vanguardList, 0, Integer.parseInt(fleets[1].getSection(0).getShips().get
                (0).getID().substring(6, 7))));
        String id = groups.get(groups.size() - 1).getID();
        combat.getChildren().add(groups.get(groups.size() - 1));
        groups.get(groups.size() - 1).addEventHandler(MouseEvent.MOUSE_CLICKED, value -> {
            if(selectedObjectID.substring(0, 5).compareTo("group") == 0 && value.getButton().compareTo(MouseButton.
                    SECONDARY) == 0){
                if(Integer.parseInt(selectedObjectID.substring(13)) != Integer.parseInt(id.substring(13))) { //if on different teams
                    if (selectedObjectID.compareTo(id) != 0) {
                        for(int i = 0; i < groups.size(); i++){
                            if(groups.get(i).getID().compareTo(selectedObjectID) == 0) {
                                Vector<ShipSprite> targeters = new Vector<>();
                                Vector<ShipSprite> targeted = new Vector<>();
                                for (String str : groups.get(i).getNamesOfShips()) {
                                    targeters.add(objects.get(str));
                                }
                                int indexOfTargetedGroup = -1;
                                for (int j = 0; j < groups.size(); j++) {
                                    if (groups.get(j).getID().compareTo(id) == 0) {
                                        indexOfTargetedGroup = j;
                                        break;
                                    }
                                }
                                for (String str : groups.get(indexOfTargetedGroup).getNamesOfShips()) {
                                    targeted.add(objects.get(str));
                                }
                                Vector<String> targets = groups.get(i).setTargetForGroup(targeters, targeted);
                                for (int j = 0; j < targets.size(); j++) {
                                    objects.get(targeters.get(j).getID()).setTarget(targets.get(j));
                                }
                            }
                        }
                    }
                }
            }
            selectedObjectID = id;
        });



    }


    public void addObject(ShipSprite obj) {
        this.objects.put(obj.getID(), obj);
    }

    public void nextFrame() {
        updateProjectiles();
        updateMissiles();

        updateShips();
        updateGroups();

        createNewProjectiles();
        createNewMissiles();

        //clears null objects if they exist (should theoretically not, but they do exist)
        for(int i = 0; i < combat.getChildren().size(); i++){
            if(combat.getChildren().get(i) == null){
                combat.getChildren().remove(i);
                i--;
            }
        }
    }

    public void updateProjectiles(){
        Set<String> projKeys = projectiles.keySet();
        Vector<String> keysToRemove = new Vector<>();
        for(String str : projKeys){
            projectiles.get(str).nextFrame();
            DoubleCooridinate worldPos = new DoubleCooridinate(projectiles.get(str).getMovementHandler().getX(),
                    projectiles.get(str).getMovementHandler().getY());
            Coordinate screenPos = worldToScreen(worldPos);
            projectiles.get(str).setXDisplay(screenPos.getX());
            projectiles.get(str).setYDisplay(screenPos.getY());

            boolean ifRemove = projectiles.get(str).checkIfRemove(combat);
            if(ifRemove){
                keysToRemove.add(str);
            }
        }
        //clears projectiles that have gone past their life time
        for(String str : keysToRemove){
            projectiles.remove(str);
            projKeys.remove(str);
        }
    }

    public void updateMissiles(){
        Set<String> missileKeys = missiles.keySet();
        Vector<String> keysToRemove = new Vector<>();
        for(String str : missileKeys){
            missiles.get(str).nextFrame(objects);
            DoubleCooridinate worldPos = new DoubleCooridinate(missiles.get(str).getMovementHandler().getX(),
                    missiles.get(str).getMovementHandler().getY());
            Coordinate screenPos = worldToScreen(worldPos);
            missiles.get(str).setXDisplay(screenPos.getX());
            missiles.get(str).setYDisplay(screenPos.getY());

            boolean ifRemove = missiles.get(str).checkIfRemove(combat);
            if(ifRemove){
                keysToRemove.add(str);
            }
        }

        //gets rid of missiles that have gone past their life time
        for(String str : keysToRemove){
            missiles.remove(str);
        }
    }

    public void updateShips(){
        Set<String> allShipsKeys = objects.keySet();
        Vector<String> projToRemoveFromCollisions;
        Vector<String> missToRemoveFromCollisions;

        Vector<String> keysOfShipsToDestroy = new Vector<>();
        for(String str : allShipsKeys){
            objects.get(str).nextFrame(objects, projectiles, missiles);
            DoubleCooridinate worldPosOfShip = new DoubleCooridinate(objects.get(str).getPhysics().getX(), objects.get(
                    str).getPhysics().getY());
            Coordinate screenPosOfShip = worldToScreen(worldPosOfShip);
            //objects.get(str).setZoom(zoomFactor);
            objects.get(str).setXDisplay(screenPosOfShip.getX());
            objects.get(str).setYDisplay(screenPosOfShip.getY());
            objects.get(str).setZoom(zoomFactor);


            projToRemoveFromCollisions = objects.get(str).getKeysOfProjectileHits();

            for(String str2 : projToRemoveFromCollisions){
                boolean ifDestroyed = objects.get(str).hit(projectiles.get(str2).getMovementHandler(), combat);
                if(ifDestroyed){
                    keysOfShipsToDestroy.add(str2);

                    //find object that shot the projectile and remove the target
                    Set<String> keys = objects.keySet();
                    for(String str3 : keys){
                        if(objects.get(str3).getTarget().compareTo(str) == 0){
                            objects.get(str3).resetTarget();
                        }
                    }
                }
                projectiles.get(str2).remove(combat);
                projectiles.remove(str2);
            }

            missToRemoveFromCollisions = objects.get(str).getKeysOfMissilesHits();
            for(String str2 : missToRemoveFromCollisions){
                boolean ifDestroyed = objects.get(str).hit(missiles.get(str2).getMovementHandler(), combat);
                if(ifDestroyed){
                    //remove ship
                    objects.remove(str);

                    //find object that shot the projectile and remove the target
                    Set<String> keys = objects.keySet();
                    for(String str3 : keys){
                        if(objects.get(str3).getTarget().compareTo(str) == 0){
                            objects.get(str3).resetTarget();
                        }
                    }

                    for(BattleGroup g : groups){
                        if(g.removeShip(str)){
                            break;
                        }
                    }
                }
            }
        }

        for(String str : keysOfShipsToDestroy){
            //remove ship of str
            objects.remove(str);
        }
    }

    public void updateGroups(){
        for(BattleGroup bg : groups){
            Vector<ShipSprite> shipList = new Vector<>();
            for(String str : bg.getNamesOfShips()){
                shipList.add(objects.get(str));
            }
            bg.nextFrame(shipList);
            bg.adjustForZoom(zoomFactor);

            DoubleCooridinate worldPos = new DoubleCooridinate(bg.getX(), bg.getY());
            Coordinate screenPos = worldToScreen(worldPos);
            bg.setLayoutX(screenPos.getX() - bg.getWidthOfButton() / 2);
            bg.setLayoutY(screenPos.getY() - bg.getHeightOfButton() / 2);


            bg.setPrefWidth(bg.getWidthOfButton());
            bg.setPrefHeight(bg.getHeightOfButton());
            bg.setMinWidth(bg.getWidthOfButton());
            bg.setMinHeight(bg.getHeightOfButton());
            bg.setMaxWidth(bg.getWidthOfButton());
            bg.setMaxHeight(bg.getHeightOfButton());
        }
    }

    public void createNewProjectiles(){
        //creates newly launched projectiles
        Set<String> allShipsKeys = objects.keySet();
        for(String str : allShipsKeys){
            for(int i = 0; i < objects.get(str).getNewlyMadeProjectiles().size(); i++){
                numberProjectilesMade++;
                projectiles.put(objects.get(str).getNewlyMadeProjectiles().get(i).getId() + numberProjectilesMade, new
                        projectile(objects.get(str).getNewlyMadeProjectiles().get(i), objects.get(str).
                        getNewlyMadeProjectiles().get(i).getId(), combat, numberProjectilesMade));
            }
        }
    }

    public void createNewMissiles(){
        Set<String> keys = objects.keySet();
        for(String str : keys){
            for(int i = 0; i < objects.get(str).getNewlyMadeMissiles().size(); i++){
                numberMissilesMade++;
                missiles.put(objects.get(str).getNewlyMadeMissiles().get(i).getId() + numberMissilesMade, new Missile
                        (combat, objects.get(str).getNewlyMadeMissiles().get(i), objects.get(str).getNewlyMadeMissiles().
                                get(i).getX(), objects.get(str).getNewlyMadeMissiles().get(i).getY(), numberMissilesMade));
            }
        }
    }


    public ConcurrentHashMap<String, ShipSprite> getObjects() {
        return objects;
    }

    private class CombatTimer extends AnimationTimer {
        @Override
        public void handle(long now){
            doStuff();
        }

        public void doStuff(){
            nextFrame();
        }
    }

    private void mouseMovement(Stage stage, Scene scene) {
        stage.addEventHandler(MouseEvent.MOUSE_CLICKED, value -> {
            if (value.getButton().compareTo(MouseButton.SECONDARY) == 0 && selectedObjectID.compareTo("None__") != 0 &&
                    selectedObjectID.substring(0, 4).compareTo("Ship") == 0) {
                //converts to from screen to world value
                DoubleCooridinate screenPos = new DoubleCooridinate(value.getX(), value.getY());
                DoubleCooridinate worldPos = screenToWorld(screenPos);
                objects.get(selectedObjectID).createWayPoint((float)(worldPos.getX()), (float) (worldPos.getY()));
            }
            if(value.getButton().compareTo(MouseButton.SECONDARY) == 0 && selectedObjectID.substring(0, 5).compareTo("group") == 0){
                int selectedGroupIndex = -1;
                for(int i = 0; i < groups.size(); i++){
                    if(groups.get(i).getID().compareTo(selectedObjectID) == 0){
                        selectedGroupIndex = i;
                        break;
                    }
                }
                Vector<ShipSprite> ships = new Vector<>();
                for(int i = 0; i < groups.get(selectedGroupIndex).getNamesOfShips().size(); i++){
                    ships.add(objects.get(groups.get(selectedGroupIndex).getNamesOfShips().get(i)));
                }
                DoubleCooridinate screenPos = new DoubleCooridinate(value.getX(), value.getY());
                DoubleCooridinate worldPos = screenToWorld(screenPos);
                Vector<Float>[] temp = groups.get(selectedGroupIndex).setWayPointForGroup(ships, (float)(worldPos.getX()
                ), (float) (
                        worldPos.getY()));
                for(int i = 0; i < groups.get(selectedGroupIndex).getNamesOfShips().size(); i++){
                    objects.get(groups.get(selectedGroupIndex).getNamesOfShips().get(i)).createWayPoint(temp[0].get(i),
                            temp[1].get(i));
                }
            }
            if(value.getButton().compareTo(MouseButton.PRIMARY) == 0){
                selectedObjectID = "None__";
            }
            /*if(value.getButton().compareTo(MouseButton.SECONDARY) == 0) {
                DoubleCooridinate screenPos = new DoubleCooridinate(value.getX(), value.getY());
                DoubleCooridinate worldPos = screenToWorld(screenPos);
                newObjects.get("first").createWayPoint((float) worldPos.getX(), (float) worldPos.getY());
                System.out.println(offSetX + " " + offSetY);
            }*/
        });

        for(BattleGroup bg : groups){
            bg.addEventHandler(MouseEvent.MOUSE_CLICKED, value -> {

            });
        }

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            DoubleCooridinate worldBeforeZoom = screenToWorld(new DoubleCooridinate(mouseX, mouseY));
            mouseWorldBeforeZoomX = worldBeforeZoom.getX();
            mouseWorldBeforeZoomY = worldBeforeZoom.getY();
            double movement = event.getDeltaY();
            if (movement > 0) {
                zoomFactor *= 1.02;
            } else if (movement < 0) {
                zoomFactor /= 1.02;
            }
            DoubleCooridinate worldAfterZoom = screenToWorld(new DoubleCooridinate(mouseX, mouseY));
            mouseWorldAfterZoomX = worldAfterZoom.getX();
            mouseWorldAfterZoomY = worldAfterZoom.getY();

            offSetX += (mouseWorldBeforeZoomX - mouseWorldAfterZoomX);
            offSetY += (mouseWorldBeforeZoomY - mouseWorldAfterZoomY);

        });

        scene.setOnMouseMoved(mouseEvent -> {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
        });

        scene.setOnMousePressed(MouseEvent -> {
            startPanX = MouseEvent.getX();
            startPanY = MouseEvent.getY();
        });

        scene.setOnMouseDragged(MouseEvent -> {
            offSetX -= (MouseEvent.getX() - startPanX) / zoomFactor;
            offSetY -= (MouseEvent.getY() - startPanY) / zoomFactor;
            startPanX = MouseEvent.getX();
            startPanY = MouseEvent.getY();
        });

    }


    private Coordinate worldToScreen(DoubleCooridinate worldPos){
        int x = (int)((worldPos.getX() - offSetX) * zoomFactor);
        int y = (int)((worldPos.getY() - offSetY) * zoomFactor);
        return new Coordinate(x, y);
    }

    private DoubleCooridinate screenToWorld(DoubleCooridinate screenPos){
        double x = (screenPos.getX() / zoomFactor + offSetX);
        double y = (screenPos.getY() / zoomFactor + offSetY);
        return new DoubleCooridinate(x, y);
    }

}


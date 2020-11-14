package gui;

public class backup {
    /*
    package sample;

import players.civilizations.DefaultCivilization;
import devTools.Coordinate;
import devTools.DoubleCooridinate;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.input.*;
import javafx.scene.shape.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import javafx.stage.Screen;
import map.*;
import map.planets.Planet;
import players.*;
import map.systems.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ships.Ship;


import java.util.ArrayList;

public class main.gui.Main extends Application {
    private static Map map;
    private static int WIDTH = 1000;
    private static int HEIGHT = 700;
    private Rectangle2D bounds;

    //all information
    private ArrayList<SystemSprite> nodes = new ArrayList<SystemSprite>();
    private ArrayList<shipSprite> shipNodes = new ArrayList<shipSprite>();
    private static ArrayList<Player> allPlayers = new ArrayList<Player>();

    //main player
    private static int mainPlayerIndex = 0;
    private ArrayList<Integer> mainPlayerOwnedSystems;
    private ArrayList<Integer> mainPlayerKnownSystems;

    private String selectedObjectID = "None__0";

    private Button createShip = new Button("Create Ship");

    private AnimationTimer timer;
    private int turn = 1;
    private double zoomFactor = 1.0;
    private int defaultSizeOfSprite = 5;

    private int mouseX = 0;
    private int mouseY = 0;


    /***************************************************************************
    -----------------------------GUI ELEMENTS----------------------------------
     ***************************************************************************/

        /*
    //different scenes
    private Scene mainScene;
    private Scene mainMenu;
    private Scene techTreeScene;
    private Scene diplomacy;
    private Scene tradeRoutes;

    //scaling and moving variables
    private double offSetX = -WIDTH / 2 + 600;
    private double offSetY = -HEIGHT / 2 + 600;
    private double panX = 0;
    private double panY = 0;
    private double startPanX = 0;
    private double startPanY = 0;
    private double mouseWorldBeforeZoomX;
    private double mouseWorldBeforeZoomY;
    private double mouseWorldAfterZoomX;
    private double mouseWorldAfterZoomY;

    //GUI elements
    private Button nextTurnButton = new Button("next turn");
    private main.gui.Main.planetGUI gui;
    private Label turnLabel;

    //top right scene change stuff(options, diplomacy, settings)


    private Button toGame = new Button("change");
    private Button toTechTree = new Button("change");


    @Override
    public void start(Stage primaryStage) throws Exception{
        Group mainGame = new Group();
        Group mainMenu = new Group();
        Group techTreeGroup = new Group();
        intializeMainMenu(mainMenu, primaryStage);
        initializeMainGame(mainGame, primaryStage);
        intializeTechTree(techTreeGroup, primaryStage);
    }

    public void initializeMainGame(Group root, Stage primaryStage){
        mainPlayerOwnedSystems = allPlayers.get(mainPlayerIndex).getAllKnownSystems();
        mainPlayerKnownSystems = mainPlayerOwnedSystems;
        mainScene = new Scene(root, WIDTH, HEIGHT);

        setupSprites(root);
        setupGUIElements(root, primaryStage);
        mouseControl(root, mainScene, primaryStage);


        timer = new main.gui.Main.MyTimer();
        timer.start();
    }

    public void intializeMainMenu(Group root, Stage primaryStage){
        primaryStage.setTitle("Game test");
        mainMenu = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(mainMenu);

        setUpMainMenu(root, primaryStage);

        primaryStage.show();

    }

    public void intializeTechTree(Group root, Stage primaryStage){
        techTreeScene = new Scene(root, WIDTH, HEIGHT);
    }


    //GUI methods
    public void setupSprites(Group root){
        for(int i = 0; i < map.getNumSystems(); i++){
            //loads all planets and sets them as invisible
            DoubleCooridinate worldPos = new DoubleCooridinate(map.getSystemAt(i).getX(), map.getSystemAt(i).getY());
            Coordinate screenPos = worldToScreen(worldPos);
            main.gui.Main.SystemSprite sys = new main.gui.Main.SystemSprite("", screenPos.getX(), screenPos.getY(), root, map.getSystemAt(i), i);
            nodes.add(sys);
            nodes.get(i).setInvisible();
            root.getChildren().add(sys);
        }

        //sets player's planet as visible
        for(int i = 0; i < nodes.size(); i++){
            for(int j = 0; j < mainPlayerOwnedSystems.size(); j++){
                if(i == mainPlayerOwnedSystems.get(j)){
                    nodes.get(i).setVisible();
                }
            }
        }
    }

    public void setupGUIElements(Group root, Stage primaryStage){
        gui = new main.gui.Main.planetGUI(root, nodes.get(mainPlayerOwnedSystems.get(0)).getSys());
        nextTurnButton.setText("Next turn");
        nextTurnButton.setTranslateX(WIDTH - 70);
        nextTurnButton.setTranslateY(HEIGHT - 30);
        root.getChildren().add(nextTurnButton);

        turnLabel = new Label("Turn: 1");
        turnLabel.setLayoutX(WIDTH - WIDTH / 25);
        turnLabel.setLayoutY(HEIGHT - HEIGHT / 10);
        root.getChildren().add(turnLabel);

        nextTurnButton.setOnAction(value -> {
            turn++;
            turnLabel.setText("Turn: " + turn);
        });

        createShip.setText("Create Ship");
        createShip.setLayoutX(WIDTH - 100);
        createShip.setOnAction(value -> {
            String newShipID = allPlayers.get(0).createTestShip(nodes.get(mainPlayerOwnedSystems.get(0)).getSys());
            main.gui.Main.shipSprite newShip = new main.gui.Main.shipSprite(root, newShipID);
            root.getChildren().add(newShip);
            shipNodes.add(newShip);
        });
        root.getChildren().add(createShip);
    }

    public void setUpMainMenu(Group root, Stage primaryStage){
        toGame = new Button("change scene");
        root.getChildren().add(toGame);
        toGame.setOnAction(value -> {
            primaryStage.setScene(mainScene);
        });
    }


    //Movement methods
    private void mouseControl(Group root, Scene scene, Stage stage){
        //resets selected object
        stage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            selectedObjectID = "None__" + 0;
        });

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            DoubleCooridinate worldBeforeZoom = screenToWorld(new DoubleCooridinate(mouseX, mouseY));
            mouseWorldBeforeZoomX = worldBeforeZoom.getX();
            mouseWorldBeforeZoomY = worldBeforeZoom.getY();
            double movement = event.getDeltaY();
            if(movement > 0){
                zoomFactor *= 1.05;
            } else if(movement < 0){
                zoomFactor /= 1.05;
            }
            DoubleCooridinate worldAfterZoom = screenToWorld(new DoubleCooridinate(mouseX, mouseY));
            mouseWorldAfterZoomX = worldAfterZoom.getX();
            mouseWorldAfterZoomY = worldAfterZoom.getY();

            offSetX += (mouseWorldBeforeZoomX - mouseWorldAfterZoomX);
            offSetY += (mouseWorldBeforeZoomY - mouseWorldAfterZoomY);

        });

        scene.setOnMouseMoved(mouseEvent -> {
            mouseX = (int)mouseEvent.getX();
            mouseY = (int)mouseEvent.getY();
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


    //Calculation methods
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


    //Gameloop objects
    private class MyTimer extends AnimationTimer {
        double zoomFactorOG = zoomFactor;
        int currentTurn = 1;
        @Override
        public void handle(long now){

            doShit();

        }

        private void doShit(){
            scale();
            if(currentTurn != turn){
                update();
                currentTurn++;
            }
        }

        private void update(){


            //updates each system
            for(int i = 0; i < nodes.size(); i++){
                if(nodes.get(i).getSys().getOwner() != 0){
                    nodes.get(i).getSys().nextTurn();
                }
            }

            //increase viewDistance
            for(int i = 0; i < mainPlayerOwnedSystems.size(); i++){
                nodes.get(mainPlayerOwnedSystems.get(i)).updateVD(30);

                //checks distance to each system
                for(int j = 0; j < nodes.size(); j++){
                    //distance = sqrt(deltaX^2 + deltaY^2)
                    double distanceX, distanceY, distance;
                    distanceX = nodes.get(j).getSys().getX() - nodes.get(mainPlayerOwnedSystems.get(i)).getSys().getX();
                    distanceY = nodes.get(j).getSys().getY() - nodes.get(mainPlayerOwnedSystems.get(i)).getSys().getY();
                    distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                    if(nodes.get(mainPlayerOwnedSystems.get(i)).getSys().getViewDistance() >= distance * HEIGHT / 100){
                        nodes.get(j).setVisible();
                    }
                }
            }

            //updates each ship
            for(int i = 0; i < shipNodes.size(); i++){
                shipNodes.get(i).nextTurn();
            }


            //updates info for each player
            for(int i = 0; i < allPlayers.size(); i++){
                allPlayers.get(i).textTurnScience(5);
            }
        }

        private void scale(){
            //scales and moves planets based off scale
            for(int i = 0; i < nodes.size(); i++){
                Coordinate screenPos = worldToScreen(new DoubleCooridinate(nodes.get(i).getSys().getX() * HEIGHT / 100
                        , nodes.get(i).getSys().getY() * HEIGHT / 100));
                nodes.get(i).setX(screenPos.getX());
                nodes.get(i).setY(screenPos.getY());
                nodes.get(i).scaleRadius(zoomFactor);
                nodes.get(i).toBack();

            }

            //scales and moves planets
            for(int i = 0; i < allPlayers.size(); i++){
                for(int j = 0; j < allPlayers.get(i).getNumShips(); j++){
                    DoubleCooridinate temp = new DoubleCooridinate(allPlayers.get(i).getLocationOfShipAtIndex(j).getX() * HEIGHT / 100,
                            allPlayers.get(i).getLocationOfShipAtIndex(j).getY() * HEIGHT / 100);
                    Coordinate screenPos = worldToScreen(temp);
                    for(int k = 0; k < shipNodes.size(); k++){
                        if(shipNodes.get(k).getSelectedID().compareTo(allPlayers.get(i).getIDOfShipAtIndex(j)) == 0){
                            shipNodes.get(k).setX(screenPos.getX());
                            shipNodes.get(k).setY(screenPos.getY());
                            shipNodes.get(k).scale(zoomFactor);
                        }
                    }
                }
            }

        }
    }


    //GUI objects
    private class SystemSprite extends Button{
        private int radiusCentral = 10;

        private SolarSystem sys;
        Circle sprite = new Circle(radiusCentral);
        Circle area = new Circle(0);

        private int index;
        public SystemSprite(String name, int x, int y, Group root, SolarSystem sys, int i){
            //setting up button
            super(name);
            super.setOpacity(0);
            super.setPrefHeight(radiusCentral * 2);
            super.setPrefWidth(radiusCentral * 2);
            super.setMinWidth(radiusCentral * 2);
            super.setMinHeight(radiusCentral * 2);
            super.setMaxHeight(radiusCentral * 2);
            super.setMaxWidth(radiusCentral * 2);
            super.setVisible(false);


            //setting up sprite
            sprite.setCenterX(x);
            sprite.setCenterY(y);
            root.getChildren().add(sprite);

            //setting up data that is locally stored for GUI
            this.sys = sys;
            this.index = i; //corresponds to index in nodes

            //set color
            if(sys.getOwner() != -1){
                String color = allPlayers.get(sys.getOwner()).getColor();
                setColor(color);
            } else {
                sprite.setFill(Color.BLACK);
            }

            //set button event
            super.setOnAction(value -> {
                String typeOfObjectSelected = selectedObjectID.substring(0, 6);
                System.out.println(selectedObjectID);
                if(typeOfObjectSelected.compareTo("Ship__") == 0){
                    System.out.println("Make this click a left click later on.");
                    for(int j = 0; j < allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getShips().size(); j++){
                        if(allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getShips().get(j).getID().compareTo(selectedObjectID) == 0){
                            String iD = allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getShips().get(j).getID();
                            if(!allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getShip(iD).getIfMoving()){
                                allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).moveShip(iD, this.getSys());
                            }
                        }
                    }

                    selectedObjectID = this.getSelectedID();
                } else if(typeOfObjectSelected.compareTo("None__") == 0) {
                    selectedObjectID = getSelectedID();
                    gui.changeObject(sys, root);
                } else if(typeOfObjectSelected.compareTo("solSys") == 0){
                    selectedObjectID = this.getSelectedID();
                    gui.changeObject(this.getSys(), root);
                }
            });

            //View Area
            if(sys.getOwner() == mainPlayerIndex){
                sys.setViewDistance(25);
            }
            area.setCenterX(x);
            area.setCenterY(y);
            area.setOpacity(.2);
            area.setFill(Color.GREY);
            area.setRadius(sys.getViewDistance());
            root.getChildren().add(area);
        }

        private String getSelectedID(){
            return "solSys" + index;
        }

        public void setX(int newX){
            sprite.setCenterX(newX);
            area.setCenterX(newX);
            super.setTranslateX(newX - sprite.getRadius());
        }

        public void setY(int newY){
            sprite.setCenterY(newY);
            area.setCenterY(newY);
            super.setTranslateY(newY - sprite.getRadius());
        }

        public void updateVD(double deltaVD){
            sys.setViewDistance(deltaVD);
            area.setRadius(sys.getViewDistance() * zoomFactor);
        }

        public void scaleRadius(double zoomFactor){
            sprite.setRadius(radiusCentral * zoomFactor);
            area.setRadius(sys.getViewDistance() * zoomFactor);
            super.setPrefHeight(radiusCentral * 2 * zoomFactor);
            super.setPrefWidth(radiusCentral * 2 * zoomFactor);
            super.setMinWidth(radiusCentral * 2 * zoomFactor);
            super.setMinHeight(radiusCentral * 2 * zoomFactor);
            super.setMaxHeight(radiusCentral * 2 * zoomFactor);
            super.setMaxWidth(radiusCentral * 2 * zoomFactor);
        }

        public SolarSystem getSys(){return sys;}

        public void setColor(String color){
            if(color.compareTo("blue") == 0){
                sprite.setFill(Color.BLUE);
            } else if(color.compareTo("red") == 0){
                sprite.setFill(Color.RED);
            } else if(color.compareTo("yellow") == 0){
                sprite.setFill(Color.YELLOW);
            } else if(color.compareTo("green") == 0){
                sprite.setFill(Color.GREEN);
            } else if(color.compareTo("orange") == 0){
                sprite.setFill(Color.ORANGE);
            } else if(color.compareTo("purple") == 0){
                sprite.setFill(Color.PURPLE);
            } else if(color.compareTo("pink") == 0){
                sprite.setFill(Color.PINK);
            } else if(color.compareTo("teal") == 0){
                sprite.setFill(Color.TEAL);
            } else {
                sprite.setFill(Color.BEIGE);
            }
        }

        public void toBack(){
            super.toFront();
            sprite.toBack();
        }

        public void setVisible(){
            super.setVisible(true);
            sprite.setOpacity(1);
            area.setOpacity(.5);
        }

        public void setInvisible(){
            sprite.setOpacity(0);
            area.setOpacity(0);
        }
    }
/*
    private class planetGUI{
        //GUI objects
        private Rectangle bottom;
        private Rectangle left;

        //Info displayers
        private Label systemName;
        private Label systemIndustry;
        private Label systemFood;
        private Label systemScience;
        private Label systemCash;
        private Label systemPower;

        private Label allPlanets = new Label("test");

        private ArrayList<Rectangle> upgradeTiles = new ArrayList<Rectangle>();

        //Display info
        private SolarSystem selectedSystem;
        private Ship selectedShip;


        public planetGUI(Group root, SolarSystem homeSystem){
            selectedSystem = homeSystem;
            bottom = new Rectangle(WIDTH * 5 / 7, HEIGHT / 4);
            bottom.setY(HEIGHT / 4 * 3);
            bottom.setX(WIDTH / 7 * 2);
            bottom.setFill(Color.WHITE);
            bottom.setStroke(Color.BLACK);
            root.getChildren().add(bottom);

            left = new Rectangle(WIDTH / 7 * 2, HEIGHT);
            left.setFill(Color.WHITE);
            left.setStroke(Color.BLACK);
            root.getChildren().add(left);
            intitializeLabels(root);
            setUpLabelsForOwnedSystem(root);

            root.getChildren().add(allPlanets);




        }

        public void intitializeLabels(Group root){
            systemName = new Label(selectedSystem.getName());
            root.getChildren().add(systemName);
            systemName.setLayoutX(WIDTH / 10);

            systemIndustry = new Label();
            root.getChildren().add(systemIndustry);
            systemIndustry.setLayoutY(120);
            systemIndustry.setLayoutX(5);

            systemFood = new Label();
            root.getChildren().add(systemFood);
            systemFood.setLayoutY(135);
            systemFood.setLayoutX(5);

            systemScience = new Label();
            root.getChildren().add(systemScience);
            systemScience.setLayoutY(150);
            systemScience.setLayoutX(5);

            systemCash = new Label();
            root.getChildren().add(systemCash);
            systemCash.setLayoutY(165);
            systemCash.setLayoutX(5);
        }

        public void changeObject(SolarSystem newSys, Group root){
            selectedSystem = newSys;
            systemName.setText(newSys.getName());
            if(selectedSystem.getOwner() == mainPlayerIndex){
                setUpLabelsForOwnedSystem(root);
            } else if(selectedSystem.getOwner() == -1){
                setUpLabelsForOwnedSystem(root);
            } else /*if(selectedSystem.getOwner() != -1 && selectedSystem.getOwner() == mainPlayerIndex)*/
              /*  setUpLabelsForOwnedSystem(root);
            }

        }

        public void changeObject(Ship ship, Group root){

        }

        private void intitializeUpgrades(){
            for(int i = 0; i < selectedSystem.getNumUpgrades(); i++){

            }
        }




        private void setUpLabelsForOwnedSystem(Group root){

            String industry = "Industry: ";
            industry += selectedSystem.getIndustry();
            systemIndustry.setText(industry);

            String food = "Food: ";
            food += selectedSystem.getFood();
            systemFood.setText(food);


            String science = "Science: ";
            science += selectedSystem.getScience();
            systemScience.setText(science);

            String cash = "Cash: ";
            cash += selectedSystem.getCash();
            systemCash.setText(cash);


            /*String militaryPower = "Military Power: ";
            militaryPower += selectedSystem.getManpowerSupported();
            systemPower.setLayoutY(180);
            systemPower.setLayoutX(5);*/
/*
            Planet[] planets = selectedSystem.getPlanets();
            String planetsString = "";
            for(int i = 0; i < planets.length; i++){
                planetsString += (planets[i].getPlanetType() + " ");
            }

            allPlanets.setText(planetsString);
            allPlanets.setLayoutX(200);

        }

        private void setUpLabelsForEnemySystem(Group root){

        }

        private void setUpLabelsForUncolonizedSystem(Group root){}

    }

    private class topRightOptions{
        private Button toTechTreeButton;
        private Button toOptions;
        private Button toDiplomacy;
        private Button toTradeRoutes;


    }

    private class shipSprite extends Button{

        double length = 30;
        double height = 15;
        private Rectangle sprite = new Rectangle(length, height);
        private String identification;
        //to get player, get identification.subString(6, 7)

        public shipSprite(Group root, String id){
            super();
            identification = id;
            DoubleCooridinate worldSpace = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getShip(identification).getPosition();
            Coordinate screenSpace = worldToScreen(worldSpace);
            sprite.setX(screenSpace.getX());
            sprite.setY(screenSpace.getY());
            root.getChildren().add(sprite);
            super.setOnAction(event -> {
                selectedObjectID = getSelectedID();
            });
            if(Integer.parseInt(identification.substring(6, 7)) != -1){
                String color = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getColor();
                setColor(color);
            } else {
                sprite.setFill(Color.BLACK);
            }
            super.setOpacity(0);
        }

        public void nextTurn(){
            allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getShip(identification).nextTurn();
        }

        public void move(SolarSystem sys){
            allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getShip(identification).move(sys);
        }

        public String getSelectedID(){
            return allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getShip(identification).getID();
        }

        public void setX(int x){
            sprite.setX(x);
            super.setLayoutX(x);
        }

        public void setY(int y){
            sprite.setY(y);
            super.setLayoutY(y);
        }

        public void scale(double scaleFactor){
            sprite.setHeight(height * zoomFactor);
            sprite.setWidth(length * zoomFactor);
            super.setPrefHeight(height * zoomFactor);
            super.setPrefWidth(length * zoomFactor);
            super.setMinWidth(length * zoomFactor);
            super.setMinHeight(height * zoomFactor);
            super.setMaxHeight(height * zoomFactor);
            super.setMaxWidth(length * zoomFactor);
        }

        public void toFront(){
            sprite.toFront();
            super.toFront();
        }

        public void toBack(){
            sprite.toBack();
            super.toBack();
        }

        private void setColor(String color){
            if(color.compareTo("blue") == 0){
                sprite.setFill(Color.BLUE);
            } else if(color.compareTo("red") == 0){
                sprite.setFill(Color.RED);
            } else if(color.compareTo("yellow") == 0){
                sprite.setFill(Color.YELLOW);
            } else if(color.compareTo("green") == 0){
                sprite.setFill(Color.GREEN);
            } else if(color.compareTo("orange") == 0){
                sprite.setFill(Color.ORANGE);
            } else if(color.compareTo("purple") == 0){
                sprite.setFill(Color.PURPLE);
            } else if(color.compareTo("pink") == 0){
                sprite.setFill(Color.PINK);
            } else if(color.compareTo("teal") == 0){
                sprite.setFill(Color.TEAL);
            } else {
                sprite.setFill(Color.BEIGE);
            }
        }
    }


    //main.gui.Main method
    public static void main(String[] args) {
        allPlayers.add(new DefaultPlayer(false, "blue", 0, new DefaultCivilization()));
        allPlayers.add(new DefaultPlayer(true, "red", 1, new DefaultCivilization()));
        allPlayers.add(new DefaultPlayer(false, "green", 2, new DefaultCivilization()));
        mainPlayerIndex = 0;

        map = new Map("disk", 3, allPlayers);
        allPlayers = map.getPlayers();
        launch(args);
    }
}



     */
}

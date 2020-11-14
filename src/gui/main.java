package gui;

import combat.Environment;
import devTools.Coordinate;
import devTools.DoubleCooridinate;
import devTools.OverlapStack;
import devTools.Scores;
import gui.components.BasicImageView;
import gui.components.BasicSprite;
import gui.components.Sprites.Star;
import gui.components.fleetSelect.fleetSelectButton;
import gui.components.fleetSelect.fleetSelectScrollbar;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import map.Map;
import map.planets.Planet;
import map.systems.SolarSystem;
import planetUpgrades.Upgrade;
import players.DefaultPlayer;
import players.Player;
import players.aiComponents.Path;
import players.aiComponents.PathWithSystems;
import players.civilizations.DefaultCivilization;
import players.score.Score;
import ships.Fleet;
import ships.Ship;
import ships.chassises.ShipTemplate;
import ships.chassises.TestShipTemplate;
import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.MissileWeapon;
import ships.shipComponents.weapons.ProjectileWeapon;
import ships.shipComponents.weapons.Weapon;
import ships.tradeRoutes.tradeships.TradeFleet;
import techTree.techs.Tech;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

public class main extends Application {
    private static Map map;
    private static int WIDTH = (int)Screen.getPrimary().getBounds().getWidth();
    private static int HEIGHT = (int)Screen.getPrimary().getBounds().getHeight();
    private Rectangle2D bounds;

    //all information
    private ArrayList<SystemSprite> nodes = new ArrayList<>();
    private ArrayList<FleetSprite> FleetNodes = new ArrayList<>();
    private static ArrayList<Player> allPlayers = new ArrayList<>();
    private ArrayList<ConnectionSprite> connectors = new ArrayList<>();
    private ArrayList<TraitorRoute> allTradeRoutes = new ArrayList<>();
    private Scores scores = new Scores();
    private boolean ifUpdating = false;


    //main player
    private static int mainPlayerIndex = 0;
    private ArrayList<Integer> mainPlayerOwnedSystems;
    private ArrayList<Integer> mainPlayerKnownSystems;

    private String selectedObjectID = "None__0";
    private String previouslySelectedObjectID = selectedObjectID;

    private AnimationTimer timer;
    private int turn = 1;
    private double zoomFactor = 1.0;
    private int defaultSizeOfSprite = 5;

    private int mouseX = 0;
    private int mouseY = 0;

    private int numFrames = 60;

    public ArrayList<String> nameOfObjectsMoved = new ArrayList<>();
    public ArrayList<Integer> framesUntilShipStopsMoving = new ArrayList<>();

    /***************************************************************************
    -------------------------------GUI ELEMENTS---------------------------------
    ****************************************************************************/

    //different scenes
    private Scene mainMenu;

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

    //private double mouse

    //GUI elements
    private Button nextTurnButton = new Button("next turn");
    private planetGUI gui;
    private ShipCustomizerGUI shipGUI;
    private Label turnLabel;
    private Rectangle background;
    private BasicImageView gameBG;

    //private ArrayList<>

    //top right scene change stuff(options, diplomacy, settings)


    private Button menuToGame = new Button("change");
    private Button toOptions = new Button("To Options");
    private Button gameToDiplomacy = new Button("To Diplomacy");
    private Button gameToTradeRoutes = new Button("To Trade Routes");
    private Button gameToTechTree = new Button("To Tech Tree");
    private Button techTreeToGame = new Button("Back to game");
    private Button diplomacyToGame = new Button("Back to game");
    private Button tradeRoutesToGame = new Button("Back to game");
    private Button gameToShip = new Button("To Ship menu");
    private Button menuToCombat = new Button("To combat test");

    private Group mainGame;
    private Group mainMenuGroup;
    private Group techTreeGroup;
    private Group shipCustomizer;
    private Group combatScreen;

    private Button menuToTest = new Button("To test");
    private Group testGroup;

    //all tech tree information stuff
    private ArrayList<TechNode> techNodes = new ArrayList<TechNode>();
    private Label techDisplay = new Label("Queue: ");

    //something else?
    private boolean ifInMainGame = false;

    private Label stackInfo = new Label("Stack info");


    //combat stuff
    private Environment combatSpace = new Environment();


    @Override
    public void start(Stage primaryStage) throws Exception{
        //test stuff
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreenExitHint("");
        mainGame = new Group();
        mainMenuGroup = new Group();
        techTreeGroup = new Group();
        combatScreen = new Group();
        shipCustomizer = new Group();
        testGroup = new Group();

        intializeMainMenu(mainMenuGroup, primaryStage);

        intializeTechTree(techTreeGroup, primaryStage);

        initializeCombat(combatScreen, primaryStage);

        intializeShipCustomizer(shipCustomizer, primaryStage, mainMenu);
    }

    public void initializeMainGame(Group root, Stage primaryStage){
        mainPlayerOwnedSystems = allPlayers.get(mainPlayerIndex).getAllKnownSystems();
        mainPlayerKnownSystems = mainPlayerOwnedSystems;

        setupSprites(root);
        setupGUIElements(root, primaryStage);
        mouseControl(root, mainMenu, primaryStage);

        background = new Rectangle(WIDTH, HEIGHT);
        background.toBack();
        root.getChildren().add(background);
        background.setFill(Color.WHITE);
        background.toBack();
        background.setOpacity(0);
        background.setOnMouseClicked(mouseEvent -> {
            selectedObjectID = "None__" + 0;
            gui.changeObject(root);
        });

        File spriteLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\ui elements\\game\\background.png");
        BasicSprite b = new BasicSprite(spriteLocation.toURI().toString(), "GameBackground");
        gameBG = new BasicImageView(b, "GameBackground");
        root.getChildren().add(gameBG);
        gameBG.toBack();
        gameBG.setFitHeight(HEIGHT);
        gameBG.setFitWidth(WIDTH);


        for (Player allPlayer : allPlayers) {
            allPlayer.initializeUpgrades();
        }

        root.getChildren().add(stackInfo);
        stackInfo.setLayoutX(100);
        stackInfo.setLayoutY(600);

        timer = new MyTimer();
        timer.start();
    }

    public void intializeMainMenu(Group root, Stage primaryStage){
        primaryStage.setTitle("Game test");
        mainMenu = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        mainMenu.getStylesheets().add(getClass().getResource("stylesheets/test.css").toExternalForm());
        primaryStage.setScene(mainMenu);

        setUpMainMenu(root, primaryStage);

        primaryStage.show();

    }

    public void intializeTechTree(Group root, Stage primaryStage){
        for(int i = 0; i < allPlayers.get(mainPlayerIndex).getTech().getNumTech(); i++){
            techNodes.add(new TechNode(allPlayers.get(mainPlayerIndex).getTech().getTech(i), root));
            root.getChildren().add(techNodes.get(i));
        }

        root.getChildren().add(techDisplay);

        root.getChildren().add(techTreeToGame);
        techTreeToGame.setOnAction(value -> {
            ifInMainGame = true;
            primaryStage.getScene().setRoot(mainGame);
        });
        techDisplay.toFront();
        techDisplay.setLayoutX(HEIGHT / 2);

    }

    public void initializeCombat(Group root, Stage primaryStage){
        menuToCombat.setLayoutX(100);
        menuToCombat.setLayoutY(100);
        mainMenuGroup.getChildren().add(menuToCombat);
        Fleet temp = new Fleet(null, "Fleet_00");
        ShipTemplate tempShipTemp =  new TestShipTemplate(2, 0, "TestShip");
        tempShipTemp.addWeapon(new ProjectileWeapon("projectileTest"));
        tempShipTemp.addWeapon(new MissileWeapon("missileTest"));
        temp.addShip(new Ship("Ship__00", tempShipTemp), "vanguard");
        temp.addShip(new Ship("Ship__01", tempShipTemp), "vanguard");
        temp.addShip(new Ship("Ship__02", tempShipTemp), "vanguard");
        temp.addShip(new Ship("Ship__03", tempShipTemp), "vanguard");
        temp.addShip(new Ship("Ship__04", tempShipTemp), "vanguard");
        System.out.println(temp.getSection(0).getShips());

        Fleet temp2 = new Fleet(null, "Fleet_10");
        temp2.addShip(new Ship("Ship__10", tempShipTemp), "vanguard");
        temp2.addShip(new Ship("Ship__11", tempShipTemp), "vanguard");
        temp2.addShip(new Ship("Ship__12", tempShipTemp), "vanguard");
        temp2.addShip(new Ship("Ship__13", tempShipTemp), "vanguard");
        temp2.addShip(new Ship("Ship__14", tempShipTemp), "vanguard");

        menuToCombat.setOnAction(value -> {
            combatSpace.loadObjects(new Fleet[]{temp, temp2}, primaryStage, mainMenuGroup);
            //combatSpace.loadTest(primaryStage);
            combatSpace.start();
        });

    }

    public void intializeShipCustomizer(Group root, Stage primaryStage, Scene scene){
        shipGUI = new ShipCustomizerGUI(root, primaryStage, scene);
    }

    public void initializeTestEnvironment(Group root, Stage primaryStage){
        Star starTest = new Star(1000, 1000, 1000, 1000, 1, "red");
        root.getChildren().add(starTest);
    }


    //GUI methods
    public void setupSprites(Group root){
        for(int i = 0; i < map.getNumSystems(); i++){
            //loads all planets and sets them as invisible
            DoubleCooridinate worldPos = new DoubleCooridinate(map.getSystemAt(i).getX(), map.getSystemAt(i).getY());
            Coordinate screenPos = worldToScreen(worldPos);
            SystemSprite sys = new SystemSprite("", screenPos.getX(), screenPos.getY(), root, map.getSystemAt(i), i);
            nodes.add(sys);
            //nodes.get(i).setInvisible();
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

        //sets any systems in close proximity to visible
        for(int j = 0; j < nodes.size(); j++){
            //distance = sqrt(deltaX^2 + deltaY^2)
            double distanceX, distanceY, distance;
            distanceX = nodes.get(j).getSys().getX() - nodes.get(mainPlayerOwnedSystems.get(0)).getSys().getX();
            distanceY = nodes.get(j).getSys().getY() - nodes.get(mainPlayerOwnedSystems.get(0)).getSys().getY();
            distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            if(nodes.get(mainPlayerOwnedSystems.get(0)).getSys().getViewDistance() >= distance * HEIGHT / 100){
                nodes.get(j).setVisible();
            }
        }

        //sets up the lines
        for(int i = 0; i < nodes.size(); i++){ //i is for system
            //goes through each array of contectors
            for(int j = 0; j < nodes.get(i).getSys().getConnections().size(); j++){ //j is for connections
                DoubleCooridinate p1 = new DoubleCooridinate(0, 0);
                DoubleCooridinate p2 = new DoubleCooridinate(nodes.get(i).getSys().getX(), nodes
                        .get(i).getSys().getY());
                for(SystemSprite sys : nodes){ //sys is system that is connected to i
                    if(sys.getSys().getName().compareTo(nodes.get(i).getSys().getConnections().get(j)) == 0){
                        p1.setX(sys.getSys().getX());
                        p1.setY(sys.getSys().getY());
                        break;
                    }
                }

                Coordinate p1Screen = worldToScreen(p1);
                Coordinate p2Screen = worldToScreen(p2);

                boolean ifExists = false;
                for(int k = 0; k < connectors.size(); k++){
                    if((connectors.get(k).getCon1().compareTo(nodes.get(i).getSys().getName()) == 0 && connectors.get(k).
                            getCon2().compareTo(nodes.get(i).getSys().getConnections().get(j)) == 0) || (connectors.get(k).
                            getCon2().compareTo(nodes.get(i).getSys().getName()) == 0 && connectors.get(k).getCon1().
                            compareTo(nodes.get(i).getSys().getConnections().get(j)) == 0)){
                        ifExists = true;
                    }
                }
                if(!ifExists){
                    connectors.add(new ConnectionSprite(p1, p2, root, nodes.get(i).getSys().getName(), nodes.get(i).getSys()
                            .getConnections().get(j)));
                    connectors.get(connectors.size() - 1).setP1(p1Screen);
                    connectors.get(connectors.size() - 1).setP2(p2Screen);
                    //connectors.get(connectors.size() - 1).setInvisible();
                }
            }
        }
    }

    public void setupGUIElements(Group root, Stage primaryStage){
        gui = new planetGUI(root, nodes.get(mainPlayerOwnedSystems.get(0)).getSys());
        nextTurnButton.setText("Next turn");
        nextTurnButton.setTranslateX(WIDTH - 70);
        nextTurnButton.setTranslateY(HEIGHT - 30);
        root.getChildren().add(nextTurnButton);


        turnLabel = new Label("Turn: 1");
        turnLabel.setLayoutX(WIDTH - WIDTH / 25);
        turnLabel.setLayoutY(HEIGHT - HEIGHT / 10);
        root.getChildren().add(turnLabel);

        nextTurnButton.setOnAction(value -> {
            if(!ifUpdating) {
                turn++;
                turnLabel.setText("Turn: " + turn);
                ifUpdating = true;
            }
        });

        gameToTechTree.setLayoutX(WIDTH * 9 / 10);
        gameToTechTree.setLayoutY(HEIGHT / 50);
        root.getChildren().add(gameToTechTree);

        gameToTechTree.setOnAction(value -> {
            ifInMainGame = false;
            primaryStage.getScene().setRoot(techTreeGroup);
        });

        gameToShip.setLayoutX((WIDTH * 7 / 10));
        gameToShip.setLayoutY(HEIGHT / 50);
        root.getChildren().add(gameToShip);

        gameToShip.setOnAction(value -> {
            ifInMainGame = false;
            primaryStage.getScene().setRoot(shipCustomizer);
        });

        gameToDiplomacy.setLayoutX(WIDTH * 8 / 10);
        gameToDiplomacy.setLayoutY(HEIGHT / 50);
        root.getChildren().add(gameToDiplomacy);
    }

    public void setUpMainMenu(Group root, Stage primaryStage){
        menuToGame = new Button("change scene");
        root.getChildren().add(menuToGame);
        menuToGame.setOnAction(value -> {
            initializeMainGame(mainGame, primaryStage);
            ifInMainGame = true;
            primaryStage.getScene().setRoot(mainGame);

        });

        root.getChildren().add(menuToTest);
        menuToTest.setLayoutX(WIDTH / 2.0);
        menuToTest.setLayoutY(HEIGHT / 2.0);
        menuToTest.setOnAction(value -> {
            initializeTestEnvironment(testGroup, primaryStage);
            primaryStage.getScene().setRoot(testGroup);
        });


    }


    //Movement methods
    private void mouseControl(Group root, Scene scene, Stage stage){

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            if(!ifUpdating && ifInMainGame) {
                DoubleCooridinate worldBeforeZoom = screenToWorld(new DoubleCooridinate(mouseX, mouseY));
                mouseWorldBeforeZoomX = worldBeforeZoom.getX();
                mouseWorldBeforeZoomY = worldBeforeZoom.getY();
                double movement = event.getDeltaY();

                if (ifInMainGame) {
                    if (movement > 0) {
                        zoomFactor *= 1.05;
                    } else if (movement < 0) {
                        zoomFactor /= 1.05;
                    }

                    double min = Math.pow(map.getDistancefactor() / 2.0, -1);
                    double max = map.getDistancefactor() / 2.0;

                    if (zoomFactor < min) {
                        zoomFactor = min;
                    }
                    if (zoomFactor > max) {
                        zoomFactor = max;
                    }
                }

                DoubleCooridinate worldAfterZoom = screenToWorld(new DoubleCooridinate(mouseX, mouseY));
                mouseWorldAfterZoomX = worldAfterZoom.getX();
                mouseWorldAfterZoomY = worldAfterZoom.getY();

                if (ifInMainGame) {
                    offSetX += (mouseWorldBeforeZoomX - mouseWorldAfterZoomX);
                    offSetY += (mouseWorldBeforeZoomY - mouseWorldAfterZoomY);

                    DoubleCooridinate topLeftScreen = new DoubleCooridinate(0, 0);
                    DoubleCooridinate worldPosTopLeft = screenToWorld(topLeftScreen);
                    DoubleCooridinate botRightScreen = new DoubleCooridinate(WIDTH, HEIGHT);
                    DoubleCooridinate worldPosBotRight = screenToWorld(botRightScreen);
                    if (worldPosTopLeft.getX() < -200) {
                        offSetX = -200 - topLeftScreen.getX() / zoomFactor;
                    } else if (worldPosBotRight.getX() > 862.5 * map.getDistancefactor() + 200) {
                        offSetX = (862.5 * map.getDistancefactor() + 200) - botRightScreen.getX() / zoomFactor;
                    } else if (worldPosBotRight.getX() > 862.5 * map.getDistancefactor() && worldPosTopLeft.getX() < -200) {

                    } else if (worldPosBotRight.getX() >= 862.5 * map.getDistancefactor() && worldPosTopLeft.getX() <= -200) {

                    }
                    if (worldPosTopLeft.getY() < -200) {
                        offSetY = -200 - topLeftScreen.getY() / zoomFactor;
                    } else if (worldPosBotRight.getY() > 862.5 * map.getDistancefactor() + 200) {
                        offSetY = (862.5 * map.getDistancefactor() + 200) - botRightScreen.getY() / zoomFactor;
                    } else if (worldPosTopLeft.getY() < -200 && worldPosBotRight.getY() > 862.5 * map.getDistancefactor() + 200) {

                    } else if (worldPosTopLeft.getY() <= -200 && worldPosBotRight.getY() >= 862.5 * map.getDistancefactor() + 200) {

                    }
                }
            }

        });

        scene.setOnMouseMoved(mouseEvent -> {
            if(!ifUpdating && ifInMainGame){
                mouseX = (int)mouseEvent.getX();
                mouseY = (int)mouseEvent.getY();
            }
            /*DoubleCooridinate mouseP = new DoubleCooridinate(mouseX, mouseY);
            DoubleCooridinate worldP = screenToWorld(mouseP);
            System.out.println(mouseP.getX() + " " + mouseP.getY());
            System.out.println(worldP.getX() + " " + worldP.getY());
            System.out.println(offSetX + " " + offSetY);
            System.out.println(zoomFactor);
            System.out.println();*/

        });

        scene.setOnMousePressed(MouseEvent -> {
            if(!ifUpdating && ifInMainGame) {
                startPanX = MouseEvent.getX();
                startPanY = MouseEvent.getY();
            }
        });

        scene.setOnMouseDragged(MouseEvent -> {
            if(!ifUpdating) {
                if (ifInMainGame) {
                    offSetX -= (MouseEvent.getX() - startPanX) / zoomFactor;
                    offSetY -= (MouseEvent.getY() - startPanY) / zoomFactor;
                    startPanX = MouseEvent.getX();
                    startPanY = MouseEvent.getY();

                    DoubleCooridinate topLeftScreen = new DoubleCooridinate(0, 0);
                    DoubleCooridinate worldPosTopLeft = screenToWorld(topLeftScreen);
                    DoubleCooridinate botRightScreen = new DoubleCooridinate(WIDTH, HEIGHT);
                    DoubleCooridinate worldPosBotRight = screenToWorld(botRightScreen);
                    if (worldPosTopLeft.getX() < -200) {
                        offSetX = -200 - topLeftScreen.getX() / zoomFactor;
                    } else if (worldPosBotRight.getX() > 862.5 * map.getDistancefactor() + 200) {
                        offSetX = (862.5 * map.getDistancefactor() + 200) - botRightScreen.getX() / zoomFactor;
                    } else if (worldPosBotRight.getX() > 862.5 * map.getDistancefactor() && worldPosTopLeft.getX() < -200) {

                    } else if (worldPosBotRight.getX() >= 862.5 * map.getDistancefactor() && worldPosTopLeft.getX() <= -200) {

                    }
                    if (worldPosTopLeft.getY() < -200) {
                        offSetY = -200 - topLeftScreen.getY() / zoomFactor;
                    } else if (worldPosBotRight.getY() > 862.5 * map.getDistancefactor() + 200) {
                        offSetY = (862.5 * map.getDistancefactor() + 200) - botRightScreen.getY() / zoomFactor;
                    } else if (worldPosTopLeft.getY() < -200 && worldPosBotRight.getY() > 862.5 * map.getDistancefactor() + 200) {

                    } else if (worldPosTopLeft.getY() <= -200 && worldPosBotRight.getY() >= 862.5 * map.getDistancefactor() + 200) {

                    }
/*
                DoubleCooridinate botRightScreen = new DoubleCooridinate(WIDTH, HEIGHT);
                DoubleCooridinate worldPosBotRight = screenToWorld(botRightScreen);
                if(worldPosBotRight.getY() < 862.5 * map.getDistancefactor()){
                    offSetY = 862.5 * map.getDistancefactor() - botRightScreen.getY() / zoomFactor;
                }
                if(worldPosBotRight.getX() < 862.5 * map.getDistancefactor()){
                    offSetX = 862.5 * map.getDistancefactor() - botRightScreen.getX() / zoomFactor;
                }
                System.out.println(worldPosTopLeft.getX() + " " + worldPosTopLeft.getY());
                System.out.println(worldPosBotRight.getX() + " " + worldPosBotRight.getY());
                System.out.println();*/
                }
            }
        });


    }


    //Calculation methods
    private Coordinate worldToScreen(DoubleCooridinate worldPos){
        int x = (int)((worldPos.getX() * HEIGHT / 100 - offSetX) * zoomFactor);
        int y = (int)((worldPos.getY() * HEIGHT / 100 - offSetY) * zoomFactor);
        return new Coordinate(x, y);
    }

    private DoubleCooridinate screenToWorld(DoubleCooridinate screenPos){
        double x = (screenPos.getX() / zoomFactor + offSetX);
        double y = (screenPos.getY() / zoomFactor + offSetY);
        return new DoubleCooridinate(x, y);
    }

    private void setUpOverlapForAllFleets(){
        //fleet stack
        /*for(int i = 0; i < FleetNodes.size(); i++){
            if(FleetNodes.get(i).getStack() == null) { //since stacts rest at the end of the turn
                Fleet selected = allPlayers.get(Integer.parseInt(FleetNodes.get(i).getSelectedID().substring(6, 7))).
                        getFleet(FleetNodes.get(i).getSelectedID());
                ArrayList<String> objectsOnSamePosition = new ArrayList<>();
                objectsOnSamePosition.add(selected.getID());

                //tests all regular fleets
                for (int j = 0; j < FleetNodes.size(); j++) {
                    System.out.println(FleetNodes.size() + " " + j);
                    Fleet selectedTest = allPlayers.get(Integer.parseInt(FleetNodes.get(j).getSelectedID().substring(6,
                            7))).getFleet(FleetNodes.get(i).getSelectedID());
                    //System.out.println(selected.getID() + " " + selectedTest.getID());
                    if (selectedTest.getID().compareTo(selected.getID()) != 0) {
                        System.out.println(selected.getID() + " " + selectedTest.getID() + " but with overlap");
                        if (selectedTest.getPosition().getX() == selected.getPosition().getX() && selectedTest.
                                getPosition().getY() == selected.getPosition().getY()) {
                            //on same position
                            objectsOnSamePosition.add(selectedTest.getID());
                        }
                    }
                }


                //hide fleets on bottom
                FleetSprite top = null;
                int topPower = -1;
                OverlapStack stack = new OverlapStack();
                for (String s : objectsOnSamePosition) {
                    //creates new overlapstack
                    stack.addObject(s);
                    //System.out.println("Added to stack " + s + " to " + FleetNodes.get(i).getSelectedID());
                }
                // System.out.println("STACK SIZE: " + stack.getStack().size());

                //goes to each sprite with this object and saves this overlap stack to it
                for (String s : objectsOnSamePosition) {
                    if(s.substring(0, 6).compareTo("Fleet_") == 0){
                        //saves to a fleet
                        for(FleetSprite sp : FleetNodes){
                            if(sp.getSelectedID().compareTo(s) == 0){
                                sp.putInStack(stack);
                                break;
                            }

                        }
                    } else if(s.substring(0, 6).compareTo("TraFle") == 0){
                        //saves to a tradeRoute
                        for(TraitorRoute tr : allTradeRoutes){
                            if(tr.getID().compareTo(s) == 0){
                                tr.putInStack(stack);
                                break;
                            }
                        }
                    }
                }

                //hides fleets and trade routes not at top of stack
                for(int j = 0; j < stack.getStack().size(); j++){
                    //System.out.println("SHOULD ALWAYS GET HERE " + j);
                    if(j > 0) {
                        //System.out.println("SHOULD GET HERE WHEN 2 SHIPS");
                        if (stack.getStack().get(j).substring(0, 6).compareTo("Fleet_") == 0) {
                            //saves to a fleet
                            for (FleetSprite sp : FleetNodes) {
                                if(sp.getSelectedID().compareTo(stack.getStack().get(j)) == 0) {
                                    sp.setInvisible(true);
                                    System.out.println("SET INVISIBLE " + sp.getTopOfStack() + " " + sp.getStack().
                                            getStack().size());
                                    break;
                                }
                            }
                        } else {
                            if(stack.getStack().get(j).substring(0, 6).compareTo("TraFle") == 0){
                                //saves to a tradeRoute
                                for(TraitorRoute tr : allTradeRoutes){
                                    if(tr.getID().compareTo(stack.getStack().get(j)) == 0){
                                        tr.setVisible(false);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                FleetNodes.get(i).setInvisible(false);
            }
        }*/

        //goes through each fleet and trade node
        for(int i = 0; i < FleetNodes.size(); i++){
            //only picks out ones that have had their layer set to null
            if(FleetNodes.get(i).getStack() == null) {
                FleetSprite selectedSprite = FleetNodes.get(i);
                Fleet selectedFleet = allPlayers.get(Integer.parseInt(selectedSprite.getSelectedID().substring(6, 7))).
                        getFleet(selectedSprite.getSelectedID());

                OverlapStack stack = new OverlapStack();
                stack.addObject(selectedFleet.getID());

                //goes through all others and tests if its on same pos
                for (FleetSprite selectedTestSprite : FleetNodes) {
                    //tests if it is a different node as well as position
                    Fleet selectedTestFleet = allPlayers.get(Integer.parseInt(selectedTestSprite.getSelectedID().
                            substring(6, 7))).getFleet(selectedTestSprite.getSelectedID());
                    if (selectedTestSprite.getSelectedID().compareTo(selectedSprite.getSelectedID()) != 0) {
                        //if on same position, add to overlapStack object
                        if (selectedTestFleet.getPosition().getX() == selectedFleet.getPosition().getX() &&
                                selectedTestFleet.getPosition().getY() == selectedFleet.getPosition().getY()) {
                            //adds to current stack
                            stack.addObject(selectedTestFleet.getID());
                        }
                    }
                }


                for(TraitorRoute selectedTestTrade : allTradeRoutes){
                    TradeFleet selectedTestTradeFleet = allPlayers.get(Integer.parseInt(selectedTestTrade.getID().
                            substring(6, 7))).getTradeFleet(selectedTestTrade.getID());
                    if(selectedTestTradeFleet.getPosition().getX() == selectedFleet.getPosition().getX() &&
                            selectedTestTradeFleet.getPosition().getY() == selectedFleet.getPosition().getY()){
                        stack.addObject(selectedTestTradeFleet.getId());
                    }
                }

                //sends stack info to each object in the stack
                for(int j = 0; j < stack.getStack().size(); j++){
                    if(stack.get(j).substring(0, 6).compareTo("Fleet_") == 0) {
                        //do the fleet update
                        for (FleetSprite fleetNode : FleetNodes) {
                            if (stack.get(j).compareTo(fleetNode.getSelectedID()) == 0) {
                                fleetNode.putInStack(stack);
                                break;
                            }
                        }
                    } else if(stack.get(j).substring(0, 6).compareTo("TraFle") == 0){
                        for(TraitorRoute tr : allTradeRoutes){
                            if(stack.get(j).compareTo(tr.getID()) == 0){
                                tr.putInStack(stack);
                                break;
                            }
                        }
                    }
                }
            }
        }


        for(int i = 0; i < allTradeRoutes.size(); i++){
            //only picks out ones that have had their layer set to null
            if(allTradeRoutes.get(i).getStack() == null) {
                TraitorRoute selectedRoute = allTradeRoutes.get(i);
                TradeFleet selectedFleet = allPlayers.get(Integer.parseInt(selectedRoute.getID().substring(6, 7))).
                        getTradeFleet(selectedRoute.getID());

                OverlapStack stack = new OverlapStack();
                stack.addObject(selectedRoute.getID());

                //goes through all others and tests if its on same pos
                for (FleetSprite selectedTestSprite : FleetNodes) {
                    //tests if it is a different node as well as position
                    Fleet selectedTestFleet = allPlayers.get(Integer.parseInt(selectedTestSprite.getSelectedID().
                            substring(6, 7))).getFleet(selectedTestSprite.getSelectedID());
                    //if on same position, add to overlapStack object
                    if (selectedTestFleet.getPosition().getX() == selectedFleet.getPosition().getX() &&
                            selectedTestFleet.getPosition().getY() == selectedFleet.getPosition().getY()) {
                        //adds to current stack
                        stack.addObject(selectedTestFleet.getID());
                    }

                }


                for(TraitorRoute selectedTestTrade : allTradeRoutes){
                    TradeFleet selectedTestTradeFleet = allPlayers.get(Integer.parseInt(selectedTestTrade.getID().
                            substring(6, 7))).getTradeFleet(selectedTestTrade.getID());
                    if(selectedTestTrade.getID().compareTo(allTradeRoutes.get(i).getID()) != 0) {
                        if (selectedTestTradeFleet.getPosition().getX() == selectedFleet.getPosition().getX() &&
                                selectedTestTradeFleet.getPosition().getY() == selectedFleet.getPosition().getY()) {
                            stack.addObject(selectedTestTradeFleet.getId());
                        }
                    }
                }

                //sends stack info to each object in the stack
                for(int j = 0; j < stack.getStack().size(); j++){
                    if(stack.get(j).substring(0, 6).compareTo("Fleet_") == 0) {
                        //do the fleet update
                        for (FleetSprite fleetNode : FleetNodes) {
                            if (stack.get(j).compareTo(fleetNode.getSelectedID()) == 0) {
                                fleetNode.putInStack(stack);
                                break;
                            }
                        }
                    } else if(stack.get(j).substring(0, 6).compareTo("TraFle") == 0){
                        for(TraitorRoute tr : allTradeRoutes){
                            if(stack.get(j).compareTo(tr.getID()) == 0){
                                tr.putInStack(stack);
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    private void setUpOverlapForFleet(FleetSprite fs, TraitorRoute tr){
        if(fs != null && !ifUpdating){
            //go through all fleets (and trade routes), and if stack contains fs, delete it
            for(FleetSprite f : FleetNodes){
                if(f.getSelectedID().compareTo(fs.getSelectedID()) != 0){
                    for(String ids : f.getStack().getStack()){
                        if(ids.compareTo(fs.getSelectedID()) == 0){
                            f.removeFromStack(ids);
                            break;
                        }
                    }
                }
            }
            for(TraitorRoute tr2 : allTradeRoutes){
                for(String ids : tr2.getStack().getStack()){
                    if(ids.compareTo(fs.getSelectedID()) == 0){
                        tr2.removeFromStack(ids);
                        break;
                    }
                }
            }

            //reset fs stack
            fs.resetStack();

            //go through all fleets and compare position
            OverlapStack stack = new OverlapStack();
            stack.addObject(fs.getSelectedID());
            Fleet selected = allPlayers.get(Integer.parseInt(fs.getSelectedID().substring(6, 7))).getFleet(fs.getSelectedID());
            for(FleetSprite f : FleetNodes){
                if(f.getSelectedID().compareTo(fs.getSelectedID()) != 0){
                    Fleet selectedTest = allPlayers.get(Integer.parseInt(f.getSelectedID().substring(6, 7))).getFleet
                            (f.getSelectedID());
                    if(selectedTest.getPosition().getX() == selected.getPosition().getX() && selectedTest.getPosition().
                            getY() == selected.getPosition().getY()){
                        //add to stack
                        stack.addObject(f.getSelectedID());
                    }
                }
            }

            for(TraitorRoute tr2 : allTradeRoutes){
                TradeFleet selectedTradeTest = allPlayers.get(Integer.parseInt(tr2.getID().substring(6, 7))).
                        getTradeFleet(tr2.getID());
                if(selectedTradeTest.getPosition().getX() == selected.getPosition().getX() && selectedTradeTest.
                        getPosition().getY() == selected.getPosition().getY()){
                    stack.addObject(selectedTradeTest.getId());
                }
            }

            //adds newly created stack to fs
            for(String str : stack.getStack()){
                if(str.substring(0, 6).compareTo("Fleet_") == 0) {
                    for (FleetSprite f : FleetNodes) {
                        if (f.getSelectedID().compareTo(str) == 0) {
                            //selects fleet f it is has the new stack
                            f.putInStack(stack);
                            break;
                        }
                    }
                }else if(str.substring(0, 6).compareTo("TraFle") == 0){
                    for(TraitorRoute tr1 : allTradeRoutes){
                        if(tr1.getID().compareTo(str) == 0){
                            tr1.putInStack(stack);
                            break;
                        }
                    }
                }
            }
        } else if(tr != null && !ifUpdating){
            //go through all fleets and routes, and if stack contains tr, delete it
            for(FleetSprite f : FleetNodes){
                for(String ids : f.getStack().getStack()){
                    if(ids.compareTo(tr.getID()) == 0){
                        f.removeFromStack(ids);
                        break;
                    }
                }
            }
            for(TraitorRoute tr2 : allTradeRoutes){
                if(tr2.getID().compareTo(tr.getID()) != 0) {
                    for (String ids : tr2.getStack().getStack()) {
                        if (ids.compareTo(tr.getID()) == 0) {
                            tr2.removeFromStack(ids);
                            break;
                        }
                    }
                }
            }

            //reset tr stack
            tr.resetStack();

            //go through all fleets and compare position
            OverlapStack stack = new OverlapStack();
            stack.addObject(tr.getID());
            TradeFleet selectedTradeFleet = allPlayers.get(Integer.parseInt(tr.getID().substring(6, 7))).getTradeFleet(tr.getID());
            for(FleetSprite f : FleetNodes){
                Fleet selectedTest = allPlayers.get(Integer.parseInt(f.getSelectedID().substring(6, 7))).getFleet
                        (f.getSelectedID());
                if(selectedTest.getPosition().getX() == selectedTradeFleet.getPosition().getX() && selectedTest.
                        getPosition().getY() == selectedTradeFleet.getPosition().getY()){
                    //add to stack
                    stack.addObject(f.getSelectedID());
                }
            }

            for(TraitorRoute tr2 : allTradeRoutes){
                TradeFleet selectedTradeTest = allPlayers.get(Integer.parseInt(tr2.getID().substring(6, 7))).getTradeFleet(
                        tr2.getID());
                if(selectedTradeTest.getId().compareTo(selectedTradeFleet.getId()) != 0){
                    if(selectedTradeFleet.getPosition().getX() == selectedTradeTest.getPosition().getX() &&
                            selectedTradeFleet.getPosition().getY() == selectedTradeTest.getPosition().getY()){
                        stack.addObject(tr2.getID());
                    }
                }
            }

            //adds newly created stack to fs
            for(String str : stack.getStack()){
                if(str.substring(0, 6).compareTo("Fleet_") == 0) {
                    for (FleetSprite f : FleetNodes) {
                        if (f.getSelectedID().compareTo(str) == 0) {
                            //selects fleet f it is has the new stack
                            f.putInStack(stack);
                            break;
                        }
                    }
                }else if(str.substring(0, 6).compareTo("TraFle") == 0){
                    for(TraitorRoute tr1 : allTradeRoutes){
                        if(tr1.getID().compareTo(str) == 0){
                            tr1.putInStack(stack);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void checkIfHide(FleetSprite fs){
        if(fs.getStack() != null) {
            if (fs.getStack().getStack().get(0).compareTo(fs.getSelectedID()) == 0) {
                fs.setInvisible(false);
            } else {
                fs.setInvisible(true);
            }
        }
    }

    private void checkIfHide(TraitorRoute tr){
        if(tr.getStack() != null) {
            if (tr.getStack().getStack().get(0).compareTo(tr.getID()) == 0) {
                tr.setInvisible(false);
            } else {
                tr.setInvisible(true);
            }
        }
    }


    //Gameloop objects
    private class MyTimer extends AnimationTimer{
        int currentTurn = 1;
        boolean ifUpdateCalled = false;
        long timeDuringNextTurnStart = -1;
        int framesOnRightNow = 0;
        @Override
        public void handle(long now){
            doStuff();
        }

        private void doStuff(){
            if(nameOfObjectsMoved.size() > 0){
                //move all objects
                updateShipPos();
            }

            for(FleetSprite f : FleetNodes){
                checkIfHide(f);
            }
            for(TraitorRoute tr : allTradeRoutes){
                checkIfHide(tr);
            }

            if(!ifUpdating) {
                timeDuringNextTurnStart = System.nanoTime(); //given in milliseconds
            }
            if(currentTurn != turn){
                //executes once at the begin, updates data
                if(!ifUpdateCalled){
                    ifUpdateCalled = true;
                    update();
                }
                framesOnRightNow++;
                //updateShips(); like 60 times
                //executes once at the end
                if(framesOnRightNow > 60){
                    endOfTurnUpdate();
                    currentTurn++;
                    ifUpdateCalled = false;
                    framesOnRightNow = 0;
                    ifUpdating = false;
                }
            } else {
                 //scale();
            }
            scale();

        }

        private void updateShipPos(){
            SolarSystem[] map = new SolarSystem[nodes.size()];
            for(int i = 0; i < nodes.size(); i++){
                map[i] = nodes.get(i).getSys();
            }

            //moves fleets and traders
            for(int i = 0; i < nameOfObjectsMoved.size(); i++){
                if(nameOfObjectsMoved.get(i).substring(0, 6).compareTo("TraFle") == 0){
                    //moves traders
                    for(TraitorRoute tr : allTradeRoutes){
                        if(tr.getID().compareTo(nameOfObjectsMoved.get(i)) == 0){
                            //move i
                            if(framesUntilShipStopsMoving.get(i) >= 0) {
                                allPlayers.get(Integer.parseInt(nameOfObjectsMoved.get(i).substring(6, 7))).
                                        moveTradeFleetAnim(nameOfObjectsMoved.get(i), map);
                                framesUntilShipStopsMoving.set(i, framesUntilShipStopsMoving.get(i) - 1);
                            } else {

                                framesUntilShipStopsMoving.remove(i);
                                nameOfObjectsMoved.remove(i);
                                i--;
                                break;
                            }
                        }
                    }
                } else if(nameOfObjectsMoved.get(i).substring(0, 6).compareTo("Fleet_") == 0) {
                    for(FleetSprite f : FleetNodes) { //replace fleetNodes with hashmap later
                        if (f.getSelectedID().compareTo(nameOfObjectsMoved.get(i)) == 0) {
                            setUpOverlapForFleet(f, null);
                            //move i
                            if (framesUntilShipStopsMoving.get(i) >= 0) {
                                allPlayers.get(Integer.parseInt(nameOfObjectsMoved.get(i).substring(6, 7))).moveFleetAnim(f.
                                        getSelectedID(), map);
                                framesUntilShipStopsMoving.set(i, framesUntilShipStopsMoving.get(i) - 1);
                                f.nextTurn();
                            } else {
                                //test overlap for this object
                                for(FleetSprite fs : FleetNodes){
                                    if(fs.getSelectedID().compareTo(nameOfObjectsMoved.get(i)) == 0){
                                        setUpOverlapForFleet(fs, null);
                                        break;
                                    }
                                }

                                framesUntilShipStopsMoving.remove(i);
                                nameOfObjectsMoved.remove(i);
                                i--;
                                break;
                            }
                            //break; WHY IS THIS HERE???? WHAT WAS I THINKING!!!???
                        }
                    }
                }
            }
        }

        private void endOfTurnUpdate(){
            //stack work
            for(FleetSprite f : FleetNodes){
                f.resetStack();
            }
            setUpOverlapForAllFleets();


            //does trade route work
            for(Player p : allPlayers){
                p.doNextTurnCheck();
            }

            //updates solar systems and garisoned ships
            for(SystemSprite ss : nodes){
                for(Player p : allPlayers){
                    for(SolarSystem s : p.getAllOwnedSystems()){
                        if(s.getName().compareTo(ss.getSys().getName()) == 0){
                            ss.updateSys(s);
                        }
                    }
                }
                ArrayList<Fleet> garrison = new ArrayList<>();
                for(Player p : allPlayers){
                    for(Fleet fleet : p.getFleets()){
                        if((fleet.getDepartedFrom().getName().compareTo(ss.getSys().getName()) == 0 && !fleet.getIfMoving())){
                            garrison.add(fleet);
                        }
                    }
                }

                ArrayList<TradeFleet> tradeGarrison = new ArrayList<>();
                for(Player p : allPlayers){
                    Set<String> keys = p.getTradeFleets().keySet();
                    for(String key : keys) {
                        if ((p.getTradeFleets().get(key).getDepartedFrom().getName().compareTo(ss.getSys().getName()) ==
                                0 && !p.getTradeFleets().get(key).getIfMoving())) {
                            tradeGarrison.add(p.getTradeFleet(key));
                        }
                    }
                }
                ss.getSys().updateGarrison(garrison);
            }


            //resets movement points for all ships for the next turn
            for(int i = 0; i < allPlayers.size(); i++){ //resets movement points for the turn
                allPlayers.get(i).updateFleets();
            }
        }

        private void update(){

            //updates player scores, then gets them, these will be used for ai decision making
            Score[] currentScores = new Score[allPlayers.size()];
            for(int i = 0; i < allPlayers.size(); i++){
                currentScores[i] = allPlayers.get(i).getScore();
            }
            scores.nextTurn(currentScores);


            //updates info for each player
            SolarSystem[] planets = new SolarSystem[nodes.size()];
            for(int i = 0; i < nodes.size(); i++){
                planets[i] = nodes.get(i).getSys();
            }
            for(int i = 0; i < allPlayers.size(); i++){
                allPlayers.get(i).nextTurn(turn, scores, planets);
            }


            //increase viewDistance of planets
            for(int i = 0; i < mainPlayerOwnedSystems.size(); i++){
                //checks distance to each system
                for(int j = 0; j < nodes.size(); j++){
                    //distance = sqrt(deltaX^2 + deltaY^2)
                    double distanceX, distanceY, distance;
                    distanceX = nodes.get(j).getSys().getX() - nodes.get(mainPlayerOwnedSystems.get(i)).getSys().getX();
                    distanceY = nodes.get(j).getSys().getY() - nodes.get(mainPlayerOwnedSystems.get(i)).getSys().getY();
                    distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                    if(nodes.get(mainPlayerOwnedSystems.get(i)).getSys().getViewDistance() >= distance){
                        nodes.get(j).setVisible();
                        for(ConnectionSprite con : connectors){
                            if(nodes.get(j).getSys().getName().compareTo(con.getCon1()) == 0 || nodes.get(j).getSys().
                                    getName().compareTo(con.getCon2()) == 0){
                                con.setVisible();
                            }
                        }
                    }
                }
            }



            //does ai colonization
            //first will see if any of the ai will be colonizing the system (only if the colonizer is there)
            ArrayList<Integer> indexsOfBotsWhoColonizeThisTurn = new ArrayList<>();
            for(int i = 0; i < allPlayers.size(); i++){
                if(allPlayers.get(i).getIfBot() && allPlayers.get(i).getIfWantsToColonize()){ //only if the player is a bot will it execute this code
                    indexsOfBotsWhoColonizeThisTurn.add(i);
                }
            }
            //check if which ships can actually colonize
            ArrayList<String> namesOfPotentialColonies = new ArrayList<>();
            for(Integer i : indexsOfBotsWhoColonizeThisTurn){
                String[] strs = allPlayers.get(i).getNamesOfNewColonies();
                /**
                 *
                 *
                 * THREAD HERE COULD BE VERY USEFUL, SEE IF YOU COULD QUICKLY OPEN ONE TO TEST FOR OVERLAP, THEN CLOSE
                 * WILL NOT BE IMPACTED SO SYNCHRONIZATION SHOULD BE EASY
                 * ALSO OPTIMIZE THIS
                 *
                 */
                for(int j = 0; j < strs.length; j++){
                    boolean ifStrAtJ = false;
                    for(int k = 0; k < namesOfPotentialColonies.size(); k++){
                        if(strs[j].compareTo(namesOfPotentialColonies.get(k)) == 0){
                            ifStrAtJ = true;
                        }
                    }

                    if(!ifStrAtJ){
                        namesOfPotentialColonies.add(strs[j]);
                    }
                }
            }
            //next checks if only 1 ai wants to colonize
            for(String str : namesOfPotentialColonies){
                int numPlayersWhoWantToColonize = 0;
                ArrayList<Integer> indexOfPlayer = new ArrayList<>();
                for(int i = 0; i < allPlayers.size(); i++){
                     if(allPlayers.get(i).getIfBot() && allPlayers.get(i).getIfWantToColonizeThisSystem(str)){
                         numPlayersWhoWantToColonize++;
                         indexOfPlayer.add(i);
                     }
                }
                if(numPlayersWhoWantToColonize == 1){
                    //colonize
                    for(int i = 0; i < nodes.size(); i++){
                        if(nodes.get(i).getSys().getName().compareTo(str) == 0) {
                            if (nodes.get(i).getSys().getOwner() == -1) {
                                //removes the fleetNode for the colonizer that will be taken away
                                int indexOfSelectedFleetNode = -1;
                                String nameOfColonizerFleet = "";
                                for (int j = 0; j < allPlayers.get(indexOfPlayer.get(0)).getNumFleets(); j++) {
                                    if (allPlayers.get(indexOfPlayer.get(0)).getFleet(allPlayers.get(indexOfPlayer.get(0)).
                                            getIDOfFleetAtIndex(j)).getDepartedFrom().getName().compareTo(nodes.get(i).getSys().getName()) == 0) {
                                        int numShips = 0;
                                        boolean ifHasColonizer = false;
                                        for (int k = 0; k < 4; k++) {
                                            for (int l = 0; l < allPlayers.get(indexOfPlayer.get(0)).getFleet(allPlayers.get(
                                                    indexOfPlayer.get(0)).getIDOfFleetAtIndex(j)).getSection(k).getNumShips(); l++) {
                                                numShips++;
                                                if (allPlayers.get(indexOfPlayer.get(0)).getFleet(allPlayers.get(indexOfPlayer
                                                        .get(0)).getIDOfFleetAtIndex(j)).getSection(k).getShipAtIndex(l).
                                                        getType().getPurpose().compareTo("colonizer") == 0) {
                                                    ifHasColonizer = true;
                                                }
                                            }
                                        }
                                        if (numShips == 1 && ifHasColonizer) {
                                            nameOfColonizerFleet = allPlayers.get(indexOfPlayer.get(0)).getFleet(allPlayers.
                                                    get(indexOfPlayer.get(0)).getIDOfFleetAtIndex(j)).getID();
                                        }
                                    }
                                }
                                for (int j = 0; j < FleetNodes.size(); j++) {
                                    if (FleetNodes.get(j).getSelectedID().compareTo(nameOfColonizerFleet) == 0 &&
                                            nameOfColonizerFleet.compareTo("") != 0) {
                                        FleetNodes.get(j).destroy();
                                        break;
                                    }
                                }

                                allPlayers.get(indexOfPlayer.get(0)).colonizeSystem(nodes.get(i).getSys(), i, allPlayers.
                                        get(indexOfPlayer.get(0)).getIndexOfBestPlanetToColonize(nodes.get(i).getSys()));
                                nodes.get(i).changeColor(indexOfPlayer.get(0));
                                nodes.get(i).updateSys(allPlayers.get(indexOfPlayer.get(0)).getSystem(nodes.get(i).getSys().getName()));
                                allPlayers.get(indexOfPlayer.get(0)).destroyColonizer(nodes.get(i).getSys().getName());
                            /*System.out.println("Population: " + allPlayers.get(indexOfPlayer.get(0)).getSystem(nodes.
                                    get(i).getSys().getName()).getPopulation());*/
                                break;
                            }
                        } else {
                            //make something that will change their target
                        }
                    }
                }
            }


            //gets rid of any fleet nodes that are null
            for(int i = 0; i < FleetNodes.size(); i++){
                if(FleetNodes.get(i).getSelectedID().compareTo("deleted") == 0){
                    FleetNodes.get(i).destroy();
                    i--;
                }
            }


            //updates each fleet node
            for(FleetSprite f : FleetNodes){
                if(allPlayers.get(Integer.parseInt(f.getSelectedID().substring(6, 7))).getFleet(f.getSelectedID()).getIfMoving()) {
                    nameOfObjectsMoved.add(f.getSelectedID());
                    framesUntilShipStopsMoving.add(60);
                }
            }

            for(TraitorRoute tr : allTradeRoutes){
                if(allPlayers.get(Integer.parseInt(tr.getID().substring(6, 7))).getTradeFleet(tr.getID()).getIfMoving()){
                    nameOfObjectsMoved.add(tr.getID());
                    framesUntilShipStopsMoving.add(60);
                }
            }





            System.out.println("\n NEXT TURN " + turn + " \n");

            /*//tradefleet stack
            for(int i = 0; i < allTradeRoutes.size(); i++){
                TradeFleet selected = allPlayers.get(Integer.parseInt(FleetNodes.get(i).getSelectedID().substring(6, 7))
                    ).getTradeFleet(allTradeRoutes.get(i).getID());
                ArrayList<String> objectsOnSamePosition = new ArrayList<>();
                objectsOnSamePosition.add(selected.getId());

                //tests all trade fleets
                for(int j = 0; j < allTradeRoutes.size(); j++){
                    TradeFleet selectedTest = allPlayers.get(Integer.parseInt(allTradeRoutes.get(j).getID().substring(6,
                            7))).getTradeFleet(allTradeRoutes.get(j).getID());
                    if(selectedTest.getId().compareTo(selected.getId()) != 0){
                        if(selectedTest.getPosition().getX() == selected.getPosition().getX() && selectedTest.getPosition().
                                getY() == selected.getPosition().getY()){
                            //on same position
                            objectsOnSamePosition.add(selectedTest.getId());
                        }
                    }
                }
                for(int j = 0; j < FleetNodes.size(); j++){
                    Fleet selectedTest = allPlayers.get(Integer.parseInt(FleetNodes.get(i).getId().substring(6, 7))).
                            getFleet(FleetNodes.get(i).getId());
                    if(selectedTest.getPosition().getX() == selected.getPosition().getX() && selectedTest.getPosition().
                            getY() == selected.getPosition().getY()){
                        objectsOnSamePosition.add(selectedTest.getID());
                    }
                }

                TraitorRoute top = null;
            }*/


            if(allPlayers.get(mainPlayerIndex).getTech().getQueue().size() > 0){
                techDisplay.setText("Queue: " + allPlayers.get(mainPlayerIndex).getTech().getQueue().get(0).getID() +
                        " Science needed: " + allPlayers.get(mainPlayerIndex).getTech().getQueue().get(0).getScienceNeeded());
            } else {
                techDisplay.setText("Queue: Empty");
            }


            //creates new ship nodes for newly created ships
            int x = 0;
            for(Player p : allPlayers){
                ArrayList<Fleet> temp = p.getNewlyConstructedFleets();
                for(int i = 0; i < temp.size(); i++){
                    x++;
                    FleetNodes.add(new FleetSprite(mainGame, temp.get(i).getID()));
                    setUpOverlapForFleet(FleetNodes.get(FleetNodes.size() - 1), null);
                }
            }
            for(int i = FleetNodes.size() - x; i < FleetNodes.size(); i++){
                mainGame.getChildren().add(FleetNodes.get(i));
            }


            //creates new trade routes
            for(Player p : allPlayers){
                ArrayList<TradeFleet> temp = p.getNewlyConstructedTraders();
                if(temp != null) {
                    for (TradeFleet tf : temp) {
                        DoubleCooridinate worldPos = new DoubleCooridinate(tf.getDepartedFrom().getX(),
                                tf.getDepartedFrom().getY());
                        Coordinate screenPos = worldToScreen(worldPos);
                        allTradeRoutes.add(new TraitorRoute(mainGame, screenPos.getX(), screenPos.getY(), tf.getId()));
                        setUpOverlapForFleet(null, allTradeRoutes.get(allTradeRoutes.size() - 1));
                    }
                }
            }

            /*if(FleetNodes.size() > 0){
                for(int j = 0; j < FleetNodes.size(); j++){
                    if(FleetNodes.get(j).getStack() != null) {
                        String str = "";
                        for (int i = 0; i < FleetNodes.get(0).getStack().getStack().size(); i++) {
                            str += FleetNodes.get(0).getStack().get(i) + " ";
                        }
                        System.out.println(str + " for " + FleetNodes.get(j));
                    }
                }
            }*/

            gui.nextTurnUpdate();
        }

        private void scale(){
            //keeps camera in view
            DoubleCooridinate worldPosToLeft = new DoubleCooridinate(0, 0);
            Coordinate screenPosTopLeft = worldToScreen(worldPosToLeft);
            gameBG.setX(screenPosTopLeft.getX());
            gameBG.setY(screenPosTopLeft.getY());
            gameBG.setFitWidth(HEIGHT * zoomFactor * map.getDistancefactor());
            gameBG.setFitHeight(HEIGHT * zoomFactor * map.getDistancefactor());
            /*if(screenPosTopLeft.getX() > 0){
                offSetX = 0;
            }
            if(screenPosTopLeft.getY() > 0){
                offSetY = 0;
            }*/


            /*Runtime rt = Runtime.getRuntime();
            System.out.println("Used memory: " + (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024));
            System.out.println("Total memory: " + rt.totalMemory() / (1024 * 1024));*/
            //checks virtual machine memory available and used

            //scales and moves planets based off scale
            for(int i = 0; i < nodes.size(); i++){
                Coordinate screenPos = worldToScreen(new DoubleCooridinate(nodes.get(i).getSys().getX()
                        , nodes.get(i).getSys().getY()));
                nodes.get(i).setX(screenPos.getX());
                nodes.get(i).setY(screenPos.getY());
                nodes.get(i).scaleRadius(zoomFactor);
                //nodes.get(i).toBack();

            }
            
            //scales and moves fleets
            for(int i = 0; i < allPlayers.size(); i++){
                for(int j = 0; j < allPlayers.get(i).getNumFleets(); j++){
                    DoubleCooridinate temp = new DoubleCooridinate(allPlayers.get(i).getLocationOfFleetAt(j).getX(),
                            allPlayers.get(i).getLocationOfFleetAt(j).getY());
                    Coordinate screenPos = worldToScreen(temp);
                    for(int k = 0; k < FleetNodes.size(); k++){
                        if(FleetNodes.get(k) == null){
                            System.out.println("Bad");
                        }
                        //System.out.println(FleetNodes.get(k).getTopOfStack() + " " + k);
                        if(FleetNodes.get(k).getSelectedID().compareTo(allPlayers.get(i).getIDOfFleetAtIndex(j)) == 0){
                            FleetNodes.get(k).setX(screenPos.getX());
                            FleetNodes.get(k).setY(screenPos.getY());
                            FleetNodes.get(k).scale(zoomFactor);
                            break;
                        }

                    }
                }
            }

            //scales and moves trade fleets
            for(TraitorRoute tr : allTradeRoutes){
                DoubleCooridinate worldPos = new DoubleCooridinate(allPlayers.get(Integer.parseInt(tr.getID().substring
                        (6, 7))).getTradeFleet(tr.getID()).getDepartedFrom().getX(), allPlayers.get(Integer.parseInt(tr.
                        getID().substring(6, 7))).getTradeFleet(tr.getID()).getDepartedFrom().getY());
                Coordinate screenPos = worldToScreen(worldPos);
                tr.setX(screenPos.getX());
                tr.setY(screenPos.getY());
            }

            //Scales and moves paths
            for(int i = 0; i < connectors.size(); i++){
                //DoubleCooridinate temp = new DoubleCooridinate(co)
                Coordinate p1Screen = worldToScreen(new DoubleCooridinate(connectors.get(i).getP1X()
                , connectors.get(i).getP1Y()));
                Coordinate p2Screen = worldToScreen(new DoubleCooridinate(connectors.get(i).getP2X()
                        , connectors.get(i).getP2Y()));

                connectors.get(i).setP1(p1Screen);
                connectors.get(i).setP2(p2Screen);
            }


            /*//creates new ship nodes for newly created ships
            int x = 0;
            for(Player p : allPlayers){
                ArrayList<Fleet> temp = p.getNewlyConstructedFleets();
                for(int i = 0; i < temp.size(); i++){
                    x++;
                    FleetNodes.add(new FleetSprite(mainGame, temp.get(i).getID()));
                }
            }
            for(int i = FleetNodes.size() - x; i < FleetNodes.size(); i++){
                mainGame.getChildren().add(FleetNodes.get(i));
            }*/

            //sets visible if in stack
            
            background.toBack();
            gameBG.toBack();
        }
    }


    //GUI objects
    private class SystemSprite extends Button{
        private int radiusCentral = 10;

        private SolarSystem sys;
        private Circle sprite = new Circle(radiusCentral);
        private Circle area = new Circle(0);
        private Star star;

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
            super.setVisible(true);

            //View Area
            if(sys.getOwner() == mainPlayerIndex){
                sys.setViewDistance(25);
            }
            area.setCenterX(x);
            area.setCenterY(y);
            area.setOpacity(.2);
            area.setFill(Color.GREY);
            area.setRadius(sys.getViewDistance() * HEIGHT / 100);
            root.getChildren().add(area);
            area.toBack();
            area.setOnMouseClicked(value -> {
                selectedObjectID = "None__";
                gui.changeObject(root);
            });


            //setting up sprite
            sprite.setCenterX(x);
            sprite.setCenterY(y);
            root.getChildren().add(sprite);

            sprite.setVisible(true);
            sprite.setOpacity(.2);
            star = new Star(sys.getX(), sys.getY(), 20.0 / (3 - sys.getStarSize()), 20.0 / (3 - sys
                    .getStarSize()), zoomFactor, sys.getStarType());
            star.setCenterX(x);
            star.setCenterY(y);
            root.getChildren().add(star);

            //setting up data that is locally stored for GUI
            this.sys = sys;
            this.index = i; //corresponds to index in nodes

            //set color
            if(sys.getOwner() != -1){
                String color = allPlayers.get(sys.getOwner()).getColor();
                setColor(color);
            } else {
                sprite.setFill(Color.WHITE);
            }


            //set button event
            super.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                String typeOfObjectSelected = selectedObjectID.substring(0, 6);

                @Override
                public void handle(MouseEvent mouseEvent) {
                    String typeOfObjectSelected = selectedObjectID.substring(0, 6);
                    if(mouseEvent.getButton() == MouseButton.SECONDARY){
                        //Move ship
                        if(typeOfObjectSelected.compareTo("Fleet_") == 0){
                            for(int j = 0; j < allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).
                                    getFleets().size(); j++){
                                if(allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getFleets().get(j)
                                        .getID().compareTo(selectedObjectID) == 0){
                                    String iD = allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).
                                            getFleets().get(j).getID();
                                    if(!allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getFleet(iD).
                                            getIfMoving()){
                                        //gets if moving and the correct ship

                                        //makes a map
                                        SolarSystem[] planets = new SolarSystem[nodes.size()];
                                        for(int i = 0; i < nodes.size(); i++){
                                            planets[i] = nodes.get(i).getSys();
                                        }
                                        allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).moveFleet(iD,
                                                nodes.get(i).getSys(), planets);


                                        //checks if it should be put in a stack
                                        SolarSystem departedFrom = allPlayers.get(Integer.parseInt(selectedObjectID.
                                                substring(6, 7))).getFleet(iD).getDepartedFrom();
                                        for(SystemSprite s : nodes) {
                                            if(s.getSys().getName().compareTo(departedFrom.getName()) == 0){
                                                s.getSys().removeFromGarrison(selectedObjectID);
                                            }
                                        }

                                        for(FleetSprite f : FleetNodes){
                                            if(f.getSelectedID().compareTo(selectedObjectID) == 0){
                                                nameOfObjectsMoved.add(f.getSelectedID());
                                                framesUntilShipStopsMoving.add(60);
                                            }
                                        }
                                        //allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).up;
                                    }
                                }
                            }

                            selectedObjectID = "solSys" + index;
                        } else if(typeOfObjectSelected.substring(0, 6).compareTo("CreTra") == 0){
                            TradeFleet traFle = allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).
                                    getTradeFleet("TraFle" + selectedObjectID.substring(6)); //used for accessing data with minimal code
                            if(!traFle.getIfMoving()){
                                //create trade route

                                //creates map
                                SolarSystem[] map = new SolarSystem[nodes.size()];
                                for(int i = 0; i < nodes.size(); i++){
                                    map[i] = nodes.get(i).getSys();
                                }
                                allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).setUpTradeRoute(
                                        "TraFle" + selectedObjectID.substring(6), sys, map);
                                nameOfObjectsMoved.add("TraFle" + selectedObjectID.substring(6));
                                framesUntilShipStopsMoving.add(60);
                            }
                        }
                    } else if(mouseEvent.getButton() == MouseButton.PRIMARY){
                        //Select System
                        if(typeOfObjectSelected.compareTo("None__") == 0) {
                            selectedObjectID = getSelectedID();
                            gui.changeObject(sys, root);
                        } else if(typeOfObjectSelected.compareTo("solSys") == 0){
                            selectedObjectID = "solSys" + index;
                            gui.changeObject(nodes.get(index).getSys(), root);
                        } else {
                            selectedObjectID = "solSys" + index;
                            gui.changeObject(nodes.get(index).getSys(), root);
                        }
                    }
                }
            });

        }

        public void updateSys(SolarSystem newSys){
            sys = newSys;
        }

        private String getSelectedID(){
            return "solSys" + index;
        }

        public void setX(int newX){
            sprite.setCenterX(newX);
            area.setCenterX(newX);
            star.setCenterX(newX);
            super.setTranslateX(newX - sprite.getRadius());
        }

        public void setY(int newY){
            sprite.setCenterY(newY);
            area.setCenterY(newY);
            star.setCenterY(newY);
            super.setTranslateY(newY - sprite.getRadius());
        }

        public void updateVD(double deltaVD){
            sys.setViewDistance(deltaVD);
            area.setRadius(sys.getViewDistance() * zoomFactor * HEIGHT / 100);
        }

        public void scaleRadius(double zoomFactor){
            sprite.setRadius(radiusCentral * zoomFactor);
            area.setRadius(sys.getViewDistance() * zoomFactor * HEIGHT / 100);
            star.setSpriteSize(zoomFactor);
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

        public void changeColor(int newOwner){
            setColor(allPlayers.get(newOwner).getColor());
        }
    }

    private class ConnectionSprite{
        private Line line;
        private DoubleCooridinate p1World;
        private DoubleCooridinate p2World;
        private String con1;
        private String con2;


        public ConnectionSprite(DoubleCooridinate p1World, DoubleCooridinate p2World, Group root, String con1, String
                con2){
            this.p1World = p1World;
            this.p2World = p2World;
            line = new Line(worldToScreen(p1World).getX(), worldToScreen(p1World).getY(), worldToScreen(p2World).getX(),
                    worldToScreen(p2World).getY());
            line.setFill(Color.WHITE);
            root.getChildren().add(line);
            this.con1 = con1;
            this.con2 = con2;
        }

        public void setP1(int x, int y){
            line.setStartX(x);
            line.setStartY(y);
        }

        public void setP2(int x, int y){
            line.setEndX(x);
            line.setEndY(y);
        }

        public void setP1(Coordinate screenPos){
            line.setStartX(screenPos.getX());
            line.setStartY(screenPos.getY());
        }

        public void setP2(Coordinate screenPos){
            line.setEndX(screenPos.getX());
            line.setEndY(screenPos.getY());
        }

        public DoubleCooridinate getP1World(){ return p1World; }
        public DoubleCooridinate getP2World(){ return p2World; }
        public double getP1X(){ return p1World.getX(); }
        public double getP1Y(){ return p1World.getY(); }
        public double getP2X(){ return p2World.getX(); }
        public double getP2Y(){ return p2World.getY(); }
        public String getCon1(){ return con1; }
        public String getCon2(){ return con2; }

        public void setInvisible(){
            line.setVisible(false);
        }

        public void setVisible(){
            line.setVisible(true);
        }
    }

    private class planetGUI{
        //GUI objects
        private Rectangle bottom;
        private Rectangle left;

        //Info displayers for system
        private Label systemName;
        private Label systemIndustry;
        private Label systemFood;
        private Label systemScience;
        private Label systemCash;
        private Label systemPower;
        private Label systemPop;
        private Label systemGarrison;
        private Label id;

        private Label allPlanets = new Label("test");

        private ArrayList<buildUpgrade> upgradeTiles = new ArrayList<>();
        private ArrayList<buildShip> shipTiles = new ArrayList<>();

        //Display info
        private SolarSystem selectedSystem;
        private String typeOfObjectSelected;
        private int selectedSystemIndex = mainPlayerIndex;

        private ArrayList<PlanetSprite> planetSprites = new ArrayList<>();



        public planetGUI(Group root, SolarSystem homeSystem){
            selectedSystem = homeSystem;
            bottom = new Rectangle(WIDTH * 5 / 7.0, HEIGHT / 4.0);
            bottom.setY(HEIGHT / 4.0 * 3);
            bottom.setX(WIDTH / 7.0 * 2);
            bottom.setFill(Color.WHITE);
            bottom.setStroke(Color.BLACK);
            root.getChildren().add(bottom);

            left = new Rectangle(WIDTH / 7.0 * 2, HEIGHT);
            left.setFill(Color.WHITE);
            left.setStroke(Color.BLACK);
            root.getChildren().add(left);
            intitializeLabels(root);
            setUpLabelsForOwnedSystem(root);
            updateTiles(root);

            root.getChildren().add(allPlanets);


        }

        public void updateTiles(Group root){
            clearTiles(root);
            ScrollList upgrades = new ScrollList("upgrades", null, root);
        }

        public void intitializeLabels(Group root){
            systemName = new Label(selectedSystem.getName());
            root.getChildren().add(systemName);
            systemName.setLayoutX(WIDTH / 10.0);
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

            systemPop = new Label();
            root.getChildren().add(systemPop);
            systemPop.setLayoutY(180);
            systemPop.setLayoutX(5);

            systemGarrison = new Label();
            root.getChildren().add(systemGarrison);
            systemGarrison.setLayoutY(250);
            systemGarrison.setLayoutX(5);

            id = new Label("Only for testing");
            id.setLayoutX(200);
            id.setLayoutY(200);
            root.getChildren().add(id);
        }

        public void changeObject(Group root){
            clearShipInfo();
            clearFleetSelector();
            clearSysLabels();
            clearTiles(root);
            clearPlanetSprites(root);
            bottom.setVisible(false);
            left.setVisible(false);
            id.setText(selectedObjectID);
        }

        public void changeObject(SolarSystem newSys, Group root){
            id.setText(selectedObjectID);
            bottom.setVisible(true);
            left.setVisible(true);
            clearShipInfo();
            clearFleetSelector();
            clearPlanetSprites(root);
            clearTiles(root);
            selectedSystem = newSys;
            typeOfObjectSelected = "solSys";
            systemName.setText(newSys.getName());
            if(selectedSystem.getName().length() == 7){
                selectedSystemIndex = Integer.parseInt(selectedSystem.getName().substring(6, 7));
            } else if(selectedSystem.getName().length() == 8){
                selectedSystemIndex = Integer.parseInt(selectedSystem.getName().substring(6, 8));
            }
            if(selectedSystem.getOwner() == mainPlayerIndex){
                setUpLabelsForOwnedSystem(root);
                updateTiles(root);
                ArrayList<String> namesOfShips = new ArrayList<>();
                for(Fleet f : newSys.getGarrison()){
                    namesOfShips.add(f.getID());
                }
                chooseFleet(namesOfShips, root);
            } else if(selectedSystem.getOwner() == -1){
                setUpLabelsForOwnedSystem(root);
                clearTiles(root);
            } else /*if(selectedSystem.getOwner() != -1 && selectedSystem.getOwner() == mainPlayerIndex)*/{
                setUpLabelsForOwnedSystem(root);
                clearTiles(root);
            }
            updatePlanets(root, Integer.parseInt(newSys.getName().substring(6, 7)));

        }

        public void changeObject(Fleet selectedFleet, Group root){
            id.setText(selectedObjectID);
            bottom.setVisible(true);
            left.setVisible(true);
            clearTiles(root);
            clearSysLabels();
            clearFleetSelector();
            clearPlanetSprites(root);
            updateFleetInfo(selectedFleet, root);
        }

        public void changeObject(TradeFleet selectedTradeFleet, Group root){
            id.setText(selectedObjectID);
            bottom.setVisible(true);
            left.setVisible(true);
            clearTiles(root);
            clearSysLabels();
            clearFleetSelector();
            clearPlanetSprites(root);
            updateTradeFleetInfo(selectedTradeFleet, root);
        }

        public void changeObject(ArrayList<String> list, Group root){
            id.setText(selectedObjectID);
            bottom.setVisible(true);
            left.setVisible(true);
            clearTiles(root);
            clearSysLabels();
            clearFleetSelector();
            clearPlanetSprites(root);
            chooseFleet(list, root);
        }

        public void chooseFleet(ArrayList<String> list, Group root){

            String[] fleets = new String[list.size()];
            for(int i = 0; i < fleets.length; i++){
                fleets[i] = list.get(i);
            }
            ScrollList fleetList = new ScrollList("fleets", fleets, root);
        }

        public void updateTradeFleetInfo(TradeFleet tradeFleet, Group root){
            int counter = 0;
            int row = 0;
            String[] actions = allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getTradeFleet(
                    selectedObjectID).getActions();
            for(String action : actions){
                FleetOption temp = new FleetOption(action, tradeFleet.getId());
                temp.setLayoutX(HEIGHT / 10.0 * counter);
                temp.setLayoutY(HEIGHT / 10.0 * row + HEIGHT / 4.0);
                root.getChildren().add(temp);
                counter++;
                if (counter > 3) {
                    counter = 0;
                    row++;
                }
            }
        }

        public void updateFleetInfo(Fleet selectedFleet, Group root){
            int counter = 0;
            int row = 0;
            String[] actions = allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getFleet(
                    selectedObjectID).getActions();
            for (String action : actions) {
                FleetOption temp = new FleetOption(action, selectedFleet.getID());
                temp.setLayoutX(HEIGHT / 10.0 * counter);
                temp.setLayoutY(HEIGHT / 10.0 * row + HEIGHT / 4.0);
                root.getChildren().add(temp);

                counter++;
                if (counter > 3) {
                    counter = 0;
                    row++;
                }
            }
        }

        public void clearShipInfo(){
            ObservableList<Node> temp = mainGame.getChildren();
            for(int i = 0; i < temp.size(); i++){
                if(temp.get(i).toString().length() > 5) {
                    if (temp.get(i).toString().substring(0, 6).compareTo("FleOpt") == 0) {
                        mainGame.getChildren().remove(i);
                        i--;
                    }
                }
            }
        }

        public void nextTurnUpdate(){
            if(selectedObjectID.substring(0, 6).compareTo("solSys") == 0){
                clearShipInfo();
                clearPlanetSprites(mainGame);
                updateTiles(mainGame);
                updateLabels(mainGame);
                updatePlanets(mainGame, Integer.parseInt(selectedObjectID.substring(6, 7)));
            } else if(selectedObjectID.substring(0, 6).compareTo("ship__") == 0){
                clearTiles(mainGame);
                clearSysLabels();
                updateFleetInfo(allPlayers.get(Integer.parseInt(selectedObjectID.substring(6, 7))).getFleet(selectedObjectID), mainGame);
            } else {
                clearTiles(mainGame);
                clearSysLabels();
                clearShipInfo();
            }
        }

        public void clearTiles(Group root){
            upgradeTiles.clear();
            shipTiles.clear();

            ObservableList<Node> temp = root.getChildren();
            for(int i = 0; i < temp.size(); i++){
                if(temp.get(i).toString().substring(0, 6).compareTo("bldShp") == 0){
                    root.getChildren().remove(i);
                    i--;
                }
                if(temp.get(i).toString().substring(0, 6).compareTo("bldUpg") == 0){
                    root.getChildren().remove(i);
                    i--;
                }
                if(temp.get(i).toString().length() >= 25){
                    if(temp.get(i).toString().substring(0, 25).compareTo("fleetSelectScrollBartiles") == 0){
                        root.getChildren().remove(i);
                        i--;
                    }
                }
            }
        }

        public void clearPlanetSprites(Group root){
            for(int i = 0; i < root.getChildren().size(); i++){
                if(root.getChildren().get(i).toString().substring(0, 12).compareTo("planetSprite") == 0){
                    root.getChildren().remove(i);
                    i--;
                }
            }
            planetSprites.clear();
        }

        public void clearSysLabels(){
            systemName.setLayoutX(-500);
            systemIndustry.setLayoutX(-500);
            systemFood.setLayoutX(-500);
            systemScience.setLayoutX(-500);
            systemCash.setLayoutX(-500);
            systemPop.setLayoutX(-500);
            systemGarrison.setLayoutX(-500);
            allPlanets.setLayoutX(-500);
            //systemPower.setLayoutX(-100);
        }

        public void clearFleetSelector(){
            ObservableList<Node> nodes = mainGame.getChildren();
            for(int i = 0; i < nodes.size(); i++){
                if(nodes.get(i).toString().length() >= 17){
                    if(nodes.get(i).toString().substring(0, 17).compareTo("fleetSelectButton") == 0){
                        mainGame.getChildren().remove(i);
                        i--;
                    }
                    if(nodes.get(i).toString().length() >= 25){
                        if(nodes.get(i).toString().substring(0, 25).compareTo("fleetSelectScrollBarfleets") == 0){
                            mainGame.getChildren().remove(i);
                            i--;
                        }
                    }
                }
            }
        }

        private void setUpLabelsForOwnedSystem(Group root){
            systemName.setLayoutX(WIDTH / 10.0);

            String industry = "Industry: ";

            industry += selectedSystem.getIndustry();
            systemIndustry.setText(industry);
            systemIndustry.setLayoutY(120);
            systemIndustry.setLayoutX(5);

            String food = "Food: ";
            food += selectedSystem.getFood();
            systemFood.setText(food);
            systemFood.setLayoutY(135);
            systemFood.setLayoutX(5);


            String science = "Science: ";
            science += selectedSystem.getScience();
            systemScience.setText(science);
            systemScience.setLayoutY(150);
            systemScience.setLayoutX(5);

            String cash = "Cash: ";
            cash += selectedSystem.getCash();
            systemCash.setText(cash);
            systemCash.setLayoutY(165);
            systemCash.setLayoutX(5);

            String population = "Population: ";
            population += selectedSystem.getPopulation();
            systemPop.setText(population);
            systemPop.setLayoutX(5);
            systemPop.setLayoutY(180);

            String garrison = "Garrison: ";
            for(Fleet f : selectedSystem.getGarrison()){
                garrison += f.getID() + " ";
            }
            systemGarrison.setText(garrison);
            systemGarrison.setLayoutX(5);
            systemGarrison.setLayoutY(250);



            /*String militaryPower = "Military Power: ";
            militaryPower += selectedSystem.getManpowerSupported();
            systemPower.setLayoutY(180);
            systemPower.setLayoutX(5);*/

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

        public void updateLabels(Group root){
            if(selectedSystem.getName().length() == 7){
                selectedSystemIndex = Integer.parseInt(selectedSystem.getName().substring(6, 7));
            } else if(selectedSystem.getName().length() == 8){
                selectedSystemIndex = Integer.parseInt(selectedSystem.getName().substring(6, 8));
            }
            if(selectedSystem.getOwner() == mainPlayerIndex){
                setUpLabelsForOwnedSystem(root);
                updateTiles(root);
            } else if(selectedSystem.getOwner() == -1){
                setUpLabelsForOwnedSystem(root);
                clearTiles(root);
            } else /*if(selectedSystem.getOwner() != -1 && selectedSystem.getOwner() == mainPlayerIndex)*/{
                setUpLabelsForOwnedSystem(root);
                clearTiles(root);
            }
        }

        public void updatePlanets(Group root, int index){
            SolarSystem newSys = nodes.get(index).getSys();

            for(int i = 0; i < newSys.getPlanets().length; i++){
                planetSprites.add(new PlanetSprite(newSys.getPlanets()[i], newSys.getName(), i, root, root.getChildren().size()));
                planetSprites.get(i).setX(WIDTH / 10 * (i + 1));
                planetSprites.get(i).setY(HEIGHT / 5 * 4);
                root.getChildren().add(planetSprites.get(i));
                if(newSys.getPlanets()[i].getPopulation() > 0){
                    planetSprites.get(i).setColor(allPlayers.get(newSys.getOwner()).getColorAsColor());
                }
            }
        }


        //UI objects
        private class ScrollList{

            public ScrollList(String type, String[] objects, Group root){
                if(type.compareTo("fleets") == 0){
                    setUpFleets(root, objects);
                } else if(type.compareTo("upgrades") == 0){
                    setUpUpgrades(root);
                }
            }

            private void setUpFleets(Group root, String[] fleets){
                int xPos = 20;
                int buttonWidth = HEIGHT / 10;
                int buttonHeight = HEIGHT / 30;

                double maxScroll = 100;
                int top = HEIGHT / 5 * 3;
                int bottom = HEIGHT / 5 * 4;
                fleetSelectScrollbar bar = new fleetSelectScrollbar("fleets");
                bar.setLayoutY(top);
                bar.setOrientation(Orientation.VERTICAL);
                bar.setPrefHeight(HEIGHT / 5.0);

                //scroll stuff
                double valuePerScroll = (bottom - top);
                bar.setBlockIncrement(valuePerScroll);
                int numShown = (bottom - top) / buttonHeight;

                if(numShown < fleets.length) {
                    root.getChildren().add(bar);
                }

                int row = 0;
                int rowsShown = 5;
                fleetSelectButton[] fleetButtons = new fleetSelectButton[fleets.length];
                for(int i = 0; i < fleets.length; i++){
                    fleetButtons[i] = new fleetSelectButton(fleets[i]);
                    root.getChildren().add(fleetButtons[i]);
                    fleetButtons[i].setLayoutX(xPos);
                    fleetButtons[i].setPrefWidth(buttonWidth);
                    fleetButtons[i].setPrefHeight(buttonHeight);
                    fleetButtons[i].setLayoutY(top + i * HEIGHT / 30.0);
                    String iD = fleetButtons[i].getFleet();
                    fleetButtons[i].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if(iD.substring(0, 6).compareTo("Fleet_") == 0){
                                for(Player p : allPlayers){
                                    for(int j = 0; j < p.getNumFleets(); j++){
                                        if(p.getIDOfFleetAtIndex(j).compareTo(iD) == 0){
                                            selectedObjectID = iD;
                                            changeObject(p.getFleet(iD), root);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            } else if(iD.substring(0, 6).compareTo("TraFle") == 0){
                                Set<String> keys = allPlayers.get(Integer.parseInt(iD.substring(6, 7))).
                                        getTradeFleets().keySet();
                                for(String str : keys){
                                    if(allPlayers.get(Integer.parseInt(iD.substring(6, 7))).getTradeFleet(str).getId().compareTo(iD) == 0){
                                        selectedObjectID = iD;
                                        changeObject(allPlayers.get(Integer.parseInt(iD.substring(6, 7))).
                                                getTradeFleet(str), root);
                                    }
                                }
                            }
                        }
                    });
                    row++;

                    fleetButtons[i].setVisible((top + i * HEIGHT / 30 >= top) && (top + i * HEIGHT / 30 <= bottom));
                }


                if(numShown < fleetButtons.length){
                    bar.valueProperty().addListener((observable, oldValue, newValue) -> {
                        //set scroll
                        for(int j = 0; j < fleets.length; j++){
                            double temp = top + j * buttonHeight - newValue.intValue() * (fleets.length - numShown) *
                                    buttonHeight / 100.0;
                            if((temp >= top) && (temp <= bottom)){
                                fleetButtons[j].setLayoutY(temp);
                                fleetButtons[j].setVisible(true);
                            } else {
                                fleetButtons[j].setVisible(false);
                            }

                        }
                    });
                }
            }

            private void setUpUpgrades(Group root){
                int xPos = WIDTH / 200;
                int yPos = HEIGHT / 5 * 2;
                int buttonWidth = WIDTH / 20;
                int buttonHeight = HEIGHT / 20;
                int rowsShown = 5;

                int numObjects = nodes.get(selectedSystemIndex).getSys().getNumUpgrades() + allPlayers.
                        get(mainPlayerIndex).getCivilization().getNumShipTemplates();

                int row = 0;
                int column = 0;
                int maxPerRow = 5;


                fleetSelectScrollbar sb = new fleetSelectScrollbar("tiles");
                sb.setLayoutY(yPos);
                sb.setLayoutX(xPos + buttonWidth * maxPerRow);
                sb.setOrientation(Orientation.VERTICAL);
                sb.setPrefHeight(HEIGHT / 5.0);


                for(int i = 0; i < nodes.get(selectedSystemIndex).getSys().getNumUpgrades(); i++){
                    upgradeTiles.add(new buildUpgrade(root, xPos + column * buttonWidth, yPos + row * buttonHeight
                            , nodes.get(selectedSystemIndex).getSys().getUpgradeAt(i), selectedSystemIndex, row));
                    upgradeTiles.get(i).setPrefHeight(buttonHeight);
                    upgradeTiles.get(i).setPrefWidth(buttonWidth);
                    upgradeTiles.get(i).setLayoutX(xPos + column * buttonWidth);
                    upgradeTiles.get(i).setLayoutY(yPos + row * buttonHeight);
                    root.getChildren().add(upgradeTiles.get(i));

                    column++;
                    if(column >= maxPerRow){
                        column = 0;
                        row++;
                    }

                }
                for(int i = 0; i < allPlayers.get(mainPlayerIndex).getCivilization().getNumShipTemplates(); i++){
                    shipTiles.add(new buildShip(root, xPos + column * buttonWidth, yPos + row * buttonHeight,
                            allPlayers.get(mainPlayerIndex).getCivilization().getShipTemplateAt(i), selectedSystemIndex,
                            row));
                    shipTiles.get(i).setPrefHeight(buttonHeight);
                    shipTiles.get(i).setPrefWidth(buttonWidth);
                    shipTiles.get(i).setLayoutX(xPos + column * buttonWidth);
                    shipTiles.get(i).setLayoutY(yPos + row * buttonHeight);
                    root.getChildren().add(shipTiles.get(i));

                    column++;
                    if(column >= maxPerRow){
                        column = 0;
                        row++;
                    }
                }

                if(rowsShown < row){
                    root.getChildren().add(sb);
                }


                final int tempRow = row;
                sb.valueProperty().addListener((observable, oldValue, newValue) -> {
                    //set scroll
                    for(int j = 0; j < upgradeTiles.size(); j++){
                        double temp = yPos + upgradeTiles.get(j).getRow() * buttonHeight - newValue.intValue() * (upgradeTiles
                                .size() + shipTiles.size() - rowsShown) * buttonHeight / 100.0;
                        upgradeTiles.get(j).setLayoutY(temp);
                        /*if((temp >= top) && (temp <= bottom)){
                            fleetButtons[j].setLayoutY(temp);
                            fleetButtons[j].setVisible(true);
                        } else {
                            fleetButtons[j].setVisible(false);
                        }*/
                    }
                    for(int j = 0; j < allPlayers.get(mainPlayerIndex).getCivilization().getNumShipTemplates(); j++){
                        double temp = yPos + shipTiles.get(j).getRow() * buttonHeight - newValue.intValue() * (upgradeTiles
                                .size() + shipTiles.size() - rowsShown) * buttonHeight / 100.0;
                        shipTiles.get(j).setLayoutY(temp);
                        //System.out.println(newValue.intValue() * (upgradeTiles
                            //    .size() + shipTiles.size() - tempRow) * buttonHeight / 100);
                    }
                });
            }
        }

    }

    private class FleetSprite extends Button{

        public int x = 0;
        public int y = 0;

        //values stored for animation
        private double prevX = -1;
        private double prevY = -1;

        double length = 30;
        double height = 15;
        private File spriteLocation;
        private BasicSprite baseSprite;
        private BasicImageView sprite;
        private String identification;
        //to get player, get identification.subString(6, 7)
        private boolean ifAdded = false;

        private OverlapStack stack;

        public FleetSprite(Group root, String id){
            super();
            super.setOpacity(.2);
            identification = id;
            spriteLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\human ships\\scout.png");
            baseSprite = new BasicSprite(spriteLocation.toURI().toString(), identification);
            sprite = new BasicImageView(baseSprite, identification);
            stack = new OverlapStack();
            stack.addObject(this.getSelectedID());

            super.toFront();
            //super.setOpacity(0);
            DoubleCooridinate worldSpace = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).getPosition();
            Coordinate screenSpace = worldToScreen(worldSpace);
            sprite.setX(screenSpace.getX());
            sprite.setY(screenSpace.getY());
            this.scale(zoomFactor);
            root.getChildren().add(sprite);
            super.setOnAction(event -> {
                //debugging
                String str = this.getSelectedID() + ": \n";
                for(int i = 0; i < stack.getStack().size(); i++){
                    str += stack.get(i) + " ";
                }
                stackInfo.setText(str);

                selectedObjectID = this.getSelectedID();
                if(stack.getStack().size() > 1){
                    //create list
                    gui.changeObject(stack.getStack(), root);
                } else {
                    //go straight to ship menu
                    gui.changeObject(allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(
                            identification), root);
                }

            });
            if(Integer.parseInt(identification.substring(6, 7)) != -1){
                String color = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getColor();
                setColor(color);
            } else {
                    //sprite.setFill(Color.BLACK);
            }
                //super.setOpacity(0);

            prevX = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).
                    getPosition().getX();
            prevY = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).
                    getPosition().getY();
        }

        public BasicImageView getSprite(){ return sprite; }

        public void nextTurn(){

            DoubleCooridinate world = new DoubleCooridinate(allPlayers.get(Integer.parseInt(identification.
                    substring(6, 7))).getFleet(identification).getPosition().getX(), allPlayers.
                    get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).getPosition().getY());
            Coordinate screen = worldToScreen(world);
            DoubleCooridinate world2 = new DoubleCooridinate(allPlayers.get(Integer.parseInt(identification.
                    substring(6, 7))).getFleet(identification).getDepartedFrom().getX(), allPlayers.get(Integer.parseInt(identification.
                    substring(6, 7))).getFleet(identification).getDepartedFrom().getY());
            this.setX(screen.getX());
            this.setY(screen.getY());

            
            prevX = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).
                    getPosition().getX();
            prevY = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).
                    getPosition().getY();
        }

        public void move(SolarSystem sys){
            SolarSystem temp = allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification)
                    .getDepartedFrom();
            Path oldPath = new Path(temp);
            oldPath.addToPath(sys);
            SolarSystem[] map = new SolarSystem[nodes.size()];
            for(int i = 0; i < nodes.size(); i++){
                map[i] = nodes.get(i).getSys();
            }
            PathWithSystems newPath = new PathWithSystems(oldPath, map);
            allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).move(newPath, map);
        }

        public String getSelectedID(){
            if (allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification) == null) {
                return "deleted";
            }
            return allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).getID();
        }

        public boolean getIfAdded(){
            return ifAdded;
        }

        public void add(){
            ifAdded = true;
        }

        public void setX(int x){
            sprite.setX(x);
            super.setLayoutX(x);
            this.x = x;
        }

        public void setY(int y){
            sprite.setY(y);
            super.setLayoutY(y);
            this.y = y;
        }

        public void scale(double scaleFactor){
            sprite.setFitHeight(height * zoomFactor);
            sprite.setFitWidth(length * zoomFactor);
            super.setPrefHeight(height * zoomFactor);
            super.setPrefWidth(length * zoomFactor);
            super.setMinWidth(length * zoomFactor);
            super.setMinHeight(height * zoomFactor);
            super.setMaxHeight(height * zoomFactor);
            super.setMaxWidth(length * zoomFactor);
            sprite.setRotate(allPlayers.get(Integer.parseInt(identification.substring(6, 7))).getFleet(identification).
                    getPositionAsVector().getTheta() * 180 / 3.14 + 90);
        }

        public void putInStack(OverlapStack newStack){
            this.stack = newStack;
        }

        public void putInStack(String str){
            stack.addObject(str);
        }

        public boolean getIfInStack(){
            if(stack == null){
                return false;
            }
            if(stack != null){
                if(stack.getStack().size() > 0){
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }

        public void removeFromStack(String str){
            stack.remove(str);
        }

        public String getTopOfStack(){
            if(stack != null && stack.getStack().size() > 0){
                return stack.get(0);
            }
            return identification;
        }

        public void resetStack(){
            //System.out.println("DELETED");
            stack = null;
        }

        public OverlapStack getStack(){ return stack; }

        public void toFront(){
            sprite.toFront();
            super.toFront();
        }

        public void toBack(){
            sprite.toBack();
            super.toBack();
        }

        public void setInvisible(boolean value){
            super.setVisible(!value);
            sprite.setVisible(!value);
        }

        private void setColor(String color){
            /*if(color.compareTo("blue") == 0){
                sprite.set(Color.BLUE);
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
            }*/
        }

        public void destroy(){
            System.out.println("DELETED THIS SHIP");
            String removalID = this.toString();
            for(int i = 0; i < FleetNodes.size(); i++){
                if(FleetNodes.get(i).getSelectedID().compareTo(this.getSelectedID()) == 0){
                    FleetNodes.remove(i);
                    i--;
                }
            }
            ObservableList<Node> temp = mainGame.getChildren();
            for(int i = 0; i < temp.size(); i++){
                if(temp.get(i).toString().compareTo(removalID) == 0){
                    mainGame.getChildren().remove(i);
                    temp.remove(i);
                    i--;
                }
                String iD = "BasicSprite" + identification;
                if(temp.get(i).toString().compareTo(iD) == 0){
                    mainGame.getChildren().remove(i);
                    temp.remove(i);
                    i--;
                }
                if(temp.get(i).toString().compareTo("ImageView" + identification) == 0){
                    mainGame.getChildren().remove(i);
                    temp.remove(i);
                    i--;
                }
            }
        }

        public void hide(){
            sprite.setOpacity(0);
            super.setVisible(false);
        }

        public void show(){
            sprite.setOpacity(1);
            super.setVisible(true);
        }

        public void nextFrame(int frameOn){

        }

        public String toString(){
            return "FleetSprite" + identification;
        }
    }

    private class TechNode extends Button{
        private Label label;
        private Rectangle background;
        private Label text;
        private Tech tech;
        public TechNode(Tech tech, Group root){
            super();
            this.tech = tech;
            text = new Label(tech.getID());
            background = new Rectangle(0, 0, text.getWidth(), text.getHeight());
            background.setOpacity(.1);
            root.getChildren().add(background);
            root.getChildren().add(text);
            moveInitially();

            this.setOnAction(value -> {
                allPlayers.get(mainPlayerIndex).selectScience(tech.getID());
                techDisplay.setText("Queue: " + allPlayers.get(mainPlayerIndex).getTech().getQueue().get(0).getID() +
                        " Science needed: " + allPlayers.get(mainPlayerIndex).getTech().getQueue().get(0).getScienceNeeded());
            });
        }

        private void moveInitially(){
            super.setLayoutY(HEIGHT / 20 + HEIGHT/ 10 * tech.getRank());
            text.setLayoutY(HEIGHT / 20 + HEIGHT/ 10 * tech.getRank());
            background.setLayoutY(HEIGHT / 20 + HEIGHT/ 10 * tech.getRank());

            super.setLayoutX(WIDTH / (tech.getWithinRankMaxIndex() + 2) * (tech.getWithinRankIndex() + 1));
            text.setLayoutX(WIDTH / (tech.getWithinRankMaxIndex() + 2) * (tech.getWithinRankIndex() + 1));
            background.setX(WIDTH / (tech.getWithinRankMaxIndex() + 2) * (tech.getWithinRankIndex() + 1));


        }

        public void translate(){

        }

        public void removeSprite(){
            
        }
    }

    private class buildOption extends Button{

        private Rectangle rect;
        private String str;
        private int row;

        public buildOption(Group root, int x, int y, String str, int row){
            super(str);
            this.row = row;
            rect = new Rectangle(HEIGHT / 10, HEIGHT / 10);
            super.toFront();
            //super.setOpacity(0);

            super.setLayoutX(x);
            super.setLayoutY(y);
            rect.setLayoutX(x);
            rect.setLayoutY(y);
        }

        public void move(int x, int y){
            super.setLayoutX(x);
            super.setLayoutY(y);
        }

        public int getRow(){ return row; }


    }

    private class buildShip extends buildOption{

        private ShipTemplate shipTemplate;

        public buildShip(Group root, int x, int y, ShipTemplate template, int index, int row){
            super(root, x, y, template.getModelName(), row);
            /*System.out.println(template.getModelName());
            System.out.println(template.getShipClass());
            System.out.println();8*/
            shipTemplate = template;
            super.setOnAction(value -> {
                //if(allPlayers.get(mainPlayerIndex).getIfCanBuildShip()){
                    nodes.get(index).getSys().addToQueue(template.getClone());
                //}
                /*******************************************************************************************************
                            Implement this later, but for now it is best to avoid it for testing purposes
                 *******************************************************************************************************/
            });
        }

        public String toString(){
            return "bldShp" + shipTemplate.getModelName();
        }


        /*
                    String newShipID = allPlayers.get(0).createTestShip(nodes.get(mainPlayerOwnedSystems.get(0)).getSys());
            shipSprite newShip = new shipSprite(root, newShipID);
            root.getChildren().add(newShip);
            shipNodes.add(newShip);
         */

    }

    private class buildUpgrade extends buildOption{

        private Upgrade upgrade;
        private String name;

        public buildUpgrade(Group root, int x, int y, Upgrade upgrade, int index, int row){
            super(root, x, y, upgrade.getName(), row);
            this.upgrade = upgrade;
            super.setOnAction(value -> {
                nodes.get(index).getSys().addToQueue(upgrade);
            });

        }

        public String toString(){
            return "bldUpg" + upgrade.getName();
        }

    }

    private class FleetOption extends Button {

        String type;
        private String iD;
        private String stringUsedForSelection;

        public FleetOption(String type, String iD){
            super(type);
            this.iD = iD;
            this.type = type;
            if(type.compareTo("idle") == 0){
                super.setOnAction(value -> {
                    if(!allPlayers.get(Integer.parseInt(iD.substring(6, 7))).getFleet(iD).getIfMoving()){

                    }
                });
            }else if(type.compareTo("attack") == 0){
                super.setOnAction(value -> {

                });
            }else if(type.compareTo("blockade/guard") == 0){
                super.setOnAction(value -> {

                });
            }else if(type.compareTo("colonize") == 0){
                super.setOnAction(value -> {
                    if(!allPlayers.get(Integer.parseInt(iD.substring(6, 7))).getFleet(iD).getIfMoving()){
                        if(Integer.parseInt(iD.substring(6, 7)) == mainPlayerIndex){
                            if(allPlayers.get(Integer.parseInt(iD.substring(6, 7))).getFleet(iD).getDepartedFrom().getOwner() == -1){
                                SolarSystem colon = allPlayers.get(Integer.parseInt(iD.substring(6, 7))).getFleet(iD).getDepartedFrom();
                                allPlayers.get(Integer.parseInt(iD.substring(6, 7))).colonizeSystem(colon, Integer.
                                        parseInt(colon.getName().substring(6)), allPlayers.get(Integer.parseInt(iD.
                                        substring(6, 7))).getIndexOfBestPlanetToColonize(colon));
                                nodes.get(Integer.parseInt(colon.getName().substring(6))).changeColor(Integer.parseInt(
                                        iD.substring(6, 7)));
                                nodes.get(Integer.parseInt(colon.getName().substring(6))).updateSys(allPlayers.get(
                                        Integer.parseInt(iD.substring(6, 7))).getSystem(colon.getName()));
                                for(int i = 0; i < FleetNodes.size(); i++){
                                    if(FleetNodes.get(i).getSelectedID().compareTo(iD) == 0){
                                        FleetNodes.get(i).destroy();
                                        break;
                                    }
                                }
                                /*//colonize
                                for(int i = 0; i < nodes.size(); i++){
                                    if(colon.getName().compareTo(nodes.get(i).getSys().getName()) == 0){
                                        for(int j = 0; j < FleetNodes.size(); j++){
                                            if(FleetNodes.get(j).getSelectedID().compareTo(allPlayers.get(mainPlayerIndex).getFleet(iD).getID()) == 0){
                                                FleetNodes.get(j).destroy();
                                            }
                                        }
                                        allPlayers.get(mainPlayerIndex).colonizeSystem(nodes.get(i).getSys(), i);
                                        nodes.get(i).colonize(mainPlayerIndex);
                                        allPlayers.get(mainPlayerIndex).destroyFleet(allPlayers.get(Integer.parseInt(iD.substring(6, 7))).getFleet(iD));

                                    }
                                }*/
                            }
                        }
                    }
                });
            } else if(type.compareTo("createTradeRoute") == 0){
                super.setOnAction(value -> {
                    selectedObjectID = "CreTra" + iD.substring(6);
                });
            }
        }

        public String toString(){
            return "FleOpt" + type;
        }
    }

    private class OverlapThing extends Rectangle{
        //unit number in world space = (DISTANCE FACTOR * 100 * 10)^2
        //each is about 1/10 per unit of world space
        private short xWorld;
        private short yWorld;
        private Color color = Color.TRANSPARENT;

        public OverlapThing(short x, short y, int widthAndHeight){
            super(widthAndHeight, widthAndHeight);
            super.setFill(color);
            this.xWorld = x;
            this.yWorld = y;
        }

        public void scale(int widthAndHeight){
            super.setHeight(widthAndHeight);
            super.setWidth(widthAndHeight);
        }

        public void setX(int x){
            super.setLayoutX(x);
        }

        public void setY(int y){
            super.setLayoutY(y);
        }

        public void setColor(Color color){
            this.color = color;
            super.setFill(color);
        }

        public String getColor(){
            if(color.getBlue() == Color.BLUE.getBlue() && color.getGreen() == Color.BLUE.getGreen() && color.getRed() == Color.BLUE.getRed()){
                return "blue";
            }else if(color.getBlue() == Color.RED.getBlue() && color.getGreen() == Color.RED.getGreen() && color.getRed() == Color.RED.getRed()){
                return "red";
            }
            return "idk";
        }

        public short getxWorld(){ return xWorld; }
        public short getyWorld(){ return yWorld; }
    }

    private class PlanetSprite extends Button {
        private String id;
        private String nameOfSystem;
        private Planet planet;
        private int indexInRoot;

        private File spriteLocation;
        private BasicSprite baseSprite;
        private BasicImageView sprite;

        private PlanetCircle circ;
        private PlanetLabel lab;

        public PlanetSprite(Planet p, String nameOfSystem, int indexOfPlanet, Group root, int indexInRoot){
            super();
            this.nameOfSystem = nameOfSystem;
            this.indexInRoot = indexInRoot;
            id = "planetSprite" + indexOfPlanet + nameOfSystem;
            planet = p;
            circ = new PlanetCircle(75, nameOfSystem + indexOfPlanet);
            lab = new PlanetLabel(p.getPlanetType() + " " + indexOfPlanet + " " + p.getPopulation());
            root.getChildren().add(circ);
            root.getChildren().add(lab);
            super.setOnAction(e -> {
                System.out.println(allPlayers.get(Integer.parseInt(nameOfSystem.substring(6, 7))).getSystem(this.nameOfSystem));
            });
        }

        public int getIndexInRoot(){ return indexInRoot; }

        public String toString(){
            return id;
        }

        public void setColor(Color col){
            circ.setFill(col);
        }


        public void setX(int x){
            super.setLayoutX(x);
            circ.setLayoutX(x);
            lab.setLayoutX(x);
        }

        public void setY(int y){
            super.setLayoutY(y);
            circ.setLayoutY(y);
            lab.setLayoutY(y);
        }

        private class PlanetCircle extends Circle{
            private String x;
            public PlanetCircle(int radius, String x){
                super(radius);
                this.x = x;
            }

            public String toString(){ return "planetSprite" + x; }
        }

        private class PlanetLabel extends Label{
            public PlanetLabel(String lab){
                super(lab);
            }

            public String toString(){ return "planetSprite" + super.getText(); }
        }

    }

    private class TraitorRoute {
        private String nameOfTheThing;
        private Button button;

        private OverlapStack stack;
        //private boolean if

        public TraitorRoute(Group root, double x, double y, String id){
            stack = new OverlapStack();
            stack.addObject(id);
            button = new Button();
            root.getChildren().add(button);
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent mouseEvent) {
                    selectedObjectID = nameOfTheThing;
                    String str = id + ": ";
                    for(String str2 : stack.getStack()){
                        str += str2 + " ";
                    }
                    stackInfo.setText(str);

                    if(stack.getStack().size() > 1){
                        //create list
                        gui.changeObject(stack.getStack(), root);
                    } else {
                        //go straight to ship menu

                    }
                }
            });
            button.setLayoutX(x);
            button.setLayoutY(y);
            nameOfTheThing = id;
        }

        public void setX(double x){
            DoubleCooridinate worldPos = allPlayers.get(Integer.parseInt(nameOfTheThing.substring(6, 7))).getTradeFleet
                    (nameOfTheThing).getPosition();
            Coordinate screenPos = worldToScreen(worldPos);
            button.setLayoutX(screenPos.getX());
        }

        public void setY(double y){
            DoubleCooridinate worldPos = allPlayers.get(Integer.parseInt(nameOfTheThing.substring(6, 7))).getTradeFleet
                    (nameOfTheThing).getPosition();
            Coordinate screenPos = worldToScreen(worldPos);
            button.setLayoutY(screenPos.getY());
        }

        public String getID(){
            return nameOfTheThing;
        }

        public void nextTurn(){

            /*if(ifInStack && (this.getSelectedID().compareTo(iDOfTopOfStack) != 0)){
                this.setInvisible(true);
            } else {
                this.setInvisible(false);
            }*/
        }

        public void putInStack(OverlapStack stack){
            this.stack = stack;
        }

        public void removeFromStack(String str){
            stack.remove(str);
        }

        public OverlapStack getStack(){ return stack; }

        public void resetStack(){ stack = new OverlapStack(); }

        public void setInvisible(boolean ifInvis){
            if(ifInvis){
                //button.setVisible(false);
            } else if(!ifInvis){
                button.setVisible(true);
            }
        }

    }

    private class LoadingScreen{

        public LoadingScreen(){

        }


    }




    //Ship customizer gui classes
    private class ShipCustomizerGUI{
        private Button shipToGame = new Button("Back to game");
        private ArrayList<DragableObject> dragableNodes = new ArrayList<>();
        private ArrayList<ShipChassisDecision> chassisOptionsButtons = new ArrayList<>();
        private ScrollListShip currentShipDesigns;
        private ScrollListShip weaponAndDefenseOptions;
        private ScrollListShip chassisOptions;
        private ScrollListShip nodesForWeaponAndOptions;


        private Button newShip = new Button("New Ship+");
        private Button finalizeDesign = new Button("Create Ship");
        private Button cancelThisShip = new Button("Cancel");
        private String newShipToString = newShip.toString();

        private ArrayList<DragableObjectNode> spots = new ArrayList<>();

        //determines what the finalize design button does
        private boolean ifCreatingNewShip = false;
        private boolean ifModifyingExistingShip = false;

        private ShipTemplate selectedChassis;


        public ShipCustomizerGUI(Group root, Stage primaryStage, Scene currentScene){
            //initialize the back button
            shipToGame.setLayoutY(HEIGHT / 10.0);
            shipToGame.setLayoutX(WIDTH / 20 * 18);
            root.getChildren().add(shipToGame);
            shipToGame.setOnAction(value -> {
                ifInMainGame = true;
                primaryStage.getScene().setRoot(mainGame);
                gui.updateTiles(mainGame);
            });

            Button butt = new Button("check");
            butt.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    System.out.println(ifCreatingNewShip + " " + ifModifyingExistingShip);
                }
            });
            root.getChildren().add(butt);
            butt.setLayoutX(WIDTH / 2);

            newShip.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    decideChassisForNewShip();
                    ifCreatingNewShip = true;
                    ifModifyingExistingShip = false;
                }
            });

            currentShipDesigns = new ScrollListShip(allPlayers.get(mainPlayerIndex).getShips(), root);
            weaponAndDefenseOptions = new ScrollListShip(root, allPlayers.get(mainPlayerIndex).getCivilization().
                    getWeapons(), allPlayers.get(mainPlayerIndex).getCivilization().getDefenses());
            weaponAndDefenseOptions.hide();

            chassisOptions = new ScrollListShip(root, allPlayers.get(mainPlayerIndex).getCivilization().getChassises());
            chassisOptions.hide();

            finalizeDesign = new Button("Create new ship");
            root.getChildren().add(finalizeDesign);
            finalizeDesign.setVisible(false);
            finalizeDesign.setLayoutX(WIDTH / 10.0 * 7);
            finalizeDesign.setLayoutY(HEIGHT / 5.0 * 4);
            finalizeDesign.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(ifCreatingNewShip){
                        //create new ship
                        nodesForWeaponAndOptions.createShip(selectedChassis);
                    }else if(ifModifyingExistingShip){
                        //remove old ship, create new
                        nodesForWeaponAndOptions.modifyShip(selectedChassis);
                    }

                    //resets everything
                    resetEverythingToInitialPositions();
                    ifCreatingNewShip = false;
                    ifModifyingExistingShip = false;
                    selectedChassis = null;
                }
            });

            root.getChildren().add(cancelThisShip);
            cancelThisShip.setVisible(false);


        }

        public void decideChassisForNewShip(){
            //hides ship models and shows possible chassises
            currentShipDesigns.hide();

            //allows you to choose chassis for new ship, then loads the nodes for it
            chassisOptions.show();

            cancelThisShip.setVisible(true);
            cancelThisShip.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    resetEverythingToInitialPositions();
                    selectedChassis = null;
                    ifModifyingExistingShip = false;
                    ifCreatingNewShip = false;
                }
            });
        }

        public void designNewShipOfChassis(ShipTemplate chassis){
            chassisOptions.hide();
            weaponAndDefenseOptions.show();
            setUpNodesForWAndD(chassis, shipCustomizer);
            finalizeDesign.setVisible(true);
            selectedChassis = chassis;
        }

        public void modifyExistingShipChassis(ShipTemplate existingShip){
            //unload the current ship model buttons
            currentShipDesigns.hide();
            //load ship data on the right side
            nodesForWeaponAndOptions = new ScrollListShip(shipCustomizer, existingShip);
            weaponAndDefenseOptions.show();
            //load the finalize design button
            finalizeDesign.setVisible(true);
            //load cancel button
            cancelThisShip.setVisible(true);
            //take note that you are modifying ship
            ifModifyingExistingShip = true;
            ifCreatingNewShip = false;
        }

        private void resetEverythingToInitialPositions(){
            currentShipDesigns.show();
            weaponAndDefenseOptions.hide();
            chassisOptions.hide();
            if(nodesForWeaponAndOptions != null) {
                nodesForWeaponAndOptions.hide();
                nodesForWeaponAndOptions.clearData();
            }
            cancelThisShip.setVisible(false);
            currentShipDesigns.updateShipListData(shipCustomizer, allPlayers.get(mainPlayerIndex).getShips());
            finalizeDesign.setVisible(false);

        }

        public void setUpNodesForWAndD(ShipTemplate chassis, Group root){
            nodesForWeaponAndOptions = new ScrollListShip(root, chassis);
        }


        private class ShipChassisDecision extends Button{
            private ShipTemplate chassis;

            public ShipChassisDecision(int x, int y, int width, int height, ShipTemplate chassis){
                super(chassis.getShipClass());
                super.setLayoutX(x);
                super.setLayoutY(y);
                super.setPrefHeight(height);
                super.setPrefWidth(width);
                this.chassis = chassis;

                super.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        designNewShipOfChassis(chassis);
                    }
                });

            }
        }

        private class DragableObject extends Button{
            //storage stuff
            private int weaponOrDefense = -1; //-1 is nothing, 0 is weapon, 1 is defense
            private Weapon weaponHeld;
            private Defense defenseHeld;

            private double offSetDragX = 0;
            private double offSetDragY = 0;

            private double startPanX;
            private double startPanY;

            private double x;
            private double y;

            private double xInitial;
            private double yInitial;

            private final double width = WIDTH / 20.0;
            private final double height = HEIGHT / 20.0;


            public DragableObject(Group root, double x, double y, Weapon weapon){
                super(weapon.getType());
                weaponHeld = weapon;
                weaponOrDefense = 0;
                offSetDragX = 0;
                offSetDragY = 0;
                this.x = x;
                this.y = y;
                xInitial = x;
                yInitial = y;
                super.setLayoutX(x);
                super.setLayoutY(y);
                super.setPrefHeight(height);
                super.setPrefWidth(width);

                super.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        startPanX = mouseEvent.getX();
                        startPanY = mouseEvent.getY();
                    }
                });

                super.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        offSetDragX += mouseEvent.getX() - startPanX;
                        offSetDragY += mouseEvent.getY() - startPanY;

                        startPanX = mouseEvent.getX();
                        startPanY = mouseEvent.getY();

                        dragObject(offSetDragX, offSetDragY);
                    }
                });

                super.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //check if is on the right spot to be let go
                        testForDroppedOnNode();
                        //if not resets the x and y positions
                        resetPos();
                    }
                });

            }

            public DragableObject(Group root, double x, double y, Defense defense){
                super(defense.getType());
                defenseHeld = defense;
                weaponOrDefense = 1;
                offSetDragX = 0;
                offSetDragY = 0;
                this.x = x;
                this.y = y;
                xInitial = x;
                yInitial = y;
                super.setLayoutX(x);
                super.setLayoutY(y);
                super.setPrefHeight(height);
                super.setPrefWidth(width);

                super.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        startPanX = mouseEvent.getX();
                        startPanY = mouseEvent.getY();
                    }
                });

                super.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        offSetDragX += mouseEvent.getX() - startPanX;
                        offSetDragY += mouseEvent.getY() - startPanY;

                        startPanX = mouseEvent.getX();
                        startPanY = mouseEvent.getY();

                        dragObject(offSetDragX, offSetDragY);
                    }
                });

                super.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //check if is on the right spot to be let go

                        testForDroppedOnNode();
                        //if not resets the x and y positions
                        resetPos();
                    }
                });

            }

            public double getX(){ return x; }
            public double getY(){ return y; }
            public double getWitdth(){ return width; }
            public double getHieght(){ return height; }

            public void resetPos(){
                super.setLayoutX(xInitial);
                super.setLayoutY(yInitial);
                offSetDragY = 0;
                offSetDragX = 0;
            }

            public void testForDroppedOnNode(){

                //gets all with overlap
                ArrayList<Integer> indexOfNodes = new ArrayList<>();
                ArrayList<Double> percentagesOfNodes = new ArrayList<>();
                for(int i = 0; i < nodesForWeaponAndOptions.getwAndDNodes().size(); i++){
                    double x = nodesForWeaponAndOptions.getwAndDNodes().get(i).getIfDragableObjectIsOnThis(this);
                    if(x > 0){
                        indexOfNodes.add(i);
                        percentagesOfNodes.add(x);
                    }
                }


                //gets most overlap
                double highestPercentage = 0;
                int indexOfHighest = -1;
                for(int i = 0; i < indexOfNodes.size(); i++){
                    if(percentagesOfNodes.get(i) > highestPercentage){
                        indexOfHighest = indexOfNodes.get(i);
                        highestPercentage = percentagesOfNodes.get(i);
                    }
                }


                //sets highest overlapped node to store module data
                if(indexOfHighest >= 0){
                    if(weaponOrDefense == 0 && nodesForWeaponAndOptions.getwAndDNodes().get(indexOfHighest).weaponOrDefenseNode == 0){
                        nodesForWeaponAndOptions.storeDataForObject(nodesForWeaponAndOptions.getwAndDNodes().get
                                (indexOfHighest).xTopLeft, nodesForWeaponAndOptions.getwAndDNodes().get(indexOfHighest)
                                .yTopLeft, weaponHeld);
                    } else if(weaponOrDefense == 1 && nodesForWeaponAndOptions.getwAndDNodes().get(indexOfHighest).weaponOrDefenseNode == 1){
                        nodesForWeaponAndOptions.storeDataForObject(nodesForWeaponAndOptions.getwAndDNodes().get
                                (indexOfHighest).xTopLeft, nodesForWeaponAndOptions.getwAndDNodes().get(indexOfHighest)
                                .yTopLeft, defenseHeld);
                    }
                }
            }

            public void dragObject(double deltaX, double deltaY){
                x += deltaX;
                y += deltaY;
                super.setLayoutX(x);
                super.setLayoutY(y);
            }

            public int getWeaponOrDefense(){ return weaponOrDefense; }
            public Weapon getWeaponHeld(){ return  weaponHeld; }
            public Defense getDefenseHeld(){ return defenseHeld; }
        }

        private class DragableObjectNode{
            //used for calculating the placement
            private int xTopLeft;
            private int yTopLeft;
            private int width;
            private int height;

            //used for storage of tons of information
            private Weapon weaponHeld;
            private Defense defenseHeld;
            private int weaponOrDefenseNode; //-1 is none, 0 is weapon, 1 is defense

            private Label lab;
            private String toStringOfLab;

            private String nameOfRectangle;
            private Rectangle borders;


            public DragableObjectNode(int xTopLeft, int yTopLeft, int width, int height, Group root, int weaponOrDefenseNode){
                this.xTopLeft = xTopLeft;
                this.yTopLeft = yTopLeft;
                this.width = width;
                this.height = height;
                this.weaponOrDefenseNode = weaponOrDefenseNode;

                borders = new Rectangle(xTopLeft, yTopLeft, width, height);
                root.getChildren().add(borders);
                borders.setFill(Color.WHITE);
                borders.setStroke(Color.BLACK);
                borders.toBack();
                borders.setVisible(true);

                lab = new Label("HIIII");
                lab.setTextFill(Color.YELLOWGREEN);
                toStringOfLab = lab.toString();
                lab.setLayoutX(xTopLeft);
                lab.setLayoutY(yTopLeft);
                root.getChildren().add(lab);
                lab.toFront();
            }

            public double getIfDragableObjectIsOnThis(DragableObject obj){
                //get % of button in area
                //get overlap
                //first decide if obj is left or right of the node
                boolean ifObjIsLeftOfNode = false;
                boolean ifObjIsHigherThanNode = false;
                if(obj.getX() <= xTopLeft){
                    ifObjIsLeftOfNode = true;
                }
                if(obj.getY() <= yTopLeft){
                    ifObjIsHigherThanNode = true;
                }


                boolean ifInHorizontalBounds = false;
                boolean ifInVerticalBounds = false;
                //if obj is left of node, then test that
                if(ifObjIsLeftOfNode){
                    //test if obj right side is between bounds of node, and get how far into the node
                    if(obj.getX() + obj.getWitdth() >= xTopLeft && obj.getX() + obj.getWitdth() <= xTopLeft + width){
                        ifInHorizontalBounds = true;
                    }
                } else {
                    //test if obj left side is between bounds, and get how far into the node
                    if(xTopLeft <= obj.getX() && xTopLeft + width >= obj.getX()){
                        ifInHorizontalBounds = true;
                    }
                }
                if(ifObjIsHigherThanNode){
                    if(obj.getY() + obj.getHieght() >= yTopLeft && obj.getY() + obj.getHieght() <= yTopLeft + height){
                        ifInVerticalBounds = true;
                    }
                } else{
                    if(obj.getY() >= yTopLeft && yTopLeft + height >= obj.getY()){
                        ifInVerticalBounds = true;
                    }
                }


                double xSide = 0;
                double ySide = 0;
                //if both overlap booleans are true, then calculate space of overlap
                if(ifInHorizontalBounds && ifInVerticalBounds){
                    if(ifObjIsLeftOfNode){
                        xSide = obj.getX() + obj.getWitdth() - xTopLeft;
                    } else {
                        xSide = xTopLeft + width - obj.getX();
                    }

                    if(ifObjIsHigherThanNode){
                        ySide = obj.getY() + obj.getHieght() - yTopLeft;
                    } else {
                        ySide = yTopLeft + height - obj.getY();
                    }
                }

                double area = ySide * xSide;
                //return %
                return 1.0 * area / (obj.getHieght() * obj.getWitdth());
            }

            public void changeStoredData(Weapon weapon){
                weaponHeld = weapon;
                lab.setText(weapon.getType());
            }

            public void changeStoredData(Defense defense){
                defenseHeld = defense;
                lab.setText(defense.getType());
            }

            public void setVisible(){
                borders.setVisible(true);
                lab.setVisible(true);
            }

            public void setInvisible(){
                borders.setVisible(false);
                lab.setVisible(false);
            }
        }

        private class ScrollListShip{
            private String nameOfScrollBar;
            private ScrollBar scrollBar;
            private ArrayList<Button> options;
            private ArrayList<DragableObject> dragableObjects;
            private ArrayList<ShipChassisDecision> chassises;
            private ArrayList<DragableObjectNode> wAndDNodes;

            public ScrollListShip(ArrayList<ShipTemplate> allShips, Group root){
                setUpAvailableModels(root, allShips);
            }

            public ScrollListShip(Group root, ArrayList<Weapon> weapons, ArrayList<Defense> defenses){
                setUpListOfShipNodes(root, weapons, defenses);
            }

            public ScrollListShip(Group root, ArrayList<ShipTemplate> chassises){
                setUpChassisOptions(root);
            }

            public ScrollListShip(Group root, ShipTemplate chassisToGenerateNodesFor){
                if(chassisToGenerateNodesFor.getIfOnlyChassis()) {
                    setUpWeaponAndDefenseNodes(root, chassisToGenerateNodesFor);
                } else {
                    setUpWeaponAndDefenseNodes(root, chassisToGenerateNodesFor);

                    //fill in options
                    fillInOptions(chassisToGenerateNodesFor);
                }
            }

            private void fillInOptions(ShipTemplate chassisToGenerateNodesFor){
                int i;
                for(i = 0; i < chassisToGenerateNodesFor.getNumWeapons(); i++){
                    if(chassisToGenerateNodesFor.getWeapons()[i] != null) {
                        wAndDNodes.get(i).changeStoredData(chassisToGenerateNodesFor.getWeapons()[i]);
                    }
                }
                for(int j = 0; j < chassisToGenerateNodesFor.getNumDefenses(); j++){
                    if(chassisToGenerateNodesFor.getDefenses()[j] != null) {
                        wAndDNodes.get(j + i).changeStoredData(chassisToGenerateNodesFor.getDefenses()[j]);
                    }
                }
            }

            private void setUpWeaponAndDefenseNodes(Group root, ShipTemplate chassisToGenerateNodesFor){
                wAndDNodes = new ArrayList<>();
                int row = 0;
                //generate header for weapons
                row++;
                for(int i = 0; i < chassisToGenerateNodesFor.getNumWeapons(); i++){
                    wAndDNodes.add(new DragableObjectNode(WIDTH / 10 * 9, HEIGHT / 10 * row, WIDTH /
                            10, HEIGHT / 10, root, 0));
                    row++;
                }

                //generate header for defense
                row++;
                for(int i = 0; i < chassisToGenerateNodesFor.getNumDefenses(); i++){
                    wAndDNodes.add(new DragableObjectNode(WIDTH / 10 * 9, HEIGHT / 10 * row, WIDTH /
                            10, HEIGHT / 10, root, 1));
                    row++;
                }

                //generate the nodes for weapons

            }

            private void setUpChassisOptions(Group root){
                int buttonWidth = WIDTH / 8;
                int buttonHeight = HEIGHT / 10;

                for(int i = 0; i < allPlayers.get(mainPlayerIndex).getCivilization().getNumShipChassis(); i++){
                    chassisOptionsButtons.add(new ShipChassisDecision(0, i * buttonHeight, buttonWidth,
                            buttonHeight, allPlayers.get(mainPlayerIndex).getCivilization().getChassises().get(i)));
                    root.getChildren().add(chassisOptionsButtons.get(i));
                }
            }

            private void setUpAvailableModels(Group root, ArrayList<ShipTemplate> allShips) {
                options = new ArrayList<>();
                //generate bounds for each object in the scroll list
                int top = 0;
                int bottom = HEIGHT;
                int buttonWidth = WIDTH / 8;
                int buttonHeight = HEIGHT / 10;

                scrollBar = new ScrollBar();
                scrollBar.setPrefHeight(HEIGHT);
                scrollBar.setOrientation(Orientation.VERTICAL);
                scrollBar.toFront();

                double valuePerScroll = (bottom - top);
                scrollBar.setBlockIncrement(valuePerScroll);
                int numShown = (bottom - top) / buttonHeight;
                if(numShown < allShips.size()) {
                    root.getChildren().add(scrollBar);
                }

                newShip.setLayoutX(20);
                newShip.setLayoutY(top);
                newShip.setPrefHeight(buttonHeight);
                newShip.setPrefWidth(buttonWidth);
                root.getChildren().add(newShip);

                int row = 1;
                int rowsShown = 5;
                for(int i = 0; i < allShips.size(); i++){
                    options.add(new Button(allShips.get(i).getModelName()));
                    root.getChildren().add(options.get(i));
                    options.get(i).setLayoutX(20);
                    options.get(i).setPrefWidth(buttonWidth);
                    options.get(i).setPrefHeight(buttonHeight);
                    options.get(i).setLayoutY(top + row * buttonHeight);
                    String iD = allShips.get(i).getModelName();
                    options.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            for(ShipTemplate ship : allPlayers.get(mainPlayerIndex).getShips()) {
                                if(ship.getModelName().compareTo(iD) == 0){
                                    selectedChassis = ship;
                                    modifyExistingShipChassis(ship);
                                    break;
                                }

                            }
                        }
                    });
                    row++;
                }

                if(numShown < allShips.size()){
                    scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                        //set scroll
                        for(int j = 0; j < options.size(); j++){
                            double temp = top + j * buttonHeight - newValue.intValue() * (options.size() - numShown) * buttonHeight / 100.0;
                            options.get(j).setLayoutY(temp);
                        }
                    });
                }

            }

            private void setUpListOfShipNodes(Group root, ArrayList<Weapon> weapons, ArrayList<Defense> defenses){
                dragableObjects = new ArrayList<>();
                int row = 0;
                int column = 0;
                int maxCol = 15;
                double top = HEIGHT / 5.0 * 4;
                double bottom = HEIGHT;
                double left = WIDTH / 4.0;
                double right = WIDTH / 4.0 * 3;
                double objectWidth = HEIGHT / 10;
                double objectHeight = WIDTH / 10;

                int numObjectsPerRow = (int)((right - left) / objectWidth);

                for(int i = 0; i < weapons.size(); i++){
                    double x = left + column * objectWidth;
                    double y = top + row * objectHeight;
                    dragableObjects.add(new DragableObject(root, x, y, weapons.get(i)));
                    root.getChildren().add(dragableObjects.get(i));
                    column++;
                    if(column > numObjectsPerRow){
                        column = 0;
                        row++;
                    }
                }
                for(int i = 0; i < defenses.size(); i++){
                    double x = left + column * objectWidth;
                    double y = top + row * objectHeight;
                    dragableObjects.add(new DragableObject(root, x, y, defenses.get(i)));
                    root.getChildren().add(dragableObjects.get(i + weapons.size()));
                    column++;
                    if(column > numObjectsPerRow){
                        column = 0;
                        row++;
                    }
                }

            }

            public ArrayList<DragableObjectNode> getwAndDNodes(){
                return wAndDNodes;
            }

            public void storeDataForObject(int x, int y, Weapon weapon){
                for(int i = 0; i < wAndDNodes.size(); i++){
                    if(wAndDNodes.get(i).xTopLeft == x && wAndDNodes.get(i).yTopLeft == y){
                        wAndDNodes.get(i).changeStoredData(weapon);
                    }
                }
            }

            public void storeDataForObject(int x, int y, Defense defense){
                for(int i = 0; i < wAndDNodes.size(); i++){
                    if(wAndDNodes.get(i).xTopLeft == x && wAndDNodes.get(i).yTopLeft == y){
                        wAndDNodes.get(i).changeStoredData(defense);
                    }
                }
            }

            private void show(){
                if(options != null){
                    for(int i = 0; i < options.size(); i++){
                        options.get(i).setVisible(true);
                        newShip.setVisible(true);
                    }
                } else if(dragableObjects != null){
                    for(int i = 0; i < dragableObjects.size(); i++){
                        dragableObjects.get(i).setVisible(true);
                    }
                } else if(chassisOptionsButtons != null){
                    for(int i = 0; i < chassisOptionsButtons.size(); i++){
                        chassisOptionsButtons.get(i).setVisible(true);
                    }
                } else if(wAndDNodes != null){
                    for(int i = 0; i < wAndDNodes.size(); i++){
                        wAndDNodes.get(i).setVisible();
                    }
                }
            }

            private void hide() {
                if(options != null){
                    for(int i = 0; i < options.size(); i++){
                        options.get(i).setVisible(false);
                        newShip.setVisible(false);
                    }
                } else if(dragableObjects != null){
                    for(int i = 0; i < dragableObjects.size(); i++){
                        dragableObjects.get(i).setVisible(false);
                    }
                } else if(chassisOptionsButtons != null){
                    for(int i = 0; i < chassisOptionsButtons.size(); i++){
                        chassisOptionsButtons.get(i).setVisible(false);
                    }
                }
                if (wAndDNodes != null){
                    for(int i = 0; i < wAndDNodes.size(); i++){
                        wAndDNodes.get(i).setInvisible();

                    }
                }
            }

            public void clearData(){
                wAndDNodes.clear();
            }

            public void updateShipListData(Group root, ArrayList<ShipTemplate> allShips ){
                clearShipListButtons(root);
                setUpAvailableModels(root, allShips);
            }

            public void clearShipListButtons(Group root){
                boolean ifNewShipButtonRemoved = false;
                for(int i = 0; i < root.getChildren().size(); i++){
                    for(int j = 0; j < options.size(); j++){
                        if(root.getChildren().get(i).toString().compareTo(options.get(j).toString()) == 0){
                            root.getChildren().remove(i);
                            i--;
                            options.remove(j);
                            break;
                        }
                    }

                    if(!ifNewShipButtonRemoved && root.getChildren().get(i).toString().compareTo(newShip.toString()) == 0){
                        ifNewShipButtonRemoved = true;
                        root.getChildren().remove(i);
                        i--;
                    }
                }
            }

            public void createShip(ShipTemplate chassis){
                ShipTemplate tempTemplate = chassis.getNonTemplateVersion("TEMPORARYTEST");
                for(int i = 0; i < nodesForWeaponAndOptions.getwAndDNodes().size(); i++){
                    if(nodesForWeaponAndOptions.getwAndDNodes().get(i).weaponOrDefenseNode == 0 &&
                            nodesForWeaponAndOptions.getwAndDNodes().get(i).weaponHeld != null){
                        tempTemplate.addWeapon(nodesForWeaponAndOptions.getwAndDNodes().get(i).weaponHeld);
                    } else if(nodesForWeaponAndOptions.getwAndDNodes().get(i).weaponOrDefenseNode == 1 &&
                            nodesForWeaponAndOptions.getwAndDNodes().get(i).defenseHeld != null){
                        tempTemplate.addDefense(nodesForWeaponAndOptions.getwAndDNodes().get(i).defenseHeld);
                    }
                }

                allPlayers.get(mainPlayerIndex).addShip(tempTemplate);
                resetEverythingToInitialPositions();
            }

            public void modifyShip(ShipTemplate ship){
                //redo weapons
                ShipTemplate newShip = ship;
                int i;
                for(i = 0; i < ship.getNumWeapons(); i++){
                    if(nodesForWeaponAndOptions.getwAndDNodes().get(i).weaponOrDefenseNode == 0 &&
                            nodesForWeaponAndOptions.getwAndDNodes().get(i).weaponHeld != null) {
                        newShip.replaceWeapon(wAndDNodes.get(i).weaponHeld, ship.getWeapons()[i]);
                    }
                }

                //redo defenses
                for(int j = 0; j < ship.getNumDefenses(); j++){
                    if(nodesForWeaponAndOptions.getwAndDNodes().get(i).weaponOrDefenseNode == 1 &&
                            nodesForWeaponAndOptions.getwAndDNodes().get(i).defenseHeld != null) {
                        newShip.replaceDefense(wAndDNodes.get(i + j).defenseHeld, ship.getDefenses()[j]);
                    }
                }


                //adds a letter to name
                String newName = ship.getModelName();
                newName += 'a';
                newShip.changeName(newName);

                //replaces ship
                allPlayers.get(mainPlayerIndex).removeShip(ship.getModelName());
                allPlayers.get(mainPlayerIndex).addShip(newShip);

                //reset
                resetEverythingToInitialPositions();
            }
        }
    }


    //main.gui.Main method
    public static void main(String[] args) {
        allPlayers.add(new DefaultPlayer(false, "blue", 0, new DefaultCivilization()));
        allPlayers.add(new DefaultPlayer(true, "red", 1, new DefaultCivilization()));
        //allPlayers.add(new DefaultPlayer(true, "green", 2, new DefaultCivilization()));
        mainPlayerIndex = 0;

        map = new Map("disk", 4, allPlayers);
        allPlayers = map.getPlayers();

        launch(args);
    }

}
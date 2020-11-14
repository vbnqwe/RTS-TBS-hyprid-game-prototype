package gui.components.Sprites;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Star extends ImageView {

    private double xWorld;
    private double yWorld;
    private double worldWidth;
    private double worldHeight;
    private double screenWidth;
    private double screenHeight;
    private Image image;

    public Star(double xWorld, double yWorld, double worldWidth, double worldHeight, double scaleFactor,
                String starType){
        super();
        //sets up image
        if(starType.compareTo("blue") == 0){
            File tempLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\world\\StupidBlueStar.png");
            image = new Image(tempLocation.toURI().toString());
            super.setImage(image);
        } else if(starType.compareTo("red") == 0){
            File tempLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\world\\StupidRedStar.png");
            image = new Image(tempLocation.toURI().toString());
            super.setImage(image);
        } else if(starType.compareTo("white") == 0){
            File tempLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\world\\StupidWhiteStar.png");
            image = new Image(tempLocation.toURI().toString());
            super.setImage(image);
        } else if(starType.compareTo("yellow") == 0){
            File tempLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\world\\StupidYellowStar.png");
            image = new Image(tempLocation.toURI().toString());
            super.setImage(image);
        } else {
            File tempLocation = new File("C:\\Users\\vbogd\\IdeaProjects\\GameTestMap\\src\\visual assets\\world\\StupidYellowStar.png");
            image = new Image(tempLocation.toURI().toString());
            super.setImage(image);
        }


        this.xWorld = xWorld;
        this.yWorld = yWorld;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        setSpriteSize(scaleFactor);
    }

    public double getWorldX(){
        return xWorld + worldWidth / 2.0;
    }

    public double getWorldY(){
        return yWorld + worldHeight / 2.0;
    }

    public double getCenterWorldX(){
        return xWorld + worldWidth / 2.0;
    }

    public double getCenterWorldY(){
        return yWorld + worldHeight / 2.0;
    }

    public void setCenterX(double centerX){
        super.setX(centerX - screenWidth / 2.0);
    }

    public void setCenterY(double centerY){
        super.setY(centerY - screenHeight / 2.0);
    }

    public void setSpriteSize(double zoomFactor){
        screenWidth = worldWidth * zoomFactor;
        screenHeight = worldHeight * zoomFactor;
        super.setFitWidth(screenWidth);
        super.setFitHeight(screenHeight);
    }

    public double getRadius(){
        return screenHeight / 2.0;
    }



}

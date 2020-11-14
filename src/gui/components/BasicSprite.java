package gui.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class BasicSprite extends Image {

    private String iD;

    public BasicSprite(String location, String iD){
        super(location);

        this.iD = iD;
        //System.out.println(this.toString());
    }


    public String toString(){
        return "BasicSprite" + iD;
    }

}

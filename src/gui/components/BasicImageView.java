package gui.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BasicImageView extends ImageView {

    private String id;

    public BasicImageView(Image image, String id){
        super(image);
        this.id = id;
    }

    public String toString(){ return "ImageView" + id; }
}

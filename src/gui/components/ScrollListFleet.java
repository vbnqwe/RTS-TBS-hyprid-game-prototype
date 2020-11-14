package gui.components;


import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import ships.Fleet;
import java.util.ArrayList;
import java.util.EventListener;

public class ScrollListFleet {

    private ArrayList<Fleet> selectedFleets;
    private ArrayList<Button> selectedFleetButtons;
    private int pxWidth;
    private int pxHeight;
    private int objectsPerRow;
    private int objectsPerColumn;

    public ScrollListFleet(ArrayList<Fleet> selectedFleets, Group root){
        this.selectedFleets = selectedFleets;
        selectedFleetButtons = new ArrayList<>();
        for(Fleet f : selectedFleets){
            Button temp = new Button(f.getID());
            temp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                }
            });
            root.getChildren().add(temp);
            temp.toFront();
        }

    }

    public void addFleet(){

    }

    public void setFleet(){

    }


}

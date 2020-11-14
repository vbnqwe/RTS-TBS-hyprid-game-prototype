package gui.components.fleetSelect;

import javafx.scene.control.ScrollBar;

public class fleetSelectScrollbar extends ScrollBar {

    String type;

    public fleetSelectScrollbar(String type){
        super();
        if(type.compareTo("fleets") == 0){
            this.type = type;
        } else if(type.compareTo("tiles") == 0){
            this.type = type;
        }
    }

    public String toString(){
        return "fleetSelectScrollBar" + type;
    }
}

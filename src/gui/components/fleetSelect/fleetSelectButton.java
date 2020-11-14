package gui.components.fleetSelect;

import javafx.scene.control.Button;

public class fleetSelectButton extends Button {

    private String fleet;

    public fleetSelectButton(String fleet){
        super(fleet);
        this.fleet = fleet;
    }

    public String getFleet(){ return fleet; }

    public String toString(){
        return "fleetSelectButton" + fleet;
    }
}

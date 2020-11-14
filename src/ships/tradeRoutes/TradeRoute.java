package ships.tradeRoutes;

import map.systems.SolarSystem;
import players.aiComponents.PathWithSystems;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TradeRoute {
    /*******************************************************************************************************************
     Trade route is an object that will contain the path and position
     Data such as travel speed and transported materials will be stored within a subclass
     *******************************************************************************************************************/

    private PathWithSystems path;


    public TradeRoute(){

    }

    public void setPath(PathWithSystems newPath){
        path = newPath;
    }


    public PathWithSystems getPath() {
        return path;
    }

    public SolarSystem[] getReturnPath(){
        SolarSystem[] temp = path.getSystems();
        Collections.reverse(Arrays.asList(temp));
        return temp;
    }

}

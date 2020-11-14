package ships;

import java.util.ArrayList;

public class Section {

    private ArrayList<Ship> ships;
    private int size;

    public Section(int size){
        ships = new ArrayList<>();
        this.size = size;
    }

    public int getNumShips(){ return ships.size(); }

    public int getMaxSize(){ return size; }

    public int getShipPowerAtIndex(int index){ return ships.get(index).getPower(); }

    public void add(Ship ship){
        if(ships.size() < size){
            ships.add(ship);
        }
    }

    public void remove(String iD){
        System.out.println("REMOVED");
        for(int i = 0; i < ships.size(); i++){
            if(iD.compareTo(ships.get(i).getID()) == 0){
                ships.remove(i);
                i--;
            }
        }
    }

    public Ship getShipAtIndex(int index){
        return ships.get(index);
    }

    public ArrayList<Ship> getShips(){
        return ships;
    }
}

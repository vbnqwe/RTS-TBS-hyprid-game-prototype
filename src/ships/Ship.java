package ships;

import ships.chassises.ShipTemplate;
import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;

public class Ship {

    private ShipTemplate type;


    private int owner;
    private String iD;

    private int power = 0;


    public Ship(String iD, ShipTemplate type){
        this.owner = owner;
        this.iD = iD;
        this.type = type;
    }

    public int getOwner(){ return owner; }


    public String getID(){
        return iD;
    }

    public int compareTo(Ship other){
        if(other.getID().compareTo(iD) == 0){
            return 0;
        } else {
            return -1;
        }
    }

    public String[] getAction(){ return type.getActions(); }

    public int getPower(){
        double power = 0;
        for(Weapon weapon : type.getWeapons()){
            power += 1.0 * weapon.getDamagePerShot() / weapon.getFireRate();
        }
        for(Defense defense : type.getDefenses()){
            power += defense.getHitPoints() + defense.getRegenRate() + defense.getRegenTime();
        }
        return (int)power;
    }

    public ShipTemplate getType(){ return type; }



}

package ships.shipComponents.weapons;

public class MissileWeapon implements Weapon{

    private String specificTypeOfLaser;
    private String typeOfWeapon = "misl";
    private double damagePerShot;
    private double fireRate = 1.0; // how many times per second
    private double industryCost;

    public MissileWeapon(String type){
        decideType(type);
    }

    public void decideType(String type){
        if(type.compareTo("missileTest") == 0){
            specificTypeOfLaser = "test";
            damagePerShot = 5;
            industryCost = 5.0;
        }
    }


    public String getType() {
        return typeOfWeapon + specificTypeOfLaser;
    }

    public double getDamagePerShot() {
        return damagePerShot;
    }

    public double getFireRate() {
        return fireRate;
    }

    public double getIndustryCost(){ return industryCost; }

    public String getName(){ return specificTypeOfLaser; }

    public float getSpeed(){ return 15;}
}

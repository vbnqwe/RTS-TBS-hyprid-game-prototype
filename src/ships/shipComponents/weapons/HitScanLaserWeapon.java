package ships.shipComponents.weapons;

public class HitScanLaserWeapon implements Weapon{

    private String specificTypeOfLaser;
    private String typeOfWeapon = "htSn";
    private double damagePerShot;
    private double fireRate = 1.0; // how many times per second
    private double industryCost;
    private float speed;
    private float acceleration;

    public HitScanLaserWeapon(String type){
        decideType(type);
    }

    public void decideType(String type){
        if(type.compareTo("hitScanTest") == 0){
            specificTypeOfLaser = "test";
            damagePerShot = 5.0;
            industryCost = 5.0;
            speed = -1;
            acceleration = -1;
        }
    }

    public float getSpeed(){
        return speed;
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

}

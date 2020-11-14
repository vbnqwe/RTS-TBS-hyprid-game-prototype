package ships.shipComponents.weapons;

public interface Weapon {
    public String getType();
    public double getDamagePerShot();
    public double getFireRate();
    public double getIndustryCost();
    public String getName();
    public float getSpeed();

}

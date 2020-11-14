package ships.shipComponents.defense;

public interface Defense {
    public double getHitPoints();
    public double getRegenTime(); //seconds before regen
    public double getRegenRate(); //points/second regeneration
    public double getIndustryCost();
    public String getType();
}

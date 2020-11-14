package ships.shipComponents.defense;

public class ShieldDefense implements Defense{
    private String typeOfDefense = "Shld";
    private String specificTypeOfDefense;
    private double hitPoints;
    private double regenTime;
    private double regenRate;
    private double industryCost;

    public ShieldDefense(String type){
        decideType(type);
    }

    public void decideType(String type){
        if(type.compareTo("test") == 0){
            specificTypeOfDefense = "test";
            hitPoints = 10;
            regenTime = 2;
            regenRate = 0.5;
            industryCost = 5.0;
        }
    }

    public double getHitPoints() {
        return hitPoints;
    }

    public double getRegenTime() {
        return regenTime;
    }

    public double getRegenRate() {
        return regenRate;
    }

    public double getIndustryCost() {
        return industryCost;
    }

    public String getType() {
        return typeOfDefense + specificTypeOfDefense;
    }
}

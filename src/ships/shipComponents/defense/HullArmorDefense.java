package ships.shipComponents.defense;

public class HullArmorDefense implements Defense{

    private String typeOfDefense = "hull";
    private String specificTypeOfDefense;
    private double hitPoints;
    private double regenTime = -1;
    private double regenRate = -1;
    private double industryCost;

    public HullArmorDefense(String type){
        decideType(type);
    }

    public void decideType(String type){
        if(type.compareTo("test") == 0){
            specificTypeOfDefense = "test";
            hitPoints = 20;
            industryCost = 2.0;
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

package planetUpgrades;

public class Resource {

    private boolean ifFlatIncrease;
    private boolean ifPercentageMultiplier;
    private boolean ifPopulationMultiplier; //ex: +2 production per person instead of +1

    private int industry = 0;
    private int science = 0;
    private int food = 0;
    private double cash = 0;
    private double viewDistance = 0;
    private double rawMaterialExport = 0;

    private double industryPercentage = 0;
    private double sciencePercentage = 0;
    private double foodPercentage = 0;
    private double cashPercentage = 0;
    private double viewDistancePercentage = 0;
    private double rawMaterialExportPercentage = 0;

    private int industryPopulation = 0;
    private int sciencePopulation = 0;
    private int foodPopulation = 0;
    private double cashPopulation = 0;
    private double viewDistancePopulation;
    private double rawMaterialExportPopualtion = 0;

    private String whatPopulationBoosted = "";


    public Resource(){}

    public void setIndustry(int industry){ this.industry = industry; }
    public void setScience(int science){ this.science = science; }
    public void setFood(int food){ this.food = food; }
    public void setCash(double cash){ this.cash = cash; }
    public void setViewDistance(double viewDistance){ this.viewDistance = viewDistance; }
    public void setRawMaterialExport(double rawMaterialExport){ this.rawMaterialExport = rawMaterialExport; }

    public void setIndustryPercentage(double industryPercentage){ this.industryPercentage = industryPercentage; }
    public void setSciencePercentage(double sciencePercentage){ this.sciencePercentage = sciencePercentage; }
    public void setFoodPercentage(double foodPercentage){ this.foodPercentage = foodPercentage; }
    public void setCashPercentage(double cashPercentage){ this.cashPercentage = cashPercentage; }
    public void setViewDistancePercentage(double viewDistancePercentage){ this.viewDistancePercentage = viewDistancePercentage; }
    public void setRawMaterialExportPercentage(double rawMaterialExportPercentage){ this.rawMaterialExportPercentage = rawMaterialExportPercentage; }

    public void setIndustryPopulation(int industryPopulation){ this.industryPopulation = industryPopulation; }
    public void setSciencePopulation(int sciencePopulation){ this.sciencePopulation = sciencePopulation; }
    public void setFoodPopulation(int foodPopulation){ this.foodPopulation = foodPopulation; }
    public void setCashPopulation(int cashPopulation){ this.cashPopulation = cashPopulation; }
    public void setViewDistancePopulation(int viewDistancePopulation){ this.viewDistancePopulation = viewDistancePopulation; }
    public void setRawMaterialExportPopualtion(double rawMaterialExportPopulation){ this.rawMaterialExportPopualtion = rawMaterialExportPopualtion; }


    public int getIndustry(){ return industry; }
    public int getScience(){ return science; }
    public int getFood(){ return food; }
    public double getCash(){ return cash; }
    public double getViewDistance(){ return viewDistance; }
    public String getWhatPopulationBoosted(){ return whatPopulationBoosted; }
    public double getRawMaterialExport() {
        return rawMaterialExport;
    }

    public double getIndustryPercentage() {
        return industryPercentage;
    }
    public double getSciencePercentage() {
        return sciencePercentage;
    }
    public double getFoodPercentage() {
        return foodPercentage;
    }
    public double getCashPercentage() {
        return cashPercentage;
    }
    public double getViewDistancePercentage() {
        return viewDistancePercentage;
    }
    public double getRawMaterialExportPercentage() {
        return rawMaterialExportPercentage;
    }

    public int getIndustryPopulation() {
        return industryPopulation;
    }
    public int getSciencePopulation() {
        return sciencePopulation;
    }
    public int getFoodPopulation() {
        return foodPopulation;
    }
    public double getCashPopulation() {
        return cashPopulation;
    }
    public double getViewDistancePopulation() {
        return viewDistancePopulation;
    }
    public double getRawMaterialExportPopualtion(){
        return rawMaterialExportPopualtion;
    }
}

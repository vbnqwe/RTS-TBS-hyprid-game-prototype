package planetUpgrades;

public class Upgrade {
    /*
        public boolean ifUnlocked();
    public boolean ifBuilt();
    public void build();
    public void destroy();
    public Resource getUpgradeValues();
    public void nextTurn(int productionOfSystem);
    public int getProductionNeeded();
     */
    private boolean ifResearched;
    private boolean ifBuilt;
    private String name;
    private Resource upgradeValues;
    private Resource emptyValues;
    private int industryNeeded;
    private int totalIndustryNeeded;
    private double rawMaterialNeeded;

    public Upgrade(String name){
        emptyValues = new Resource();
        upgradeValues = new Resource();
        this.name = name;
        if(name.compareTo("empty") == 0){
            //just supposed to exist
        } else if(name.compareTo("rocketry") == 0){
            upgradeValues.setViewDistance(5);
            upgradeValues.setScience(5);
            industryNeeded = 20;
            rawMaterialNeeded = 0;
        } else if(name.compareTo("satellite") == 0){
            upgradeValues.setViewDistance(10);
            industryNeeded = 10;
            rawMaterialNeeded = 0;
        } else if(name.compareTo("moonLanding") == 0){
            upgradeValues.setViewDistance(5);
            upgradeValues.setScience(5);
        }

        totalIndustryNeeded = industryNeeded;

    }

    public void nextTurn(int industry, double rawMaterial){
        industryNeeded -= industry;
        rawMaterial -= rawMaterialNeeded;
        if(rawMaterialNeeded <= 0 && industryNeeded <= 0){
            ifBuilt = true;
        }
    }

    public Resource build(){
        if(ifBuilt){
            return upgradeValues;
        }
        return emptyValues;
    }

    public boolean ifBuilt(){
        if(ifBuilt){
            return true;
        }
        return false;
    }

    public String getName(){ return name; }

    public Resource getStats(){
        if(name.compareTo("empty") == 0){
            return emptyValues;
        }
        //System.out.println(name);System
        return upgradeValues;
    }

    public int getTotalIndustryNeeded(){ return totalIndustryNeeded; }

}

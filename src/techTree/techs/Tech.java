package techTree.techs;


import planetUpgrades.Upgrade;
import ships.shipComponents.defense.Defense;
import ships.shipComponents.weapons.Weapon;

public class Tech {

    private Tech[] prequisites;
    private boolean ifUnlocked;
    private boolean ifBeingBuilt;
    private String iD;
    private int scienceNeeded;
    private int initialScience;

    private String[] upgradeType;
    private int rank;
    private int withinRankIndex;
    private int withinRankMaxIndex;
    private int score;
    private String type;

    private boolean ifUpgradeExists;
    private boolean ifWeaponExists;
    private boolean ifDefenseExists;

    private Upgrade tech;
    private String unlockedAbility = "";
    private Weapon unlockedWeapon;
    private Defense unlockedDefense;

    public Tech(String iD, Tech[] prequisites, int rank, int withinRankIndex, int withinRankMaxIndex, int score){
        ifUnlocked = false;
        ifBeingBuilt = false;
        this.iD = iD;
        setPrequisites(prequisites);
        this.rank = rank;
        this.withinRankIndex = withinRankIndex;
        this.withinRankMaxIndex = withinRankMaxIndex;
        this.score = score;
    }

    public int getScore(){ return score; }

    private void setPrequisites(Tech[] prequisites){
        this.prequisites = prequisites;
        if(iD.compareTo("rocketry") == 0) {
            ifUpgradeExists = true;
            ifWeaponExists = false;
            ifDefenseExists = false;

            prequisites = new Tech[0];
            unlockedAbility = "buildShip";
            tech = new Upgrade(iD);
            scienceNeeded = 100;
            upgradeType = new String[]{"military", "science"};
        } else if(iD.compareTo("satellite") == 0) {
            ifUpgradeExists = true;
            ifWeaponExists = false;
            ifDefenseExists = false;

            tech = new Upgrade(iD);
            scienceNeeded = 200;
            upgradeType = new String[]{"viewDistance"};
        } else if(iD.compareTo("moonLanding") == 0) {
            ifUpgradeExists = true;
            ifWeaponExists = false;
            ifDefenseExists = false;

            unlockedAbility = "colonizeMoon";
            tech = new Upgrade(iD);
            scienceNeeded = 200;
            upgradeType = new String[]{"travel", "science"};
        } else if(iD.compareTo("interplanetaryTravel") == 0){
            ifUpgradeExists = true;
            ifWeaponExists = false;
            ifDefenseExists = false;

            ifUpgradeExists = false;
            scienceNeeded = 400;
            unlockedAbility = "interplanetaryTravel";
            upgradeType = new String[]{"travel"};
        } else if(iD.compareTo("basicMoonColony") == 0){
            ifUpgradeExists = false;
            ifWeaponExists = false;
            ifDefenseExists = false;
            scienceNeeded = 400;
            unlockedAbility = "basicMoonColony";
        } else if(iD.compareTo("simpleRobotics") == 0){
            scienceNeeded = 400;
            ifUpgradeExists = false;
            ifWeaponExists = false;
            ifDefenseExists = false;
            /*************************************************************************
                    add upgrade latter
             *************************************************************************/
        } else if(iD.substring(0, 5).compareTo("custom") == 0){
            //this is for faction specific
        }
        initialScience = scienceNeeded;
    }

    public void workOnTech(int science){
        scienceNeeded -= science;
        if(scienceNeeded <= 0){
            ifUnlocked = true;
        }
    }

    public String[] getUpgradeType(){ return upgradeType; }

    public void setIfBeingBuilt(boolean build){
        ifBeingBuilt = build;
    }

    public boolean getIfBeingBuilt(){ return ifBeingBuilt; }

    public boolean getIfComplete(){ return ifUnlocked; }

    public String getID(){
        return iD;
    }

    public int compareTo(Tech tech){
        if(tech.getID().compareTo(this.getID()) == 0){
            return 0;
        }
        return -1;
    }

    public boolean getIfPrequisitesUnlocked(){
        if(prequisites.length == 0){
            return true;
        }
        for(Tech t : prequisites){
            if(t.getIfUnlocked()){
                return true;
            }
        }
        return false;
    }

    public int getRank(){ return rank; }
    public int getWithinRankIndex(){ return withinRankIndex; }
    public int getWithinRankMaxIndex(){ return withinRankMaxIndex; }
    public Upgrade getUpgrade(){ return tech; }
    public boolean getIfUpgradeExists(){ return ifUpgradeExists; }
    public boolean getIfWeaponExists(){ return ifWeaponExists; }
    public Weapon getWeapon(){ return unlockedWeapon; }
    public boolean getIfDefenseExists(){ return ifDefenseExists; }
    public Defense getDefense(){ return unlockedDefense; }
    public boolean getIfUnlocked(){ return ifUnlocked; }
    public int getScienceNeeded(){ return scienceNeeded; }
    public String getUnlockedAbility(){ return unlockedAbility; }

    public String getType(){ return type; }
}

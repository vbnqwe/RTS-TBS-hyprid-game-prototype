package techTree;

import techTree.techs.Tech;

import java.util.ArrayList;

public class TechTree {

    private Tech[] allTechs = new Tech[6]; //WILL NEED TO BE ADJUSTED AS MORE TECH ADDED
    private Tech researchedTech;
    private ArrayList<Tech> queue;
    private boolean ifCompleted;
    private Tech completedTech;
    private int techScore;

    public TechTree(String civID){
        ifCompleted = false;
        queue = new ArrayList<Tech>();
        setUpTech(civID);
    }

    public void setUpTech(String civID){
        if(civID.compareTo("defCiv") == 0){
            allTechs[0] = new Tech("rocketry", new Tech[0], 0 , 0, 0, 50);
            allTechs[1] = new Tech("satellite", new Tech[]{allTechs[0]}, 1, 0, 1, 75);
            allTechs[2] = new Tech("moonLanding", new Tech[]{allTechs[0]}, 1, 1, 1, 75);
            allTechs[3] = new Tech("interplanetaryTravel", new Tech[]{allTechs[1], allTechs[2]}, 2, 0 , 2, 100);
            allTechs[4] = new Tech("basicMoonColony", new Tech[]{allTechs[1], allTechs[2]}, 2, 1, 2, 100);
            allTechs[5] = new Tech("simpleRobotics", new Tech[]{allTechs[1], allTechs[2]}, 2, 2, 2, 100);
        }
    }

    public void nextTurn(int science){
        if(queue.size() > 0) {
            queue.get(0).workOnTech(science);
            if (queue.get(0).getIfComplete()) {
                completedTech = queue.get(0);
                ifCompleted = true;
                techScore += queue.get(0).getScore();
                queue.remove(0);
            }
        } else {
            //System.out.println("Queue empty");
        }
    }

    public int getTechScore(){ return techScore; }

    public Tech getCompletedTech(int playerINdex){
        if(ifCompleted){
            ifCompleted = false;
            return completedTech;
        }
        return null;
    }

    public int getNumTech(){
        return allTechs.length;
    }

    public Tech getTech(int index){
        return allTechs[index];
    }

    public void selectTech(String iD){
        for(int i = 0; i < allTechs.length; i++){
            if(allTechs[i].getID().compareTo(iD) == 0){
                if(allTechs[i].getIfPrequisitesUnlocked() && !allTechs[i].getIfUnlocked() && !allTechs[i].getIfBeingBuilt()){
                    queue.add(0, allTechs[i]);
                    allTechs[i].setIfBeingBuilt(true);
                }
            }
        }
    }

    public ArrayList<Tech> getQueue(){ return queue; }


}

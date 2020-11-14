package devTools;

import players.score.Score;

import java.lang.reflect.Array;

public class Scores {

    private Score[] scoreForPlayers;

    public Scores(){
        //not necessary for now
    }

    public void nextTurn(Score[] scores){
        scoreForPlayers = scores;
    }

    public int getPlacementOfPlayerScience(int index){
        int placement = 0;
        for(int i = 0 ; i < scoreForPlayers.length; i++){
            if(i != index){
                if(scoreForPlayers[i].getSciScore() > scoreForPlayers[index].getSciScore()){
                    placement++;
                }
            }
        }
        return placement;
    }

    public int getPlacementOfPlayerIndustry(int index){
        int placement = 0;
        for(int i = 0 ; i < scoreForPlayers.length; i++){
            if(i != index){
                if(scoreForPlayers[i].getIndScore() > scoreForPlayers[index].getIndScore()){
                    placement++;
                }
            }
        }
        return placement;
    }

    public int getPlacementOfPlayerMilitary(int index){
        int placement = 0;
        for(int i = 0 ; i < scoreForPlayers.length; i++){
            if(i != index){
                if(scoreForPlayers[i].getMilScore() > scoreForPlayers[index].getMilScore()){
                    placement++;
                }
            }
        }
        return placement;
    }

    public int getPlacementOfPlayerCash(int index){
        int placement = 0;
        for(int i = 0 ; i < scoreForPlayers.length; i++){
            if(i != index){
                if(scoreForPlayers[i].getCasScore() > scoreForPlayers[index].getCasScore()){
                    placement++;
                }
            }
        }
        return placement;
    }

    public int getPlacementOfPlayerRME(int index){
        int placement = 0;
        for(int i = 0 ; i < scoreForPlayers.length; i++){
            if(i != index){
                if(scoreForPlayers[i].getRmeScore() > scoreForPlayers[index].getRmeScore()){
                    placement++;
                }
            }
        }
        return placement;
    }

    public int getNumPlayers(){ return scoreForPlayers.length; }


}

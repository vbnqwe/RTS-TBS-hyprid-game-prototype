package players.score;

public class Score {

    private int sciScore;
    private int indScore;
    private int casScore;
    private int milScore;
    private int rmeScore;

    public Score(int sciScore, int indScore, int casScore, int milScore, int rmeScore){
        this.sciScore = sciScore;
        this.indScore = indScore;
        this.casScore = casScore;
        this.milScore = milScore;
        this.rmeScore = rmeScore;
    }

    public int getSciScore(){ return sciScore; }
    public int getCasScore(){ return casScore; }
    public int getIndScore(){ return indScore; }
    public int getMilScore(){ return milScore; }
    public int getRmeScore(){ return rmeScore; }
}

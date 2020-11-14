package devTools;

public class Vector {

    private double theta;
    private double magnitude;
    private int x;
    private int y;

    public Vector(double theta, double magnitude){
        this.theta = theta;
        this.magnitude = magnitude;
        x = 0;
        y = 0;
    }

    public Vector(double theta, double magnitude, int x, int y){
        this.theta = theta;
        this.magnitude = magnitude;
        this.x = x;
        this.y = y;
    }

    public int getX(){ return x; }
    public int getY(){ return y; }
    public double getTheta(){ return theta; }
    public double getMagnitude(){ return magnitude; }

    public double getXEnd(){
        return x + magnitude * Math.cos(theta);
    }
    public double getYEnd(){ return y + magnitude * Math.sin(theta); }
}

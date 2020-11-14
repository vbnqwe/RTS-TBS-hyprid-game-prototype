package combat.objects;


import combat.sprites.ShipSprite;
import devTools.Coordinate;
import devTools.DoubleCooridinate;

import java.util.Vector;

public class ObjectWithPhysics {
    private float x;
    private float y;
    private float velocity;
    private float velocityDirection = 0;
    private float force;
    private float forceDirection;
    private float mass;
    private String id;
    private float radiusOfCollision;
    private float maxSpeed = 300;
    private float accelValue = 30;

    //states used for movement
    private boolean ifTurningTowardsWaypoint = false;
    private boolean ifOnCorrectAngle = false;

    private float xWayPoint;
    private float yWayPoint;
    private Vector<Coordinate> path = new Vector<>();
    private boolean ifTurnRightOverRide = false;
    private boolean ifTurnLeftOverRide = false;
    private boolean newWayPointAdded = false;
    private boolean ifUsingWaypoint = false;


    /**
     * This class is used for handling the physics of an object
     * SpriteObject will contain ai which will apply forces here that will change position
     * To move ship sprite, the environment will access cooridinates and send them to shipsprite
     */

    public ObjectWithPhysics(float x, float y, String id, float mass, float radiusOfCollision) {
        this.x = x;
        this.y = y;
        xWayPoint = x;
        yWayPoint = y;
        this.id = id;
        this.mass = mass;
        this.radiusOfCollision = radiusOfCollision;
    }

    public void translate(float deltaX, float deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(float theta, float magnitude){
        velocityDirection = theta;
        velocity = magnitude;
    }


    public void nextFrame() {
        double angleTurn = 0.035;


        if(ifUsingWaypoint) {
            boolean isNotWithinSlowDown = true;
            double timeRequiredToSlowDown = 1.0 * velocity / accelValue;
            double distanceRequired = 0.5 * accelValue * Math.pow(timeRequiredToSlowDown, 2) + velocity *
                    timeRequiredToSlowDown; //to slow down
            double distanceToWaypoint = Math.sqrt(Math.pow(x - xWayPoint, 2) + Math.pow(y - yWayPoint, 2));
            if (distanceRequired > distanceToWaypoint) {
                isNotWithinSlowDown = false;
            }


            if (velocity < maxSpeed && isNotWithinSlowDown) {
                velocity += accelValue;
            } else if (isNotWithinSlowDown && velocity > 0) {
                velocity -= accelValue;
            }

            if (velocity < 0) {
                velocity = 0;
            }

            if(distanceToWaypoint - 5 < 0){
                ifUsingWaypoint = false;
                velocity = 0;
            }


            if (ifTurningTowardsWaypoint) {


                double thetaToWaypoint = Math.atan2(yWayPoint - y, xWayPoint - x);
                if (thetaToWaypoint >= 2 * Math.PI) {
                    thetaToWaypoint = 2 * Math.PI - thetaToWaypoint;
                } else if (thetaToWaypoint < 0) {
                    thetaToWaypoint = 2 * Math.PI + thetaToWaypoint;
                }

                if (!ifTurnRightOverRide && !ifTurnLeftOverRide && newWayPointAdded) {
                    newWayPointAdded = false;
                    //decides which direction will turn

                    if ((x - xWayPoint) * Math.sin(velocityDirection) * velocity > (y - yWayPoint) * Math.cos(
                            velocityDirection) * velocity) {
                        ifTurnRightOverRide = true;
                        ifTurnLeftOverRide = false;
                    } else {
                        ifTurnRightOverRide = false;
                        ifTurnLeftOverRide = true;
                    }
                }

                double bounds1 = thetaToWaypoint + angleTurn * 4;
                double bounds2 = thetaToWaypoint - angleTurn * 4;
                if (velocityDirection > bounds2 && velocityDirection < bounds1) {
                    velocityDirection = (float) thetaToWaypoint;
                    ifTurnLeftOverRide = false;
                    ifTurnRightOverRide = false;
                    ifTurningTowardsWaypoint = false;
                } else if (ifTurnLeftOverRide) {
                    velocityDirection -= angleTurn;
                    if (velocityDirection < 0) {
                        velocityDirection = 2 * (float) Math.PI - velocityDirection;
                    }
                } else if (ifTurnRightOverRide) {
                    velocityDirection += angleTurn;
                    if (velocityDirection >= 2 * (float) Math.PI) {
                        velocityDirection = velocityDirection - 2 * (float) Math.PI;
                    }
                }

            /*if(velocityDirection >= Math.PI * 2){
                velocityDirection = velocityDirection + (float)Math.PI * 2;
            } else if(velocityDirection < 0){
                velocityDirection = (float)Math.PI * 2 - velocityDirection;
            }*/
            }
        }


        if (this.force != 0.0F) {
            double velocityX = Math.cos((double)this.velocityDirection) * (double)this.velocity;
            double velocityY = Math.sin((double)this.velocityDirection) * (double)this.velocity;
            double forceX = Math.cos((double)this.forceDirection) * (double)this.force;
            double forceY = Math.sin((double)this.forceDirection) * (double)this.force;
            double accelX = forceX / (double)this.mass;
            double accelY = forceY / (double)this.mass;
            double newVelocityX = accelX / 60.0D + velocityX;
            double newVelocityY = accelY / 60.0D + velocityY;
            this.velocity = (float)Math.sqrt(Math.pow(newVelocityX, 2.0D) + Math.pow(newVelocityY, 2.0D));
            this.velocityDirection = (float)Math.atan2(newVelocityX, newVelocityY);
        }

        if (this.velocity != 0.0F) {
            float velocityX = (float)Math.cos((double)this.velocityDirection) * this.velocity;
            float velocityY = (float)Math.sin((double)this.velocityDirection) * this.velocity;
            float newX = this.x + velocityX / 60.0F;
            float newY = this.y + velocityY / 60.0F;
            this.x = newX;
            this.y = newY;
        }

        this.force = 0.0F;
    }

    public void nextFrame(ObjectWithPhysics objectThatIsTracked) {
        double angleTurn = 0.035;
        float followDistance = 300;
        float distanceToTarget = (float)Math.sqrt(Math.pow(objectThatIsTracked.getX() - x, 2) + Math.pow(
                objectThatIsTracked.getY() - y, 2));
        if(distanceToTarget - followDistance > 0){
            double theta = Math.atan2(objectThatIsTracked.getY() - y, objectThatIsTracked.getX() - x);
            float newX = (distanceToTarget - followDistance) * (float)Math.cos(theta) + x;
            float newY = (distanceToTarget - followDistance) * (float)Math.sin(theta) + y;
            createWayPoint(newX, newY);
        }

        if(ifUsingWaypoint) {
            boolean isNotWithinSlowDown = true;
            double timeRequiredToSlowDown = 1.0 * velocity / accelValue;
            //integration babyyyyyyyyyyyyyyyyyyy
            double distanceRequired = 0.5 * accelValue * Math.pow(timeRequiredToSlowDown, 2) + velocity *
                    timeRequiredToSlowDown; //to slow down
            double distanceToWaypoint = Math.sqrt(Math.pow(x - xWayPoint, 2) + Math.pow(y - yWayPoint, 2));
            if (distanceRequired > distanceToWaypoint) {
                isNotWithinSlowDown = false;
            }


            if (velocity < maxSpeed && isNotWithinSlowDown) {
                velocity += accelValue;
            } else if (isNotWithinSlowDown && velocity > 0) {
                velocity -= accelValue;
            }

            if (velocity < 0) {
                velocity = 0;
            }

            if(distanceToWaypoint - 5 < 0){
                ifUsingWaypoint = false;
                velocity = 0;
            }


            if (ifTurningTowardsWaypoint) {


                double thetaToWaypoint = Math.atan2(yWayPoint - y, xWayPoint - x);
                if (thetaToWaypoint >= 2 * Math.PI) {
                    thetaToWaypoint = 2 * Math.PI - thetaToWaypoint;
                } else if (thetaToWaypoint < 0) {
                    thetaToWaypoint = 2 * Math.PI + thetaToWaypoint;
                }

                if (!ifTurnRightOverRide && !ifTurnLeftOverRide && newWayPointAdded) {
                    newWayPointAdded = false;
                    //decides which direction will turn

                    if ((x - xWayPoint) * Math.sin(velocityDirection) * velocity > (y - yWayPoint) * Math.cos(
                            velocityDirection) * velocity) {
                        ifTurnRightOverRide = true;
                        ifTurnLeftOverRide = false;
                    } else {
                        ifTurnRightOverRide = false;
                        ifTurnLeftOverRide = true;
                    }
                }

                double bounds1 = thetaToWaypoint + angleTurn * 4;
                double bounds2 = thetaToWaypoint - angleTurn * 4;
                if (velocityDirection > bounds2 && velocityDirection < bounds1) {
                    velocityDirection = (float) thetaToWaypoint;
                    ifTurnLeftOverRide = false;
                    ifTurnRightOverRide = false;
                    ifTurningTowardsWaypoint = false;
                } else if (ifTurnLeftOverRide) {
                    velocityDirection -= angleTurn;
                    if (velocityDirection < 0) {
                        velocityDirection = 2 * (float) Math.PI - velocityDirection;
                    }
                } else if (ifTurnRightOverRide) {
                    velocityDirection += angleTurn;
                    if (velocityDirection >= 2 * (float) Math.PI) {
                        velocityDirection = velocityDirection - 2 * (float) Math.PI;
                    }
                }

            /*if(velocityDirection >= Math.PI * 2){
                velocityDirection = velocityDirection + (float)Math.PI * 2;
            } else if(velocityDirection < 0){
                velocityDirection = (float)Math.PI * 2 - velocityDirection;
            }*/
            }
        }


        if (this.force != 0.0F) {
            double velocityX = Math.cos((double)this.velocityDirection) * (double)this.velocity;
            double velocityY = Math.sin((double)this.velocityDirection) * (double)this.velocity;
            double forceX = Math.cos((double)this.forceDirection) * (double)this.force;
            double forceY = Math.sin((double)this.forceDirection) * (double)this.force;
            double accelX = forceX / (double)this.mass;
            double accelY = forceY / (double)this.mass;
            double newVelocityX = accelX / 60.0D + velocityX;
            double newVelocityY = accelY / 60.0D + velocityY;
            this.velocity = (float)Math.sqrt(Math.pow(newVelocityX, 2.0D) + Math.pow(newVelocityY, 2.0D));
            this.velocityDirection = (float)Math.atan2(newVelocityX, newVelocityY);
        }

        if (this.velocity != 0.0F) {
            float velocityX = (float)Math.cos((double)this.velocityDirection) * this.velocity;
            float velocityY = (float)Math.sin((double)this.velocityDirection) * this.velocity;
            float newX = this.x + velocityX / 60.0F;
            float newY = this.y + velocityY / 60.0F;
            this.x = newX;
            this.y = newY;
        }

        this.force = 0.0F;
    }

    public void nextFrameCheckForCollisions(Vector<ShipSprite> allOtherShips){

    }

    public void createWayPoint(float x, float y){
        ifOnCorrectAngle = false;
        ifTurningTowardsWaypoint = true;
        ifUsingWaypoint = true;
        xWayPoint = x;
        yWayPoint = y;
        ifTurnRightOverRide = false;
        ifTurnLeftOverRide = false;
        newWayPointAdded = true;
    }

    public boolean getIfCollidedWith(ObjectWithPhysics obj) {
        float distanceBetween = (float)Math.sqrt(Math.pow((double)(obj.getX() - this.getX()), 2.0D) + Math.pow((double)(obj.getY() - this.getY()), 2.0D));
        float radiusCombined = obj.getRadiusOfCollision() + this.getRadiusOfCollision();
        return distanceBetween <= radiusCombined;
    }

    public float[] collide(ObjectWithPhysics obj) {
        /*double velXA = 1.0 * this.getVelocity() * Math.cos(this.getVelocityDirection());
        double velYA = 1.0 * this.getVelocity() * Math.sin(this.getVelocityDirection());
        double velXB = 1.0 * obj.getVelocity() * Math.cos(obj.getVelocityDirection());
        double velYB = 1.0 * obj.getVelocity() * Math.sin(obj.getVelocityDirection());
        double cx = 0.5 * this.getMass() * Math.pow(velXA, 2) + 0.5 * this.getMass() * Math.pow(velXA, 2) - (Math.pow(
                this.getMass(), 2) * Math.pow(velXA, 2) + Math.pow(obj.getMass(), 2) * Math.pow(velXB, 2) + 2.0 *
                this.getMass() * obj.getMass() * velXA * velXB) / 2 / obj.getMass();
        double bx = (2.0 * Math.pow(this.getMass(), 2) - 2.0 * this.getMass() * obj.getMass() * velXB) / 2 / obj.getMass();
        double ax = (this.getMass() * obj.getMass() - Math.pow(obj.getMass(), 2)) / 2.0 / obj.getMass();

        double cy = 0.5 * this.getMass() * Math.pow(velYA, 2) + 0.5 * this.getMass() * Math.pow(velYA, 2) - (Math.pow(
                this.getMass(), 2) * Math.pow(velYA, 2) + Math.pow(obj.getMass(), 2) * Math.pow(velYB, 2) + 2.0 *
                this.getMass() * obj.getMass() * velYA * velYB) / 2 / obj.getMass();
        double by = (2.0 * Math.pow(this.getMass(), 2) - 2.0 * this.getMass() * obj.getMass() * velYB) / 2 / obj.getMass();
        double ay = (this.getMass() * obj.getMass() - Math.pow(obj.getMass(), 2)) / 2.0 / obj.getMass();

        double vx1, vx2, vy1, vy2;

        if(ax != 0) {
            vx1 = (-bx + Math.sqrt(bx * bx - 4.0 * ax * cx)) / 2 / ax;
            vx2 = (-bx - Math.sqrt(bx * bx - 4.0 * ax * cx)) / 2 / ax;

            vy1 = (-by + Math.sqrt(by * by - 4.0 * ay * cy)) / 2 / ay;
            vy2 = (-by - Math.sqrt(by * by - 4.0 * ay * cy)) / 2 / ay;
        } else {
            vx1 = (-bx + Math.sqrt(bx * bx - 4.0 * ax * cx)) / 2;
            vx2 = (-bx - Math.sqrt(bx * bx - 4.0 * ax * cx)) / 2;

            vy1 = (-by + Math.sqrt(by * by - 4.0 * ay * cy)) / 2;
            vy2 = (-by - Math.sqrt(by * by - 4.0 * ay * cy)) / 2;
        }


        double newDirection = Math.atan2(vy1, vx1);
        double newVelocity = Math.sqrt(vx1 * vx1 + vy1 * vy1);
        System.out.println("***********************************");
        System.out.println((this.getMass()) + " " + obj.getMass() + " " + Math.pow(obj.getMass(), 2));
        System.out.println(ax + " " + bx + " " + cx);
        System.out.println(newVelocity);
        System.out.println("***********************************");
        velocity = (float)newVelocity;
        velocityDirection = (float)newDirection;*/

        double velXA = 1.0 * this.getVelocity() * Math.cos(this.getVelocityDirection());
        double velYA = 1.0 * this.getVelocity() * Math.sin(this.getVelocityDirection());
        double velXB = 1.0 * obj.getVelocity() * Math.cos(obj.getVelocityDirection());
        double velYB = 1.0 * obj.getVelocity() * Math.sin(obj.getVelocityDirection());

        double finalVX = (this.getMass() * velXA + obj.getMass() * velXB) / (this.getMass() + obj.getMass());
        double finalVY = (this.getMass() * velYA + obj.getMass() * velYB) / (this.getMass() + obj.getMass());

        float theta = (float)Math.atan2(finalVY, finalVX);
        float magnitude = (float)Math.sqrt(finalVX * finalVX + finalVY + finalVY);

        velocity = magnitude;
        velocityDirection = theta;

        System.out.println(velXA + " " + velYA + " " + velXB + " " + velYB);
        System.out.println(finalVX + " " + finalVY);
        System.out.println("*************************************");

        return new float[]{magnitude, theta};
    }

    public String getId() {
        return this.id;
    }

    public void applyForce(float magnitude, float direction) {
        this.force = magnitude;
        this.forceDirection = direction;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getRadiusOfCollision() {
        return this.radiusOfCollision;
    }

    public float getVelocity() {
        return this.velocity;
    }

    public float getVelocityDirection() {
        return this.velocityDirection;
    }

    public float getMass() {
        return this.mass;
    }

    public float getForceDirection(){ return forceDirection; }

    public DoubleCooridinate getWayPoint(){
        return new DoubleCooridinate(xWayPoint, yWayPoint);
    }

    public void setSpeed(float maxVelocity, float accel){
        accelValue = accel;
        maxSpeed = maxVelocity;
    }

    public void setID(String newId){
        id = newId;
    }


}

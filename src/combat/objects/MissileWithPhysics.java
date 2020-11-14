package combat.objects;

import ships.shipComponents.weapons.Weapon;

public class MissileWithPhysics extends ObjectWithPhysics{

    private Weapon missile;
    private String target;

    public MissileWithPhysics(float x, float y, String id, float mass, float radiusOfCollision, Weapon weapon, String
            target, float theta){
        super(x, y, id, mass, radiusOfCollision);
        super.setVelocity(theta, 0);
        missile = weapon;
        this.target = target;
        super.setSpeed(500, 50);
    }

    public String getTarget(){ return target; }
    public Weapon getMissile(){ return missile; }

}

package combat.objects;

import ships.shipComponents.weapons.Weapon;

public class ProjectileWithPhysics extends ObjectWithPhysics{

    private Weapon weapon;

    public ProjectileWithPhysics(float x, float y, String id, float mass, float radiusOfCollision, float speed, float
            theta, Weapon weapon){
        super(x, y, id, mass, radiusOfCollision);
        super.setVelocity(theta, speed);
        this.weapon = weapon;
    }

    public Weapon getWeapon(){ return weapon; }
}

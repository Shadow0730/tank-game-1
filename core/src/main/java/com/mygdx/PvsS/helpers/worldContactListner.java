package com.mygdx.PvsS.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.PvsS.tanks.car;

public class worldContactListner implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Object userDataA = bodyA.getUserData();
        Object userDataB = bodyB.getUserData();

        if (isProjectileGroundCollision(userDataA, userDataB)) {
            markForDestruction(bodyA, bodyB, userDataA, userDataB);
        }
        if (isProjectileTankCollision(userDataA, userDataB)) {
            handleProjectileTankCollision(bodyA, bodyB, userDataA, userDataB);
        }
    }

    private boolean isProjectileGroundCollision(Object dataA, Object dataB) {

        return (isProjectile(dataA) && isGround(dataB)) ||
            (isGround(dataA) && isProjectile(dataB));
    }

    private boolean isProjectileTankCollision(Object dataA, Object dataB) {
        return (isProjectile(dataA) && isChassis(dataB)) ||
            (isChassis(dataA) && isProjectile(dataB));
    }
    private void handleProjectileTankCollision(Body bodyA, Body bodyB, Object dataA, Object dataB) {
        Body projectileBody = isProjectile(dataA) ? bodyA : bodyB;
        Body chassisBody = isChassis(dataA) ? bodyA : bodyB;
        projectileBody.setUserData("destroy");
        Object chassisUserData = chassisBody.getUserData();
        if (chassisUserData instanceof car) {
            car tank = (car) chassisUserData;
            tank.takeDamage(20);
            System.out.println("Projectile hit tank!");
        }
    }

    private boolean isProjectile(Object data) {
        return data != null && data.equals("projectile");
    }

    private boolean isGround(Object data) {
        return data != null && (data.equals("ground") ||
            data.equals("terrain") ||
            data.equals("static"));
    }
    private boolean isChassis(Object data) {
        return data instanceof car;
    }


    private void markForDestruction(Body bodyA, Body bodyB, Object dataA, Object dataB) {
        if (isProjectile(dataA)) {
            bodyA.setUserData("destroy");
        } else if (isProjectile(dataB)) {
            bodyB.setUserData("destroy");
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

package com.mygdx.PvsS.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class worldContactListner implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Object userDataA = bodyA.getUserData();
        Object userDataB = bodyB.getUserData();

        // Check if projectile hit ground
        if (isProjectileGroundCollision(userDataA, userDataB)) {
            markForDestruction(bodyA, bodyB, userDataA, userDataB);
        }
    }

    private boolean isProjectileGroundCollision(Object dataA, Object dataB) {
        // Check if one is projectile and other is ground
        return (isProjectile(dataA) && isGround(dataB)) ||
            (isGround(dataA) && isProjectile(dataB));
    }

    private boolean isProjectile(Object data) {
        return data != null && data.equals("projectile");
    }

    private boolean isGround(Object data) {
        // Ground objects typically have "ground" or "terrain" or null userData
        return data != null && (data.equals("ground") ||
            data.equals("terrain") ||
            data.equals("static"));
    }

    private void markForDestruction(Body bodyA, Body bodyB, Object dataA, Object dataB) {
        if (isProjectile(dataA)) {
            bodyA.setUserData("destroy");
            System.out.println("Projectile marked for destruction!");
        } else if (isProjectile(dataB)) {
            bodyB.setUserData("destroy");
            System.out.println("Projectile marked for destruction!");
        }
    }

    @Override
    public void endContact(Contact contact) {
        // Not needed for projectile destruction
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Not needed for projectile destruction
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Not needed for projectile destruction
    }
}

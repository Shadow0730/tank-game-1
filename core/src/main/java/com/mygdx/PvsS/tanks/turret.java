package com.mygdx.PvsS.tanks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class turret {
    private World world;
    private ArrayList<projectile> projectiles;
    private Texture projectileTexture;

    public turret(World world, Texture projectileTexture) {
        this.world = world;
        this.projectiles = new ArrayList<>();
        this.projectileTexture = projectileTexture;
    }

    public void shoot(float x, float y, float angle, float power) {
        projectile projectile = new projectile(world, x, y, angle, power, projectileTexture);
        projectiles.add(projectile);
    }

    public void update(float delta) {
        // Update all projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            projectile projectile = projectiles.get(i);
            projectile.update(delta);

            // Check for ground collision
            if (projectile.getBody().getUserData() != null &&
                projectile.getBody().getUserData().equals("destroy")) {
                projectile.markForDestruction();
            }

            // Remove destroyed projectiles
            if (projectile.shouldDestroy()) {
                world.destroyBody(projectile.getBody());
                projectiles.remove(i);
                System.out.println("Projectile destroyed");
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (projectile projectile : projectiles) {
            projectile.render(batch);
        }
    }

    public ArrayList<projectile> getProjectiles() {
        return projectiles;
    }
}

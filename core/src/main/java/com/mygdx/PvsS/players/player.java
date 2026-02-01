package com.mygdx.PvsS.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.mygdx.PvsS.helpers.constants.PPM;

public class player extends player1 {
    public player(float height, float width, Body body) {
        super(height, width, body);
        this.speed = 4f;
    }

    @Override
    public void update() {
        x=body.getPosition().x * PPM;
        y=body.getPosition().y * PPM;

        checkUserInput();
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    private void checkUserInput(){
        velX = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = 10;
        }
        if (velX != 0 || velY != 0) {
            // Moving - enable gravity
            body.setGravityScale(20f);
        } else {
            // Not moving - disable gravity
            body.setGravityScale(0);
            // Also stop any vertical velocity to prevent falling
            body.setLinearVelocity(0, 0);
        }
        body.setLinearVelocity(velX * speed,0);
    }
}

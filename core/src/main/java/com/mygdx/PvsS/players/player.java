package com.mygdx.PvsS.players;

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
    }

    @Override
    public void render(SpriteBatch batch) {

    }
}

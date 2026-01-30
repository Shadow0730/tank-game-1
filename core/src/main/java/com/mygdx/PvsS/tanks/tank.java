package com.mygdx.PvsS.tanks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;

public class tank extends Sprite {
    public World world;
    public Body tankbody;

    public tank(float x, float y, float width, float height, World world) {
        this.world=world;
        definetank();
    }

    public void definetank(){
        BodyDef bd = new BodyDef();
        bd.position.set(64,444);
        bd.type = BodyDef.BodyType.DynamicBody;
        tankbody = world.createBody(bd);
        FixtureDef fd = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5);
        fd.shape = shape;
        tankbody.createFixture(fd);

    }
}

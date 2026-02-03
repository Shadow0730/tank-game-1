package com.mygdx.PvsS.tanks;

import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.PvsS.helpers.constants.PPM;

public class bodyhelper {
    public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world){
        BodyDef bdef = new BodyDef();
        bdef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bdef.position.set(x/PPM, y/PPM);
        bdef.fixedRotation = true;
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2/PPM, height/2/PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 1.0f;
        body.createFixture(fdef);
        shape.dispose();
        return body;
    }
}

package com.mygdx.PvsS.tanks;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;

public class car {
    private Body chassis, leftWheel, rightWheel;
    private WheelJoint leftaxeis, rightaxeis;

    public car(World world, FixtureDef chassisFdef, FixtureDef wheelFdef, float x,float y,float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //chassis
        PolygonShape chassisshape = new PolygonShape();
        chassisshape.set(new float[] {-width/2, -height/2, width/2, -height/2, width/2, height/2*.75f, height/2,-width/2*.8f, height/2*.8f, });
        chassisFdef.shape = chassisshape;
        chassis = world.createBody(bodyDef);
        chassis.createFixture(chassisFdef);

        //left wheel
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(height/2.5f);
        wheelFdef.shape = wheelShape;
        leftWheel = world.createBody(bodyDef);
        leftWheel.createFixture(wheelFdef);

        //right wheel
        rightWheel = world.createBody(bodyDef);
        rightWheel.createFixture(wheelFdef);

    }
}

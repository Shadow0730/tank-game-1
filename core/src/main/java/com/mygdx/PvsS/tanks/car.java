package com.mygdx.PvsS.tanks;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.Input.Keys;

public class car extends InputAdapter {
    private Body chassis, leftWheel, rightWheel;
    private WheelJoint leftAxis, rightAxis;
    private float motorSpeed = 100;

    public car(World world, FixtureDef chassisFdef, FixtureDef wheelFdef, float x,float y,float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        //chassis
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-width/2, -height/2);           // Bottom-left corner
        vertices[1] = new Vector2(width/2, -height/2);            // Bottom-right corner
        vertices[2] = new Vector2(width/2 * 0.4f, height/2);     // Top-right (sloped in)
        vertices[3] = new Vector2(-width/2 * 0.8f, height/2*0.8f);     // Top-left (narrower)

        PolygonShape chassisShape = new PolygonShape();
        chassisShape.set(vertices);chassisFdef.shape = chassisShape;
        chassis = world.createBody(bodyDef);
        chassis.createFixture(chassisFdef);

        //left wheel
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(height/3.5f);
        wheelFdef.shape = wheelShape;
        leftWheel = world.createBody(bodyDef);
        leftWheel.createFixture(wheelFdef);

        //right wheel
        rightWheel = world.createBody(bodyDef);
        rightWheel.createFixture(wheelFdef);

        //left axis
        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = chassis;
        axisDef.bodyB = leftWheel;
        axisDef.localAnchorA.set(-width/2*.75f + wheelShape.getRadius(), -height/2 * 1.25f);
        axisDef.frequencyHz = 5f;
        axisDef.localAxisA.set(Vector2.Y);
        axisDef.maxMotorTorque = chassisFdef.density*10;
        leftAxis = (WheelJoint) world.createJoint(axisDef);

        //right axis
        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.x *= -1;
        rightAxis = (WheelJoint) world.createJoint(axisDef);

    }
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                leftAxis.enableMotor(true);
                leftAxis.setMotorSpeed(-motorSpeed);
                break;
            case Keys.S:
                leftAxis.enableMotor(true);
                leftAxis.setMotorSpeed(motorSpeed);

        }
        return true;
    }
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.W:
            case Keys.S:
                leftAxis.enableMotor(false);
        }
        return true;
    }
}

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
    private float motorSpeed = 10;
    private boolean isMovingForward = false;
    private boolean isMovingBackward = false;

    public car(World world, FixtureDef chassisFdef, FixtureDef wheelFdef, float x,float y,float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        bodyDef.angularDamping = 5.0f;
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
        axisDef.dampingRatio = 0.7f;
        axisDef.localAxisA.set(Vector2.Y);
        axisDef.maxMotorTorque = 300;
        leftAxis = (WheelJoint) world.createJoint(axisDef);

        //right axis
        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.x *= -1;
        rightAxis = (WheelJoint) world.createJoint(axisDef);

    }
    public boolean keyDown(int keycode) {
        if (chassis.getType() == BodyDef.BodyType.StaticBody) {
            chassis.setType(BodyDef.BodyType.DynamicBody);
            System.out.println("Car UNLOCKED - switched to Dynamic");
        }
        switch (keycode) {
            case Keys.W:
                leftAxis.enableMotor(true);
                rightAxis.enableMotor(true);
            case Keys.UP:
                leftAxis.setMotorSpeed(-motorSpeed);
                rightAxis.setMotorSpeed(-motorSpeed);
                isMovingForward = true;
                isMovingBackward = false;
                break;
            case Keys.S:
                leftAxis.enableMotor(true);
                rightAxis.enableMotor(true);
            case Keys.DOWN:
                leftAxis.setMotorSpeed(motorSpeed);
                rightAxis.setMotorSpeed(motorSpeed);
                isMovingBackward = true;
                isMovingForward = false;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.W:
            case Keys.UP:
                leftAxis.setMotorSpeed(0);
                rightAxis.setMotorSpeed(0);
                isMovingForward = false;
                break;
            case Keys.S:
            case Keys.DOWN:
                leftAxis.setMotorSpeed(0);
                rightAxis.setMotorSpeed(0);
                isMovingBackward = false;
                break;
        }
        if (!isMovingForward && !isMovingBackward) {
            // Stop all movement first

            // Convert to Static - NOW IMMUNE TO GRAVITY!
            chassis.setType(BodyDef.BodyType.StaticBody);

            System.out.println("Car LOCKED - switched to Static");
        }
        return true;
    }

    public void update() {
        // Only apply force when moving
        if (isMovingForward || isMovingBackward) {

            // Get chassis angle (slope angle in radians)
            float theta = chassis.getAngle();

            // Calculate gravity component along slope: F_gravity = mg * sin(θ)
            float mass = chassis.getMass();
            float gravity = 9.8f;
            float gravityComponentAlongSlope = mass * gravity * (float) Math.sin(theta);

            // Get the direction of movement
            Vector2 velocity = chassis.getLinearVelocity();
            float direction = isMovingForward ? 1f : -1f;

            // Calculate the angle of the car's forward direction
            float forwardAngleX = (float) Math.cos(theta);
            float forwardAngleY = (float) Math.sin(theta);

            // When climbing upward, we need to counteract gravity
            // Apply force in the direction of movement to counter gravity
            if (Math.abs(Math.toDegrees(theta)) > 5) {  // On a slope

                // Force to counteract gravity component
                // We apply MORE force than gravity to actually climb
                float counterForce = Math.abs(gravityComponentAlongSlope) * 2.0f;  // 2x for climbing power

                // Apply force in the direction the car is facing
                float forceX = direction * forwardAngleX * counterForce;
                float forceY = direction * forwardAngleY * counterForce;

                chassis.applyForceToCenter(forceX, forceY, true);

                // Debug output
                if (Math.random() < 0.02) {  // Print occasionally
                    System.out.println(String.format(
                        "Slope: %.1f° | Gravity force: %.1f | Applied force: (%.1f, %.1f)",
                        Math.toDegrees(theta),
                        gravityComponentAlongSlope,
                        forceX,
                        forceY
                    ));
                }
            }

            // Prevent excessive rotation (anti-flip)
            float angularVelocity = chassis.getAngularVelocity();
            if (Math.abs(angularVelocity) > 2f) {
                chassis.setAngularVelocity(Math.signum(angularVelocity) * 2f);
            }
        }
    }
    public Body getChassis() {
        return chassis;
    }
}

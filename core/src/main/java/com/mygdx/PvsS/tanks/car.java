package com.mygdx.PvsS.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.Input.Keys;

import java.util.ArrayList;

import static com.mygdx.PvsS.helpers.constants.PPM;

public class car extends InputAdapter {
    private World world;
    private Body chassis, leftWheel, rightWheel;
    private WheelJoint leftAxis, rightAxis;
    private float motorSpeed = 80f;
    private Sprite turretSprite;
    private float turretAngle = 0;
    private float turretLength = 80f;
    private turret gunManager;
    private float power = 5f;
    private OrthographicCamera camera;

    public car(World world,OrthographicCamera camera, FixtureDef chassisFdef, FixtureDef wheelFdef, FixtureDef rwheeldef, float x,float y,float width, float height, Texture turretTexture, Texture projectileTexture) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        bodyDef.angularDamping = 10.0f;  // VERY HIGH - resists rotation strongly!
        bodyDef.linearDamping = 1.0f;
        this.world = world;
        this.camera = camera;

        //chassis
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-width/2 * 0.7f, -height/2);
        vertices[1] = new Vector2(width/2 * 0.7f, -height/2);
        vertices[2] = new Vector2(width/2 * 0.75f, height/2);
        vertices[3] = new Vector2(-width/2 * 0.75f, height/2);     // Top-left (narrower)

        PolygonShape chassisShape = new PolygonShape();
        chassisShape.set(vertices);
        chassisFdef.shape = chassisShape;
        chassisFdef.friction = 2.0f;
        chassis = world.createBody(bodyDef);
        chassis.setUserData("chassis");
        chassis.createFixture(chassisFdef);

        //left wheel
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(height/3.5f);
        wheelFdef.shape = wheelShape;
        wheelFdef.friction = 2.5f;
        leftWheel = world.createBody(bodyDef);
        leftWheel.createFixture(wheelFdef);
        leftWheel.setUserData("wheel");

        //right wheel
        rwheeldef.shape = wheelShape;
        rwheeldef.friction = 2.5f;
        rightWheel = world.createBody(bodyDef);
        rightWheel.createFixture(rwheeldef);
        rightWheel.setUserData("wheel");


        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = chassis;
        axisDef.bodyB = leftWheel;
        axisDef.localAnchorA.set(-width/2*.75f + wheelShape.getRadius(), -height/2 * 1.25f);
        axisDef.frequencyHz = 5f;
        axisDef.dampingRatio = 0.7f;
        axisDef.localAxisA.set(Vector2.Y);
        axisDef.maxMotorTorque = 800;
        leftAxis = (WheelJoint) world.createJoint(axisDef);

        //right axis
        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.x *= -1;
        rightAxis = (WheelJoint) world.createJoint(axisDef);

        turretSprite = new Sprite(new TextureRegion(turretTexture));
        turretSprite.setSize(turretLength, 20);
        turretSprite.setOrigin(0, turretSprite.getHeight()/2);

        gunManager = new turret(world, projectileTexture);
    }
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                leftAxis.enableMotor(true);
                rightAxis.enableMotor(true);
            case Keys.UP:
                leftAxis.setMotorSpeed(-motorSpeed);
                rightAxis.setMotorSpeed(-motorSpeed);
                break;
            case Keys.S:
                leftAxis.enableMotor(true);
                rightAxis.enableMotor(true);
            case Keys.DOWN:
                leftAxis.setMotorSpeed(motorSpeed);
                rightAxis.setMotorSpeed(motorSpeed);
                break;
            case Keys.LEFT:
                power = Math.max(10, power - 5);
                System.out.println("Power: " + power);
                break;
            case Keys.RIGHT:
                power = Math.min(100, power + 5);
                System.out.println("Power: " + power);
                break;
            case Keys.F:
                shoot();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.W:
            case Keys.UP:
            case Keys.S:
            case Keys.DOWN:
                leftAxis.setMotorSpeed(0);
                rightAxis.setMotorSpeed(0);
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            shoot();
        }
        return true;
    }

    private void shoot() {
        Vector2 chassisPos = chassis.getPosition();
        float turretRadians = (float) Math.toRadians(turretAngle);

        float tipX = (chassisPos.x * PPM) + (float) Math.cos(turretRadians) * turretLength;
        float tipY = (chassisPos.y * PPM) + (float) Math.sin(turretRadians) * turretLength;

        gunManager.shoot(tipX, tipY, turretAngle, power);
        System.out.println("BANG! Power: " + power + ", Angle: " + turretAngle + "°");
    }

    public void update(float delta) {
        updateTurretAngle();
        gunManager.update(delta);
        Vector2 pos = chassis.getPosition();
        turretSprite.setPosition(pos.x * PPM, pos.y * PPM);
        turretSprite.setRotation(turretAngle);
    }

    private void updateTurretAngle() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        Vector3 worldCoords = new Vector3(mouseX, mouseY, 0);
        camera.unproject(worldCoords);

        Vector2 chassisPos = chassis.getPosition();
        float chassisWorldX = chassisPos.x * PPM;
        float chassisWorldY = chassisPos.y * PPM;

        float dx = worldCoords.x - chassisWorldX;
        float dy = worldCoords.y - chassisWorldY;
        turretAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    public void render(SpriteBatch batch) {
        // Draw chassis

        // Draw turret
        turretSprite.draw(batch);

        // Draw projectiles
        gunManager.render(batch);
    }

    public Body getChassis() {
        return chassis;
    }

    public turret getGunManager() {
        return gunManager;
    }

    public float getPower() {
        return power;
    }
}

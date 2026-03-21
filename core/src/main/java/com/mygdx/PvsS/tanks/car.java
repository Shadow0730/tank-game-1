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
    private Sprite healthbarSprite;
    private float turretAngle = 0;
    private float turretLength = 80f;
    private turret gunManager;
    private float power = 5f;
    private OrthographicCamera camera;
    private boolean isActive = true;
    private int maxHP = 100;
    private int currentHP = 100;
    private boolean isDestroyed = false;
    private boolean isMovingForward = false;
    private boolean isMovingBackward = false;

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
        chassis.setUserData(this);
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
        if (!isActive) return false;
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
        if (!isActive) return false;
        switch (keycode) {
            case Keys.W:
            case Keys.UP:
                leftAxis.setMotorSpeed(0);
                rightAxis.setMotorSpeed(0);
                isMovingForward = false;
                break;
            case Keys.S:
                leftAxis.enableMotor(false);
            case Keys.DOWN:
                leftAxis.setMotorSpeed(0);
                rightAxis.setMotorSpeed(0);
                isMovingBackward = false;
                break;
        }
        if (!isMovingForward && !isMovingBackward) {
            chassis.setType(BodyDef.BodyType.StaticBody);
            System.out.println("Car LOCKED - switched to Static");
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isActive) return false;
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

    private void updateTurretAngle() {
        if (!isActive) return;
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
        if (isDestroyed) return;
        // Draw chassis

        // Draw turret
        turretSprite.draw(batch);

        // Draw projectiles
        gunManager.render(batch);
        renderHealthBar(batch);
    }
    private void renderHealthBar(SpriteBatch batch) {
        Vector2 pos = chassis.getPosition();
        float barWidth = 60;
        float barHeight = 8;
        float barX = pos.x * PPM - barWidth/2;
        float barY = pos.y * PPM + 40;  // Above tank

        // Background (red)
        batch.setColor(1, 0, 0, 0.7f);
        batch.draw(healthbarSprite.getTexture(), barX, barY, barWidth, barHeight);

        // Foreground (green, proportional to HP)
        float hpPercent = (float) currentHP / maxHP;
        batch.setColor(0, 1, 0, 0.9f);
        batch.draw(healthbarSprite.getTexture(), barX, barY, barWidth * hpPercent, barHeight);

        // Reset color
        batch.setColor(1, 1, 1, 1);
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

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }
    public void takeDamage(int damage) {
        if (isDestroyed) return;

        currentHP -= damage;
        System.out.println("Tank hit! HP: " + currentHP + "/" + maxHP);

        if (currentHP <= 0) {
            currentHP = 0;
            isDestroyed = true;
            System.out.println("Tank destroyed!");
        }
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}

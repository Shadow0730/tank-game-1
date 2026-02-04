//package com.mygdx.PvsS.players;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.World;
//import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
//
//import static com.mygdx.PvsS.helpers.constants.PPM;
//
//public class player extends player1 {
//    private World world;
//    private RevoluteJoint leftWheelJoint;
//    private RevoluteJoint rightWheelJoint;
//    private float motorSpeed = 5.0f;
//    public player(float height, float width, Body body, World world) {
//        super(height, width, body);
//        this.speed = 4f;
//        this.world = world;
//    }
//    public void setWheelJoints(RevoluteJoint left, RevoluteJoint right) {
//        this.leftWheelJoint = left;
//        this.rightWheelJoint = right;
//    }
//
//    @Override
//    public void update() {
//        x=body.getPosition().x * PPM;
//        y=body.getPosition().y * PPM;
//
//        checkUserInput();
//    }
//
//    @Override
//    public void render(SpriteBatch batch) {
//
//    }
//
//    private void checkUserInput() {
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//            // Move left - spin wheels forward
//            leftWheelJoint.setMotorSpeed(motorSpeed);
//            rightWheelJoint.setMotorSpeed(motorSpeed);
//            body.setGravityScale(1.0f);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            // Move right - spin wheels backward
//            leftWheelJoint.setMotorSpeed(-motorSpeed);
//            rightWheelJoint.setMotorSpeed(-motorSpeed);
//            body.setGravityScale(1.0f);
//        } else {
//            // Stop wheels
//            leftWheelJoint.setMotorSpeed(0);
//            rightWheelJoint.setMotorSpeed(0);
//            body.setGravityScale(0);  // Disable gravity when stopped
//        }
//    }
//}

package com.mygdx.PvsS.tanks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.PvsS.helpers.constants.PPM;

public class projectile {
    private Body body;
    private Sprite sprite;
    private boolean shouldDestroy = false;
    private float width = 50;   // pixels
    private float height = 15;  // pixels

    public projectile(World world, float x, float y, float angle, float power, Texture texture) {

        // Create Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;  // Fast-moving projectile

        body = world.createBody(bodyDef);

        // Create projectile shape (rectangle)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width/2) / PPM, (height/2) / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.3f;
        fixtureDef.friction = 0.5f;

        body.createFixture(fixtureDef);
        body.setUserData("projectile");
        shape.dispose();

        // Create sprite
        sprite = new Sprite(new TextureRegion(texture));
        sprite.setSize(width, height);
        sprite.setOrigin(width/2, height/2);

        // Calculate velocity from angle and power
        // Tank Stars uses: startingVelocity.rotateDeg(deg - 45).scl(power/4)
        Vector2 velocity = new Vector2(3, 4);  // Base velocity
        velocity.rotateDeg(angle - 45);
        velocity.scl(power / 4f);

        body.setLinearVelocity(velocity);
        body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
    }

    public void update(float delta) {
        if (body != null) {
            sprite.setPosition(
                body.getPosition().x * PPM - width/2,
                body.getPosition().y * PPM - height/2
            );
            sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        }
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Body getBody() {
        return body;
    }

    public boolean shouldDestroy() {
        return shouldDestroy;
    }

    public void markForDestruction() {
        shouldDestroy = true;
    }
}

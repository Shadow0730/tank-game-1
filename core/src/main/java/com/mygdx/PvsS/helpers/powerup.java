package com.mygdx.PvsS.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.PvsS.helpers.constants.PPM;

public class powerup {
    public enum PowerupType {
        HEALTH_BOOST(30, "Health +30"),
        POWER_BOOST(20, "Power +20"),
        DAMAGE_BOOST(15, "Damage +15");

        private final int value;
        private final String label;

        PowerupType(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public int getValue() { return value; }
        public String getLabel() { return label; }
    }

    private Body body;
    private Sprite sprite;
    private PowerupType type;
    private boolean collected = false;
    private float width = 30;
    private float height = 30;
    private float lifetime = 0;
    private static final float MAX_LIFETIME = 30f;  // 30 seconds

    public powerup(World world, float x, float y, PowerupType type, Texture texture) {
        this.type = type;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.type = BodyDef.BodyType.KinematicBody;  // Not affected by gravity

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius((width / 2) / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;  // Don't collide physically, just detect

        body.createFixture(fixtureDef);
        body.setUserData("powerup:" + type.name());
        shape.dispose();

        sprite = new Sprite(new TextureRegion(texture));
        sprite.setSize(width, height);
    }

    public void update(float delta) {
        if (collected) return;

        lifetime += delta;

        // Destroy after 30 seconds
        if (lifetime > MAX_LIFETIME) {
            collected = true;
        }

        // Update sprite position
        Vector2 pos = body.getPosition();
        sprite.setPosition(pos.x * PPM - width/2, pos.y * PPM - height/2);

        // Slight bobbing animation
        sprite.setY(sprite.getY() + (float)Math.sin(lifetime) * 0.5f);
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            sprite.draw(batch);
        }
    }

    public Body getBody() {
        return body;
    }

    public PowerupType getType() {
        return type;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}

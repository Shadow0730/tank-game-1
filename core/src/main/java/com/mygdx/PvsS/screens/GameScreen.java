package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.PvsS.helpers.map;
import com.mygdx.PvsS.helpers.worldContactListner;
import com.mygdx.PvsS.tankgame;
import com.mygdx.PvsS.tanks.car;


import static com.mygdx.PvsS.helpers.constants.PPM;

public class GameScreen implements Screen {
    private tankgame game;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Viewport gamePort;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer dR;
    private SpriteBatch batch;
    private map tileMapHelper;

    //private player player;
    private car Car;

    public GameScreen(tankgame game, OrthographicCamera camera) {

        this.camera = camera;
        camera.setToOrtho(false, 1280, 720);
        camera.position.set(640, 360, 0); // Center of screen
        camera.update();
        this.game = game;
        mapLoader = new TmxMapLoader();
        world = new World(new Vector2(0,-9.8f), true);
        dR = new Box2DDebugRenderer();//use of debug
        this.batch = game.batch;
        this.tileMapHelper = new map(this);
        renderer = tileMapHelper.setupMap();
        world.setContactListener(new worldContactListner());
    }

    @Override
    public void show() {
        FixtureDef fixtureDef = new FixtureDef();
        FixtureDef wheelFixtureDef = new FixtureDef();
        FixtureDef rwheelFixtureDef = new FixtureDef();
        fixtureDef.density = 2.0f;
        fixtureDef.friction = 2.0f;
        fixtureDef.restitution = 0.1f;


        wheelFixtureDef.density = 1.5f;
        wheelFixtureDef.friction = 3.0f;
        wheelFixtureDef.restitution = 0.2f;

        rwheelFixtureDef.density = 3.0f;
        rwheelFixtureDef.friction = 3.0f;
        rwheelFixtureDef.restitution = 0.2f;
        Texture turretTexture = new Texture(Gdx.files.internal("libgdx.png"));
        Texture projectileTexture = new Texture(Gdx.files.internal("libgdx.png"));
        Car = new car(world,camera,fixtureDef, wheelFixtureDef, rwheelFixtureDef, 1f, 3f, 1f, .5f,turretTexture,projectileTexture);
        Gdx.input.setInputProcessor(new InputMultiplexer(Car));


    }
    public void handleInput(float dt){

    }

    public void update(float delta){
        world.step(1/60f,6,2);
        batch.setProjectionMatrix(camera.combined);

        cameraUpdate();
        renderer.setView(camera);
        //player.update();
        if (Car != null) {
            Car.update(delta);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    private void cameraUpdate(){
        camera.position.set(640, 360, 0);
        camera.update();
    }




    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        batch.begin();
        if (Car != null) {
            Car.render(batch);
        }
        batch.end();
        this.update(v);
        dR.render(world, camera.combined.cpy().scl(PPM));
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public World getWorld() {
        return world;
    }
    //public void setPlayer(player player) {
        //this.player = player;
    //}

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

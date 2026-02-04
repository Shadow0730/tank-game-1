package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.PvsS.helpers.map;
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
    }

    @Override
    public void show() {
        FixtureDef fixtureDef = new FixtureDef();
        FixtureDef wheelFixtureDef = new FixtureDef();
        fixtureDef.density = 5;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.3f;

        wheelFixtureDef.density = fixtureDef.density - 0.5f;
        wheelFixtureDef.friction = 1;
        wheelFixtureDef.restitution = 0.4f;
        Car = new car(world,fixtureDef, wheelFixtureDef, 1f, 2f, 0.5f, 0.5f);
        // Top-left corner
        createTestBox(world, 1f, 6.2f);
        // Top-right corner
        createTestBox(world, 11.8f, 6.2f);
        // Bottom-left corner
        createTestBox(world, 1f, 1f);
        // Bottom-right corner
        createTestBox(world, 11.8f, 1f);
        // Center
        createTestBox(world, 6.4f, 3.6f);


    }
    public void handleInput(float dt){

    }

    public void update(){
        world.step(1/60f,6,2);
        batch.setProjectionMatrix(camera.combined);
        cameraUpdate();
        renderer.setView(camera);
        //player.update();
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    private void cameraUpdate(){

        camera.position.set(640, 360, 0);
        camera.update();
    }
    private void createTestBox(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(0.5f, 0.5f);
        body.createFixture(box, 0f);
        box.dispose();

        System.out.println("Test box at: " + x + ", " + y + " (pixels: " + (x*100) + ", " + (y*100) + ")");
    }



    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        batch.begin();
        batch.end();
        this.update();
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

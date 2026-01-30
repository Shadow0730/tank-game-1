package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.PvsS.helpers.map;
import com.mygdx.PvsS.tankgame;

import java.awt.*;

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

    public GameScreen(tankgame game, OrthographicCamera camera) {

        this.camera = camera;
        this.game = game;
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map.tmx");
        world = new World(new Vector2(0,0), true);
        dR = new Box2DDebugRenderer();
        this.batch = game.batch;
        this.tileMapHelper = new map(this);
        renderer = tileMapHelper.setupMap();
    }

    @Override
    public void show() {


    }
    public void handleInput(float dt){

    }

    public void update(){
        world.step(1/60f,6,2);
        batch.setProjectionMatrix(camera.combined);
        cameraUpdate();
        renderer.setView(camera);
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    private void cameraUpdate(){

        camera.setToOrtho(false, 1280, 720);
        camera.update();
    }



    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        batch.begin();
        batch.end();
        this.update();
        dR.render(world, camera.combined.scl(PPM));
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

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

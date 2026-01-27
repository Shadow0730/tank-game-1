package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.PvsS.tankgame;

public class GameScreen implements Screen {
    private tankgame game;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Viewport gamePort;
    private OrthographicCamera camera;

    public GameScreen(tankgame game) {
        camera = new OrthographicCamera();
        gamePort = new FitViewport(1280/2, 720/2, camera);
        this.game = game;
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map.tmx");
        camera.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    @Override
    public void show() {

    }
    public void handleInput(float dt){

    }

    public void update(float dt){
        handleInput(dt);
        renderer.setView(camera);
    }

    @Override
    public void render(float v) {
        update(v);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);
        renderer.render();
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

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

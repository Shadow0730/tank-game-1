package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.PvsS.tankgame;


public class MainMenuScreen implements Screen {
    tankgame game;
    public static final int width = 1280;
    public static final int height = 720;
    public static final int Bwidth = 50;
    public static final int Bheight = 50;
    public static final int pBwidth = 200;
    public static final int pBheight = 150;

    private OrthographicCamera camera;

    Texture exitButtonactive;
    Texture playButtonactive;
    Texture background;

    Rectangle playButtonBounds;
    Rectangle exitButtonBounds;
    public MainMenuScreen(tankgame game,  OrthographicCamera camera) {
        this.game = game;
        exitButtonactive = new Texture("exitButton.jpg");
        playButtonactive = new Texture("playButton.png");
        background = new Texture("background.jpg");
        int x = (width - pBwidth) / 2;
        int y = (height - pBheight) / 2;
        this.camera = camera;

        playButtonBounds = new Rectangle(x, y, pBwidth, pBheight);
        exitButtonBounds = new Rectangle(width-100,height-100, Bwidth, Bheight);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, width, height);

        int x=(width-pBwidth)/2;
        int y=(height-pBheight)/2;
        float mouseY = Gdx.graphics. getHeight() - Gdx.input.getY();
        float mouseX = Gdx.input. getX();

        if (playButtonBounds.contains(Gdx.input.getX(), mouseY)) {
            game.batch.draw(playButtonactive, x, y, pBwidth + 20, pBheight + 20);
            if (Gdx.input.isTouched()) {
                this.dispose();
                game.setScreen(new GameScreen(game, camera));
            }
        } else {
            game.batch.draw(playButtonactive, x, y, pBwidth, pBheight);
        }
        if (exitButtonBounds.contains(mouseX, mouseY)) {
            game.batch.draw(exitButtonactive, width-100,height-100, Bwidth+10, Bheight+10);
            if  (Gdx.input.isTouched()) {
                Gdx.app.exit();
            }
        } else {
            game.batch. draw(exitButtonactive, width-100,height-100, Bwidth, Bheight);
        }
        game.batch.end();
    }

    @Override
    public void resize(int i, int i1) {

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

package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.PvsS.helpers.SaveGameManager;
import com.mygdx.PvsS.tankgame;


public class MainMenuScreen implements Screen {
    tankgame game;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final int EXIT_BUTTON_SIZE = 50;

    public static final int START_BUTTON_WIDTH = 200;
    public static final int START_BUTTON_HEIGHT = 150;

    public static final int LOAD_BUTTON_WIDTH = 200;
    public static final int LOAD_BUTTON_HEIGHT = 80;

    public static final int MENU_CENTER_X = WIDTH / 2;
    public static final int START_BUTTON_X = MENU_CENTER_X - START_BUTTON_WIDTH / 2;
    public static final int START_BUTTON_Y = 330;

    public static final int LOAD_BUTTON_X = MENU_CENTER_X - LOAD_BUTTON_WIDTH / 2;
    public static final int LOAD_BUTTON_Y = 230;

    public static final int EXIT_BUTTON_X = MENU_CENTER_X - EXIT_BUTTON_SIZE / 2;
    public static final int EXIT_BUTTON_Y = 130;

    private OrthographicCamera camera;

    private Texture exitButtonactive;
    private Texture playButtonactive;
    private Texture loadButton;
    private Texture background;

    private Rectangle playButtonBounds;
    private Rectangle exitButtonBounds;
    private Rectangle loadButtonBounds;
    private BitmapFont font;
    public MainMenuScreen(tankgame game,  OrthographicCamera camera) {
        this.game = game;
        exitButtonactive = new Texture("ui/exit.png");
        playButtonactive = new Texture("playButton.png");
        loadButton = new Texture("ui/load.png");
        background = new Texture("background.jpg");
        this.camera = camera;

        playButtonBounds = new Rectangle(
            START_BUTTON_X,
            START_BUTTON_Y,
            START_BUTTON_WIDTH,
            START_BUTTON_HEIGHT
        );

        loadButtonBounds = new Rectangle(
            LOAD_BUTTON_X,
            LOAD_BUTTON_Y,
            LOAD_BUTTON_WIDTH,
            LOAD_BUTTON_HEIGHT
        );

        exitButtonBounds = new Rectangle(
            EXIT_BUTTON_X,
            EXIT_BUTTON_Y,
            EXIT_BUTTON_SIZE,
            EXIT_BUTTON_SIZE
        );
        font = new BitmapFont();
    }
    @Override
    public void show() {

    }//find use

    @Override
    public void render(float v) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, WIDTH, HEIGHT);
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float mouseX = Gdx.input.getX();
        //replace using button
        if (playButtonBounds.contains(mouseX, mouseY)) {
            game.batch.draw(playButtonactive,
                playButtonBounds.x - 10,
                playButtonBounds.y - 10,
                playButtonBounds.width + 20,
                playButtonBounds.height + 20
            );

            if (Gdx.input.isTouched()) {
                this.dispose();
                game.setScreen(new GameScreen(game, camera, false));
            }
        } else {
            game.batch.draw(playButtonactive,
                playButtonBounds.x,
                playButtonBounds.y,
                playButtonBounds.width,
                playButtonBounds.height
            );
        }
        if (loadButtonBounds.contains(mouseX, mouseY)) {
            game.batch.draw(loadButton,
                loadButtonBounds.x - 5,
                loadButtonBounds.y - 5,
                loadButtonBounds.width + 10,
                loadButtonBounds.height + 10
            );

            if (Gdx.input.isTouched()) {
                if (SaveGameManager.hasSaveFile()) {
                    this.dispose();
                    game.setScreen(new GameScreen(game, camera, true));
                } else {
                    System.out.println("No save file found. Start a new game first.");
                }
            }
        } else {
            game.batch.draw(loadButton,
                loadButtonBounds.x,
                loadButtonBounds.y,
                loadButtonBounds.width,
                loadButtonBounds.height
            );
        }
        if (exitButtonBounds.contains(mouseX, mouseY)) {
            game.batch.draw(exitButtonactive,
                exitButtonBounds.x - 5,
                exitButtonBounds.y - 5,
                exitButtonBounds.width + 10,
                exitButtonBounds.height + 10
            );

            if (Gdx.input.isTouched()) {
                Gdx.app.exit();
            }
        } else {
            game.batch.draw(exitButtonactive,
                exitButtonBounds.x,
                exitButtonBounds.y,
                exitButtonBounds.width,
                exitButtonBounds.height
            );
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
        exitButtonactive.dispose();
        playButtonactive.dispose();
        background.dispose();
        font.dispose();
    }
}

package com.mygdx.PvsS.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.PvsS.tankgame;

public class endGameScreen implements Screen {
    private tankgame game;
    private String winner;
    private Texture background;
    private Stage stage;
    private Skin skin;
    private BitmapFont font;

    public static final int width = 1280;
    public static final int height = 720;

    public endGameScreen(tankgame game, String winner) {
        this.game = game;
        this.winner = winner;
        this.background = new Texture("background.jpg");

        // Create stage for UI elements
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create font
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Create skin
        skin = new Skin();
        skin.add("default-font", font);

        // Create button textures
        Texture buttonUpTexture = new Texture("libgdx.png");
        Texture buttonDownTexture = new Texture("libgdx.png");

        // Create button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new com.badlogic.gdx.graphics.Color(1, 1, 1, 1);
        buttonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
            new com.badlogic.gdx.graphics.g2d.TextureRegion(buttonUpTexture));
        buttonStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
            new com.badlogic.gdx.graphics.g2d.TextureRegion(buttonDownTexture));

        skin.add("default", buttonStyle);

        // Create RESTART button
        TextButton restartButton = new TextButton("RESTART", skin);
        restartButton.setSize(200, 80);
        restartButton.setPosition((width / 4) - 100, (height / 2) - 40);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
            }
        });
        stage.addActor(restartButton);

        // Create MAIN MENU button
        TextButton menuButton = new TextButton("MAIN MENU", skin);
        menuButton.setSize(200, 80);
        menuButton.setPosition((3 * width / 4) - 100, (height / 2) - 40);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToMainMenu();
            }
        });
        stage.addActor(menuButton);
    }

    private void restartGame() {
        this.dispose();
        OrthographicCamera camera = new OrthographicCamera();
        game.setScreen(new GameScreen(game, camera, true));
    }

    private void goToMainMenu() {
        this.dispose();
        OrthographicCamera camera = new OrthographicCamera();
        game.setScreen(new MainMenuScreen(game, camera));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, width, height);

        // Draw winner text
        font.draw(game.batch, winner + " WINS!", width / 2 - 100, height - 150);

        game.batch.end();

        // Draw stage (buttons)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
        skin.dispose();
        font.dispose();
    }
}

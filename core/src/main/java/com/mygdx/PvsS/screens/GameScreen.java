package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.PvsS.helpers.*;
import com.mygdx.PvsS.tankgame;
import com.mygdx.PvsS.tanks.car;


import java.util.ArrayList;

import static com.mygdx.PvsS.helpers.constants.PPM;

public class GameScreen implements Screen {
    private final boolean isLoadingGame;
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
    private ArrayList<powerup> powerups;

    //private player player;
    private car Car;
    private car Car2;
    private int currentPlayerIndex = 0;  // 0 = Car, 1 = Car2
    private boolean turnEnded = false;

    // UI Elements for bullet selection
    private Stage stage;
    private Skin skin;
    private BitmapFont font;
    private TextButton bulletButton1;
    private TextButton bulletButton2;
    private TextButton bulletButton3;
    private TextButton powerUpButton;
    private TextButton powerDownButton;

    public GameScreen(tankgame game, OrthographicCamera camera, boolean loadGame) {

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
        initializeUI();
        this.isLoadingGame = loadGame;
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        skin = new Skin();
        skin.add("default-font", font);

        // Create button style
        Texture buttonUpTexture = new Texture("libgdx.png");
        Texture buttonDownTexture = new Texture("libgdx.png");

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new com.badlogic.gdx.graphics.Color(1, 1, 1, 1);
        buttonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
            new com.badlogic.gdx.graphics.g2d.TextureRegion(buttonUpTexture));
        buttonStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
            new com.badlogic.gdx.graphics.g2d.TextureRegion(buttonDownTexture));

        skin.add("default", buttonStyle);

        // Bullet selection buttons (top left area)
        bulletButton1 = new TextButton("Normal", skin);
        bulletButton1.setSize(120, 40);
        bulletButton1.setPosition(10, 720 - 50);
        bulletButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectBulletType(0);
            }
        });
        stage.addActor(bulletButton1);

        bulletButton2 = new TextButton("Explosive", skin);
        bulletButton2.setSize(120, 40);
        bulletButton2.setPosition(135, 720 - 50);
        bulletButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectBulletType(1);
            }
        });
        stage.addActor(bulletButton2);

        bulletButton3 = new TextButton("Piercing", skin);
        bulletButton3.setSize(120, 40);
        bulletButton3.setPosition(260, 720 - 50);
        bulletButton3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectBulletType(2);
            }
        });
        stage.addActor(bulletButton3);

        // Power adjustment buttons (top right area)
        powerDownButton = new TextButton("Power -", skin);
        powerDownButton.setSize(100, 40);
        powerDownButton.setPosition(1280 - 210, 720 - 50);
        powerDownButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                decreasePower();
            }
        });
        stage.addActor(powerDownButton);

        powerUpButton = new TextButton("Power +", skin);
        powerUpButton.setSize(100, 40);
        powerUpButton.setPosition(1280 - 105, 720 - 50);
        powerUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                increasePower();
            }
        });
        stage.addActor(powerUpButton);
    }

    private void selectBulletType(int type) {
        if (Car.isActive()) {
            Car.setBulletType(type);
            System.out.println("Player 1 selected: " + Car.getBulletName(type));
        } else if (Car2.isActive()) {
            Car2.setBulletType(type);
            System.out.println("Player 2 selected: " + Car2.getBulletName(type));
        }
    }

    private void increasePower() {
        if (Car.isActive()) {
            Car.increasePower();
        } else if (Car2.isActive()) {
            Car2.increasePower();
        }
    }

    private void decreasePower() {
        if (Car.isActive()) {
            Car.decreasePower();
        } else if (Car2.isActive()) {
            Car2.decreasePower();
        }
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
        Car2 = new car(world,camera,fixtureDef, wheelFixtureDef, rwheelFixtureDef, 5f, 3f, 1f, .5f,turretTexture,projectileTexture);
        Car.setActive(true);   // Player 1 starts
        Car2.setActive(false);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, Car, Car2));
        this.powerups = new ArrayList<>();
        spawnRandomPowerups();

        if (isLoadingGame) {
            loadGameState();
        }


    }
    private void spawnRandomPowerups() {
        Texture powerupTexture = new Texture(Gdx.files.internal("libgdx.png"));

        // Spawn 3 random powerups on the map
        for (int i = 0; i < 3; i++) {
            float randomX = 200 + (float)Math.random() * 800;
            float randomY = 200 + (float)Math.random() * 400;
            powerup.PowerupType[] types = powerup.PowerupType.values();
            powerup p = new powerup(world, randomX, randomY,
                types[(int)(Math.random() * types.length)],
                powerupTexture);
            powerups.add(p);
        }
    }
    public void handleInput(float dt){

    }

    public void update(float delta){
        world.step(1/60f,6,2);
        batch.setProjectionMatrix(camera.combined);

        cameraUpdate();
        renderer.setView(camera);
        //player.update();
        if (Car != null && !Car.isDestroyed()) {
            Car.update(delta);
        }
        if (Car2 != null && !Car2.isDestroyed()) {
            Car2.update(delta);
        }
        // Update powerups
        for (int i = powerups.size() - 1; i >= 0; i--) {
            powerup p = powerups.get(i);
            p.update(delta);
            // Check if powerup was destroyed by bullet collision
            if (p.getBody().getUserData() != null && p.getBody().getUserData().equals("destroy")) {
                p.collect();
            }
            if (p.isCollected()) {
                world.destroyBody(p.getBody());
                powerups.remove(i);
                System.out.println("Powerup destroyed by bullet!");
            }
        }

        checkGameOver();
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            switchTurn();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
        // Draw UI stage
        stage.act(delta);
        stage.draw();
    }

    private void cameraUpdate(){
        camera.position.set(640, 360, 0);
        camera.update();
    }
    private void checkGameOver() {
        if (Car != null && Car.isDestroyed()) {
            System.out.println("GAME OVER! Player 2 wins!");
            this.dispose();
            game.setScreen(new endGameScreen(game, "Player 2"));
        }
        if (Car2 != null && Car2.isDestroyed()) {
            System.out.println("GAME OVER! Player 1 wins!");
            this.dispose();
            game.setScreen(new endGameScreen(game, "Player 1"));
        }
    }
    private void saveGameState() {
        gamesavedata data = new gamesavedata();

        // Save Player 1
        data.player1 = new gamesavedata.Player1Data();
        data.player1.posX = Car.getChassis().getPosition().x;
        data.player1.posY = Car.getChassis().getPosition().y;
        data.player1.health = Car.getCurrentHP();

        // Save Player 2
        data.player2 = new gamesavedata.Player2Data();
        data.player2.posX = Car2.getChassis().getPosition().x;
        data.player2.posY = Car2.getChassis().getPosition().y;
        data.player2.health = Car2.getCurrentHP();

        // Save current turn
        data.currentPlayerIndex = currentPlayerIndex;

        SaveGameManager.saveGame(data);
    }

    private void loadGameState() {
        gamesavedata data = SaveGameManager.loadGame();
        if (data != null) {
            // Load Player 1
            Car.setPosition(data.player1.posX, data.player1.posY);
            Car.setHealth(data.player1.health);

            // Load Player 2
            Car2.setPosition(data.player2.posX, data.player2.posY);
            Car2.setHealth(data.player2.health);

            // Load current turn
            currentPlayerIndex = data.currentPlayerIndex;
            Car.setActive(currentPlayerIndex == 0);
            Car2.setActive(currentPlayerIndex == 1);

            System.out.println("Game state loaded!");
        }
    }




    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        batch.begin();
        if (Car != null && !Car.isDestroyed()) {
            Car.render(batch);
        }
        if (Car2 != null && !Car2.isDestroyed()) {
            Car2.render(batch);
        }
        for (powerup p : powerups) {
            p.render(batch);
        }
        batch.end();
        this.update(v);
        dR.render(world, camera.combined.cpy().scl(PPM));
    }
    private void switchTurn() {
        // Deactivate current player
        if (currentPlayerIndex == 0) {
            Car.setActive(false);
            Car2.setActive(true);
        } else {
            Car.setActive(true);
            Car2.setActive(false);
        }
        currentPlayerIndex = 1 - currentPlayerIndex;
        System.out.println("Player " + (currentPlayerIndex + 1) + "'s turn!");
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

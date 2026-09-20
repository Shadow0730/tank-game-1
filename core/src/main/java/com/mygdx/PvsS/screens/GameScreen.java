package com.mygdx.PvsS.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.PvsS.helpers.*;
import com.mygdx.PvsS.tankgame;
import com.mygdx.PvsS.tanks.car;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;


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

    private car Car;
    private car Car2;
    private int currentPlayerIndex = 0;
    private boolean turnEnded = false;

    // UI Elements for bullet selection
    private Stage stage;
    private Skin skin;
    private BitmapFont font;
    private ImageButton bulletButton1;
    private ImageButton bulletButton2;
    private ImageButton bulletButton3;

    private Texture normalBulletIcon;
    private Texture explosiveBulletIcon;
    private Texture piercingBulletIcon;
    private int selectedBulletType = 0;
    private static final float BULLET_BUTTON_SIZE = 80;
    private static final float SELECTED_BULLET_BUTTON_SIZE = 100;
    private static final float BULLET_BUTTON_Y = 45;

    private Slider powerSlider;
    private Label powerLabel;
    private TextButton menuButton;
    private Table pauseMenu;

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
        Texture buttonUpTexture = new Texture("button.png");
        Texture buttonDownTexture = new Texture("button.png");

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new com.badlogic.gdx.graphics.Color(1, 1, 1, 1);
        buttonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
            new com.badlogic.gdx.graphics.g2d.TextureRegion(buttonUpTexture));
        buttonStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
            new com.badlogic.gdx.graphics.g2d.TextureRegion(buttonDownTexture));

        skin.add("default", buttonStyle);

        normalBulletIcon = new Texture(Gdx.files.internal("bullet_icons/bullet_normal.png"));
        explosiveBulletIcon = new Texture(Gdx.files.internal("bullet_icons/bullet_explosive.png"));
        piercingBulletIcon = new Texture(Gdx.files.internal("bullet_icons/bullet_piercing.png"));

        bulletButton1 = addBulletButton(normalBulletIcon, "Normal", 0, 10);
        bulletButton2 = addBulletButton(explosiveBulletIcon, "Explosive", 1, 110);
        bulletButton3 = addBulletButton(piercingBulletIcon, "Piercing", 2, 210);

        updateBulletSelectionVisuals();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        powerLabel = new Label("Power: 30", labelStyle);
        powerLabel.setPosition(900, 110);
        stage.addActor(powerLabel);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();

        Texture sliderBackgroundTexture = new Texture(Gdx.files.internal("ui/slider_bar.png"));
        Texture sliderKnobTexture = new Texture(Gdx.files.internal("ui/slider_knob.png"));

        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBackgroundTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnobTexture));

        powerSlider = new Slider(0, 100, 5, false, sliderStyle);
        powerSlider.setValue(30);
        powerSlider.setSize(350, 30);
        powerSlider.setPosition(900, 70);

        powerSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = powerSlider.getValue();

                if (Car != null && Car.isActive()) {
                    Car.setPower(value);
                } else if (Car2 != null && Car2.isActive()) {
                    Car2.setPower(value);
                }

                powerLabel.setText("Power: " + (int)value);
            }
        });

        stage.addActor(powerSlider);

        menuButton = new TextButton("Menu", skin);
        menuButton.setSize(100, 40);
        menuButton.setPosition(520, 720 - 50);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseMenu.setVisible(!pauseMenu.isVisible());
            }
        });
        stage.addActor(menuButton);

        createPauseMenu();
        updateBulletSelectionVisuals();
    }

    private void createPauseMenu() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        Label titleLabel = new Label("Game Menu", labelStyle);

        TextButton menuSaveButton = new TextButton("Save", skin);
        menuSaveButton.setSize(180, 50);
        menuSaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGameState();
                pauseMenu.setVisible(false);
            }
        });

        TextButton menuExitButton = new TextButton("Exit", skin);
        menuExitButton.setSize(180, 50);
        menuExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new MainMenuScreen(game, camera));
            }
        });

        pauseMenu = new Table();
        pauseMenu.setSize(260, 200);
        pauseMenu.setPosition(510, 260);
        pauseMenu.setVisible(false);
        pauseMenu.add(titleLabel).padBottom(20).row();
        pauseMenu.add(menuSaveButton).width(180).height(50).padBottom(15).row();
        pauseMenu.add(menuExitButton).width(180).height(50);

        stage.addActor(pauseMenu);
    }

    private void createScreenWalls() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // Left wall
        bodyDef.position.set(0, 3.6f);
        Body leftWall = world.createBody(bodyDef);
        shape.setAsBox(0.0f, 3.6f);
        fixtureDef.shape = shape;
        leftWall.createFixture(fixtureDef);

        // Right wall
        bodyDef.position.set(12.8f, 3.6f);
        Body rightWall = world.createBody(bodyDef);
        rightWall.createFixture(fixtureDef);

        shape.dispose();
    }
    private void selectBulletType(int type) {
        selectedBulletType = type;
        updateSelectedBulletButtonSize();

        if (Car.isActive()) {
            Car.setBulletType(type);
            System.out.println("Player 1 selected: " + Car.getBulletName(type));
        } else if (Car2.isActive()) {
            Car2.setBulletType(type);
            System.out.println("Player 2 selected: " + Car2.getBulletName(type));
        }
    }
    private void updateSelectedBulletButtonSize() {
        resizeBulletButton(bulletButton1, 0);
        resizeBulletButton(bulletButton2, 1);
        resizeBulletButton(bulletButton3, 2);
    }
    private void resizeBulletButton(ImageButton button, int bulletType) {
        if (button == null) return;

        float size = bulletType == selectedBulletType
            ? SELECTED_BULLET_BUTTON_SIZE
            : BULLET_BUTTON_SIZE;

        float oldCenterX = button.getX() + button.getWidth() / 2;
        float oldCenterY = button.getY() + button.getHeight() / 2;

        button.setSize(size, size);
        button.setPosition(
            oldCenterX - size / 2,
            oldCenterY - size / 2
        );
    }

    private ImageButton addBulletButton(Texture iconTexture, String description, final int bulletType, float x) {
        float buttonY = BULLET_BUTTON_Y;

        ImageButton button = new ImageButton(
            new TextureRegionDrawable(new TextureRegion(iconTexture))
        );

        button.setSize(BULLET_BUTTON_SIZE, BULLET_BUTTON_SIZE);
        button.setPosition(x, buttonY);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectBulletType(bulletType);
            }
        });

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        Label label = new Label(description, labelStyle);
        label.setPosition(x + 5, 15);
        label.setSize(BULLET_BUTTON_SIZE, 25);

        stage.addActor(button);
        stage.addActor(label);

        return button;
    }

    @Override
    public void show() {
        Car = new car(world,camera, 1f, 3f);
        Car2 = new car(world,camera, 5f, 3f);
        Car.setActive(true);   // Player 1 starts
        Car2.setActive(false);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, Car, Car2));
        this.powerups = new ArrayList<>();
        spawnRandomPowerups();
        createScreenWalls();

        if (isLoadingGame) {
            loadGameState();
        }


    }
    private void spawnRandomPowerups() {
        Texture healthCrate = new Texture(Gdx.files.internal("powerups/health_crate.png"));
        Texture damageCrate = new Texture(Gdx.files.internal("powerups/damage_crate.png"));

        // Spawn 3 random powerups on the map
        for (int i = 0; i < 3; i++) {
            float randomX = 200 + (float)Math.random() * 800;
            float randomY = 350 + (float)Math.random() * 400;
            powerup.PowerupType[] types = powerup.PowerupType.values();
            powerup.PowerupType type = types[(int)(Math.random() * types.length)];
            Texture selectedTexture;
            if (type == powerup.PowerupType.HEALTH_BOOST) {
                selectedTexture = healthCrate;
            } else {
                selectedTexture = damageCrate;
            }
            powerup p = new powerup(world, randomX, randomY, type, selectedTexture);
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
//        dR.render(world, camera.combined.cpy().scl(PPM));

        stage.act(v);
        stage.draw();
    }
    private void updateBulletSelectionVisuals() {
        bulletButton1.setColor(selectedBulletType == 0 ? Color.YELLOW : Color.WHITE);
        bulletButton2.setColor(selectedBulletType == 1 ? Color.YELLOW : Color.WHITE);
        bulletButton3.setColor(selectedBulletType == 2 ? Color.YELLOW : Color.WHITE);
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
        float activePower = currentPlayerIndex == 0 ? Car.getPower() : Car2.getPower();
        powerSlider.setValue(activePower);
        powerLabel.setText("Power: " + (int)activePower);

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
        if (normalBulletIcon != null) normalBulletIcon.dispose();
        if (explosiveBulletIcon != null) explosiveBulletIcon.dispose();
        if (piercingBulletIcon != null) piercingBulletIcon.dispose();

        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (font != null) font.dispose();
    }
}

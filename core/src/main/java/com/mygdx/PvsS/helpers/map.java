package com.mygdx.PvsS.helpers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.PvsS.screens.GameScreen;

import static com.mygdx.PvsS.helpers.constants.PPM;

public class map {

    private TiledMap tiledMap;
    private GameScreen gameScreen;

    public map(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMap(){
        tiledMap = new TmxMapLoader().load("map1.tmx");
        parseMapObjects(tiledMap.getLayers().get("ground").getObjects());
        return  new OrthogonalTiledMapRenderer(tiledMap);
    }
    private void parseMapObjects(MapObjects mapObjects){
        for (MapObject mapObject : mapObjects){
            if (mapObject instanceof PolygonMapObject){
                createstaticbody((PolygonMapObject) mapObject);
            }
//            if (mapObject instanceof RectangleMapObject){
//                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
//                String rectangleName = mapObject.getName();
//
//                if(rectangleName != null && rectangleName.equals("player")){
//                    float centerX = rectangle.getX() + rectangle.getWidth() / 2;
//                    float centerY = rectangle.getY() + rectangle.getHeight() / 2;
//
//                    // Create tank body (main chassis)
//                    Body tankBody = createTankBody(centerX, centerY, rectangle.getWidth(), rectangle.getHeight());
//
//                    // Create left wheel
//                    Body leftWheel = createWheel(centerX - rectangle.getWidth()/3, centerY - rectangle.getHeight()/2,1.0f);
//
//                    // Create right wheel
//                    Body rightWheel = createWheel(centerX + rectangle.getWidth()/3, centerY - rectangle.getHeight()/2,4.0f);
//
//                    // Connect wheels to tank with revolute joints (with motors)
//                    RevoluteJoint leftJoint = createWheelJoint(tankBody, leftWheel, true);
//                    RevoluteJoint rightJoint = createWheelJoint(tankBody, rightWheel, true);
//
//                    // Create player with tank and joints
//                    player newPlayer = new player(rectangle.getWidth(), rectangle.getHeight(), tankBody, gameScreen.getWorld());
//                    newPlayer.setWheelJoints(leftJoint, rightJoint);
//                    gameScreen.setPlayer(newPlayer);
//                }
//            }
        }
    }


    private void createstaticbody(PolygonMapObject polygonMapObject){
        BodyDef bodydef =new BodyDef();
        bodydef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodydef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.setUserData("ground");
        body.createFixture(shape,1000);
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject){
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < vertices.length / 2; i++){
            Vector2 current = new Vector2(vertices[i * 2]/PPM, vertices[i * 2 + 1]/PPM);
            worldVertices[i] = current;

        }
        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }
}

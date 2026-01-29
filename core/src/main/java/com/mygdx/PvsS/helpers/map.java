package com.mygdx.PvsS.helpers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class map {

    private TiledMap tiledMap;

    public map(){

    }

    public OrthogonalTiledMapRenderer setupMap(){
        tiledMap = new TmxMapLoader().load("map.tmx");
        return  new OrthogonalTiledMapRenderer(tiledMap);
    }
}

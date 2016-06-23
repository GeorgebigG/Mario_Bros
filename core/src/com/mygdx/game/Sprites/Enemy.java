package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by gio on 09/04/16.
 */
public abstract class Enemy extends Sprite {
    protected World world;
    protected TiledMap map;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {
        world = screen.getWorld();
        map = screen.getMap();
        this.screen = screen;
        defineEnemy(x, y);
        velocity = new Vector2(1, 0);
        b2body.setActive(false);
    }

    public void disGrow() {
        screen.player.disGrow();
    }

    protected abstract void defineEnemy(float x, float y);

    public abstract void update(float dt);

    public abstract void hitOnHead();

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}

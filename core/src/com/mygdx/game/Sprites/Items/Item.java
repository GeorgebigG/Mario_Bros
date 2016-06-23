package com.mygdx.game.Sprites.Items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.screens.PlayScreen;


/**
 * Created by gio on 10/04/16.
 */
public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean setToDestroy;
    protected boolean Destroyed;
    protected Body b2body;

    public Item(PlayScreen screen, float x, float y) {
        this.screen = screen;
        world = screen.getWorld();
        setToDestroy = false;
        Destroyed = false;
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        defineItem(x, y);
    }

    public abstract void defineItem(float x, float y);
    public abstract void use();

    public void update(float dt) {
        if (setToDestroy && !Destroyed) {
            world.destroyBody(b2body);
            Destroyed = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!Destroyed)
            super.draw(batch);
    }

    public void destroy() {
        setToDestroy = true;
    }

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}

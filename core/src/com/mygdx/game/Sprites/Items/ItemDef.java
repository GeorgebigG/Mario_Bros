package com.mygdx.game.Sprites.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by gio on 10/04/16.
 */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}

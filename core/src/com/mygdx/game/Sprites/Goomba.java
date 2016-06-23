package com.mygdx.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by gio on 09/04/16.
 */
public class Goomba extends Enemy {
    private float stateTimer;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean Destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        walkAnimation = new Animation(0.4f, frames);
        stateTimer = 0;
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setToDestroy = false;
        Destroyed = false;
    }

    public void update(float dt) {
        stateTimer += dt;
        if (setToDestroy && !Destroyed) {
            Hud.addScore(500);
            world.destroyBody(b2body);
            Destroyed = true;
            setRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16);
            stateTimer = 0;
            MarioBros.manager.get("audio/sounds/stomp.wav", Sound.class).play();
        } else if (!setToDestroy && !Destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTimer, true));
        }
    }

    @Override
    protected void defineEnemy(float x, float y) {
        BodyDef bDef = new BodyDef();
        bDef.position.set(x, y);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.MARIO_BIT |
                MarioBros.OBJECT_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0]= new Vector2(-4, 11).scl(1 / MarioBros.PPM);
        vertice[1] = new Vector2(4, 11).scl(1 / MarioBros.PPM);
        vertice[2] = new Vector2(-4, 7).scl(1 / MarioBros.PPM);
        vertice[3] = new Vector2( 4, 7).scl(1 / MarioBros.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch) {
        if (!Destroyed || stateTimer < 1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
    }
}
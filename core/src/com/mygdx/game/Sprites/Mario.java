package com.mygdx.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by gio on 07/04/16.
 */
public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DISGROWING, DIE }
    public State currentState;
    public State previousState;

    private float stateTimer;
    private boolean runningRight;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDied;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;

    private Animation bigMarioRun;
    private Animation growMario;
    private Animation disGrowMario;

    public boolean marioIsBig;
    private boolean runGrownAnimation = false;
    private boolean runDisGrownAnimation = false;
    private boolean timeToDefineBigMario = false;
    private boolean timeToDefineLittleMario = false;
    public boolean marioDie = false;
    public boolean goToGameOverScreen = false;
    public boolean first = true;

    public Mario(PlayScreen screen) {
        this.world = screen.getWorld();

        currentState = State.STANDING;
        previousState = State.STANDING;

        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1f, frames);

        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);

        marioDied = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 6 * 16, 0, 16, 16);

        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        frames.clear();
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);

        //creating mario location
        defineMario();

        //set growing mario animation
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);

        //set disgrowing animation
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        disGrowMario = new Animation(0.2f, frames);

        //creating mario picture
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        setBounds(0 / MarioBros.PPM, 0 / MarioBros.PPM, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);
    }

    public void update(float dt) {
        if (b2body.getPosition().y <= 0 - 32) {
            marioDie = true;

            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).stop();
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
        }
        if (marioDie) {
            if (first) {
                timeToDefineDiedMario();
                first = false;
            } else {
                goToGameOverScreen = true;
            }

            if (b2body.getPosition().y <= 0 - marioDied.getRegionHeight())
                goToGameOverScreen = true;
        }
        if (marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));

        if (timeToDefineBigMario)
            defineBigMario();
        else if (timeToDefineLittleMario)
            defineLittleMario();
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DIE:
                region = marioDied;
                break;
            case DISGROWING:
                region = disGrowMario.getKeyFrame(stateTimer);
                if (disGrowMario.isAnimationFinished(stateTimer)) {
                    runDisGrownAnimation = false;
                }
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrownAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            case FALLING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;

        return region;
    }

    public State getState() {
        if (marioDie)
            return State.DIE;
        else if (runDisGrownAnimation)
            return State.DISGROWING;
        else if (runGrownAnimation)
            return State.GROWING;
        else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else  if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void defineMario() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.filter.maskBits = MarioBros.BRICK_BIT | MarioBros.COIN_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void defineBigMario() {
        Vector2 marioPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(marioPosition.add(0 / MarioBros.PPM, 10 / MarioBros.PPM));
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.filter.maskBits = MarioBros.BRICK_BIT | MarioBros.COIN_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToDefineBigMario = false;
    }

    public void grow() {
        if (!marioIsBig) {
            runGrownAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public void defineLittleMario() {
        Vector2 marioPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(marioPosition.add(0 / MarioBros.PPM, -10 / MarioBros.PPM));
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.filter.maskBits = MarioBros.BRICK_BIT | MarioBros.COIN_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToDefineLittleMario = false;
    }

    public void disGrow() {
        if (marioIsBig) {
            runDisGrownAnimation = true;
            marioIsBig = false;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            timeToDefineLittleMario = true;
        }
        else {
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).stop();
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioDie = true;
        }
    }

    public void timeToDefineDiedMario() {
        Vector2 marioPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(marioPosition);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.filter.maskBits = MarioBros.BRICK_BIT | MarioBros.COIN_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }
}

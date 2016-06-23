package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.screens.GameOverScreen;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by gio on 07/04/16.
 */
public class Hud implements Disposable {
    public Stage stage;
    public Viewport viewport;

    private Integer minutes;
    private Integer seconds;
    private float timeCount;
    public static Integer score;

    private Label countDownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label MarioLabel;

    private Button up, left, right;

    PlayScreen screen;

    public Hud(PlayScreen screen, SpriteBatch sb) {
        minutes = 1;
        seconds = 0;
        timeCount = 0;
        score = 0;

        this.screen = screen;

        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        if (seconds == 0)
            countDownLabel = new Label(minutes + ":00", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        else
            countDownLabel = new Label(minutes + ":" + seconds, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        MarioLabel = new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));


        //create move buttons
        up = new Button();
        left = new Button();
        right = new Button();

        up.setBounds(Gdx.graphics.getWidth() - 60, 10, 60, 20);
        left.setBounds(10, 10, 60, 20);
        right.setBounds(90, 10, 60, 20);

        table.add(MarioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);

        table.row();

        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();

        //table.bottom();

        //table.add(left).expandX().padBottom(10);

        stage.addActor(table);
    }

    public void update(float dt) {
        timeCount += dt;
        if (timeCount >= 1) {
            if (seconds == 0 && minutes == 0) {
                screen.music.stop();
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
                screen.game.setScreen(new GameOverScreen(screen.game, score));
            } else {
                if (seconds != 0)
                    seconds--;
                else  {
                    minutes--;
                    seconds = 59;
                }
                if (seconds > 9)
                    countDownLabel.setText(minutes + ":" + seconds);
                else
                    countDownLabel.setText(minutes + ":0" + seconds);
                timeCount = 0;
            }
        }
    }

    public static void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

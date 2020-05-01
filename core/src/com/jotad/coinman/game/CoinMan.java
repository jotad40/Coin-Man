package com.jotad.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import javax.swing.Renderer;

import static com.badlogic.gdx.math.Intersector.*;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] unicorn;
	int unicornState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int unicornY = 0;
	Rectangle unicornRectangle;
	BitmapFont font;
	Texture golpe;
	int score = 0;
	int gameState = 0;

	Random random;

	ArrayList<Integer> rainbowXs = new ArrayList<Integer>();
	ArrayList<Integer> rainbowYs = new ArrayList<Integer>();
	ArrayList<Rectangle> rainbowRectangles = new ArrayList<Rectangle>();
	Texture rainbow;
	int rainbowCount;

	ArrayList<Integer> obstacleXs = new ArrayList<Integer>();
	ArrayList<Integer> obstacleYs = new ArrayList<Integer>();
	ArrayList<Rectangle> obstacleRectangles = new ArrayList<Rectangle>();
	Texture obstacle;
	int obstacleCount;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("artboard.png");
		unicorn = new Texture[6];
		unicorn[0] = new Texture("unicornio.png");
		unicorn[1] = new Texture("unicornio1.png");
		unicorn[2] = new Texture("unicornio2.png");
		unicorn[3] = new Texture("unicornio3.png");
		unicorn[4] = new Texture("unicornio4.png");
		unicorn[5] = new Texture("unicornio5.png");

		unicornY = Gdx.graphics.getHeight() / 2;

		rainbow = new Texture("rainbow.png");
		obstacle = new Texture("obstacle.png");
		random = new Random();
		unicornRectangle = new Rectangle();

		golpe = new Texture("unicornio5golpe.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

	}

	public void makeRainbow() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		rainbowYs.add((int)height);
		rainbowXs.add(Gdx.graphics.getWidth());
	}

	public void makeObstacle() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		//obstacleXs.add(Gdx.graphics.getHeight());
		obstacleYs.add((int)height);
		obstacleXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState == 1){
			//Game is Live

			//Obstacle

			if (obstacleCount < 100){
				obstacleCount++;
			} else {
				obstacleCount = 0;
				makeObstacle();
			}

			obstacleRectangles.clear();
			for (int i=0;i < obstacleXs.size();i++){
				batch.draw(obstacle, obstacleXs.get(i), obstacleYs.get(i));
				obstacleXs.set(i,obstacleXs.get(i) -6);
				obstacleRectangles.add(new Rectangle(obstacleXs.get(i), obstacleYs.get(i), obstacle.getWidth(), obstacle.getHeight()));
			}

			//Rainbows
			if (rainbowCount < 100){
				rainbowCount++;
			} else {
				rainbowCount = 0;
				makeRainbow();
			}

			rainbowRectangles.clear();
			for (int i=0;i < rainbowXs.size();i++){
				batch.draw(rainbow, rainbowXs.get(i), rainbowYs.get(i));
				rainbowXs.set(i,rainbowXs.get(i) -4);
				rainbowRectangles.add(new Rectangle(rainbowXs.get(i), rainbowYs.get(i), rainbow.getWidth(), rainbow.getHeight()));

			}
			if (Gdx.input.justTouched()){
				velocity = -10;

			}

			if (pause < 6 ){
				pause++;
			}else {
				pause = 0;

				if (unicornState < 5 ){
					unicornState++;
				} else {
					unicornState = 0;
				}
			}

			velocity += gravity;
			unicornY -= velocity;

			if (unicornY <=120){
				unicornY = 120;
			}

		}else if (gameState == 0) {
			//Waiting to start
			if (Gdx.input.justTouched()){
				gameState=1;
			}
		} else if (gameState == 2) {
			//GAME OVER
			if (Gdx.input.justTouched()) {
				gameState = 1;
				unicornY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				rainbowXs.clear();
				rainbowYs.clear();
				rainbowRectangles.clear();
				rainbowCount = 0;
				obstacleXs.clear();
				obstacleYs.clear();
				obstacleRectangles.clear();
				obstacleCount = 0;
			}
		}

		if (gameState ==2){
			batch.draw(golpe, Gdx.graphics.getWidth() / 2 - unicorn[unicornState].getWidth() / 2, unicornY);
		}else {
			batch.draw(unicorn[unicornState], Gdx.graphics.getWidth() / 2 - unicorn[unicornState].getWidth() / 2, unicornY);
		}
		unicornRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - unicorn[unicornState].getWidth() / 2, unicornY, unicorn[unicornState].getWidth(), unicorn[unicornState].getHeight());

		for (int i=0; i < rainbowRectangles.size(); i++){
			if (Intersector.overlaps(unicornRectangle, rainbowRectangles.get(i))){
				score++;

				rainbowRectangles.remove(i);
				rainbowXs.remove(i);
				rainbowYs.remove(i);
				break;
			}
		}

		for (int i=0; i < obstacleRectangles.size(); i++){
			if (Intersector.overlaps(unicornRectangle, obstacleRectangles.get(i))){
				Gdx.app.log("Unicorn!", "Collision");
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}

package core.mygdx.game;

import java.util.LinkedList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import enums.GameStatus;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGdxGame extends ApplicationAdapter {
	
	private GameRenderer gameRenderer;
	private DeckRenderer deckRenderer;
	private Stage currentStage;


	@Override
	public void create() {
		
		GameStatus.gamestatus = GameStatus.PLAYING;
		gameRenderer = new GameRenderer(this);
		gameRenderer.setDebugAll(true);
		deckRenderer = new DeckRenderer(this,gameRenderer);
		deckRenderer.setDebugAll(true);
		
		currentStage = gameRenderer;
		Gdx.input.setInputProcessor(currentStage);
	}

	@Override
	public void render() {
		super.render();
		
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.4f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		currentStage.getBatch().setProjectionMatrix(currentStage.getCamera().combined);
		currentStage.act();
		
		currentStage.draw();
		
		if(Gdx.input.isKeyJustPressed(Keys.S)) {
			if(currentStage == gameRenderer) {
				currentStage = deckRenderer;
			}
			else {
				currentStage = gameRenderer;
			}
			Gdx.input.setInputProcessor(currentStage);
		}

	}
	
	
	public void changeToDeckViewer(LinkedList<Card> cardsToDisplay, boolean isPrompt) {
		if(!(currentStage instanceof DeckRenderer)) {
			currentStage = deckRenderer;
			deckRenderer.update(cardsToDisplay, isPrompt);
			Gdx.input.setInputProcessor(currentStage);
		}
	}
	
	public void changeToGame() {
		if(!(currentStage instanceof GameRenderer)) {
			currentStage = gameRenderer;
			Gdx.input.setInputProcessor(currentStage);
		}
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		gameRenderer.resize(width,height);
		deckRenderer.resize(width, height);
	}
	
}
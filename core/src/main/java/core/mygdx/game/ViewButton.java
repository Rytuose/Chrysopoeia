package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * 
 * Allows the player to view the board when there is a prompt or upgrade window
 *
 */
public class ViewButton extends Button {

	private GameRenderer gameRenderer;
	
	public ViewButton(GameRenderer gr) {
		super(new Texture(Gdx.files.classpath("UncommonCard.png")),
				new Texture(Gdx.files.classpath("BasicCard.png")));
		
		gameRenderer = gr;
		
		this.setBounds(7.5f, (Constants.game_height - Constants.viewButtonHeight)/2, Constants.viewButtonWidth, Constants.viewButtonHeight);
	}

	@Override
	public void click() {
		gameRenderer.toggleView();
		this.toFront();
	}
}

package core.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A listener that is behind the game in order to process clicks that don't click
 * on anything
 */
public class BackgroundListener extends Actor {

	//====================================FIELDS==========================================
	
	private GameRenderer gameRenderer;
	
	
	
	//==================================CONSTRUCTORS======================================
	
	public BackgroundListener(GameRenderer gr) {
		gameRenderer = gr;
		setBounds(0,0,gr.getWidth(), gr.getHeight());
		toBack(); //So that it does not pick up inputs that matter
		
		addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				gameRenderer.release(); //handles click-miss interactions
			}
		});
	}
}

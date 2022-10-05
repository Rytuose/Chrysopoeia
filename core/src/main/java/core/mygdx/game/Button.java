package core.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * 
 * A class that represents a button and uses two textures instead of a skin
 *
 */
public abstract class Button extends GameActor {
	
	private Texture normalTexture,hoverTexture;
	private boolean touchDown;
	
	/**
	 * @param nt The normal texture
	 * @param ht The texture when clicked
	 */
	public Button(Texture nt, Texture ht) {
		super();
		normalTexture = nt;
		hoverTexture = ht;
		touchDown = false;
		
		this.setTexture(normalTexture);
		
		this.addListener(new ClickListener() {
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				if(pointer  == -1 && this.isOver() && touchDown) {
					Button.this.getSprite().setTexture(hoverTexture);
				}
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.exit(event, x, y, pointer, toActor);
				if(pointer == -1) {
					Button.this.getSprite().setTexture(normalTexture);
				}
			}
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				Button.this.getSprite().setTexture(hoverTexture);
				touchDown = true;
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				if(this.isOver()) {
					Button.this.getSprite().setTexture(normalTexture);
					Button.this.click();
				}
				touchDown = false;
			}
			
		});
	}
	
	/**
	 * What the button does when the button is clicked
	 */
	public abstract void click();
	
	
}

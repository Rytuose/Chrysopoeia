package core.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

/**
 * 
 * An actor with a sprite attached to it. Used as the basis for all
 * actors in this game
 *
 */
public class GameActor extends Actor implements Disposable {

	
	
	//====================================FIELDS==========================================
	
	private Sprite sprite;
	
	
	
	//==================================CONSTRUCTORS======================================
	
	/**
	 * Creates an actor with no sprite and no bounds
	 */
	public GameActor() {
		sprite = null;
		setBounds(0,0,0,0);
	}
	
	/**
	 * Creates an actor with no sprite and set bounds
	 * @param x The x position of the sprite
	 * @param y The y position of the sprite
	 * @param width The width of the sprite
	 * @param height The height of the sprite
	 */
	public GameActor(float x, float y, float width, float height) {	
		sprite = null;
		setBounds(x,y,width,height);

	}
	
	/**
	 * Creates an actor with a sprite that follows the image path. There is no
	 * sprite if the image cannot be found
	 * @param imgPath The image path to the sprite
	 */
	public GameActor(String imgPath) {
		
		try {
			sprite = new Sprite(new Texture(Gdx.files.classpath(imgPath)));
			setBounds(sprite.getX(),sprite.getY(), sprite.getWidth(), sprite.getHeight());
		}
		
		catch (Exception e){
			sprite = null;
		}
	}	
	
	
	
	//===================================METHODS==========================================
	
	@Override
	public void dispose() {}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(sprite != null) {
			sprite.draw(batch);
		}
	}
	
	@Override
	protected void positionChanged() {
		super.positionChanged();
		if (sprite != null) {
			sprite.setPosition(getX(), getY());
		}

	}
	
	
	
	//===============================GETTERS/SETTERS======================================
	
	/**
	 * Sets the bounds of the actor but not the sprite
	 * @param x The x position of the actor
	 * @param y The y position of the actor
	 * @param width The width of the actor
	 * @param height The height of the actor
	 */
	public void setActorBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
	}
	
	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x,y,width,height);
		if (sprite != null) {
			sprite.setBounds(x, y, width, height);
		}
	}

	/**
	 * 
	 * @param newSprite The new sprite that will be set to this actor
	 */
	public void setSprite(Sprite newSprite) {
		if(newSprite !=null) {
			sprite = newSprite;
		}
	}
	
	/**
	 * 
	 * @param texture The new texture that will be set to this actor
	 */
	public void setTexture(Texture texture) {
		if(texture!=null) {
			sprite = new Sprite(texture);
		}
	}
	
	/**@return The actor's sprite*/
	public Sprite getSprite() {return sprite;}
}

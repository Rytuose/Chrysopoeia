package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * 
 * A scroll bar for deck renderer
 *
 */
public class ScrollBar extends GameActor {
	
	private DeckRenderer deckRenderer;
	private int range;
	private float ratio,moveScale;
	
	public ScrollBar(DeckRenderer dr) {
		super();
		
		deckRenderer = dr;
		
		super.setTexture(new Texture(Gdx.files.classpath("CommonCard.png")));
		
		this.setBounds(deckRenderer.getWidth() - Constants.scrollBarXGap - Constants.scrollBarWidth,
				deckRenderer.getHeight() - 100 - Constants.scrollBarYGap,
				Constants.scrollBarWidth, 100);
		
		this.addListener(new ClickListener() {
			
			@Override
			public void touchDragged(InputEvent event,float x,float y,int pointer) {
				super.touchDragged(event, x, y, pointer);
				scroll(Gdx.input.getDeltaY());
				
			}		
		});
	}
	
	
	public void scroll(float y) {
		float topY = deckRenderer.getCamera().position.y 
				+ deckRenderer.getCamera().viewportHeight/2 - Constants.scrollBarYGap - this.getHeight();
		float bottomY = deckRenderer.getCamera().position.y 
				- deckRenderer.getCamera().viewportHeight/2 + Constants.scrollBarYGap;
		if(this.getY() > topY) {
			this.setPosition(this.getX(), topY);
		}
		else if(this.getY() < bottomY) {
			this.setPosition(this.getX(), bottomY);
		}
		deckRenderer.updateCamera(-moveScale * y);
	}
	
	public void resizeScrollBar(int r) {
		if(r == deckRenderer.getCamera().viewportHeight) {
			this.setVisible(false);
			return;
		}
		
		range = r;
		ratio = deckRenderer.getCamera().viewportHeight/r;
		
		float length = (deckRenderer.getCamera().viewportHeight - 2 * Constants.scrollBarYGap) * ratio;
		
		this.setVisible(true);
		this.setBounds(getX(), deckRenderer.getHeight() - length - Constants.scrollBarYGap, getWidth(), length);
		
		float scrollGap = deckRenderer.getCamera().viewportHeight - 2 * Constants.scrollBarYGap - this.getHeight();
		float deckGap = range - deckRenderer.getCamera().viewportHeight;
		
		float scaleRatio =  1/scrollGap;
		moveScale = deckGap * scaleRatio;
	}
	
	/**
	 * Update the position after a screen refresh
	 */
	public void setScrollBar(float deltaY) {
		float scrollGap = deckRenderer.getCamera().viewportHeight - 2 * Constants.scrollBarYGap - this.getHeight();
		float deckGap = range - deckRenderer.getCamera().viewportHeight;
		//Find position relative to the range and move it to gap
		float ratio = deltaY/deckGap;
		float scrollY =  scrollGap * ratio;
		this.setPosition(getX(), getY() + scrollY + deltaY);
	}
	
}

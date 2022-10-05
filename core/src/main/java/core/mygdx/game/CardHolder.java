package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * 
 * A class used in Deck Renderer in order to hold and display cards in Game Renderer
 *
 */
public class CardHolder extends GameActor {
	
	private DeckRenderer deckRenderer;
	private Card card;
	
	public CardHolder(DeckRenderer dr) {
		this.setBounds(getX(), getY(), 
				Constants.deckRendererCardWidth, Constants.deckRendererCardHeight);
		this.setTexture(new Texture(Gdx.files.classpath("CommonCard.png")));
		deckRenderer = dr;
		
		this.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				if(this.isOver()) {
					System.out.println("Release");
					deckRenderer.viewCard(card);
				}
			}
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(card != null) {
			card.drawSymbols(batch, getSprite());
		}
	}
	
	public void setCard(Card c) {card = c;}
	
	public Card getCard() {return card;}
	
}

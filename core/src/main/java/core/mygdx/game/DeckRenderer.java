package core.mygdx.game;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DeckRenderer extends Stage {

	private int lowestY;
	private MyGdxGame game;
	private Button exitButton;
	private Pool<CardHolder> cardHolderPool;
	private LinkedList<CardHolder> activeCardHolders;
	private Image transparentLayer;
	private Card viewingCard;
	private Sprite viewingSprite;
	private ScrollBar scrollBar;
	
	public DeckRenderer(MyGdxGame g) {
		super(new FitViewport(Constants.game_width,Constants.game_height,new OrthographicCamera()));
		game = g;
		activeCardHolders = new LinkedList<CardHolder>();
		viewingSprite = new Sprite();
		
		scrollBar = new ScrollBar(this);
		this.addActor(scrollBar);
		
		exitButton = new Button(new Texture(Gdx.files.classpath("CommonCard.png")),
				new Texture(Gdx.files.classpath("RareCard.png"))) {
					@Override
					public void click() {
						DeckRenderer.this.exit();
					}
		};
		
		this.addActor(exitButton);
		exitButton.setBounds(Constants.deckExitButtonXGap, 
				this.getHeight() - Constants.deckExitButtonYGap - Constants.deckExitButtonHeight, 
				Constants.deckExitButtonWidth, Constants.deckExitButtonHeight);
		
		cardHolderPool = new Pool<CardHolder>() {
			@Override
			protected CardHolder newObject() {
				return new CardHolder(DeckRenderer.this);
			}
		};
		

		Pixmap shadedLayer = new Pixmap((int)this.getWidth(), (int)this.getHeight(), Pixmap.Format.RGBA8888);
		shadedLayer.setColor(0, 0, 0, Constants.transparencyRatio);
		shadedLayer.fillRectangle(0, 0, (int)this.getWidth(), (int)this.getHeight());
		
		
		transparentLayer = new Image(new Texture(shadedLayer));
		shadedLayer.dispose();
		
		transparentLayer.setSize(this.getWidth(), this.getHeight());
		this.addActor(transparentLayer);
		transparentLayer.setVisible(false);
		transparentLayer.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				viewCard(null);
			}
		});
		
		this.addListener(new ClickListener() {
			@Override
			public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
				super.scrolled(event, x, y, amountX, amountY);
				if(!transparentLayer.isVisible()) {
					System.out.println("scrolled");
					updateCamera( -50 * amountY);
				}
				return true;
			}
		});
		

	}


	@Override
	public void draw() {
		super.draw();
		
		getBatch().begin();
		Card.cardFont.draw(getBatch(),
				"" + Gdx.graphics.getFramesPerSecond(), 
				getCamera().position.x + getCamera().viewportWidth/2 - 60,
				getCamera().position.y + getCamera().viewportHeight/2 - 25);
		if(viewingCard != null) {
			viewingSprite.draw(getBatch());
			viewingCard.drawSymbols(getBatch(), viewingSprite);
		}
		getBatch().end();
	}
	
	private void exit() {
		CardHolder ch;
		while(!activeCardHolders.isEmpty()) {
			ch = activeCardHolders.pop();
			ch.setCard(null);
			ch.remove();
			cardHolderPool.free(ch);
			
		}
		game.changeToGame();
	}
	
	public void update(LinkedList<Card> ctd) {
		ArrayList<Card> cardsToDisplay = new ArrayList<Card>();
		for(Card c: ctd) {
			cardsToDisplay.add(c);
		}
		int maxCards = (int) (Constants.game_width/(Constants.deckRendererCardWidth + Constants.deckRendererCardGap));
		CardHolder ch;
		int x , y;
		float startX = (Constants.game_width - 
					(Constants.deckRendererCardWidth * maxCards + Constants.deckRendererCardGap * (maxCards - 1)))/2;
		for(int i = 0 ; i < cardsToDisplay.size(); i++) {
			ch = cardHolderPool.obtain();
			ch.setCard(cardsToDisplay.get(i));
			this.addActor(ch);
			activeCardHolders.add(ch);
			x = i%maxCards;
			y = i/maxCards;
			ch.setBounds(startX + x * (Constants.deckRendererCardWidth + Constants.deckRendererCardGap),
					Constants.game_height - 
						(y+1) * (Constants.deckRendererCardHeight + Constants.deckRendererCardGap)
						+ Constants.deckRendererCardGap - Constants.deckRendererTopGap, 
					Constants.deckRendererCardWidth, Constants.deckRendererCardHeight);
			ch.setVisible(true);
			ch.toFront(); 
			
		}
		lowestY = (int) (Constants.game_height - (ctd.size()/maxCards * 
				(Constants.deckRendererCardHeight + Constants.deckRendererCardGap) + Constants.deckRendererTopGap));
		if(ctd.size()%maxCards > 0) {
			lowestY -= Constants.deckRendererCardHeight + Constants.deckRendererCardGap;
		}
		if(lowestY > 0) {
			lowestY = 0;
		}
		
		this.getCamera().position.y = this.getCamera().viewportHeight/2;
		exitButton.setPosition(exitButton.getX(),
				this.getHeight() - Constants.deckExitButtonYGap - Constants.deckExitButtonHeight);
		scrollBar.resizeScrollBar((int) (this.getCamera().viewportHeight - lowestY));
	}
	
	public void viewCard(Card card) {
		viewingCard = card;
		if(viewingCard == null) {
			transparentLayer.toBack();
			transparentLayer.setVisible(false);
		}
		else {
			float viewX = (Constants.game_width - Constants.deckRendererViewingCardWidth)/2;
			float viewY = this.getCamera().position.y - Constants.game_height/2 
					+ ((Constants.game_height - Constants.deckRendererViewingCardHeight)/2);
			viewingSprite.set(card.getSprite());
			viewingSprite.setBounds(viewX, viewY, 
					Constants.deckRendererViewingCardWidth, Constants.deckRendererViewingCardHeight);
			transparentLayer.toFront();
			transparentLayer.setVisible(true);
			transparentLayer.setPosition(transparentLayer.getX(),
					this.getCamera().position.y - this.getCamera().viewportHeight/2);
		}
		
	}
	
	public void updateCamera(float deltaY) {
		
		//float deltaY = - y * 50;
		//yPos += y;
		
		System.out.println("DeltaY " + deltaY + " LowestY " + lowestY);
		
		if (this.getCamera().position.y + deltaY > this.getViewport().getWorldHeight() - this.getCamera().viewportHeight/2) {
			System.out.println("stop scroll up");
			deltaY += this.getViewport().getWorldHeight() - this.getCamera().viewportHeight/2 - (this.getCamera().position.y + deltaY);
		}
		else if (this.getCamera().position.y + deltaY < lowestY + this.getCamera().viewportHeight/2) {
			System.out.println("stop scroll down");
			deltaY += lowestY + this.getCamera().viewportHeight/2 - (this.getCamera().position.y + deltaY);
		}
		

		
		this.getCamera().translate(0, deltaY,0);
		
		exitButton.setPosition(exitButton.getX(), exitButton.getY() + deltaY);
		scrollBar.setScrollBar(deltaY);
		
		//setExitButton();
	}
	
	public void resize(int width, int height) {this.getViewport().update(width,height,true);}
	
	
}

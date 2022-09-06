package core.mygdx.game;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import enums.Location;
import enums.Symbol;

public class Deck {
	
	private MyGdxGame game;
	private GameRenderer gameRenderer;
	private LinkedList<Card> deck,discard,deckList,upgradeList;
	private Card refreshCard;
	private Button deckViewer,discardViewer, decklistViewer;
	
	public Deck(GameRenderer gr, MyGdxGame g) {
		gameRenderer = gr;
		game = g;
		
		deck = new LinkedList<Card>();
		discard = new LinkedList<Card>();
		deckList = new LinkedList<Card>();
		upgradeList = new LinkedList<Card>();
		
		makeStartingDeck();	
		this.shuffle();
		
		refreshCard = new Card(gameRenderer);
		refreshCard.addSymbol(Symbol.REFRESH4, Location.CENTER);
		refreshCard.addSymbol(Symbol.RETURN, Location.CENTER);
//		refreshCard.addSymbol(Symbol.SEARCH, Location.CENTER);
//		refreshCard.addSymbol(Symbol.UPGRADE1, Location.CENTER);
//		refreshCard.addSymbol(Symbol.UPGRADE2, Location.CENTER);
//		refreshCard.addSymbol(Symbol.UPGRADE3, Location.CENTER);
		
		deckList.add(refreshCard);
		deck.add(0, refreshCard);
		refreshCard.remove();
		
		createButtons();
	}
	
	private void createButtons() {
		Texture texture1 = new Texture(Gdx.files.classpath("CommonCard.png"));
		Texture texture2 = new Texture(Gdx.files.classpath("RareCard.png"));
		
		deckViewer = new Button(texture1, texture2) {
			@Override
			public void click() {
				game.changeToDeckViewer(deck,false);
			}
		};
		gameRenderer.addActor(deckViewer);
		deckViewer.setBounds(Constants.deckButtonXGap, Constants.deckButtonYGap,
				Constants.deckButtonWidth, Constants.deckButtonHeight);
		
		discardViewer = new Button(texture1, texture2) {
			@Override
			public void click() {
				game.changeToDeckViewer(discard,false);
			}
		};
		gameRenderer.addActor(discardViewer);
		discardViewer.setBounds(gameRenderer.getWidth() - Constants.deckButtonXGap - Constants.deckButtonWidth,
				Constants.deckButtonYGap,Constants.deckButtonWidth, Constants.deckButtonHeight);
		
		decklistViewer = new Button(texture1, texture2) {
			@Override
			public void click() {
				game.changeToDeckViewer(deckList,false);
			}
		};
		gameRenderer.addActor(decklistViewer);
		decklistViewer.setBounds(Constants.deckButtonXGap, 
				gameRenderer.getHeight() - Constants.deckButtonYGap - Constants.deckButtonHeight, 
				Constants.deckButtonWidth, Constants.deckButtonHeight);
	}

	public void add(Card c) {
		deckList.add(c);
		deck.add(c);
		c.remove();
	}
	
	public void dispose() { for(Card c: deckList) c.dispose();}
	
	public Card draw() {
		if(deck.isEmpty()) {
			LinkedList<Card> temp = discard;
			discard = deck;
			deck = temp;
			shuffle();
			if(deck.isEmpty()) {return null;}
		}

		Card c = deck.pop();
		gameRenderer.addActor(c);
		c.setBounds(c.getX(), c.getY(), Constants.cardWidth, Constants.cardHeight);
		c.setVisible(true);
		c.setTouchable(Touchable.enabled);
		
		return c;
	}
	
	public Card randomUgradeCard() {
		
		if(upgradeList.isEmpty()) {
			return null;
		}
		
		int randPos = (int)(Math.random() * upgradeList.size());
		
		Card c = upgradeList.get(randPos);
		upgradeList.remove(randPos);
		
		return c;
	}
	

	public void resetUpgradeOptions(int level) {
		upgradeList.clear();
		for(Card c: deckList) {
			if(c.getLevel() < level) {
				upgradeList.add(c);
			}
		}		
	}
	
	public void resetDeck() {
		while(!discard.isEmpty()) {
			deck.add(discard.pop());
		}
		this.shuffle();
	}
	
	public void shuffle() {
		LinkedList<Card> temp = new LinkedList<Card>();
		
		for (Card c: deck) {
			temp.add((int)(Math.random()*temp.size()), c);
		}
		
		deck = temp;
	}
	
	public void toDiscard(Card c) {
		discard.add(c);
		c.remove();
	}
	
	public int deckListSize() {return deckList.size();}

	public Card deckListGet(int i) {return deckList.get(i);}
	
	public LinkedList<Card> getDeck(){return deck;}
	
	public LinkedList<Card> getDiscard(){return discard;}
	
	public LinkedList<Card> getDeckList(){return deckList;}
	
	private void makeStartingDeck() {
		
		Card c;	
		
		//Lead Production Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.LEAD, Location.CENTER);
		c.addSymbol(Symbol.LEAD, Location.CENTER);
		this.add(c);
		
		//Copper Production Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.COPPER, Location.CENTER);
		this.add(c);
		
		//Copper Trade Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.COPPER, Location.INPUT);
		c.addSymbol(Symbol.COPPER, Location.LEFT);
		c.addSymbol(Symbol.COPPER, Location.RIGHT);
		this.add(c);
		
//		//Silver Trade Card
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.LEAD, Location.INPUT);
//		c.addSymbol(Symbol.LEAD_OR_COPPER, Location.INPUT);
//		c.addSymbol(Symbol.COPPER, Location.INPUT);
//		c.addSymbol(Symbol.SILVER, Location.CENTER);
//		this.add(c);
//		
//		//Gold Trade Card (Probably remove this later)
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.SILVER, Location.INPUT);
//		c.addSymbol(Symbol.COPPER, Location.INPUT);
//		c.addSymbol(Symbol.GOLD, Location.CENTER);
//		this.add(c);
		
		//Upgrade Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.LEAD, Location.INPUT);
		c.addSymbol(Symbol.COPPER, Location.INPUT);
		c.addSymbol(Symbol.UPGRADE1, Location.CENTER);
		this.add(c);
		
		//Move Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.MOVE_LEFT, Location.CENTER);
		c.addSymbol(Symbol.MOVE_RIGHT, Location.CENTER);
		this.add(c);
		
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.GHOST, Location.CENTER);
		this.add(c);
		
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.GHOST, Location.INPUT);
		c.addSymbol(Symbol.COPPER, Location.CENTER);
		c.addSymbol(Symbol.GHOST, Location.RIGHT);
		this.add(c);
	}

	
	
}

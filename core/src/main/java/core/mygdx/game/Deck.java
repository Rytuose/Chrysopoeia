package core.mygdx.game;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import enums.Location;
import enums.Symbol;

/**
 * 
 * Holds all cards in use and records their locations
 *
 */
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
		//this.shuffle();
		
		refreshCard = new Card(gameRenderer);
		refreshCard.addSymbol(Symbol.REFRESH4, Location.CENTER);
		refreshCard.addSymbol(Symbol.RETURN, Location.CENTER);
//		refreshCard.addSymbol(Symbol.SEARCH, Location.CENTER);
//		refreshCard.addSymbol(Symbol.UPGRADE1, Location.CENTER);
//		refreshCard.addSymbol(Symbol.UPGRADE2, Location.CENTER);
//		refreshCard.addSymbol(Symbol.UPGRADE3, Location.CENTER);
//		refreshCard.addSymbol(Symbol.NEW_CARD1, Location.CENTER);
//		refreshCard.addSymbol(Symbol.NEW_CARD2, Location.CENTER);
//		refreshCard.addSymbol(Symbol.NEW_CARD3, Location.CENTER);
		
		deckList.add(refreshCard);
		deck.add(0, refreshCard);
		refreshCard.remove();
		
		createButtons();
		
		//testUpgrade();
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

	/**
	 * Adds a card to the deck
	 */
	public void add(Card c) {
		deckList.add(c);
		deck.add(c);
		c.remove();
	}
	
	public void delete(Card c) {
		deckList.remove(c);
		deck.remove(c);
		discard.remove(c);
		c.dispose();
	}
	
	public void dispose() { for(Card c: deckList) c.dispose();}
	
	/**
	 * Draws a card from the deck, the card is not placed anywhere
	 */
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
	
	/**
	 * Sets up all cards able to be upgraded at a certain level
	 */
	public void resetUpgradeOptions(int level) {
		upgradeList.clear();
		for(Card c: deckList) {
			if(c.getLevel() < level) {
				upgradeList.add(c);
			}
		}		
	}
	
	/**
	 * Returns a random upgradeable card
	 */
	public Card randomUgradeCard() {
		
		if(upgradeList.isEmpty()) {
			return null;
		}
		
		int randPos = (int)(Math.random() * upgradeList.size());
		
		Card c = upgradeList.get(randPos);
		upgradeList.remove(randPos);
		
		return c;
	}
	
	/**
	 * Returns all cards to the draw pile and shuffles the deck
	 */
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
	
	/**
	 * Sends a card to the discard pile
	 */
	public void toDiscard(Card c) {
		discard.add(c);
		c.remove();
	}
	
	public int deckListSize() {return deckList.size();}

	public Card deckListGet(int i) {return deckList.get(i);}
	
	public LinkedList<Card> getDeck(){return deck;}
	
	public LinkedList<Card> getDiscard(){return discard;}
	
	public LinkedList<Card> getDeckList(){return deckList;}
	
	/**
	 * A testing function used to test upgrade algorithms and probabilites
	 */
	private void testUpgrade() {
		UpgradeCard uc1,uc2,temp;
		Card c;//,newCard;
		
		uc1 = new UpgradeCard(gameRenderer);
		uc2 = new UpgradeCard(gameRenderer);
		
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.GHOST, Location.INPUT);
		c.addSymbol(Symbol.COPPER, Location.CENTER);
		//this.add(c);
		
//		int step = 1;
//		int amount = 1000;
//		int error = 100;
//		
//		System.out.println((amount/4 - error) + " " + (amount/4 + error));
//		
//		for(int l = 250 ; l < 260; l+=step) {
//			for(int k = 790; k < 850; k+=step) {
//				UpgradeManager.prob1 = l/1000.0;
//				UpgradeManager.prob2 = k/1000.0;
//				
//				for(int i = 0; i < amount; i++) {
//					uc1.copyCard(c);
//					//newCard = new Card(gameRenderer);
//					
//					for(int j = 0 ; j < 3; j++) {
//						UpgradeManager.createUpgrade(uc1, uc2, j);
//						temp = uc2;
//						uc2 = uc1;
//						uc1 = temp;
//					}
//					
//					//newCard.copyCard(uc1);
//					
//					//this.add(newCard);
//				}
//				
////				System.out.println("(" + l + " " + k + ")\t" + 
////						UpgradeManager.cardRatio.toString());
//				
//
//				
//				boolean valid = true;
//				int errors = 0;
//				for(int val :UpgradeManager.cardRatio.values()) {
//					if(val <= amount/4 - error || val >= amount/4 + error) {
//						valid = false;
//						continue;
//					}
//					errors += Math.abs(amount/4 - val);
//				}
//				
//				if(UpgradeManager.cardRatio.values().size() < 4 || !valid || errors > 160) {
//					UpgradeManager.cardRatio.clear();
//					//System.out.println("(" + l + " " + k + ")\tINVAID");
//					continue;
//				}
//				
//
//				
//				System.out.print("(" + l + " " + k + ")\t");
//				for(int val :UpgradeManager.cardRatio.values()) {
//					System.out.print(val + "\t");
//				}
//				System.out.println(errors);
//				UpgradeManager.cardRatio.clear();
//			}
//		}
		
		for(int k = 0 ; k < 100; k++) {
			for(int i = 0; i < 1000; i++) {
				uc1.copyCard(c);
				//newCard = new Card(gameRenderer);
				
				for(int j = 0 ; j < 3; j++) {
					UpgradeManager.createUpgrade(uc1, uc2, j);
					temp = uc2;
					uc2 = uc1;
					uc1 = temp;
				}
				
				//newCard.copyCard(uc1);
				
				//this.add(newCard);
			}
			
			System.out.println(UpgradeManager.cardRatio.toString());
			for(int val :UpgradeManager.cardRatio.values()) {
				System.out.print(val + "\t");
			}
			UpgradeManager.cardRatio.clear();
			System.out.println();
		}
	}
	
	private void makeStartingDeck() {
		
		Card c;
		
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.DELETE,Location.CENTER);
//		this.add(c);
//		
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.DRAW1, Location.CENTER);
//		c.addSymbol(Symbol.DRAW1, Location.CENTER);
//		c.addSymbol(Symbol.QUICK, Location.CENTER);
//		this.add(c);
//		
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.DRAW1, Location.CENTER);
//		c.addSymbol(Symbol.DRAW1, Location.CENTER);
//		c.addSymbol(Symbol.QUICK, Location.CENTER);
//		this.add(c);
		
		//Lead Production Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.LEAD, Location.CENTER);
		c.addSymbol(Symbol.LEAD, Location.CENTER);
		this.add(c);
		
		//Copper Production Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.COPPER, Location.CENTER);
		this.add(c);

		//Upgrade Card
		c = new Card(gameRenderer);
		c.addSymbol(Symbol.LEAD, Location.INPUT);
		c.addSymbol(Symbol.COPPER, Location.INPUT);
		c.addSymbol(Symbol.UPGRADE1, Location.CENTER);
		c.addSymbol(Symbol.NEW_CARD1, Location.CENTER);
		this.add(c);
		
//		//Move Card
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.MOVE_LEFT, Location.CENTER);
//		c.addSymbol(Symbol.MOVE_RIGHT, Location.CENTER);
//		this.add(c);


		

		
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.GHOST, Location.INPUT);
//		c.addSymbol(Symbol.LEAD, Location.CENTER);
//		c.addSymbol(Symbol.GHOST, Location.RIGHT);
//		this.add(c);
//		
//		c = new Card(gameRenderer);
//		c.addSymbol(Symbol.GHOST, Location.INPUT);
//		c.addSymbol(Symbol.COPPER, Location.CENTER);
//		this.add(c);
	}

	
	
}

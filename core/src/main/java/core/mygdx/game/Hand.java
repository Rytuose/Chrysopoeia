package core.mygdx.game;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.Touchable;

import enums.GameStatus;

/**
 * 
 * Represents the cards accessable by the player
 *
 */
public class Hand {

	private static int handSize = 7;
	
	private ArrayList<Card> hand;
	private Deck deck;
	private GameRenderer gameRenderer;
	
	
	public Hand(GameRenderer gr,Deck d) {
		gameRenderer = gr;
		deck = d;
		hand = new ArrayList<Card>();
		
		drawStartingHand(4);
		
		setHand();
	}
	
	public void discardCard(Card c) {
		int pos = hand.indexOf(c);
		
		if(pos == -1) {
			return;
		}
		
		deck.toDiscard(hand.get(pos));
		hand.remove(pos);
	}
	
	/**
	 * Discards hand and draws given amount of cards
	 */
	public void drawStartingHand(int cardToDraw) {
		while(hand.size() > 1) {
			deck.toDiscard(hand.get(1));
			hand.remove(1);
		}
		
		//int handSize = Constants.handSize;

		while(hand.size() < cardToDraw+1) {
			Card c = deck.draw();
			if(c!= null) {
				hand.add(c);
			}
			else {
				break;
			}
		}
		
		this.setHand();
				
	}
	
	/**
	 * Draws a random card from the deck
	 */
	public void drawCard() {
		if (hand.size() < handSize) {
			Card c = deck.draw();
			if(c != null) {
				hand.add(c);
			}
		}
		else {
			Card c = deck.draw();
			if(c != null) {
				deck.toDiscard(c);
			}
		}
		
		this.setHand();
	}
	
	/**
	 * Draws a specific card c
	 */
	public void drawCard(Card c) {
		deck.getDeck().remove(c);
		deck.getDiscard().remove(c);
		hand.add(c);
		gameRenderer.addActor(c);
		c.setVisible(true);
		c.setTouchable(Touchable.enabled);
	}
	
	/**
	 * Puts the cards in the hand in the right position
	 */
	public void setHand() {
		System.out.println("-------------------------");
		System.out.println("Set Hand " + hand.toString());
		int middle = Constants.game_width/2;
		int totalLength = (int) (hand.size() * Constants.cardWidth * 
				(1-Constants.cardOverlapRatio) 
				+ Constants.cardWidth * Constants.cardOverlapRatio);
		int start = middle - (totalLength/2);
		Card c;
		
		int startZ = 0;
		if(GameStatus.gamestatus == GameStatus.PROMPTING && hand.size() > 1) {
			startZ = hand.get(1).getZIndex(); //Hand should never have 0 cards
			for(int i = 1; i < hand.size(); i++) {
				c = hand.get(i);
				if(startZ > c.getZIndex()) {
					startZ = c.getZIndex();
				}
			}
		}
		
		for (int i = hand.size()-1; i >=0; i--/*int i = 0; i<hand.size();i++*/) {
			
			c = hand.get(i);
			
			if(!c.equals(gameRenderer.getClickedActor())){
				
				
				if(c.getSprite().getWidth() != Constants.cardWidth) {
					c.getSprite().setBounds(c.getX(), c.getY(), Constants.cardWidth, Constants.cardHeight);
				}
				
				//Set hit box but does not stretch the sprite
				c.setActorBounds(start + Constants.cardWidth * i * 
						(1-Constants.cardOverlapRatio), 
						Constants.cardHideRatio * Constants.cardHeight, 
						Constants.cardWidth, Constants.cardHeight);
				
				if(i != hand.size()-1) {
					c.setActorBounds(c.getX(), c.getY(), 
							Constants.cardWidth * (1 - Constants.cardOverlapRatio),
							c.getHeight());
				}
				c.setVisible(true);
				
				
				
				
				if(GameStatus.gamestatus != GameStatus.PROMPTING || i == 0) {
					c.setZIndex(1);
				}
				else {
					c.setZIndex(startZ + i - 1);
				}
				//c.toFront();
				//c.setZIndex(1);
			}
		}
	}
	
	public void setHandInteractable(boolean interactable) {
		for (Card c: hand) {	
			if(interactable) {
				c.setTouchable(Touchable.enabled);
			}
			else {
				c.setTouchable(Touchable.disabled);
			}
		}
	}
	
	public void removeCard(Card c) {hand.remove(c);}
	
	public void toFront() {
		for(int i = hand.size()-1; i >= 0; i-- ) {
			hand.get(i).toFront();
		}
	}
	
	public void dispose() {
		while(!hand.isEmpty()) {
			hand.remove(0);
		}
	}
	
	public int size() {return hand.size();}
	
	public ArrayList<Card> getHand() { return hand;}

}

package core.mygdx.game;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Hand {

	private static int handSize = 5;
	
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
	
	public void drawCard() {
		if (hand.size() < handSize) {
			Card c = deck.draw();
			if(c != null) {
				hand.add(c);
			}
		}
		else {
			Card c = deck.draw();
			deck.toDiscard(c);
		}
	}
	
	
	public void setHand() {
		int middle = Constants.game_width/2;
		int totalLength = (int) (hand.size() * Constants.cardWidth * 
				(1-Constants.cardOverlapRatio) 
				+ Constants.cardWidth * Constants.cardOverlapRatio);
		int start = middle - (totalLength/2);
		Card c;
		
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
				c.setZIndex(1); //c.toFront();
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
	
	public void dispose() {
		while(!hand.isEmpty()) {
			hand.remove(0);
		}
	}

}

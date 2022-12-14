package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import enums.GameStatus;
import enums.Location;
import enums.Symbol;

/**
 * 
 * Represents a card in the game
 *
 */
public class Card extends GameActor {
	
	private ArrayList<Symbol> input,leftOutput,centerOutput,rightOutput;
	protected CardListener cardListener;
	private GameRenderer gameRenderer;
	private int level;
	public static BitmapFont cardFont = new BitmapFont(Gdx.files.classpath("Consolas.fnt"));
	
	public Card(GameRenderer gr) {
		super();
		gameRenderer = gr;
		cardListener = new CardListener(this);
		level = 0;
		
		input = new ArrayList<Symbol>();
		leftOutput = new ArrayList<Symbol>();
		centerOutput = new ArrayList<Symbol>();
		rightOutput = new ArrayList<Symbol>();

		super.setTexture(new Texture(Gdx.files.classpath("CommonCard.png")));
		
		this.addListener(cardListener);
		this.setBounds(this.getX(), this.getY(), Constants.cardWidth, Constants.cardHeight);
	}
	
	/**
	 * Adds all inputs and outputs of card c into this card
	 */
	public void add(Card c) {
		merge(this.getInput(),c.getInput());
		merge(this.getLeftOutput(),c.getLeftOutput());
		merge(this.getCenterOutput(),c.getCenterOutput());
		merge(this.getRightOutput(), c.getRightOutput());
	}
	
	private static void merge(ArrayList<Symbol> destination, ArrayList<Symbol> target) {
		destination.addAll(target);
		destination.sort(null);
	}
	
	public void copyCard(Card c) {
		copyArray(this.getInput(),c.getInput());
		copyArray(this.getLeftOutput(),c.getLeftOutput());
		copyArray(this.getCenterOutput(),c.getCenterOutput());
		copyArray(this.getRightOutput(), c.getRightOutput());
		level = c.getLevel();
	}
	
	private static void copyArray(ArrayList<Symbol> destination, ArrayList<Symbol> target) {
		destination.clear();
		for(Symbol s: target) {
			destination.add(s);
		}
		destination.sort(null);
	}
	
	/**
	 * Resets input, output, and level of the card
	 */
	public void resetCard() {
		input.clear();
		leftOutput.clear();
		centerOutput.clear();
		rightOutput.clear();
		level = 0;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		drawSymbols(batch, this.getSprite());
	}
	
	public void drawSymbols(Batch batch, Sprite sprite) {
		long time = System.nanoTime();
		float scale = sprite.getWidth()/Constants.cardWidth;
		
		if(!input.isEmpty()) {
			float startX = (Constants.cardWidth - (Constants.symbolWidth + Constants.symbolGap)*(input.size()) + Constants.symbolGap)/2; 
			for(int i = 0 ; i < input.size() ; i++) {
				batch.draw(ImageSearcher.getCardSymbol(input.get(i)), 
					sprite.getX() + (startX + i*(Constants.symbolWidth + Constants.symbolGap))*scale, 
					sprite.getY() + 125*scale, 
					Constants.symbolWidth * scale, Constants.symbolHeight*scale);
			}
		}
		
		if(!leftOutput.isEmpty()) {
			float startX = (Constants.cardWidth - (Constants.symbolWidth + Constants.symbolGap)*(leftOutput.size()) + Constants.symbolGap)/2; 
			for(int i = 0 ; i < leftOutput.size() ; i++) {
				batch.draw(ImageSearcher.getCardSymbol(leftOutput.get(i)), 
						sprite.getX() + (startX + i*(Constants.symbolWidth + Constants.symbolGap))*scale, 
						sprite.getY() + 75*scale, 
						Constants.symbolWidth * scale, Constants.symbolHeight*scale);
			}
		}
		
		if(!centerOutput.isEmpty()) {
			float startX = (Constants.cardWidth - (Constants.symbolWidth + Constants.symbolGap)*(centerOutput.size()) + Constants.symbolGap)/2; 
			for(int i = 0 ; i < centerOutput.size() ; i++) {
				batch.draw(ImageSearcher.getCardSymbol(centerOutput.get(i)), 
						sprite.getX() + (startX + i*(Constants.symbolWidth + Constants.symbolGap))*scale, 
						sprite.getY() + 50*scale, 
						Constants.symbolWidth * scale, Constants.symbolHeight*scale);
			}
		}
		
		if(!rightOutput.isEmpty()) {
			float startX = (Constants.cardWidth - (Constants.symbolWidth + Constants.symbolGap)*(rightOutput.size()) + Constants.symbolGap)/2; 
			for(int i = 0 ; i < rightOutput.size() ; i++) {
				batch.draw(ImageSearcher.getCardSymbol(rightOutput.get(i)), 
						sprite.getX() + (startX + i*(Constants.symbolWidth + Constants.symbolGap))*scale, 
						sprite.getY() + 25*scale, 
						Constants.symbolWidth * scale, Constants.symbolHeight*scale);
			}
		}

		cardFont.draw(batch, "" + level, sprite.getX() + 10, 
				sprite.getY() + sprite.getHeight() - 10);
		
		//System.out.println("Time to draw a card " + (System.nanoTime() - time));
	}

	/**
	 * Called when mouse enters card
	 */
	public void enter() {
		super.getSprite().setBounds(
			this.getX() + (Constants.cardWidth * (1- Constants.cardOverlapRatio))/2 - Constants.cardHoverWidth/2, 
			0, Constants.cardHoverWidth, Constants.cardHoverHeight);
		this.toFront();
		gameRenderer.setHoverActor(this);
	}
	
	/**
	 * Called when mouse exits card
	 */
	public void exit() {
		if(gameRenderer.getClickedActor() == null || !gameRenderer.getClickedActor().equals(this)) {
			super.getSprite().setBounds(this.getX(), this.getY(), Constants.cardWidth, Constants.cardHeight);
			gameRenderer.removeHoverActor(this);
			gameRenderer.setHand();
		}	
	}
	
	/**
	 * Called when mouse is pressed down on card
	 */
	public void click() {
		gameRenderer.setHand();
		super.setBounds(this.getX() + (Constants.cardWidth * (1- Constants.cardOverlapRatio))/2 - Constants.cardHoverWidth/2, 
			0, Constants.cardHoverWidth, Constants.cardHoverHeight);
		gameRenderer.setClickedActor(this);
		gameRenderer.setHandInteractable(false);
		this.toFront();
	}
	
	/**
	 * Called when mouse is released after clicked on card
	 */
	public void release() {
		if(GameStatus.gamestatus == GameStatus.PLAYING 
				&& gameRenderer.getHoverActor() instanceof StorageContainer 
				&& gameRenderer.isPlayable(this,(StorageContainer) gameRenderer.getHoverActor()) ) {
			boolean battleOver = gameRenderer.play(this, (StorageContainer) gameRenderer.getHoverActor());
			if(battleOver) {
				return;
			}
		}
		
		if(gameRenderer.getClickedActor() != null) {
			gameRenderer.getClickedActor().clearActions();
		}
		
		this.clearActions();
		gameRenderer.removeClickedActor(this);
		gameRenderer.removeHoverActor(this);
		
		gameRenderer.setHandInteractable(true);
		gameRenderer.setHand();

		
		cardListener.resetIsFirst();
	}
	
	/**
	 * Adds a symbol at the given location
	 */
	public void addSymbol(Symbol symbol, Location location) {
		ArrayList<Symbol> al = null;
		
		switch(location) {
		case INPUT:
			al = input;
			break;
		case LEFT:
			al = leftOutput;
			break;
		case CENTER:
			al = centerOutput;
			break;
		case RIGHT:
			al = rightOutput;
			break;
		}
		
		al.add(symbol);
		al.sort(null);
	}
	
	/**
	 * Gets contents of a given location
	 */
	public ArrayList<Symbol> getLocation(Location location){
		switch(location) {
		case INPUT:
			return input;
		case LEFT:
			return leftOutput;
		case CENTER:
			return centerOutput;
		case RIGHT:
			return rightOutput;
		}
		return null;
	}
	
	/**
	 * Returns all output of a card
	 */
	public ArrayList<Symbol> getAllOutput(){
		ArrayList<Symbol> totalSymbols = new ArrayList<Symbol>();
		totalSymbols.addAll(leftOutput);
		totalSymbols.addAll(centerOutput);
		totalSymbols.addAll(rightOutput);
		totalSymbols.sort(null);
		return totalSymbols;
	}
	
	@Override
	public String toString() {
		return "Input " + input.toString() + "\n" +
				"Left " + leftOutput.toString() + "\n" +
				"Center " + centerOutput.toString() + "\n" +
				"Right " + rightOutput.toString();
	}
	
	public boolean isEqual (Object card) {
		if(!(card instanceof Card)) {
			return false;
		}
		Card c = (Card)card;
		
		return(input.equals(c.getInput()) &&
				leftOutput.equals(c.getLeftOutput()) &&
				centerOutput.equals(c.getCenterOutput()) &&
				rightOutput.equals(c.getRightOutput()) &&
				level == c.getLevel());
	}
	
	/**
	 * Increases level by 1
	 */
	public void upgrade() {level++;}
	
	public int getLevel() {return level;}
	
	/**
	 * Process this card as a prompt
	 */
	public void recievePrompt() {gameRenderer.finishPrompt(this);}
	
	public ArrayList<Symbol> getInput(){return input;}
	
	public ArrayList<Symbol> getLeftOutput(){return leftOutput;}
	
	public ArrayList<Symbol> getCenterOutput(){return centerOutput;}
	
	public ArrayList<Symbol> getRightOutput(){return rightOutput;}

}

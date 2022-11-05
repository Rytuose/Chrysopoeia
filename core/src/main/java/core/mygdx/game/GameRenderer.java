package core.mygdx.game;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import core.mygdx.game.Button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;

import enums.GameStatus;
import enums.Symbol;

/**
 * 
 * Manages, draws, and displays the game logic
 *
 */
public class GameRenderer extends Stage {
	
	private Button confirmButton;
	private boolean quick;
	private int turnCounter;
	private LinkedList<Symbol> symbolQueue;
	private Symbol promptSymbol;
	private GameActor clickedActor, hoverActor;
	private Deck deck;
	private Hand hand;
	private MyGdxGame game;
	private StorageContainer currentContainer;
	private StorageContainer[] storageContainers;
	private Image transparentLayer;
	private QuestViewer questViewer;
	private UpgradeWindow upgradeWindow;
	private ViewButton viewButton;
	
	public GameRenderer(MyGdxGame g) {
		super(new FitViewport(Constants.game_width,Constants.game_height,new OrthographicCamera()));
		
		this.addActor(new BackgroundListener(this));
		game = g;
		turnCounter = 0;
		//this.addActor(new Card(this));
		deck = new Deck(this,game);
		
		hand = new Hand(this,deck);
		
		symbolQueue = new LinkedList<Symbol>();
		
		upgradeWindow = new UpgradeWindow(this,deck);
		this.addActor(upgradeWindow);
		upgradeWindow.setVisible(false);
		
		//this.addActor(new RefreshButton(this,hand));
		
		viewButton = new ViewButton(this);
		this.addActor(viewButton);
		viewButton.setVisible(false);
		

		
		StorageContainer sc;
		storageContainers = new StorageContainer[3];
		float startX = (Constants.game_width - (storageContainers.length * (Constants.storageContainerWidth + Constants.storageContainerGap)) 
				+	 Constants.storageContainerGap)/2;
		for(int i = 0; i < storageContainers.length;i++) {
			sc = new StorageContainer(this,startX + i*(Constants.storageContainerWidth + Constants.storageContainerGap),
					Constants.storageContainerY);
			storageContainers[i] = sc;
			this.addActor(sc);
		}
		
		questViewer = new QuestViewer(this,storageContainers);
		this.addActor(questViewer);
		questViewer.setPosition((getWidth() - questViewer.getWidth())/2, getHeight() - questViewer.getHeight());
		questViewer.setQuest();
		
		Pixmap shadedLayer = new Pixmap((int)this.getWidth(), (int)this.getHeight(), Pixmap.Format.RGBA8888);
		shadedLayer.setColor(0, 0, 0, Constants.transparencyRatio);
		shadedLayer.fillRectangle(0, 0, (int)(this.getWidth()), (int)(this.getHeight()));
		
		transparentLayer = new Image(new Texture(shadedLayer));
		shadedLayer.dispose();
		
		transparentLayer.setSize(this.getWidth(), this.getHeight());
		this.addActor(transparentLayer);
		transparentLayer.setVisible(false);
		
		confirmButton = new Button(new Texture(Gdx.files.classpath("UncommonCard.png")),
				new Texture(Gdx.files.classpath("BasicCard.png"))) {
			@Override
			public void click() {
				GameRenderer.this.finishPrompt(confirmButton);
			}
			
		};
		
		this.addActor(confirmButton);
		confirmButton.setBounds(this.getWidth() - Constants.confirmButtonXGap - Constants.confirmButtonWidth,
				Constants.confirmButtonYGap, 
				Constants.confirmButtonWidth, Constants.confirmButtonHeight);
		confirmButton.setVisible(false);
		
		

//
//		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
//		Dialog testDia = new Dialog("this is a test", skin){
//			{
//				this.text("this is also a test");
//				this.button("option1", true);
//				this.button("option1", false);
//			}
//			
//			@Override
//			protected void result(Object object) {
//				System.out.println(object);
//			}
//		}.show(this);
//		
//		Button button = new TextButton("Test", skin);
//		this.addActor(button);
		
	}
	
	
	@Override
	public void draw() {
		super.draw();
		
		getBatch().begin();
		Card.cardFont.draw(getBatch(),
				"" + Gdx.graphics.getFramesPerSecond(), 
				getCamera().position.x + getCamera().viewportWidth/2 - 60,
				getCamera().position.y + getCamera().viewportHeight/2 - 25);
		
		Card.cardFont.draw(getBatch(),
				"Turns " + turnCounter, 
				getCamera().position.x + getCamera().viewportWidth/2 - 250,
				getCamera().position.y + getCamera().viewportHeight/2 - 25);
		//questViewer.drawQuestSymbol(getBatch());
		getBatch().end();
	}
	
	
	//Assume every array list is sorted
	/**
	 * 
	 * Checks if card is playable in a storage container, assuming
	 * everything is sorted (which it should be by default)
	 */
	public boolean isPlayable(Card card, StorageContainer destination) {
		ArrayList<Symbol> requirements,storage;
		
		requirements = card.getInput();
		storage = destination.getStorage();
		
		System.out.println("requirements " + requirements);
		System.out.println("storage " + storage);
		
		return containsAll(requirements,storage);
		
	}
	
	/**
	 * 
	 * Checks if requirements are in storage taking into account duplicates
	 * 
	 */
	public boolean containsAll(ArrayList<Symbol> requirements, ArrayList<Symbol> storage ) {
		Iterator<Symbol> reqIter = requirements.iterator();
		Iterator<Symbol> storIter = storage.iterator();
		
		Symbol reqSymbol,storSymbol;
		
		while(reqIter.hasNext()) {
			if(!storIter.hasNext()) {
				System.out.println("Exhausted Array");
				return false;
			}
			reqSymbol = reqIter.next();
			while(storIter.hasNext()) {
				storSymbol = storIter.next();
				System.out.println("Comparing " + reqSymbol + " with " + storSymbol);
				if (reqSymbol.equals(storSymbol)) { //Implies they have the same value, takes care of the or symbols
					break;
				}
				else if(reqSymbol.getValue() < storSymbol.getValue() && storSymbol != Symbol.GHOST ) {
					System.out.println(reqSymbol + " does not exist/has too little");
					return false;
				}
				else { // if(reqSymbol.getValue() > storSymbol.getValue())
					if(!storIter.hasNext()) {
						System.out.println("Exhausted Array");
						return false;
					}
				}
			}	
		}
		return true;
	}
	
	/**
	 * Play a card at the storage container, assumes card is already playable, so
	 * call isPlayable beforehand
	 */
	public boolean play(Card card, StorageContainer destination) {
		clickedActor = null;
		currentContainer = destination;
		quick = false;
		
		//Play the actual card
		ArrayList<Symbol> requirements,storage;

		
		requirements = card.getInput();
		storage = destination.getStorage();
		
		boolean hasReturn = card.getCenterOutput().contains(Symbol.RETURN);
		
		System.out.println("requirements " + requirements);
		System.out.println("storage " + storage);
		
		int storagePos = 0;
		
		//We are guaranteed there are enough resources
		for(int i = 0; i < requirements.size(); i++) {
			while(storagePos < storage.size()) {
				if(requirements.get(i).equals(storage.get(storagePos))) {
					storage.remove(storagePos);
					break;
				}
				else {
					storagePos++;
				}
			}
		}
		
		add(destination,card.getCenterOutput());
		
		StorageContainer tempContainer;
		
		tempContainer = getLeftContainer(destination);
		if(tempContainer != null) {
			add(tempContainer,card.getLeftOutput());
		}
		
		tempContainer = getRightContainer(destination);
		if(tempContainer != null) {
			add(tempContainer,card.getRightOutput());
		}
		
		//Clean up stuff
		if(!hasReturn) {
			hand.removeCard(card);
			deck.toDiscard(card);
		}
		
		card.clearActions();
		//hand.drawCard();
		
		hand.setHand();
		hand.setHandInteractable(true);
		
		processSymbolQueue();
		
//		turnCounter++;
//		for(StorageContainer sc:storageContainers) {
//			sc.endTurn();
//		}
		
		return false;
	}
	
	private void add(StorageContainer container, ArrayList<Symbol> list) {
		for(Symbol symbol: list) {
			if(symbol.getValue() >= 0) {
				container.addStorage(symbol);	
			}
			else if (symbol.getValue() == -1) {
				applySymbol(symbol);
			}
			else {
				symbolQueue.add(symbol);
			}
		}
	}
	
	/**
	 * Activate the effect of a symbol whose value is -1
	 */
	private void applySymbol(Symbol symbol) {
		switch(symbol) {
		case DRAW1:
			hand.drawCard();
			break;
		case REFRESH4:
			hand.drawStartingHand(4);
			break;
		case REFRESH5:
			hand.drawStartingHand(5);
			break;
		case RETURN:
			//Do nothing
			break;
		case QUICK:
			quick = true;
			break;
		default:
			break;
		}
	}
	
	/**
	 * Process the next symbol in line
	 */
	private void processSymbolQueue() {
		if(symbolQueue.isEmpty()) {
			GameStatus.gamestatus = GameStatus.PLAYING;
			if(!quick) {
				for(StorageContainer sc:storageContainers) {
					sc.endTurn();
				}
				turnCounter++;
			}

			return;
		}
		Symbol nextSymbol = symbolQueue.pop();
		switch(nextSymbol) {
		case DISCARD:
			if(hand.size() > 1) {
				startPrompt(nextSymbol);
				hand.toFront();
			}
			break;
		case MOVE_LEFT:
			//Intentionally Left Blank (Pun not intended)
		case MOVE_RIGHT:
			startPrompt(nextSymbol);
			currentContainer.toFront();
			currentContainer.updateSymbols();
			confirmButton.setVisible(true);
			confirmButton.toFront();
			break;
		case SEARCH:
			LinkedList<Card> available = new LinkedList<Card>();
			available.addAll(deck.getDiscard());
			available.addAll(deck.getDeck());
			
			if(available.isEmpty()) {
				processSymbolQueue();
				return;
			}
			
			startPrompt(nextSymbol);
			game.changeToDeckViewer(available, true);
			//processSymbolQueue();
			break;
		case DELETE:
			LinkedList<Card> deleteable = new LinkedList<Card>();
			deleteable.addAll(deck.getDiscard());
			deleteable.addAll(deck.getDeck());
			deleteable.addAll(hand.getHand().subList(1, hand.size()));
			
			if(deleteable.isEmpty()) {
				processSymbolQueue();
				return;
			}
			
			startPrompt(nextSymbol);
			game.changeToDeckViewer(deleteable, true);
			break;
		case UPGRADE1:
			startUpgrade(1,true);
			break;
		case UPGRADE2:
			startUpgrade(2,true);
			break;
		case UPGRADE3:
			startUpgrade(3,true);
			break;
		case NEW_CARD1:
			startUpgrade(1,false);
			break;
		case NEW_CARD2:
			startUpgrade(2,false);
			break;
		case NEW_CARD3:
			startUpgrade(3,false);
			break;
		default:
			processSymbolQueue();
			break;
		}
	}
	
	/**
	 * Start asking the user for an input
	 */
	private void startPrompt(Symbol symbol) {
		GameStatus.gamestatus = GameStatus.PROMPTING;
		promptSymbol = symbol;
		transparentLayer.setVisible(true);
		transparentLayer.toFront();
	}
	
	/**
	 * Process the received input
	 */
	public void finishPrompt(GameActor ga) {
		transparentLayer.setVisible(false);
		confirmButton.setVisible(false);
		switch(promptSymbol) {
		case DISCARD:
			hand.discardCard((Card)ga);
			GameStatus.gamestatus = GameStatus.PLAYING;
			hand.setHand();
			break;
		case DELETE:
			hand.discardCard((Card)(ga));
			deck.delete((Card)(ga));
			hand.setHand();
			break;
		case MOVE_LEFT:
			StorageContainer leftContainer = this.getLeftContainer(currentContainer);
			if(leftContainer != null) {
				for(Symbol s: currentContainer.getSelected()) {
					leftContainer.addStorage(s);
				}
			}
			currentContainer.processSelected();
			GameStatus.gamestatus = GameStatus.PLAYING;
			currentContainer.updateSymbols();
			break;
		case MOVE_RIGHT:
			StorageContainer rightContainer = this.getRightContainer(currentContainer);
			if(rightContainer != null) {
				for(Symbol s: currentContainer.getSelected()) {
					rightContainer.addStorage(s);
				}
			}
			currentContainer.processSelected();
			GameStatus.gamestatus = GameStatus.PLAYING;
			currentContainer.updateSymbols();
			break;
		case SEARCH:
			hand.drawCard((Card)ga);
			GameStatus.gamestatus = GameStatus.PLAYING;
			hand.setHand();
		default:
			break;
		}
		promptSymbol = Symbol.NONE; //Just in case
		processSymbolQueue();
	}

	/**
	 * Start and open the upgrade window
	 */
	private void startUpgrade(int level, boolean isUpgrade) {
		GameStatus.gamestatus = GameStatus.UPGRADING;
		transparentLayer.setVisible(true);
		upgradeWindow.toFront();
		upgradeWindow.setVisible(true);
		upgradeWindow.setOptions(level, isUpgrade);
		viewButton.setVisible(true);
		viewButton.toFront();
		for(StorageContainer sc: storageContainers) {
			sc.setQuestTouchable(Touchable.disabled);
		}
	}
	
	/**
	 * Return everything to the state before after upgrading
	 */
	public void finishUpgrade() {
		transparentLayer.setVisible(false);
		upgradeWindow.setVisible(false);
		viewButton.setVisible(false);
		processSymbolQueue();
		for(StorageContainer sc: storageContainers) {
			sc.setQuestTouchable(Touchable.enabled);
		}
	}
	
	/**
	 * Unused old code don't call
	 */
	public void startQuest() {
		GameStatus.gamestatus = GameStatus.QUESTING;
		transparentLayer.setVisible(true);
		for(StorageContainer sc : storageContainers) {
			sc.toFront();
			sc.updateSymbols();
		}
		questViewer.toFront();
		questViewer.setTouchable(Touchable.disabled);
		viewButton.setVisible(true);
		viewButton.toFront();
	}
	
	/**
	 * Unused old code don't call
	 */
	public boolean recieveQuest(Symbol s, boolean selected) {
		if(selected) {
			questViewer.removeSymbol(s);
			return true;
		}
		
		if(questViewer.isSymbolNeeded(s)) {
			questViewer.recieveSymbol(s);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Old Unused Version don't call
	 */
	public void finishQuest() {
		if(!questViewer.isComplete()) {
			return;
		}
		System.out.println("finish quest");
		GameStatus.gamestatus = GameStatus.PLAYING;
		transparentLayer.setVisible(false);
		for(StorageContainer sc : storageContainers) {
			sc.processSelected();
			sc.updateSymbols();

		}
		questViewer.setQuest();
		questViewer.setTouchable(Touchable.enabled);
		viewButton.setVisible(false);
		
		
		for(StorageContainer sc: storageContainers) {
			sc.checkQuest();
		}
	}
	
	/**
	 * Process and submit a quest at given storage container
	 */
	public void completeQuest(StorageContainer sc) {
		GameStatus.gamestatus = GameStatus.PLAYING;
		QuestViewer.questProgress++;
		transparentLayer.setVisible(false);
		
		ArrayList<Symbol> pool = sc.getStorage();
		
		for(Symbol s:questViewer.getQuest()) {
			pool.remove(s);
		}
		
		
		questViewer.setQuest();
		questViewer.setTouchable(Touchable.enabled);
		viewButton.setVisible(false);
		
		for(StorageContainer stor: storageContainers) {
			stor.checkQuest();
		}
		
		this.startUpgrade(-1, false);
	}
	
	/**
	 * Removes the transparent layer but not the ability to interact with cards
	 */
	public void toggleView() {
		boolean newVisible = !transparentLayer.isVisible();
		transparentLayer.setVisible(newVisible);
		if(GameStatus.gamestatus == GameStatus.UPGRADING) {
			upgradeWindow.setVisible(newVisible);
		}
	}
	
	
	private int getContainerPos(StorageContainer sc) {
		int pos = -1;
		for(int i = 0 ; i < storageContainers.length; i++) {
			if(storageContainers[i] == sc) {
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	public StorageContainer getLeftContainer(StorageContainer sc) {
		int pos = getContainerPos(sc);
		if(pos == -1)
			return null;
		try {
			return storageContainers[pos-1];
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public StorageContainer getRightContainer(StorageContainer sc) {
		int pos = getContainerPos(sc);		
		if(pos == -1)
			return null;
		try {
			return storageContainers[pos + 1];
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public ArrayList<Symbol> getAllSymbols(){
		ArrayList<Symbol> pool = new ArrayList<Symbol>(24);
		for(int i = 0 ; i < storageContainers.length ; i++) {
			pool.addAll(storageContainers[i].getStorage());
		}
		pool.sort(null);
		System.out.println(pool);
		
		return pool;
	}
	
	
	public ArrayList<Symbol> getQuest() {return questViewer.getQuest();}
	
	public void release() {if(clickedActor instanceof Card)((Card)clickedActor).release();}
	
	public GameActor getClickedActor() {return clickedActor;}
	
	public void setClickedActor(GameActor ga) {clickedActor = ga;}
	
	public void removeClickedActor(GameActor ga) {if(ga == clickedActor) clickedActor = null;}
	
	public GameActor getHoverActor() {return hoverActor;}
	
	public void setHoverActor(GameActor ga) {hoverActor = ga;}
	
	public void removeHoverActor(GameActor ga) {if(ga == hoverActor)hoverActor = null;}

	public void setHand() {hand.setHand();}

	public void setHandInteractable(boolean b) {hand.setHandInteractable(b);}
	
	public void resize(int width, int height) {this.getViewport().update(width,height,true);}
	
	public boolean isViewOnly() {return viewButton.isVisible() && !transparentLayer.isVisible();}


}

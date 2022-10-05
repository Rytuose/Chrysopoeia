package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;

import enums.GameStatus;
import enums.Symbol;

/**
 * 
 * Holds symbols with value 0 or greater
 *
 */
public class StorageContainer extends GameActor {

	public static BitmapFont containerFont = new BitmapFont(Gdx.files.classpath("Consolas.fnt"));
	
	private int attackCountdown, attackAmount;
	private SymbolHolder[] symbolHolders;
	private GameRenderer gameRenderer;
	private ArrayList<Symbol> storage;//,selected;
	private boolean[] selected;
	private Texture unselectedTexture, selectedTexture;
	private boolean canFinishQuest;
	private Button questButton;
	
	public StorageContainer(GameRenderer gr, float x, float y) {
		super();
		
		gameRenderer = gr;
		attackCountdown = (int)(Math.random() * 5) + 1;
		attackAmount = 2;
		canFinishQuest = false;

		storage = new ArrayList<Symbol>();
		symbolHolders = new SymbolHolder[Constants.maxSymbolStorage];
		selected = new boolean[Constants.maxSymbolStorage];
		
		for(int i = 0; i < symbolHolders.length; i++) {
			symbolHolders[i] = new SymbolHolder(gameRenderer,this,i);
			gameRenderer.addActor(symbolHolders[i]);
			symbolHolders[i].setVisible(false);
		}
		
//		storage.add(Symbol.COPPER);
//		
//		storage.add(Symbol.LEAD);
//		
//		storage.add(Symbol.SILVER);
//		
//		storage.add(Symbol.GOLD);
//		
//		storage.add(Symbol.GHOST);
//		
//		storage.sort(null);
		
		unselectedTexture = new Texture(Gdx.files.classpath("CommonCard.png"));
		selectedTexture = new Texture(Gdx.files.classpath("BasicCard1.png"));
		
		super.setTexture(unselectedTexture);
		this.setBounds(x,y,Constants.storageContainerWidth,Constants.storageContainerHeight);
		
		this.addListener(new ClickListener() {
			@Override
		 	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		 		super.enter(event, x, y, pointer, fromActor);
		 		if(pointer == -1) {
		 			StorageContainer.this.enter();
		 		}
		 	}
		 	
		 	@Override
		 	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		 		super.exit(event, x, y, pointer, toActor);
		 		if(pointer == -1) {
		 			StorageContainer.this.exit();
		 		}

		 	}
		 	
		 	@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		 		super.touchUp(event, x, y, pointer, button);
		 		StorageContainer.this.release(); //Handles click-click interactions
		 	}
			
		});
		
		questButton = new Button(selectedTexture, unselectedTexture) {
			@Override
			public void click() {
				gameRenderer.completeQuest(StorageContainer.this);
			}	
		};
		
		gameRenderer.addActor(questButton);
		questButton.setBounds(this.getX(), this.getY() + this.getHeight(), 50, 50);
		questButton.setVisible(false);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		writeText(batch, this.getSprite());
		drawSymbols(batch,this.getSprite());
	}
	
	public void writeText(Batch batch, Sprite sprite) {
		float scale = sprite.getWidth()/Constants.cardWidth;
		
		containerFont.getData().setScale(Constants.cardTextRatio * scale);
		containerFont.setColor(Color.BLACK);
		containerFont.draw(batch, "Attack in: " + attackCountdown + " for " + attackAmount,
				sprite.getX(),
				sprite.getY() + 30,
				sprite.getWidth(), Align.center,false);
		
//		String str = "";
//		
//		for(Symbol symbol : storage) {
//			str += (symbol + "\n");
//		}
//		
//		containerFont.getData().setScale(Constants.cardTextRatio * scale);
//		containerFont.setColor(Color.BLACK);
//		containerFont.draw(batch, str , sprite.getX(), 
//				sprite.getY() + (Constants.cardTextStartY * scale), sprite.getWidth(), Align.center, false);
	}
	
	public void drawSymbols(Batch batch, Sprite sprite) {
		long time = System.nanoTime();
		
		Texture texture;
		
		int size = (storage.size() > 4)?4:storage.size();
		
		float startX = (Constants.storageContainerWidth - (Constants.symbolContainerWidth + Constants.symbolContainerGap)*(size) + Constants.symbolContainerGap)/2; 

		for(int i = 0 ; i < storage.size() ; i++) {
			if(selected[i]) {
				texture = ImageSearcher.getSelectedCardSymbol(storage.get(i));
			}
			else {
				texture = ImageSearcher.getCardSymbol(storage.get(i));
			}
			batch.draw(texture, 
				this.getSprite().getX() + startX + (i%4)*(Constants.symbolContainerWidth + Constants.symbolContainerGap), 
				this.getSprite().getY() + this.getSprite().getHeight() - Constants.symbolContainerTopGap - Constants.symbolContainerHeight - 
				i/4*(Constants.symbolContainerHeight + Constants.symbolContainerGap), 
				Constants.symbolContainerWidth, Constants.symbolContainerHeight);
		}
	}
	
	/**
	 * Sets the position of the symbols in this container
	 */
	public void updateSymbols() {
		
		for(SymbolHolder sh: symbolHolders) {
			sh.setVisible(false);
		}
		
		if(GameStatus.gamestatus != GameStatus.QUESTING && GameStatus.gamestatus != GameStatus.PROMPTING) {
			return;
		}
		
		
		int size = (storage.size() > 4)?4:storage.size();
		float startX = (Constants.storageContainerWidth - (Constants.symbolContainerWidth + Constants.symbolContainerGap)*(size) + Constants.symbolContainerGap)/2; 
		for(int i = 0 ; i < storage.size() ; i++) {
			symbolHolders[i].setVisible(true);
			symbolHolders[i].toFront();
			symbolHolders[i].setPosition(this.getSprite().getX() + startX + (i%4)*(Constants.symbolContainerWidth + Constants.symbolContainerGap), 
				this.getSprite().getY() + this.getSprite().getHeight() - Constants.symbolContainerTopGap - Constants.symbolContainerHeight - 
				i/4*(Constants.symbolContainerHeight + Constants.symbolContainerGap));
		}
	}

	
	/**
	 * Finds the corresponding symbol to the position selected from a prompt
	 */
	public void select(int position) {
		System.out.println("Clicked symbol " + position + " " + GameStatus.gamestatus);
		Symbol symbol = storage.get(position);
		
		if(GameStatus.gamestatus == GameStatus.QUESTING) {
			if(gameRenderer.recieveQuest(symbol,selected[position])) {
				selected[position] = !selected[position];
			}
			gameRenderer.finishQuest();
		}
		else {
			selected[position] = !selected[position];
		}
	}
	
	/**
	 * Adds a symbol to this container
	 */
	public void addStorage(Symbol s) {
		if(storage.size() < Constants.maxSymbolStorage) {
			storage.add(s);
			storage.sort(null);
		}
	}

	/**
	 * Removes all selected symbols
	 */
	public void processSelected() {
		int position = 0;;
		for(int i = 0; i < selected.length; i++) {
			if(selected[i]) {
				storage.remove(position);
				selected[i] = false;
			}
			else {
				position ++;
			}
		}
	}
	
	public ArrayList<Symbol> getSelected(){
		ArrayList<Symbol> selectedSymbols = new ArrayList<Symbol>();
		for(int i = 0; i < selected.length; i++) {
			if(selected[i]) {
				selectedSymbols.add(storage.get(i));
			}
		}
		return selectedSymbols;
	}
	
	public void endTurn() {
		attackCountdown --;
		if(attackCountdown <= 0) {
			//System.out.println("Attack " + storage.toString());
			int ghosts = 0;
			int material = 0;
			int attack = attackAmount;
			for(Symbol s: storage) {
				if(s == Symbol.GHOST) {
					ghosts++;
				}
				else {
					material++;
				}
			}
			attack -= ghosts;
			
			//System.out.println("Ghosts " + ghosts + " Attack " + attack);
			
			while(attack > 0 && storage.size() - ghosts > 0) {
				int randPos = (int)(Math.random() * (storage.size() - ghosts) + ghosts);
				//System.out.println("Removing position " + randPos);
				storage.remove(randPos);
				attack --;
			}
			
			attackAmount = 0;
			attackCountdown = 0;
			
			//attackCountdown = (int)(Math.random() * 3) + 3;
			
			if(Math.random() < .07 * material) {
				int min = (int)(material/2.3);
				int max = (int)(material/1.8);
				
				attackAmount = (int)(Math.random() * (max - min)) + min;
				
				min = (int)((attackAmount*1.2) + .5) + 1;
				max = (int)((attackAmount*1.5) + .5) + 1;
				
				attackCountdown = (int)(Math.random() * (max - min)) + min;
			}
			
		}
		
		checkQuest();

	}
	
	public void checkQuest() {
		if(gameRenderer.containsAll(gameRenderer.getQuest(), storage)) {
			this.getSprite().setTexture(selectedTexture);
			canFinishQuest = true;
		}
		else {
			this.getSprite().setTexture(unselectedTexture);
			canFinishQuest = false;
		}
		questButton.setVisible(canFinishQuest);
	}
	
	public void setQuestTouchable(Touchable touchable) {questButton.setTouchable(touchable);}
	
	private void enter() {gameRenderer.setHoverActor(this);}
	
	private void exit() {gameRenderer.removeHoverActor(this);}
	
	public boolean getCanFinishQuest() {return canFinishQuest;}

	public ArrayList<Symbol> getStorage() {return storage;}
	
	private void release() {
		if(GameStatus.gamestatus == GameStatus.PLAYING) {
			gameRenderer.release();
		}
	}
	
}

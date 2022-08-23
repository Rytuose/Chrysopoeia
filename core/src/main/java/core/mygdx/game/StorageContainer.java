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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;

import enums.GameStatus;
import enums.Symbol;

public class StorageContainer extends GameActor {

	public static BitmapFont containerFont = new BitmapFont(Gdx.files.classpath("Consolas.fnt"));
	
	private SymbolHolder[] symbolHolders;
	private GameRenderer gameRenderer;
	private ArrayList<Symbol> storage;//,selected;
	private boolean[] selected;
	
	public StorageContainer(GameRenderer gr, float x, float y) {
		super();
		
		gameRenderer = gr;
		

		storage = new ArrayList<Symbol>();
		symbolHolders = new SymbolHolder[Constants.maxSymbolStorage];
		selected = new boolean[Constants.maxSymbolStorage];
		
		for(int i = 0; i < symbolHolders.length; i++) {
			symbolHolders[i] = new SymbolHolder(gameRenderer,this,i);
			gameRenderer.addActor(symbolHolders[i]);
			symbolHolders[i].setVisible(false);
		}
		
		storage.add(Symbol.COPPER);
		
		storage.add(Symbol.LEAD);
		
		storage.add(Symbol.SILVER);
		
		storage.add(Symbol.GOLD);
		
		storage.add(Symbol.GHOST);
		
		storage.sort(null);
		
		super.setTexture(new Texture(Gdx.files.classpath("CommonCard.png")));
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
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		//writeText(batch, this.getSprite());
		drawSymbols(batch,this.getSprite());
	}
	
	public void writeText(Batch batch, Sprite sprite) {
		float scale = sprite.getWidth()/Constants.cardWidth;
		
		String str = "";
		
		for(Symbol symbol : storage) {
			str += (symbol + "\n");
		}
		
		containerFont.getData().setScale(Constants.cardTextRatio * scale);
		containerFont.setColor(Color.BLACK);
		containerFont.draw(batch, str , sprite.getX(), 
				sprite.getY() + (Constants.cardTextStartY * scale), sprite.getWidth(), Align.center, false);
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

	public void select(int position) {
		System.out.println("Clicked symbol " + position);
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
	
	public void addStorage(Symbol s) {
		if(storage.size() < Constants.maxSymbolStorage) {
			storage.add(s);
			storage.sort(null);
		}
	}

	
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
	
	private void enter() {gameRenderer.setHoverActor(this);}
	
	private void exit() {gameRenderer.removeHoverActor(this);}

	public ArrayList<Symbol> getStorage() {return storage;}
	
	private void release() {
		if(GameStatus.gamestatus == GameStatus.PLAYING) {
			gameRenderer.release();
		}
	}
	
}

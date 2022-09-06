package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import enums.Symbol;

public class QuestViewer extends GameActor {
	
	private GameRenderer gameRenderer;
	private ArrayList<Symbol> quest;
	private ArrayList<Boolean> completed;
	private int startX,startY,questStage;
	private StorageContainer[] storageContainers;
	
	public QuestViewer(GameRenderer gr, StorageContainer[] sc) {
		super();
		gameRenderer = gr;
		this.setTexture(new Texture(Gdx.files.classpath("CommonCard.png")));
		this.setBounds(getX(), getY(), Constants.questWidth, Constants.questHeight);
		quest = new ArrayList<Symbol>();
		completed = new ArrayList<Boolean>();
		startX = 0;
		questStage = 0;
		storageContainers = sc;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		Texture texture;
		
		for(int i = 0 ; i < quest.size() ; i++) {
			if(completed.get(i)) {
				texture = ImageSearcher.getSelectedCardSymbol(quest.get(i));
			}
			else {
				texture = ImageSearcher.getCardSymbol(quest.get(i));
			}
			batch.draw(texture,
					startX + i * (Constants.questSymbolWidth + Constants.questSymbolGap),
					startY, Constants.questSymbolWidth, Constants.questSymbolHeight);
		}
	}
//	
//	public void drawQuestSymbol(Batch batch) {
//		if(questPos >= 0) {
//			batch.draw(ImageSearcher.getCardSymbol(quest.get(questPos)),
//					startX + questPos * (Constants.questSymbolWidth + Constants.questSymbolGap),
//					startY, Constants.questSymbolWidth, Constants.questSymbolHeight);
//		}
//		
////		
////		for(int i = 0 ; i < quest.size() ; i+=2) {
////			batch.draw(ImageSearcher.getCardSymbol(quest.get(i)),
////					startX + i * (Constants.questSymbolWidth + Constants.questSymbolGap),
////					startY, Constants.questSymbolWidth, Constants.questSymbolHeight);
////		}
//	}
	
	
	public void setQuest() {
		questStage++;
		quest.clear();
		completed.clear();
		
		for(int i = 0; i < questStage; i++) {
			switch(i%4){
			case 0:
				quest.add(Symbol.LEAD);
				break;
			case 1:
				quest.add(Symbol.COPPER);
				break;
			case 2:
				quest.add(Symbol.SILVER);
				break;
			case 3:
				quest.add(Symbol.GOLD);
				break;
			}
		}

		quest.sort(null);
		for(int i = 0; i < quest.size(); i++) {
			completed.add(false);
		}
		
		startX = (int) (this.getX() + (this.getWidth() - (Constants.questSymbolWidth + Constants.questSymbolGap)* quest.size() + Constants.questSymbolGap)/2);
		startY = (int) (this.getY() + (this.getHeight() - Constants.questSymbolHeight)/2);
		
	}
	
	public boolean isSymbolNeeded(Symbol s) {
		for(int i = 0; i < quest.size(); i++) {
			if(quest.get(i)==s && completed.get(i)==false) {
				return true;
			}
			else if(s.getValue() < quest.get(i).getValue()) {
				return false;
			}
		}
		return false;
	}
	
	public void recieveSymbol(Symbol s) {
		for(int i = 0; i < quest.size(); i++) {
			if(quest.get(i)==s && completed.get(i)==false) {
				completed.set(i, true);
				return;
			}
		}
	}
	
	public void removeSymbol(Symbol s) {
		for(int i = quest.size()-1; i >= 0; i--) {
			if(quest.get(i)==s && completed.get(i) == true) {
				completed.set(i, false);
				return;
			}
		}
	}
	
	public boolean isComplete() {
		for(int i = 0 ; i< quest.size(); i++) {
			if(!completed.get(i)) {
				return false;
			}
		}
		return true;
	}

	public void click() {
		//Can't use contains as quantity matters
		boolean isPlayable = false;
		
//		ArrayList<Symbol> pool = gameRenderer.getAllSymbols();
		
		
//		isPlayable = gameRenderer.containsAll(quest, pool);
//		
//		System.out.println("isPlayabe " + isPlayable );
		
		for(StorageContainer sc: storageContainers) {
			if(sc.getCanFinishQuest()) {
				isPlayable = true;
			}
		}
		
		if(isPlayable) {
			gameRenderer.startQuest();
		}
	}
	
	public ArrayList<Symbol> getQuest() { return quest;}
}

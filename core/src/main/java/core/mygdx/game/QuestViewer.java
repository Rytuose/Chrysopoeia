package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import enums.Symbol;

public class QuestViewer extends Button {
	
	private GameRenderer gameRenderer;
	private ArrayList<Symbol> quest;
	private ArrayList<Boolean> completed;
	private int startX,startY,questStage;
	
	public QuestViewer(GameRenderer gr) {
		super(new Texture(Gdx.files.classpath("CommonCard.png")),
				new Texture(Gdx.files.classpath("RareCard.png")));
		gameRenderer = gr;
		this.setBounds(getX(), getY(), Constants.questWidth, Constants.questHeight);
		quest = new ArrayList<Symbol>();
		completed = new ArrayList<Boolean>();
		startX = 0;
		questStage = 0;
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

	@Override
	public void click() {
		//Can't use contains as quantity matters
		ArrayList<Symbol> pool = gameRenderer.getAllSymbols();
		
		boolean isPlayable = gameRenderer.containsAll(quest, pool);
		
		System.out.println("isPlayabe " + isPlayable );
		
		if(isPlayable) {
			gameRenderer.startQuest();
		}
	}
}

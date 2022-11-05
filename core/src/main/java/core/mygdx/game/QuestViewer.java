package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import enums.Bias;
import enums.Symbol;

/**
 * 
 * Displays and manages quests
 *
 */
public class QuestViewer extends GameActor {
	
	private static Bias[] biasOrder = {Bias.NONE, Bias.PRODUCTION, Bias.TRADE, Bias.GHOST};
	public static int questProgress = 0;
	
	private boolean isQuest;
	private GameRenderer gameRenderer;
	private ArrayList<Symbol> quest;
	private ArrayList<Boolean> completed;
	private int startX,startY,questStage;
	private StorageContainer[] storageContainers;
	private Button rightSwapButton, leftSwapButton;	
	private BiasSwitch[] biasSwitches;
	
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
		isQuest = true;

		rightSwapButton = new Button(new Texture(Gdx.files.classpath("CommonCard.png")),
				new Texture(Gdx.files.classpath("RareCard.png"))) {
			@Override
			public void click() {
				swapQuest();
			}
		};
		
		gameRenderer.addActor(rightSwapButton);
		
		
		leftSwapButton = new Button(new Texture(Gdx.files.classpath("CommonCard.png")),
				new Texture(Gdx.files.classpath("RareCard.png"))) {
			@Override
			public void click() {
				swapQuest();
			}
		};
		
		gameRenderer.addActor(leftSwapButton);
		
		
		biasSwitches = new BiasSwitch[biasOrder.length];
		for(int i = 0 ; i < biasOrder.length; i++) {
			biasSwitches[i] = new BiasSwitch(new Texture(Gdx.files.classpath("UncommonCard.png")),
				new Texture(Gdx.files.classpath("BasicCard.png")), this,  biasOrder[i]);
			gameRenderer.addActor(biasSwitches[i]);
			biasSwitches[i].setVisible(false);
		}
		biasSwitches[0].click();
		setBiasPositions();
		
		//swapQuest();

	}

	@Override
	public void positionChanged() {
		super.positionChanged();
		rightSwapButton.setBounds(this.getX() + this.getWidth(),this.getY(),
				Constants.questSwapWidth, Constants.questHeight);
		leftSwapButton.setBounds(this.getX() - Constants.questSwapWidth, this.getY(),
				Constants.questSwapWidth, Constants.questHeight);
		setBiasPositions();
	}
	
	private void setBiasPositions() {
		float startX = this.getX() + (this.getWidth() - (Constants.biasButtonWidth * biasSwitches.length))/2;
		
		for(int i = 0; i < biasSwitches.length; i++) {
			biasSwitches[i].setPosition(startX + (i*Constants.biasButtonWidth), this.getY());
			biasSwitches[i].toFront();
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		Texture texture;
		
		if(isQuest) {
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
	}
	
	private void swapQuest() {
		isQuest = !isQuest;
		boolean isVisible = (isQuest)?false:true;
		for(int i = 0 ; i < biasSwitches.length; i++) {
			biasSwitches[i].setVisible(isVisible);
		}
	}
	
	/**
	 * Creates a new quest
	 */
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
	
	/**
	 * Old don't use
	 */
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
	
	/**
	 * Old don't use
	 */
	public void recieveSymbol(Symbol s) {
		for(int i = 0; i < quest.size(); i++) {
			if(quest.get(i)==s && completed.get(i)==false) {
				completed.set(i, true);
				return;
			}
		}
	}
	
	/**
	 * Old don't use
	 */
	public void removeSymbol(Symbol s) {
		for(int i = quest.size()-1; i >= 0; i--) {
			if(quest.get(i)==s && completed.get(i) == true) {
				completed.set(i, false);
				return;
			}
		}
	}
	
	/**
	 * Old don't use
	 */
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
	
	public void selectBias(BiasSwitch bs) {
		boolean noSelect = true;
		for(int i = 0 ; i < biasSwitches.length; i++) {
			if(biasSwitches[i].getSelected()) {
				noSelect = false;
			}
			if(biasSwitches[i] != bs && biasSwitches[i].getSelected()) {
				biasSwitches[i].toggleSelect();
				biasSwitches[i].reset();
			}
		}
		
		if(noSelect) {
			biasSwitches[0].click();
		}
	}
}

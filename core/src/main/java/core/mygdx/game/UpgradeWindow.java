package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

/**
 * 
 * A window that holds the upgrade options
 *
 */
public class UpgradeWindow extends GameActor {

	private GameRenderer gameRenderer;
	private Deck deck;
	private UpgradeOption[] upgradeOptions;
	private Button exitButton;
	
	public UpgradeWindow(GameRenderer gr,Deck d) {
		super();
		
		super.setTexture(new Texture(Gdx.files.classpath("RareCard.png")));
		this.setBounds((1-Constants.upgradeWindowWidthRatio)*Constants.game_width*Constants.upgradeWindowHorizGapRatio,
				(1-Constants.upgradeWindowHeightRatio)*Constants.game_height/2,
				Constants.upgradeWindowWidthRatio * Constants.game_width,
				Constants.upgradeWindowHeightRatio * Constants.game_height);
		
		gameRenderer = gr;
		deck = d;
		upgradeOptions = new UpgradeOption[4];
		
		for(int i = 0 ; i < upgradeOptions.length; i++) {
			upgradeOptions[i] = new UpgradeOption(gr,deck,
					getX() + i%2*Constants.upgradeOptionWidth,
					getY() + getHeight() - (i/2 + 1) * Constants.upgradeOptionHeight);
		}
		
		exitButton = new Button(new Texture(Gdx.files.classpath("UncommonCard.png")),
				new Texture(Gdx.files.classpath("BasicCard.png"))) {
					@Override
					public void click() {
						gameRenderer.finishUpgrade();
					}
		};
		
		gameRenderer.addActor(exitButton);
		exitButton.setBounds(0, 0, Constants.upgradeExitWidth, Constants.upgradeExitHeight);
		exitButton.setPosition(this.getX() + this.getWidth() - Constants.upgradeExitWidth/2,
				this.getY() - Constants.upgradeExitHeight/2);
		exitButton.setVisible(false);
	}
	
	/**
	 * Sets the options of all upgrade options and removes duplicate upgrades
	 */
	public void setOptions(int level, boolean isUpgrade) {
		System.out.println("Upgradeing level " + -1);
		deck.resetUpgradeOptions(level);
		for(UpgradeOption uo: upgradeOptions) {
			uo.setOptions(level, isUpgrade);
		}
		
		//Prevent Duplicates
		for(int i = 0; i < upgradeOptions.length; i++) {
			for(int j = 0; j < i; j++) {
				if(upgradeOptions[i].getUpgradeCard().equals(upgradeOptions[j].getUpgradeCard()) &&
						upgradeOptions[i].getUpgradeResult().equals(upgradeOptions[j].getUpgradeResult())) {
					upgradeOptions[i].setOptions(level, isUpgrade); 
					j =  -1;
				}
			}
		}
		
		for(int i = 0 ; i < upgradeOptions.length - 1; i++) {
			for(int j = 0; j < upgradeOptions.length - i - 1; j++) {
				System.out.println("Comparing " + j + " and " + (j+1));
				if(upgradeOptions[j].getCardAmount() < upgradeOptions[j+1].getCardAmount()) {
					System.out.println("SWAPPING");
					
					UpgradeOption first,second,temp;
					float tempPos;
					
					first = upgradeOptions[j];
					second = upgradeOptions[j+1];
					
					tempPos = first.getX();
					first.setX(second.getX());
					second.setX(tempPos);
					
					tempPos = first.getY();
					first.setY(second.getY());
					second.setY(tempPos);
					
					temp = upgradeOptions[j];
					upgradeOptions[j] = upgradeOptions[j+1];
					upgradeOptions[j+1] = temp;
					
					first.updatePosition();
					first.updateUpgradeButton();
					second.updatePosition();
					second.updateUpgradeButton();
				}
			}
		}
	}
	
	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);			
		exitButton.setVisible(isVisible);
		exitButton.toFront();
		for(UpgradeOption uo: upgradeOptions) {
			uo.setVisible(isVisible);
		}
	}
	
}

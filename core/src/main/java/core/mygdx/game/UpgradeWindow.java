package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

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
	
	public void setOptions(int level, boolean isUpgrade) {
		deck.resetUpgradeOptions(level);
		for(UpgradeOption uo: upgradeOptions) {
			uo.setOptions(level, isUpgrade);
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

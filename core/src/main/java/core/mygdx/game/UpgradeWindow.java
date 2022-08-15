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
	}
	
	public void setOptions() {
		deck.resetUpgradeOptions();
		for(UpgradeOption uo: upgradeOptions) {
			uo.setOptions();
		}
	}
	
	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);																													
		for(UpgradeOption uo: upgradeOptions) {
			uo.setVisible(isVisible);
		}
	}
	
}

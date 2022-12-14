package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * The confirm button for an upgrade choice
 */
public class UpgradeButton extends Button {

	private UpgradeOption upgradeOption;
	private Texture normalTexture, hoverTexture;
	private boolean touchDown;
	
	public UpgradeButton(UpgradeOption uo) {
		super(new Texture(Gdx.files.classpath("UncommonCard.png")),
				new Texture(Gdx.files.classpath("BasicCard.png")));
		
		upgradeOption = uo;

		this.setBounds(getX(), getY(), Constants.biasButtonWidth, Constants.upgradeButtonHeight);
	}

	@Override
	public void click() {
		upgradeOption.select();
	}
	
}

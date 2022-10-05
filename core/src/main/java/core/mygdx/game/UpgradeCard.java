package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * 
 * A card meant only to display upgrades and therefore does not
 * respond to mouse clicks
 *
 */
public class UpgradeCard extends Card {

	public UpgradeCard(GameRenderer gr) {
		super(gr);
		
		this.removeListener(cardListener);
		this.addListener(new ClickListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
			}
		});
		this.setBounds(getX(), getY(), Constants.cardUpgradeWidth, Constants.cardUpgradeHeight);
	}
	
	

}

package core.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import enums.Bias;

public class BiasSwitch extends Button {

	private QuestViewer questViewer;
	private Bias bias;
	boolean selected;
	
	public BiasSwitch(Texture nt, Texture ht, QuestViewer qv, Bias b) {
		super(nt,ht);
		bias = b;
		selected = false;
		questViewer = qv;
		
		this.setBounds(getX(), getY(), Constants.biasButtonWidth, Constants.questHeight);
	}

	public void click() {
		Bias.bias = bias;
		
		toggleSelect();
		
		questViewer.selectBias(this);
		
		BiasSwitch.this.getSprite().setTexture(normalTexture);
	}
	
	public void toggleSelect() {
		selected = !selected;
		Texture temp;
		temp = normalTexture;
		normalTexture = hoverTexture;
		hoverTexture = temp;
	}
	
	public void reset() {
		this.getSprite().setTexture(normalTexture);
	}
	
	public boolean getSelected() {
		return selected;
	}

}

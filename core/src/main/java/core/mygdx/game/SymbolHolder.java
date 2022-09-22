package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import enums.Symbol;

public class SymbolHolder extends GameActor {
	
	private GameRenderer gameRenderer;
	private StorageContainer storageContainer;
	private int position;
	private Circle range;
	
	public SymbolHolder(GameRenderer gr, StorageContainer sc, int pos) {
		super();
		this.setBounds(getX(), getY(), Constants.symbolContainerWidth, Constants.symbolContainerHeight);
		gameRenderer = gr;
		storageContainer = sc;
		position = pos;
		
		range = new Circle(this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2, this.getWidth()/2);
		
		this.addListener(new ClickListener() {
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				System.out.println("Touch Up");
				if(!gameRenderer.isViewOnly() && this.isOver()) {
					storageContainer.select(position);
				}
//				if(range.contains(Gdx.input.getX(),(Constants.game_height-Gdx.input.getY()))) {
//					if(!gameRenderer.isViewOnly()) {
//						storageContainer.select(position);
//					}
//				}
			}
		});
		
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		range.setPosition(x + range.radius, y + range.radius);
	}
}

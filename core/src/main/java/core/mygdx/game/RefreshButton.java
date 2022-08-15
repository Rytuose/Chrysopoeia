package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class RefreshButton extends GameActor {
	
	//====================================FIELDS==========================================
	
	private Hand hand;
	private GameRenderer gameRenderer;
	private Circle range;
	private Sprite sprite,hoverSprite;
	private boolean isOver,isOverCircle;
	
	
	
	//==================================CONSTRUCTORS======================================
	
	/**@param br The battle renderer the button is attached to*/
	public RefreshButton(GameRenderer gr, Hand h) {
		super();
		gameRenderer = gr;
		hand = h;
		
		
		sprite = new Sprite(new Texture(Gdx.files.classpath("Refresh1.png")));
		sprite.setBounds(Constants.game_width - Constants.refreshButtonRadius, -Constants.refreshButtonRadius,
				Constants.refreshButtonRadius*2, Constants.refreshButtonRadius*2);
		hoverSprite = new Sprite(new Texture(Gdx.files.classpath("Refresh2.png")));
		hoverSprite.setBounds(Constants.game_width - Constants.refreshButtonRadius, -Constants.refreshButtonRadius,
				Constants.refreshButtonRadius*2, Constants.refreshButtonRadius*2);
		this.setSprite(sprite);
		
		//Creates the circular range of the button
		this.setBounds(Constants.game_width - Constants.refreshButtonRadius, -Constants.refreshButtonRadius,
				Constants.refreshButtonRadius*2, Constants.refreshButtonRadius*2);
		
		range = new Circle(this.getX() + Constants.refreshButtonRadius,
				this.getY() + Constants.refreshButtonRadius, Constants.refreshButtonRadius);
		
		isOver = false;
		
		//Adding the listener
		this.addListener(new ClickListener() {
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				if(pointer == -1) {
					//System.out.println("Enter");
					isOver = true;
				}
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.exit(event, x, y, pointer, fromActor);
				if(pointer == -1) {
					//System.out.println("Exit");
				}
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				
				if(isOverCircle) {
					//hand.drawStartingHand();
				}

			}
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(isOver) {
			if(range.contains(Gdx.input.getX(),(Constants.game_height-Gdx.input.getY()))) {
				if(!isOverCircle) {
					isOverCircle = true;
					this.setSprite(hoverSprite);
				}
			}
			else {
				if(isOverCircle) {
					isOverCircle = false;
					this.setSprite(sprite);
				}
			}
		}
	}
	
}

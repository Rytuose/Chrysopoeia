package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import enums.GameStatus;

public class CardListener extends ClickListener {

private boolean isFirst;
	
	private int count;
	private Card card;
	private RepeatAction ra;
	
	public CardListener(Card card) {
		this.card = card;
		isFirst = false;
		ra = new RepeatAction();
		count = 0;
	}
	
	@Override
	public void touchDragged(InputEvent event,float x,float y,int pointer) {
		super.touchDragged(event, x, y, pointer);

		//TODO maybe make a timer to prevent errors with rapid clicking
		if(card.getStage() instanceof GameRenderer) {
			if(count > 5) {
				isFirst = true;
				count = 0;
			}
			else {
				count++;
			}
		}
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		super.touchDown(event, x, y, pointer, button);
		if(card.getStage() instanceof GameRenderer) {
			click();
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		super.touchUp(event, x, y, pointer, button);
		if(card.getStage() instanceof GameRenderer) {
			isFirst = !isFirst;
			
			if(GameStatus.gamestatus == GameStatus.PROMPTING && isOver()) {
				card.recievePrompt();
				//System.out.println("Release");
			}
			else if(!isFirst) {
				card.release(); //handles click-drag interactions
				isFirst = false;
			}
		}
	}
	
	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		super.enter(event, x, y, pointer, fromActor);
		
		if(card.getStage() instanceof GameRenderer) {

			if(pointer == -1 && this.isOver()) {
				card.enter();
			}
		}
	}
	
	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		super.exit(event, x, y, pointer, toActor);
		if(card.getStage() instanceof GameRenderer) {
			if(pointer == -1) {
				//something something something is null now
				//Me from after the above post, idk what this is
				//9-23-21 ^
				//5-13-22 I need help I haven't done this in months and am reworking everything
				card.exit();
			}
			
		}
	}
	
	/**
	 * Passes what is considered a click to the card
	 */
	private void click() {
		
		System.out.println("click");
		
		if((!card.getActions().contains(ra, false) && GameStatus.gamestatus == GameStatus.PLAYING)) {
			
			//If not prompting make the card follow the mouse
			float deltaX = Gdx.input.getX()/Constants.getRatio() -card.getSprite().getX();
			float deltaY = Constants.game_height - Gdx.input.getY()/Constants.getRatio()  - card.getSprite().getY();
			//Constants.game_height - Gdx.input.getY()/Constants.widthRatio()  - card.getSprite().getY();
			ra.setAction(new FollowAction(deltaX,deltaY));
			ra.setCount(RepeatAction.FOREVER);
			card.addAction(ra);
			
			card.click();
		}
	}

	public void resetIsFirst() {
		isFirst = false;
	}
	
}

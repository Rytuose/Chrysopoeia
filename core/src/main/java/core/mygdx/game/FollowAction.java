package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;

public class FollowAction extends Action {

	private float DeltaX, DeltaY, OriginalHeight;
	
	public FollowAction(float XDiff, float YDiff) {
		super();
		DeltaX = XDiff;
		DeltaY = YDiff;
		OriginalHeight = Constants.game_height;
	}
	
	@Override
	public boolean act(float delta) {
//		this.getActor().setPosition(Gdx.input.getX()/Constants.widthRatio() - DeltaX, 
//				OriginalHeight - Gdx.input.getY()/Constants.heightRatio() - DeltaY);
		
		//System.out.println(Constants.heightRatio() + " " + Constants.widthRatio() + "\n\t" + Gdx.input.getX() + " " + Gdx.input.getY());
		
		this.getActor().setPosition(Gdx.input.getX()/Constants.getRatio() - DeltaX, 
				OriginalHeight - Gdx.input.getY()/Constants.getRatio() - DeltaY);
		
//		this.getActor().setPosition(Gdx.input.getX()*Constants.getRatio(),
//				OriginalHeight - Gdx.input.getY());
		
//		this.getActor().setPosition(Gdx.input.getX() - DeltaX, Gdx.graphics.getHeight() - Gdx.input.getY() - DeltaY);
		return true;
	}

}

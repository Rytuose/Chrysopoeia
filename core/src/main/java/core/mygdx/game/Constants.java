package core.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;

public class Constants {
	
	public static int game_width = 960;
	public static int game_height = 600;

	public static float widthRatio() {return ((float)Gdx.graphics.getWidth())/game_width;}
	public static float heightRatio() {return ((float)Gdx.graphics.getHeight())/game_height;}
	public static float getRatio() {return(widthRatio() < heightRatio())?widthRatio():heightRatio();}
	
	public static final int cardWidth = 150;
	public static final int cardHeight = 210;
	
	public static final float deckRendererCardScale = 1.2f;
	public static final int deckRendererCardWidth = (int) (cardWidth*deckRendererCardScale);
	public static final int deckRendererCardHeight = (int) (cardHeight*deckRendererCardScale);
	
	public static final float deckRendererViewingCardScale = 2.5f;
	public static final int deckRendererViewingCardWidth = (int) (cardWidth*deckRendererViewingCardScale);
	public static final int deckRendererViewingCardHeight = (int) (cardHeight*deckRendererViewingCardScale);
	
	public static final int deckRendererCardGap = 20;
	public static final int deckRendererTopGap = 30;
	public static final float cardHideRatio = (float)(-1.0/3);
	public static final int cardHoverWidth = 200;
	public static final int cardHoverHeight = 280;
	public static final float cardOverlapRatio = (float) (2.0/7);
	
	public static final float cardTextRatio = .3f;
	public static final int cardTextStartY = 100;
	
	public static final int storageContainerWidth = 200;
	public static final int storageContainerHeight = 150;
	public static final int storageContainerGap = 50;
	public static final int storageContainerY = 200;
	public static final int maxSymbolStorage = 8;
	
	public static final int handSize = 4;
	
	public static final int symbolWidth = 14;
	public static final int symbolHeight = 14;
	public static final int symbolGap = 5;
	public static final int cardSymbolStartY = 40;
	
	public static final int symbolContainerWidth = 30;
	public static final int symbolContainerHeight = 30;
	public static final int symbolContainerGap = 7;
	public static final int symbolContainerTopGap = 10;
	
	public static final int refreshButtonRadius = 100;
	public static final float transparencyRatio = .7f;
	
	public static final float upgradeWindowWidthRatio = .80f;
	public static final float upgradeWindowHeightRatio = .80f;
	public static final float upgradeWindowHorizGapRatio = .5f;
	
	public static final float upgradeOptionWidth = game_width*upgradeWindowWidthRatio/2;
	public static final float upgradeOptionHeight = game_height*upgradeWindowHeightRatio/2;
	public static final int cardUpgradeWidth = 120;
	public static final int cardUpgradeHeight = 168;
	public static final int cardUpgradeCenterGap = 50;
	public static final float cardOptionLowerRatio = .7f;
	
	public static final int upgradeButtonWidth = 120;
	public static final int upgradeButtonHeight = 30;

	
	public static final int viewButtonWidth = 80;
	public static final int viewButtonHeight = 60;
	
	public static final int questWidth = 400;
	public static final int questHeight = 50;
	public static final int questSymbolWidth = 40;
	public static final int questSymbolHeight = 40;
	public static final int questSymbolGap = 10;
	
	public static final int biasButtonWidth = Constants.questWidth/4;
	public static final int questSwapWidth = 25;
	
	public static final int deckButtonXGap = 30;
	public static final int deckButtonYGap = 25;
	public static final int deckButtonWidth = 75;
	public static final int deckButtonHeight = 75;

	public static final int scrollBarWidth = 25;
	public static final int scrollBarXGap = 15; 
	public static final int scrollBarYGap = 20;
	
	public static final int confirmButtonWidth = 125;
	public static final int confirmButtonHeight = 65;
	public static final int confirmButtonXGap = 50;
	public static final int confirmButtonYGap = 120;
	
	public static final int deckExitButtonXGap = 20;
	public static final int deckExitButtonYGap = 30;
	public static final int deckExitButtonWidth = 50;
	public static final int deckExitButtonHeight = 50;
	public static final int deckConfirmWidth = 150;
	public static final int deckConfirmHeight = 50;
	public static final int deckConfirmXGap = Constants.scrollBarWidth 
			+ Constants.scrollBarXGap*2;
	public static final int deckConfirmYGap = 25;
	
	public static final int upgradeExitWidth = 100;
	public static final int upgradeExitHeight = 25;

}

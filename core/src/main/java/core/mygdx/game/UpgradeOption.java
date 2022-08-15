package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UpgradeOption extends Actor {
	
	private GameRenderer gameRenderer;
	private Deck deck;
	private UpgradeCard upgradeCard,upgradeResult;
	private Card card,upgradeQualities;
	private UpgradeButton upgradeButton;
	
	
	public UpgradeOption(GameRenderer gr, Deck d, float x, float y) {
		
		this.setBounds(x, y, Constants.upgradeOptionWidth, Constants.upgradeOptionHeight);
		
		gameRenderer = gr;
		deck = d;
		upgradeCard = new UpgradeCard(gr);
		upgradeResult = new UpgradeCard(gr);
		
		gameRenderer.addActor(upgradeCard);
		upgradeCard.setPosition(x, y);
		upgradeCard.setVisible(false);
		
		gameRenderer.addActor(upgradeResult);
		upgradeResult.setPosition(x, y);
		upgradeResult.setVisible(false);
		
		upgradeQualities = new Card(gr);
		
		upgradeButton = new UpgradeButton(this);
		gameRenderer.addActor(upgradeButton);
		upgradeButton.setVisible(false);
		upgradeButton.setPosition(this.getX() + (this.getWidth() - upgradeButton.getWidth())/2,
				this.getY() + ((this.getHeight() - Constants.cardUpgradeHeight) * Constants.cardOptionLowerRatio - upgradeButton.getHeight())/2);
	}
	
	public void setOptions() {
		
		upgradeCard.setVisible(true);
		upgradeCard.toFront();
		
		upgradeResult.setVisible(true);
		upgradeResult.toFront();
		
		upgradeButton.setVisible(true);
		
		card = deck.randomUgradeCard();
		
		int cards = 2;
		
		float cardGap = (this.getWidth() - cards*Constants.cardUpgradeWidth - (cards-1) * Constants.cardUpgradeCenterGap)/2;
		float cardY = (this.getHeight() - Constants.cardUpgradeHeight) * Constants.cardOptionLowerRatio;
		
		System.out.println(cardGap + " " + cardY);
		System.out.println(this.getX() + " " + this.getY());
		
		upgradeCard.setPosition(this.getX() + cardGap,this.getY() + cardY);
		upgradeCard.copyCard(card);
		
		if(cards > 1) {
			upgradeResult.setPosition(this.getX() + cardGap + Constants.cardUpgradeWidth + Constants.cardUpgradeCenterGap,
					this.getY() + cardY);
			UpgradeManager.createUpgrade(upgradeCard, upgradeResult,card.getLevel());
//			upgradeResult.copyCard(upgradeCard);
//			UpgradeManager.createUpgradeQualities(upgradeCard, upgradeQualities);
//			upgradeResult.add(upgradeQualities);
			
		}
		else {
			upgradeResult.setVisible(false);
		}
		
		upgradeButton.toFront();
		
	}
	
	public void select() {
		card.copyCard(upgradeResult);
		card.upgrade();
		gameRenderer.finishUpgrade();
		System.out.println("Select option");
	}
	
	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		upgradeCard.setVisible(isVisible);
		upgradeResult.setVisible(isVisible);
		upgradeButton.setVisible(isVisible);
	}
	
}

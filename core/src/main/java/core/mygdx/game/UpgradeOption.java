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
	private boolean newCard;
	private int cardAmount;
	
	
	public UpgradeOption(GameRenderer gr, Deck d, float x, float y) {
		
		this.setBounds(x, y, Constants.upgradeOptionWidth, Constants.upgradeOptionHeight);
		
		gameRenderer = gr;
		deck = d;
		newCard = false;
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
	
	public void setOptions(int level) {
		
		upgradeCard.setVisible(true);
		upgradeCard.toFront();
		
		upgradeResult.setVisible(true);
		upgradeResult.toFront();
		
		upgradeButton.setVisible(true);
		
		card = deck.randomUgradeCard();
		
		if(card == null || newCard) {
			cardAmount = 1;
			//return;
		}
		else {
			cardAmount = 2;
		}
		
		float cardGap = (this.getWidth() - cardAmount*Constants.cardUpgradeWidth - (cardAmount-1) * Constants.cardUpgradeCenterGap)/2;
		float cardY = (this.getHeight() - Constants.cardUpgradeHeight) * Constants.cardOptionLowerRatio;
		
		upgradeCard.setPosition(this.getX() + cardGap,this.getY() + cardY);

		if(cardAmount > 1) {
			upgradeCard.copyCard(card);
			upgradeResult.setPosition(this.getX() + cardGap + Constants.cardUpgradeWidth + Constants.cardUpgradeCenterGap,
					this.getY() + cardY);
			UpgradeManager.createUpgrade(upgradeCard, upgradeResult,card.getLevel());
			upgradeResult.upgrade();
//			upgradeResult.copyCard(upgradeCard);
//			UpgradeManager.createUpgradeQualities(upgradeCard, upgradeQualities);
//			upgradeResult.add(upgradeQualities);
			
		}
		else {
			UpgradeManager.newCard(upgradeCard,level);
			upgradeResult.setVisible(false);
		}
		
		upgradeButton.toFront();
		
	}
	
	public void setNewCard(boolean nc) {
		newCard = nc;
	}
	
	public void select() {
		if(cardAmount == 1) {
			Card c = new Card(gameRenderer);
			c.copyCard(upgradeCard);
			deck.add(c);
		}
		else {
			card.copyCard(upgradeResult);
		}
		gameRenderer.finishUpgrade();
	}
	
	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		upgradeCard.setVisible(isVisible);
		if(cardAmount != 1) {
			upgradeResult.setVisible(isVisible);
		}
		upgradeButton.setVisible(isVisible);
	}
	
}

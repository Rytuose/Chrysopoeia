package core.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * 
 * Represents one out of four possible upgrade options
 *
 */
public class UpgradeOption extends Actor {
	
	private GameRenderer gameRenderer;
	private Deck deck;
	private UpgradeCard upgradeCard,upgradeResult;
	private Card card,upgradeQualities;
	private UpgradeButton upgradeButton;
	private int cardAmount;
	
	
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
		
		this.updateUpgradeButton();
		
//		upgradeButton.setPosition(this.getX() + (this.getWidth() - upgradeButton.getWidth())/2,
//				this.getY() + ((this.getHeight() - Constants.cardUpgradeHeight) * Constants.cardOptionLowerRatio - upgradeButton.getHeight())/2);
	}
	
	/**
	 * Creates an upgrade option if isUpgrade or a new card otherwise
	 * or if there is no card to upgrade
	 */
	public void setOptions(int level, boolean isUpgrade) {
		
		upgradeCard.setVisible(true);
		upgradeCard.toFront();
		
		upgradeResult.setVisible(true);
		upgradeResult.toFront();
		
		upgradeButton.setVisible(true);
		
		card = deck.randomUgradeCard();
		
		if(card == null || !isUpgrade) {
			cardAmount = 1;
			upgradeResult.resetCard();
			//return;
		}
		else {
			cardAmount = 2;
		}
		
		//float cardGap = (this.getWidth() - cardAmount*Constants.cardUpgradeWidth - (cardAmount-1) * Constants.cardUpgradeCenterGap)/2;
		//float cardY = (this.getHeight() - Constants.cardUpgradeHeight) * Constants.cardOptionLowerRatio;
		
		//upgradeCard.setPosition(this.getX() + cardGap,this.getY() + cardY);

		if(cardAmount > 1) {
			upgradeCard.copyCard(card);
			//upgradeResult.setPosition(this.getX() + cardGap + Constants.cardUpgradeWidth + Constants.cardUpgradeCenterGap,
			//		this.getY() + cardY);
			UpgradeManager.createUpgrade(upgradeCard, upgradeResult,card.getLevel());
			upgradeResult.upgrade();
//			upgradeResult.copyCard(upgradeCard);
//			UpgradeManager.createUpgradeQualities(upgradeCard, upgradeQualities);
//			upgradeResult.add(upgradeQualities);
			
		}
		else {
			UpgradeManager.newCard(upgradeCard,level);
			//upgradeResult.setVisible(false);
		}
		
		updatePosition();
		upgradeButton.toFront();
		
	}
	
	public void updatePosition() {
		float cardGap = (this.getWidth() - cardAmount*Constants.cardUpgradeWidth - (cardAmount-1) * Constants.cardUpgradeCenterGap)/2;
		float cardY = (this.getHeight() - Constants.cardUpgradeHeight) * Constants.cardOptionLowerRatio;
		
		upgradeCard.setPosition(this.getX() + cardGap,this.getY() + cardY);
		
		if(cardAmount > 1) {
			upgradeResult.setPosition(this.getX() + cardGap + Constants.cardUpgradeWidth + Constants.cardUpgradeCenterGap,
					this.getY() + cardY);
		}
		else {
			upgradeResult.setVisible(false);
		}
	}
	
	public void updateUpgradeButton() {
		upgradeButton.setPosition(this.getX() + (this.getWidth() - upgradeButton.getWidth())/2,
				this.getY() + ((this.getHeight() - Constants.cardUpgradeHeight) * Constants.cardOptionLowerRatio - upgradeButton.getHeight())/2);
	}
	
	/**
	 * Confirms and processes the upgrade
	 */
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
	
	public UpgradeCard getUpgradeCard() { return upgradeCard; }
	
	public UpgradeCard getUpgradeResult() { return upgradeResult; }
	
	public int getCardAmount() { return cardAmount; }
	
}

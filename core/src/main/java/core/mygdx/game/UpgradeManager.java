package core.mygdx.game;

import java.util.ArrayList;
import java.util.LinkedList;

import enums.CardType;
import enums.Location;
import enums.Symbol;

public class UpgradeManager {
	
	private static Symbol[] symbolOrder = {Symbol.LEAD, Symbol.COPPER, Symbol.SILVER, Symbol.GOLD};
	private static Symbol[] expandedSymbolOrder = {Symbol.LEAD, Symbol.LEAD_OR_COPPER, Symbol.COPPER,
			Symbol.COPPER_OR_SILVER, Symbol.SILVER, Symbol.SILVER_OR_GOLD, Symbol.GOLD};
	private static Location[] locations = {Location.LEFT, Location.CENTER, Location.RIGHT};
	
	public static void createUpgrade(UpgradeCard card, UpgradeCard result, int level) {
		result.clearSymbols();
		result.copyCard(card);
		CardType type = getCardType(card);
		
		switch(type) {
		case PRODUCTION:
			upgradeProduction(result,level);
			break;
		case REDRAW:
			upgradeRedraw(result,level);
			break;
		case TRADE:
			upgradeTrade(result,level);
			break;
		case UPGRADE:
			upgradeUpgrade(result,level);
			break;
		}
//		
//		result.addSymbol(Symbol.LEAD, Location.INPUT);
//		result.addSymbol(Symbol.COPPER, Location.CENTER);
	}
	
	private static CardType getCardType(Card c) {
		if(c.getCenterOutput().contains(Symbol.REFRESH4) || c.getCenterOutput().contains(Symbol.REFRESH5)) {
			return CardType.REDRAW;
		}
		else if (c.getInput().isEmpty()) {
			return CardType.PRODUCTION;
		}
		else if(c.getCenterOutput().contains(Symbol.UPGRADE)) {
			return CardType.UPGRADE;
		}
		else {
			return CardType.TRADE;
		}
	}
	
	
	private static void upgradeProduction(Card card, int level) {
		/* Options
		 * 1. Increase Production Amount
		 * 2. Upgrade Production Quality
		 * 3? Turn it into a trade card
		 * 4? Add a direction*/
		
		int upgradePoints = level + 1;
		LinkedList<Location> activeLocations = getActiveLocations(card);
		
		while(upgradePoints > 0) {
			int option = (int)(Math.random()*2);
			switch(option) {
			case 0:
				upgradePoints = UpgradeManager.addOutput(card,upgradePoints,activeLocations);
				break;
			case 1:
				upgradePoints = UpgradeManager.upgradeOutput(card,upgradePoints,activeLocations);
				break;
			}
		}
	}
	
	private static void upgradeRedraw(Card card, int level) {
		ArrayList<Symbol> output = card.getCenterOutput();
		switch(level) {
		case 0:
			output.remove(Symbol.REFRESH4);
			output.add(Symbol.REFRESH5);
			output.add(Symbol.DISCARD);
			output.sort(null);
			break;
		case 1:
			output.remove(Symbol.DISCARD);
			break;
		case 2:
			break;
		}
	}
	
	private static void upgradeTrade(Card card, int level) {
		int upgradePoints = level + 1;
		LinkedList<Location> activeLocations = getActiveLocations(card);
		while(upgradePoints > 0) {
			int option = (int)(Math.random()*2);
			switch(option) {
			case 0:
				upgradePoints = UpgradeManager.addOutput(card, upgradePoints,activeLocations);
				break;
			case 1:
				upgradePoints = UpgradeManager.reduceInput(card, upgradePoints, activeLocations);
			}
		}
		UpgradeManager.reduceTrade(card);
		
		
	}
	
	private static void upgradeUpgrade(Card card, int level) {
		int upgradePoints = level + 1;
		LinkedList<Location> activeLocations = getActiveLocations(card);
		while(upgradePoints > 0) {
			int option = (int)(Math.random()*1);
			switch(option) {
			case 0:
				upgradePoints = UpgradeManager.reduceInput(card, upgradePoints,activeLocations);
				break;
			}
		}
	}
	
	private static LinkedList<Location> getActiveLocations(Card card){
		LinkedList<Location> activeLocations = new LinkedList<Location>();
		if(!card.getLeftOutput().isEmpty()) {
			activeLocations.add(Location.LEFT);
		}
		if(!card.getCenterOutput().isEmpty()) {
			activeLocations.add(Location.CENTER);
		}
		if(!card.getRightOutput().isEmpty()) {
			activeLocations.add(Location.RIGHT);
		}
		return activeLocations;
	}
	
	private static int addOutput(Card card, int value, LinkedList<Location> activeLocations) { 
		value -= addRandomSymbol(card, value, activeLocations.get((int)(Math.random() * activeLocations.size())));
		return value;
	}
	
	private static int addRandomSymbol(Card card, int value, Location location) {
		int pos = (int) (Math.random() * (value));
		if(pos >= 4) {
			pos = pos%4;
		}
		card.addSymbol(symbolOrder[pos], location);
		value = pos +1;
		return value;
	}
	
	private static int upgradeOutput(Card card, int value, LinkedList<Location> activeLocations) {
		ArrayList<Symbol> totalOutput = card.getAllOutput();
		int randPos = (int)(Math.random() * totalOutput.size());
		
		for(Location loc:activeLocations) {
			if(randPos > card.getLocation(loc).size()) {
				randPos -= card.getLocation(loc).size();
			}
			else {
				Symbol s = card.getLocation(loc).get(randPos);
				if(s == Symbol.GOLD) {
					int newValue = UpgradeManager.addOutput(card, value, activeLocations);
					return newValue;
				}
				
				System.out.println("Upgrading " + s);
				card.getLocation(loc).remove(s);
				card.getLocation(loc).add(symbolOrder[s.getValue()]);
				card.getLocation(loc).sort(null);
				return value - 1;
			}
		}		
		
		return value;
	}
	
	private static int reduceInput(Card card, int value, LinkedList<Location> activeLocations) {
		ArrayList<Symbol> input = card.getInput();
		if(input.isEmpty() /*|| (input.size() == 1 && input.get(0) == Symbol.LEAD)*/) {
			//return value; //Shouldn't matter
			return UpgradeManager.addOutput(card, value, activeLocations);
		}
		
		int randPos = (int)(Math.random() * input.size());
		Symbol s = input.get(randPos);
		
		int pos = 0;
		for(int i = 0 ; i < expandedSymbolOrder.length; i++) {
			if(expandedSymbolOrder[i] == s) {
				pos = i;
				break;
			}
		}
		
		input.remove(randPos);
		if(pos > 0) {
			input.add(randPos, expandedSymbolOrder[pos -1]);
		}
		
		return value - 1;
	}
	
	private static void reduceTrade(Card c) {
		ArrayList<Symbol> input = c.getInput();
		ArrayList<Symbol> output = c.getCenterOutput();
		
		System.out.println("input " + input.toString());
		System.out.println("output " + output.toString());
		
		Symbol s;
		int outputSymbol = 0;
		
		for(int i = 0 ; i < input.size() ; i ++) {
			s = input.get(i);
			if(s.getValue() > 0) {
				while(output.get(outputSymbol).getValue() < s.getValue()) {
					outputSymbol ++;
					if(outputSymbol >= output.size()) {
						return;
					}
				}
				if(output.get(outputSymbol) == s) {
					input.remove(i);
					output.remove(outputSymbol);
					i--;
				}
			}
		}
	}
	
}

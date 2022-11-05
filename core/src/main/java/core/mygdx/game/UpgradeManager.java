package core.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import enums.Bias;
import enums.CardType;
import enums.Location;
import enums.Symbol;

/**
 * 
 * Manages card upgrades and card creation
 *
 */
public class UpgradeManager {
	
	private static Symbol[] symbolOrder = {Symbol.LEAD, Symbol.COPPER, Symbol.SILVER, Symbol.GOLD};
	private static Symbol[] expandedSymbolOrder = {Symbol.LEAD, Symbol.LEAD_OR_COPPER, Symbol.COPPER,
			Symbol.COPPER_OR_SILVER, Symbol.SILVER, Symbol.SILVER_OR_GOLD, Symbol.GOLD};
	private static Location[] locations = {Location.LEFT, Location.CENTER, Location.RIGHT};
	private static Symbol[] upgradeOrder = {Symbol.UPGRADE1, Symbol.UPGRADE2, Symbol.UPGRADE3};
	private static Symbol[] newCardOrder = {Symbol.NEW_CARD1, Symbol.NEW_CARD2, Symbol.NEW_CARD3}; 
	private static CardType[] cardTypes = {CardType.PRODUCTION, CardType.TRADE, CardType.UPGRADE, CardType.UTILITY};
	
	public static double prob1, prob2;
	
	public static HashMap<String,Integer> cardRatio = new HashMap<String,Integer>(); 
	
	/**
	 * Creates and upgrade of card onto result with given level
	 */
	public static void createUpgrade(UpgradeCard card, UpgradeCard result, int level) {
		result.resetCard();
		result.copyCard(card);
		CardType type = getCardType(card);
		switch(type) {
		case GHOST:
			upgradeGhost(result,level);
			break;
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
		case UTILITY:
			break;
		}
		
		UpgradeManager.simplifyCard(card, Symbol.GOLD);
	}
	
	/**
	 * Gets the classification of card c
	 */
	private static CardType getCardType(Card c) {
		if(c.getCenterOutput().contains(Symbol.REFRESH4) || c.getCenterOutput().contains(Symbol.REFRESH5)) {
			return CardType.REDRAW;
		}
		if(c.getCenterOutput().contains(Symbol.UPGRADE1) 
				|| c.getCenterOutput().contains(Symbol.UPGRADE2) 
				|| c.getCenterOutput().contains(Symbol.UPGRADE3)) {
			return CardType.UPGRADE;
		}
		
		if(c.getAllOutput().contains(Symbol.GHOST) || c.getInput().contains(Symbol.GHOST)) {
			return CardType.GHOST;
		}
		
		for(Symbol s: c.getCenterOutput()) {
			if(s.getValue() < 0) {
				return CardType.UTILITY;
			}
		}
		
		if (c.getInput().isEmpty()) {
			return CardType.PRODUCTION;
		}
		else {
			return CardType.TRADE;
		}
	}
	
	private static void upgradeGhost(Card card, int level) {
		int upgradePoints = 2 + (level+1)/2;
		
		if(!card.getInput().contains(Symbol.GHOST)) {
			//Ghost Production
			if(!card.getInput().isEmpty() && Math.random() < .5) {
				for(int i = 0; i < level+1; i++) {
					if(!card.getInput().isEmpty()) {
						UpgradeManager.reduceInput(card, 0, null);
					}
				}
			}
			else {
				card.getInput().add(Symbol.LEAD);
				card.getCenterOutput().add(Symbol.GHOST);
			}
		}
		//After this card has ghost input
		else if(card.getLeftOutput().contains(Symbol.GHOST) ||
				card.getRightOutput().contains(Symbol.GHOST)) {
			for(int i = 0; i < upgradePoints; i++) {
				UpgradeManager.mergeOutput(card, 0, Location.CENTER);
			}
		} 
		//Insert Send Ghosts for quest card here
		else if(!card.getCenterOutput().contains(Symbol.GHOST)) {
			//Sacrifice
			LinkedList<Location> locations = new LinkedList<Location>();
			locations.add(Location.CENTER);
			for(int i = 0; i < upgradePoints; i++) {
				UpgradeManager.upgradeLocation(card, 0, locations);
			}
			
			if(level == 1) {
				card.getInput().add(Symbol.GHOST);
			}
			
			if(level == 2) {
				if(cardRatio.containsKey(card.getCenterOutput().toString())) {
					cardRatio.put(card.getCenterOutput().toString(), cardRatio.get(card.getCenterOutput().toString())+1);
				}
				else {
					cardRatio.put(card.getCenterOutput().toString(), 1);
				}
			}
		}
	}
	
	private static void upgradeProduction(Card card, int level) {
		LinkedList<Location> activeLocations = getActiveLocations(card);
		
		int upgradePoints = (level+1)/2 + 1;//level + 1;
		while(upgradePoints > 0) {
			upgradePoints = UpgradeManager.upgradeLocation(card, upgradePoints, activeLocations);
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
			output.remove(Symbol.REFRESH5);
			output.add(Symbol.REFRESH4);
			output.add(Symbol.SEARCH);
			output.sort(null);
			break;
		}
	}
	
	private static void upgradeTrade(Card card, int level) {
		int upgradePoints = level + 2;
		LinkedList<Location> activeLocations = getActiveLocations(card);
		
		int outputSize = 0;
		for(Location loc : activeLocations) {
			outputSize += card.getLocation(loc).size();
		}
		
		int cardType = card.getInput().size() - outputSize;
		
		while (upgradePoints > 0) {
			if(cardType < 0) {
				//Split
				upgradePoints = UpgradeManager.upgradeLocation(card, upgradePoints, activeLocations);
			}
			else if(cardType > 0) {
				//Merge
				upgradePoints = UpgradeManager.reduceInput(card, upgradePoints, activeLocations);
			}
			else {
				//Transfer
				if(Math.random() < .5) {
					upgradePoints = UpgradeManager.upgradeLocation(card, upgradePoints, activeLocations);
				}
				else{
					upgradePoints = UpgradeManager.reduceInput(card, upgradePoints, activeLocations);
				}
			}
			
			if(card.getInput().isEmpty()) {
				UpgradeManager.addLead(card, upgradePoints, Location.INPUT);
				UpgradeManager.mergeOutput(card, upgradePoints, activeLocations.get((int)(Math.random() * activeLocations.size())));
			}
			//Add if clause when card turns into a production card
		}
		
//		while(upgradePoints > 0) {
//			int option = (int)(Math.random()*2);
//			switch(option) {
//			case 0:
//				upgradePoints = UpgradeManager.addOutput(card, upgradePoints,activeLocations);
//				break;
//			case 1:
//				upgradePoints = UpgradeManager.reduceInput(card, upgradePoints, activeLocations);
//			}
//		}
//		UpgradeManager.reduceTrade(card);
		
		
	}
	
	private static void upgradeUpgrade(Card card, int level) {
		int upgradePoints = level + 2;
		LinkedList<Location> activeLocations = getActiveLocations(card);
		
		int totalValue = 0;
		for(Symbol s: card.getInput()) {
			for(int i = 0; i <expandedSymbolOrder.length; i++) {
				if(expandedSymbolOrder[i] == s) {
					totalValue += (i/2)+1;
				}
			}
		}
		
		System.out.println("Total Value = " + totalValue + " upgradePoints = " + upgradePoints );
		
		while(upgradePoints > 0) {
			int option = (int)(Math.random()*3);
			switch(option) {
			case 0:
				if(totalValue >= upgradePoints) {
					while(upgradePoints > 0) {
						upgradePoints = UpgradeManager.reduceInput(card, upgradePoints,activeLocations);
					}
					return;
				}
			case 1:
				//NOTHING
			case 2:
				upgradePoints = UpgradeManager.upgradeUpgradeQuality(card, upgradePoints, activeLocations);
				break;
			}
		}
	}

	
	/*------------------------------HELPER FUNCTIONS------------------------------*/
	
	private static int addLead(Card card, int value, Location location) {
		if(card.getLocation(location).contains(Symbol.GHOST)) {
			card.addSymbol(Symbol.GHOST, location);
			return value-1;
		}
		card.addSymbol(Symbol.LEAD, location);
		return value - 1;
	}
	
	private static int mergeOutput(Card c, int value, Location location) {
		/*
		 * L -> C
		 * CC -> S
		 * SC -> G
		 */
		ArrayList<Symbol> storage = c.getLocation(location);
		ArrayList<Integer> mergePositions = new ArrayList<Integer>();
		ArrayList<Symbol> mergeSymbols = new ArrayList<Symbol>();
		Symbol s;
		boolean hasCopper = false;
		int leadCount = 0;
		
		for(int i = 0 ; i < storage.size() ; i++) {
			s = storage.get(i);
			switch(s) {
			case LEAD:
				mergePositions.add(i);
				if(!mergeSymbols.contains(s)) {
					mergeSymbols.add(s);
					mergeSymbols.add(s);
				}
				break;
			case COPPER:
				if((i + 1 < storage.size() && storage.get(i + 1)==Symbol.COPPER) ||
						(i - 1 >= 0 && storage.get(i - 1) == Symbol.COPPER)) {
					mergePositions.add(i);
					if(!mergeSymbols.contains(s)) {
						mergeSymbols.add(s);
					}
				}
				hasCopper = true;
				break;
			case SILVER:
				if(hasCopper) {
					if(!mergeSymbols.contains(s)) {
						mergeSymbols.add(s);
					}
					mergePositions.add(i);
//					storage.remove(Symbol.SILVER);
//					storage.remove(Symbol.COPPER);
//					storage.add(Symbol.GOLD);
//					return value-1;
				}
				break;
			}
		}
		
		if(mergePositions.isEmpty() || mergeSymbols.isEmpty()) {
			return UpgradeManager.addLead(c, value, location);
		}
		
		
		//s = storage.get(mergePositions.get((int)(Math.random() * mergePositions.size())));
		s = mergeSymbols.get((int)(Math.random() * mergeSymbols.size()));
		
		switch(s) {
		case LEAD:
			storage.remove(Symbol.LEAD);
			storage.add(Symbol.COPPER);
			break;
		case COPPER:
			storage.remove(Symbol.COPPER);
			storage.remove(Symbol.COPPER);
			storage.add(Symbol.SILVER);
			break;
		case SILVER:
			storage.remove(Symbol.SILVER);
			storage.remove(Symbol.COPPER);
			storage.add(Symbol.GOLD);
			break;
		}
		
		storage.sort(null);
		
		return value - 1;
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
		LinkedList<Location> validLocations = new LinkedList<Location>();
		for(Location loc:activeLocations) {
			if(card.getLocation(loc).size() < 4) {
				validLocations.add(loc);
			}
		}
		
		if(validLocations.isEmpty()) {
			return value;
		}
		
		value -= addRandomSymbol(card, value, validLocations.get((int)(Math.random() * validLocations.size())));
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
	
	private static int upgradeLocation(Card card, int upgradePoints, LinkedList<Location> locations) {
		
		Location location = locations.get((int)(Math.random() * locations.size()));
		double probability = (card.getLocation(location).size() <= 2)?.253:.8;
		if(Math.random() < probability && card.getLocation(location).size() < 4) {
			return UpgradeManager.addLead(card,upgradePoints, location);
		}
		else {
			return UpgradeManager.mergeOutput(card, upgradePoints, location);
		}
	}
	
	private static int reduceInput(Card card, int value, LinkedList<Location> activeLocations) {
		ArrayList<Symbol> input = card.getInput();
		if(input.isEmpty() /*|| (input.size() == 1 && input.get(0) == Symbol.LEAD)*/) {
			//return value; //Shouldn't matter
			return UpgradeManager.upgradeLocation(card, value, activeLocations);
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
		
		pos--;
		if(s.getValue() < 0 || Math.random() > .1) {
			pos--;
//			if(Math.random() < .5) {
//				pos--;
//			}
		}
		
		if(pos >= 0) {
			input.add(randPos, expandedSymbolOrder[pos]);
		}
			
		input.sort(null);
		
		return value - 1;
	}
	
	private static void reduceTrade(Card c) {
		ArrayList<Symbol> input = c.getInput();
		ArrayList<Symbol> output = c.getCenterOutput();
		
		if(input.isEmpty() || output.isEmpty()) {
			return;
		}
		
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
	
	private static int upgradeUpgradeQuality(Card card, int value, LinkedList<Location> activeLocations) {
		System.out.println("Upgrade Upgrade");
		ArrayList<Symbol> output = card.getCenterOutput();
		Symbol symbol;
		for(int i = 0 ; i < output.size(); i++) {
			symbol = output.get(i);
			for(int j = 0 ; j < upgradeOrder.length; j++) {
				if(symbol == upgradeOrder[j] && symbol != Symbol.UPGRADE3) {
					System.out.println("FOUND " + symbol);
					output.remove(symbol);
					output.remove(i); //ERROR HERE, Out of boutnd index 1 for upgrade 2->3
					output.add(upgradeOrder[j+1]);
					output.add(newCardOrder[j+1]);
					output.sort(null);
					
					
					
					//card.getInput().add(symbolOrder[j+2]);
					
					ArrayList<Symbol> input = card.getInput();
					int points = j+2;
					
					if(card.getInput().size() < 3) {
						//Add a symbol
						int randInt = (int)(Math.random()*points);
						input.add(symbolOrder[randInt]);
						points -= randInt+1;
					}
					
					while(points > 0) {
						int randint = (int)(Math.random() * input.size());
						Symbol s = input.get(randint);
						for(int k = 0; k < expandedSymbolOrder.length-1; k++) {
							if(expandedSymbolOrder[k] == s) {
								input.remove(s);
								if(k+2 < expandedSymbolOrder.length) {
									input.add(expandedSymbolOrder[k+2]);
								}
								else {
									input.add(expandedSymbolOrder[k+1]);
								}
								points --;
								break;
							}
						}
						
						
						for(int k = 0; k<expandedSymbolOrder.length-1; k++) {
							if(expandedSymbolOrder[k] == s) {
								
							}
						}
					}
					
					input.sort(null);
					
					return 0;
				}
			}
		}
		
		return UpgradeManager.reduceInput(card, value, activeLocations);
		
	}

	
	
	/*================================CARD GENERATION================================*/
	
	/**
	 * Creates a new card on upgradeCard, with a base rank of rank.
	 * The rank should equal the level of the upgrade (Ex. cards offered
	 * from UPGRADE1 should be rank 1)
	 */
	public static void newCard(Card upgradeCard, int rank) {
		upgradeCard.resetCard();
		int netValue = rank + 3;
		int symbolPos = (int)(Math.random() * (symbolOrder.length + 1));
		
		System.out.println("Upgrade Numbers " + netValue + " " + symbolPos + " " + rank);
		
		if(rank == -1) {
			UpgradeManager.createUtility(upgradeCard);
			return;
		}
		
		int randNum = (Bias.bias == Bias.NONE)? (int)(Math.random()*80):(int)(Math.random()*100);
		
		if(randNum < 20) {
			//Production
			UpgradeManager.createProduction(upgradeCard,rank);
		}
		else if(randNum < 50) {
			//Trade
			UpgradeManager.createTrade(upgradeCard, rank);
		}
		else if(randNum < 80) {
			//Ghost
			UpgradeManager.createGhost(upgradeCard,rank);
		}
		else {
			switch(Bias.bias){
			case GHOST:
				UpgradeManager.createGhost(upgradeCard,rank);
				break;
			case PRODUCTION:
				UpgradeManager.createProduction(upgradeCard, rank);
				break;
			case TRADE:
				UpgradeManager.createTrade(upgradeCard, rank);
				break;
			case NONE: //Shouldn't happen but just in case
				UpgradeManager.createProduction(upgradeCard, rank);
			}
		}
	}

	private static void createUtility(Card upgradeCard) {
		switch((int)(Math.random() * 4)) {
		case 0:
			upgradeCard.addSymbol(Symbol.MOVE_LEFT, Location.CENTER);
			upgradeCard.addSymbol(Symbol.MOVE_RIGHT, Location.CENTER);
			break;
		case 1:
			System.out.println("Creating upgradeCard with level " +  QuestViewer.questProgress);
			int randPos = (int)(Math.random() * (QuestViewer.questProgress/2));
			if(randPos > 2) {randPos = 2;}
			int upgradePoints = (randPos + 1) * 2;
			System.out.println("UpgradePoints " + upgradePoints);
			LinkedList<Location> upgradeLocation = new LinkedList<Location>();
			upgradeLocation.add(Location.INPUT);
			for(int i = 0 ; i < upgradePoints ; i++) {
				UpgradeManager.upgradeLocation(upgradeCard, upgradePoints, upgradeLocation);
			}
			upgradeCard.addSymbol(upgradeOrder[randPos], Location.CENTER);
			upgradeCard.addSymbol(newCardOrder[randPos], Location.CENTER);
			break;
		case 2:
			upgradeCard.addSymbol(Symbol.DRAW1, Location.CENTER);
			upgradeCard.addSymbol(Symbol.DRAW1, Location.CENTER);
			upgradeCard.addSymbol(Symbol.QUICK, Location.CENTER);
			break;
		case 3:
			upgradeCard.addSymbol(Symbol.LEAD, Location.INPUT);
			upgradeCard.addSymbol(Symbol.COPPER, Location.INPUT);
			upgradeCard.addSymbol(Symbol.DELETE, Location.CENTER);
			break;
		case 4:
			Symbol mainSymbol;
			int rank;
			
			//questProgress++ happens first so breakpoints are increased by 1
			if(QuestViewer.questProgress < 3) {
				mainSymbol = Symbol.SILVER;
				rank = 1;
			}
			else {
				mainSymbol = Symbol.GOLD;
				if(QuestViewer.questProgress < 5) {
					rank = 2;
				}
				else {
					rank = 3;
				}
			}
			
			int netValue = mainSymbol.getValue() - rank + 1;
			
			upgradeCard.addSymbol(mainSymbol, Location.CENTER);
			UpgradeManager.createTradeMergeHelper(upgradeCard, mainSymbol, netValue);
			
			break;
			
		}
	}

	private static void createProduction(Card upgradeCard, int rank) {
		int netValue = rank + 2;
		
		ArrayList<Location> locations = UpgradeManager.createOutputShapes(upgradeCard);
		
		for(Location loc: locations) {
			netValue = UpgradeManager.addLead(upgradeCard, netValue, loc);
		}
		
		LinkedList<Location> activeLocations = getActiveLocations(upgradeCard);
		while(netValue > 0) {
			Location location = activeLocations.get((int)(Math.random() * activeLocations.size()));
			if(Math.random() < .5 && upgradeCard.getLocation(location).size() < 4) {
				netValue =  UpgradeManager.addLead(upgradeCard,netValue, location);
			}
			else {
				netValue =  UpgradeManager.mergeOutput(upgradeCard, netValue, location);
			}
		}

	}
	
	/**
	 * Creates and returns the input/output location of upgradeCard 
	 */
	private static ArrayList<Location> createOutputShapes(Card upgradeCard) {
		double prob = .9;
		ArrayList<Location> positions = new ArrayList<Location>();
		
		if(Math.random() < .8) {
			positions.add(Location.CENTER);
		}
		else {
			positions.add(Location.LEFT);
			positions.add(Location.RIGHT);
		}
		
		return positions;
	}
	
	private static void createTrade(Card upgradeCard, int rank) {
		
		int netValue;//= level + 2;
		if(Math.random() < 1) {
			//Merge and Split
			int rand = (rank == 1)?(int)(Math.random()*3)+1:(int)(Math.random()*5)+1; //(1,2,or 3)
			switch(rank) {
			case 1:
				rand = (int)(Math.random()*4)+1;
				break;
			case 2:
				rand = (int)(Math.random()*7)+1;
				break;
			case 3:
				rand = (int)(Math.random()*6)+2;
				break;
			}
			
			Symbol mainSymbol = symbolOrder[(rand+1)/3 + 1];
			netValue = mainSymbol.getValue() - rank + 1;
			
			if(Math.random() < .5) {
				//Split
				netValue = mainSymbol.getValue() + rank - 1;
				upgradeCard.addSymbol(mainSymbol,Location.INPUT);
				UpgradeManager.createTradeSplitHelper(upgradeCard, mainSymbol, netValue);
			}
			else {
				//Merge
				netValue = mainSymbol.getValue() - rank + 1;
				upgradeCard.addSymbol(mainSymbol, Location.CENTER);
				UpgradeManager.createTradeMergeHelper(upgradeCard, mainSymbol, netValue);
			}
			
		}
		else {
			//Transfer?
		}
	}
	
	private static void createTradeSplitHelper(Card upgradeCard, Symbol symbol, int netValue) {
		//Make sure that requirements are lesser value than symbol
		
		ArrayList<Location> locations = UpgradeManager.createOutputShapes(upgradeCard);
		
		if(locations.size() == 2) {
			netValue ++;
		}
		
		int maxPos = 1;
		for(int i = 0 ; i < symbolOrder.length; i++) {
			if(symbolOrder[i] == symbol) {
				maxPos = i;
				break;
			}
		}
		
		int randPos,limit;
		while(netValue > 0) {
			limit = maxPos;
			for(int i = 0 ; i < maxPos; i++) {
				if(symbolOrder[i].getValue() > netValue) {
					limit = i;
					break;
				}
			}
			randPos = (int)(Math.random() * limit);
			
			Location location = locations.get((int)(Math.random() * locations.size()));
			
			upgradeCard.addSymbol(symbolOrder[randPos], location);
			
			netValue -= symbolOrder[randPos].getValue();

		}
		
		ArrayList<Symbol> holder;
		
		if(upgradeCard.getLocation(Location.CENTER).isEmpty()) {
			if(upgradeCard.getLocation(Location.RIGHT).isEmpty()) {
				System.out.println("Converting Right");
				holder = upgradeCard.getLocation(Location.LEFT);
				upgradeCard.getLocation(Location.CENTER).addAll(0, holder);
				holder.clear();
			}
			else if(upgradeCard.getLocation(Location.LEFT).isEmpty()) {
				System.out.println("Converting Left");
				holder = upgradeCard.getLocation(Location.RIGHT);
				upgradeCard.getLocation(Location.CENTER).addAll(0, holder);
				holder.clear();
			}
		}
		
		UpgradeManager.simplifyCard(upgradeCard, symbol);
	}
	
	private static void createTradeMergeHelper(Card upgradeCard, Symbol symbol, int netValue) {
		//Location = Input (We can have half metals)
		
		ArrayList<Symbol> input = upgradeCard.getLocation(Location.INPUT);
		
		//Make sure that requirements are lesser value than symbol
		int maxPos = 1;
		for(int i = 0 ; i < symbolOrder.length; i++) {
			if(symbolOrder[i] == symbol) {
				maxPos = i;
				break;
			}
		}
		
		int randPos,limit;
		while(netValue > 0) {
			limit = maxPos;
			for(int i = 0 ; i < maxPos; i++) {
				if(symbolOrder[i].getValue() > netValue) {
					limit = i;
					break;
				}
			}
			randPos = (int)(Math.random() * limit);
			
			upgradeCard.addSymbol(symbolOrder[randPos], Location.INPUT);
			
			netValue -= symbolOrder[randPos].getValue();

		}
		
		System.out.println("Before --------------" + symbol);
		System.out.println(upgradeCard.toString());
		System.out.println("---------------------");
		
		UpgradeManager.simplifyCard(upgradeCard, symbol);
		
		for(int i = 0; i < input.size(); i++) {
			if(Math.random() < .1) {
				for(int j = 1 ; j < expandedSymbolOrder.length; j++) {
					if(expandedSymbolOrder[j] == input.get(i) && j > 0) {
						input.remove(i);
						input.add(expandedSymbolOrder[j -1]);
						input.sort(null);
					}
				}
			}
		}
	}
	
	private static void createGhost(Card upgradeCard, int rank) {
		// TODO Auto-generated method stub
		
		switch((int)(Math.random() * 3)) {
		case 0:
			//Production
			upgradeCard.addSymbol(Symbol.LEAD, Location.INPUT);
			upgradeCard.addSymbol(Symbol.GHOST, Location.CENTER);
			
			for(int i = 0; i < rank-1; i++) {
				UpgradeManager.upgradeGhost(upgradeCard, rank);
			}
			
			upgradeCard.getCenterOutput().sort(null);
			
			break;
		case 1:
			//Move
			upgradeCard.addSymbol(Symbol.GHOST, Location.INPUT);
			if(Math.random() < .5) {
				upgradeCard.addSymbol(Symbol.GHOST, Location.LEFT);
			}
			else {
				upgradeCard.addSymbol(Symbol.GHOST, Location.RIGHT);
			}
			upgradeCard.addSymbol(Symbol.COPPER, Location.CENTER);
			
			for(int i = 0 ; i < rank-1; i++) {
				UpgradeManager.mergeOutput(upgradeCard, 0, Location.CENTER);
			}
			
			break;
		case 2:
			//Sacrifice
			upgradeCard.addSymbol(Symbol.GHOST, Location.INPUT);
			LinkedList<Location> locations = new LinkedList<Location>();
			locations.add(Location.CENTER);
			for(int i = 0; i < rank + 3; i++) {
				UpgradeManager.upgradeLocation(upgradeCard, 0, locations);
			}
			break;
		}
		
	}
	
	/**
	 * Merges arrayList until the size is of given size or smaller.
	 * The merge will not cause the arrayList to contain materials of maxSymbol
	 * value or higher
	 */
	private static void mergeToSize(ArrayList<Symbol> arrayList, Symbol maxSymbol, int size) {
		Symbol s;
		ArrayList<Symbol> mergeableSymbols = new ArrayList<Symbol>();
		
		while(arrayList.size() > size) {
			boolean hasLead = false;
			mergeableSymbols.clear();
			
			for(int i = 0 ; i < arrayList.size(); i++) {
				s = arrayList.get(i);
				switch(s) {
				case LEAD:
					if((i - 1 > 0 && arrayList.get(i-1) == Symbol.LEAD) && maxSymbol != Symbol.COPPER) {
						if(!mergeableSymbols.contains(Symbol.LEAD)) {
							mergeableSymbols.add(Symbol.LEAD);
						}
					}
					hasLead = true;
					break;
				case COPPER:
					if(hasLead && i - 1 > 0 && arrayList.get(i-1) == Symbol.COPPER && maxSymbol != Symbol.SILVER) {
						if(!mergeableSymbols.contains(Symbol.COPPER)) {
							mergeableSymbols.add(Symbol.COPPER);
						}
					}
					break;
				}
			}
			
			if(mergeableSymbols.isEmpty()) {
				arrayList.sort(null);
				return;
			}
			
			switch(mergeableSymbols.get((int)(Math.random() * mergeableSymbols.size()))) {
			case LEAD:
				arrayList.remove(Symbol.LEAD);
				arrayList.remove(Symbol.LEAD);
				arrayList.add(Symbol.COPPER);
				break;
			case COPPER:
				arrayList.remove(Symbol.LEAD);
				arrayList.remove(Symbol.COPPER);
				arrayList.remove(Symbol.COPPER);
				arrayList.add(Symbol.SILVER);
				break;
			}
			
			arrayList.sort(null);
		}
	}
	
	/**
	 * Makes sure the card isn't too big or uneven
	 */
	public static void simplifyCard(Card upgradeCard, Symbol maxSymbol) {
		UpgradeManager.mergeToSize(upgradeCard.getLocation(Location.INPUT), maxSymbol, 4);
		UpgradeManager.mergeToSize(upgradeCard.getLocation(Location.LEFT), maxSymbol, 3);
		UpgradeManager.mergeToSize(upgradeCard.getLocation(Location.CENTER), maxSymbol, 4);
		UpgradeManager.mergeToSize(upgradeCard.getLocation(Location.RIGHT), maxSymbol, 3);
	}
	
}

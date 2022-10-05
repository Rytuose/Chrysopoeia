
package core.mygdx.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import enums.Symbol;

/**
 * 
 * Class of static functions that get textures for actors
 *
 */
public class ImageSearcher {
	
	private static HashMap<Symbol,Texture> cardSymbolMap = new HashMap<Symbol,Texture>();
	private static HashMap<Symbol, Texture> selectedCardSymbolMap = new HashMap<Symbol, Texture>();
	
	public static Texture getCardSymbol(Symbol symbol) {
		if(cardSymbolMap.containsKey(symbol)) {
			return cardSymbolMap.get(symbol);
		}
//		
//		if(symbol == Symbol.LEAD) {
//			Texture texture = new Texture(Gdx.files.internal("Images/LEAD4.png"));
//			cardSymbolMap.put(symbol, texture);
//			return texture;
//		}
//		if(symbol == Symbol.SILVER) {
//			Texture texture = new Texture(Gdx.files.internal("Images/SILVER.png"));
//			cardSymbolMap.put(symbol, texture);
//			return texture;
//		}
//		if(symbol == Symbol.GHOST) {
//			Texture texture = new Texture(Gdx.files.internal("Images/GHOST_KNIGHT.png"));
//			cardSymbolMap.put(symbol, texture);
//			return texture;
//		}
		
		String pathname = "Images/" + symbol.toString() + ".png";
		Texture texture;
		
		System.out.println("Pathname " + pathname);
		
		try {
			texture  = new Texture(Gdx.files.internal(pathname));
		}
		catch(Exception e) {
			texture  = null;
		}
		
		cardSymbolMap.put(symbol, texture);
		
		return texture;
	}
	
	public static Texture getSelectedCardSymbol(Symbol symbol) {
		if(selectedCardSymbolMap.containsKey(symbol)) {
			return selectedCardSymbolMap.get(symbol);
		}
		
//		if(symbol == Symbol.GHOST) {
//			Texture texture = new Texture(Gdx.files.internal("Images/GHOST_KNIGHT_CHECK.png"));
//			selectedCardSymbolMap.put(symbol, texture);
//			return texture;
//		}
		
		String pathname = "Images/" + symbol.toString() + "_CHECK.png";
		Texture texture;
		
		try {
			texture  = new Texture(Gdx.files.internal(pathname));
		}
		catch(Exception e) {
			System.out.println("Catch Panic");
			texture  = getCardSymbol(symbol);
		}
		
		selectedCardSymbolMap.put(symbol, texture);
		
		return texture;
	}
}

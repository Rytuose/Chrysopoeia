package enums;

/**
 * 
 * Describes that current status of the game
 * Playing - Normal Play
 * Prompting - A card or material needs to be selected for the game to continue
 * Questing (Outdated) - State where game wants materials to be turned in for a quest
 * Upgrading - The upgrade window is open
 *
 */
public enum GameStatus {
	PLAYING,
	PROMPTING,
	QUESTING,
	UPGRADING;
	
	public static GameStatus gamestatus;
}

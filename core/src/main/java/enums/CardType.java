package enums;

/**
 * 
 * Describes Different Categories of Card
 * Ghost - Anything with a ghost in the card
 * Production - A card that produces only materials with no input
 * Redraw - The card that redraws your hand, always placed furthest left
 * Trade - Trade materials for materials
 * Upgrade - A card with an upgrade symbol
 * Utility - A card with a special symbol not explained above
 *
 */
public enum CardType {
	GHOST,
	PRODUCTION,
	REDRAW,
	TRADE,
	UPGRADE,
	UTILITY;
}

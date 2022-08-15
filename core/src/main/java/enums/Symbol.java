package enums;

/*Value Meaning
 * >=0, can be in a container
 * <0, symbol on card*/
public enum Symbol {
	NONE(-1),
	RETURN(-1),
	REFRESH4(-1),
	REFRESH5(-1),
	DISCARD(-1),
	UPGRADE(-1),
	UPGRADE1(-1),
	UPGRADE2(-1),
	UPGRADE3(-1),
	LEAD(1),
	LEAD_OR_COPPER(-1),
	COPPER(2),
	COPPER_OR_SILVER(-1),
	SILVER(3),
	SILVER_OR_GOLD(-1),
	GOLD(4);

	
	private final int value;
	private Symbol(int i) {this.value = i;}
	public int getValue() {return this.value;}
	
	public boolean equals(Symbol other) {
		if(this == other) {
			return true;
		}
		
		switch(other) {
		case LEAD_OR_COPPER:
			return this == LEAD || this == COPPER;
		case COPPER_OR_SILVER:
			return this == COPPER || this == SILVER;
		case SILVER_OR_GOLD:
			return this == SILVER || this == GOLD;
		default:
			break;
		}
		
		switch(this) {
		case LEAD_OR_COPPER:
			return other == LEAD || other == COPPER;
		case COPPER_OR_SILVER:
			return other == COPPER || other == SILVER;
		case SILVER_OR_GOLD:
			return other == SILVER || other == GOLD;
		default:
			break;
		}
		
		return false;
	};
}

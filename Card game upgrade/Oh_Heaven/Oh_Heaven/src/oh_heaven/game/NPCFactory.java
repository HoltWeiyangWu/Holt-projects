package oh_heaven.game;

// A signletion Factory to inisitalise and create the NPCType for each logic 
public class NPCFactory {
    private static NPCFactory singleInstance = null;

	private final String TYPE_RANDOM = "random";
	private final String TYPE_LEGAL = "legal";
	private final String TYPE_SMART = "smart";

    private final NPCType random;
    private final NPCType legal;
    private final NPCType smart;

    private NPCFactory(Publisher subject, int seed){
        this.random = new RandomNPCType(seed);
        this.legal = new LegalNPCType(subject, random, seed);
        this.smart = new SmartNPCType(subject, legal, seed);
    }

    public static NPCFactory getInstance(Publisher subject, int seed) {
		if (singleInstance == null) {
			singleInstance = new NPCFactory(subject, seed);
		}
		return singleInstance;
	}

    public static NPCFactory getInstance() {
		return singleInstance;
	}

	// return the NPC type given the request type
    public NPCType getNpc(String npcType) {
		if (npcType.equals(TYPE_RANDOM)){
			return random;
		}else if (npcType.equals(TYPE_LEGAL)){
			return legal;
		}else if(npcType.equals(TYPE_SMART)){
			return smart;
		}
		return null;
	}
}

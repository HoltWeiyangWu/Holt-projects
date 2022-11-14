package oh_heaven.game;

import ch.aplu.jcardgame.*;
import java.util.*;

// Utilities class take partial responsibility from `GamePlay`
public class GamePlayUtilities {
	private final String DEFAULT_NPCPLAYER = "random";

    private static int seed;
    private static final Random random = new Random(seed);

	private final int nbPlayers = 4;
    private final int thinkingTime = 2000;

	
    public GamePlayUtilities(int seed) {
		GamePlayUtilities.seed = seed;
    }

    //* Card 
	// return random Enum value
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
		int x = random.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

	public Card randomCard(Hand hand){
		int x = random.nextInt(hand.getNumberOfCards());
		return hand.get(x);
	}

	public void dealingOut(int nbCardsPerPlayer, List<Player> players, Deck deck) {
		Hand pack = deck.toHand(false);
		// pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
		for (int i = 0; i < nbCardsPerPlayer; i++) {
			for (int j=0; j < nbPlayers; j++) {
				if (pack.isEmpty()) 
					return;
				Card dealt = randomCard(pack);
				// System.out.println("Cards = " + dealt);
				dealt.removeFromHand(false);
				players.get(j).getHand().insert(dealt, false);
				// dealt.transfer(hands[j], true);
			}
		}
	}

	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}

	//* initialisation 
	public void initScore(List<Player> players, GamePlayUI ui) {
		for (int i = 0; i < nbPlayers; i++) {
			// scores[i] = 0;
			Player currPlayer = players.get(i);
			ui.setPlayerScore(currPlayer, i);
			currPlayer.setScores(0);
		}
	}

	public void initTricks(List<Player> players) {
		for (int i = 0; i < nbPlayers; i++) {
			Player currPlayer = players.get(i);
			currPlayer.setTricks(0);
		}
	}

	public void initBids(Suit trumps, int nextPlayer, List<Player> players, int nbStartCards) {
		int total = 0;
		for (int i = nextPlayer; i < nextPlayer + nbPlayers; i++) {
			int iP = i % nbPlayers;
			Player currPlayer = players.get(iP);
			currPlayer.setBids(nbStartCards / 4 + random.nextInt(2));
			total += currPlayer.getBids();
		}
		if (total == nbStartCards) {  // Force last bid so not every bid possible
			int iP = (nextPlayer + nbPlayers) % nbPlayers;
			Player currPlayer = players.get(iP);
			if (currPlayer.getBids() == 0) {
				currPlayer.setBids(1);
			} else {
				currPlayer.setBids(currPlayer.getBids() + (random.nextBoolean() ? -1 : 1));
			}
		}
		// for (int i = 0; i < nbPlayers; i++) {
		// 	Player currPlayer = players.get(i);
		// 	currPlayer.setBids(nbStartCards / 4 + 1);
		// }
	}
	
	// initialise player given the properties file
	public List<Player> initialPlayer(Properties properties, GamePlayUI ui, Publisher subject){
        List<Player> players = new ArrayList<Player>();
		NPCFactory.getInstance(subject, seed);
		for (int i = 0; i < nbPlayers; i++) {
			String npcType = properties.getProperty("players." + Integer.toString(i));
			Player player;
			if (npcType != null){
				if(npcType.equals("human")){
					player = new HumanPlayer(i, ui, subject);
				}else{
					player = new NPCPlayer(i, npcType, ui, thinkingTime);
				}
			}else{
				player = new NPCPlayer(i, DEFAULT_NPCPLAYER, ui, thinkingTime);
			}
			players.add(player);
		}
        return players;
	}
}
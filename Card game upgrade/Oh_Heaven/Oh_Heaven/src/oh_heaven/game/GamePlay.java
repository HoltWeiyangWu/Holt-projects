package oh_heaven.game;

import ch.aplu.jcardgame.*;
import java.util.*;

public class GamePlay{
	private static final int DEFAULT_SEED = 30006;
	private final int DEFAULT_MADEBIDBONUS = 10;
	private final int DEFAULT_NBSTARTCARDS = 13;
	private final int DEFAULT_NBROUNDS = 3;
	private final boolean DEFAULT_ENFORCERULES = false;

	private static int seed = DEFAULT_SEED;
	private static final Random random = new Random(seed);
	
	private Publisher subject;
	private GamePlayUI ui;
	private GamePlayUtilities util;
	
	private String version;
	
	private final int nbPlayers = 4;
	private int madeBidBonus = DEFAULT_MADEBIDBONUS;
	
	private int nbStartCards = DEFAULT_NBSTARTCARDS;
	private int nbRounds = DEFAULT_NBROUNDS;
	private int winner;
	
	private boolean enforceRules = DEFAULT_ENFORCERULES;

	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
	private Card selected;
	private Card winningCard;

	private List<Player> players =  new ArrayList<>();
	private Set<Integer> winners;

    public GamePlay(Properties properties, String version){
		unloadProperties(properties);

		this.version = version;
		this.subject = new Publisher(nbPlayers);
		this.ui = new GamePlayUI(nbPlayers);
		this.util = new GamePlayUtilities(seed);

        this.players = util.initialPlayer(properties, ui, subject);
		util.initScore(players, ui);
    }

	public void unloadProperties(Properties properties) {
		if (properties.getProperty("seed") != null) {
			GamePlay.seed = Integer.parseInt(properties.getProperty("seed"));
		}
		
		if (properties.getProperty("nbStartCards") != null) {
			this.nbStartCards = Integer.parseInt(properties.getProperty("nbStartCards"));
		}
		
		if (properties.getProperty("rounds") != null) {
			this.nbRounds = Integer.parseInt(properties.getProperty("rounds"));
		}
		
		if(properties.getProperty("madeBidBonus") != null){
			this.madeBidBonus = Integer.parseInt(properties.getProperty("madeBidBonus"));
		}
		
		if(properties.getProperty("enforceRules") != null){
			this.enforceRules = Boolean.parseBoolean(properties.getProperty("enforceRules"));
		}
	}

	/* main game */
    public void playGame(){
		ui.initialise(version);
		// main game
		for (int i=0; i <nbRounds; i++) {
			initRound();
			playRound();
			updateScores();
		};
		// end game
		ui.updateAllScore(players);
		getWinner();
		ui.displayGameOver(winners);
	}
	
	// Utilities/Calculation
	public void updateScores() {
		for (int i = 0; i < nbPlayers; i++) {
			Player currPlayer = players.get(i);
			currPlayer.setScores(currPlayer.getScores() + currPlayer.getTricks());
			if (currPlayer.getTricks() == currPlayer.getBids()) {
				currPlayer.setScores(currPlayer.getScores() + madeBidBonus);
			}
		}
	}
		
	public void selectCard(int nextPlayer, Hand trick) {
		selected = null;
		Player player = players.get(nextPlayer);
		// if (false) {
        selected = player.playCard();
        
        if (player instanceof HumanPlayer) {  // Select lead depending on player type
            player.getHand().setTouchEnabled(true);
            while (null == selected) GamePlayUI.delay(100);
        }
        
        ui.setTableView(trick, selected);
        subject.setLead(selected);
		}
	
	public void checkRule(Suit lead, int nextPlayer) {
		if (selected.getSuit() != lead && 
		players.get(nextPlayer).getHand().getNumberOfCardsWithSuit(lead) > 0) {
			// Rule violation
			String violation = "Follow rule broken by player " + nextPlayer + 
			" attempting to play " + selected;
			System.out.println(violation);
			
			if (enforceRules) 
				try {
					throw(new BrokeRuleException(violation));
				} catch (BrokeRuleException e) {
					e.printStackTrace();
					System.out.println("A cheating player spoiled the game!");
					System.exit(0);
				}  
		}
	}

	public void setWinState(int nextPlayer, Suit trumps){
		if (// leading card
			winningCard == null ||
			// beat current winner with higher card
			(selected.getSuit() == winningCard.getSuit() && util.rankGreater(selected, winningCard)) ||
			// trumped when non-trump was winning
			(selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
				if(winningCard != null){
					System.out.println("NEW WINNER");
				}
				winner = nextPlayer;
				winningCard = selected;
				subject.setWinning(winningCard);
			}
	}
		
	public void getWinner(){
		int maxScore = 0;
		winners = new HashSet<Integer>();
		for (int i = 0; i <nbPlayers; i++){
			int currScore = players.get(i).getScores();
			if (currScore > maxScore){ 
				winners.clear();
				maxScore = currScore;
			}
			if (currScore == maxScore){ 
				winners.add(i);
			}
		}
	}

	// Game
	private void initRound() {
		util.initTricks(players);

		// Player humanPlayer = players.get(0);
		List<Player> humanPlayers = new ArrayList<>();

		for (int i = 0; i < nbPlayers; i++) {
			Player currPlayer = players.get(i);
			if(currPlayer instanceof HumanPlayer){
				humanPlayers.add(currPlayer);
			}
			currPlayer.setHand(deck);
		}

		util.dealingOut(nbStartCards, players, deck);
		for (int i = 0; i < nbPlayers; i++) {
			Player currPlayer = players.get(i);
			currPlayer.sortHand();
		}

		// Set up human player for interaction
		CardListener cardListener = new CardAdapter(){  
			// Human Player plays card
			public void leftDoubleClicked(Card card) { 
				selected = card; 
				for (int i = 0; i < humanPlayers.size(); i++){
					humanPlayers.get(i).getHand().setTouchEnabled(false); 
				}
			}
		};

		for (int i = 0; i < humanPlayers.size(); i++){
			humanPlayers.get(i).getHand().addCardListener(cardListener);
		}

		// graphics
		ui.setHands(players);

		// for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide the cards in a hand (make them face down)
		// 	hands[i].setVerso(true);			// You do not need to use or change this code.
		// End graphics
	}

	private void playRound() {
		// Select and display trump suit
		final Suit trumps = GamePlayUtilities.randomEnum(Suit.class);
		ui.setTrumpActor(trumps);
		subject.setTrump(trumps);
		// End trump suit
		
		Hand trick;
		Suit lead;
		int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
		util.initBids(trumps, nextPlayer, players, nbStartCards);
		// initScore();
		ui.updateAllScore(players);

		for (int i = 0; i < nbStartCards; i++) {
			trick = new Hand(deck);
			/* !!! Duplicate code !!! */
			subject.setNull();
			lead = null; 
			winningCard = null;
			selectCard(nextPlayer, trick);

			// No restrictions on the card being lead
			lead = (Suit) selected.getSuit();
			ui.cardTransfer(selected, trick); // selected.transfer(trick, true); // transfer to trick (includes graphic effect)
			setWinState(nextPlayer, trumps);
			// End Lead
			for (int j = 1; j < nbPlayers; j++) {
				if (++nextPlayer >= nbPlayers) 
					nextPlayer = 0;  // From last back to first

				selectCard(nextPlayer, trick);
				// Check: Following card must follow suit if possible
				checkRule(lead, nextPlayer);
				ui.cardTransfer(selected, trick);  // transfer to trick (includes graphic effect) 
				System.out.println("winning: " + winningCard);
				System.out.println(" played: " + selected);
				setWinState(nextPlayer, trumps);
				// End Follow
			}
			GamePlayUI.delay(600);
			ui.resetTableView(trick);
			nextPlayer = winner;
			ui.setStatusText("Player " + nextPlayer + " wins trick.");

			Player currPlayer = players.get(nextPlayer);
			currPlayer.setTricks(currPlayer.getTricks() + 1);
			ui.updateScore(currPlayer, nextPlayer);
		}
		ui.removeTrumpActor();
	}
}
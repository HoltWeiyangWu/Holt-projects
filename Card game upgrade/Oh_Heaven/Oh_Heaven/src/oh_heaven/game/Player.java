package oh_heaven.game;

import ch.aplu.jcardgame.*;

// Player class records the player related information, including Hand, scores 
// tricks, etc 
@SuppressWarnings("serial")
public abstract class Player {
	
	private int scores;
	private int tricks;
	private int bids;
	protected int playerNum;
	protected Hand hand;
	protected GamePlayUI ui;

	Player(int playerNum, GamePlayUI ui){
		this.playerNum = playerNum;
		this.ui = ui;
	}

	public int getScores() {
		return scores;
	}

	public void setScores(int scores) {
		this.scores = scores;
	}

	public int getTricks() {
		return tricks;
	}

	public void setTricks(int tricks) {
		this.tricks = tricks;
	}

	public int getBids() {
		return bids;
	}

	public  void setBids(int bids) {
		this.bids = bids;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public String getText() {
		String text = "[" + String.valueOf(scores) + "]" + String.valueOf(tricks) + "/" + String.valueOf(bids);
		return text;
	}

	public Hand getHand(){
		return hand;
	}

	public void setHand(Deck deck){
		hand = new Hand(deck);
	}

	public void sortHand(){
		hand.sort(Hand.SortType.SUITPRIORITY, true);
	}

	public abstract Card playCard();
}
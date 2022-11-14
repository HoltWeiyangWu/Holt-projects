package oh_heaven.game;

import ch.aplu.jcardgame.Card;

// HumanPlayer extends functions to display information and seperate from NPC
public class HumanPlayer extends Player implements Subscriber{
    private Card lead;
    private Publisher subject;
    
    HumanPlayer(int playerNum,  GamePlayUI ui, Publisher subject) {
        super(playerNum, ui);
        this.subject = subject;
        this.subject.attach(this);
    }
    
    @Override
    public void update() {
        this.lead = subject.getLead();
    }
    
    @Override
    public Card playCard(){
        if(lead == null){
            super.ui.setStatus("Player " + playerNum + " double-click on card to lead.");
        }else{
            super.ui.setStatus("Player " + playerNum + " double-click on card to follow.");
        }
        return null;
	}

}

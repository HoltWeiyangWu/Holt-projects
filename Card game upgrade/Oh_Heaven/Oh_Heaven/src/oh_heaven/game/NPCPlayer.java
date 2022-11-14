package oh_heaven.game;

import ch.aplu.jcardgame.Card;

// NPCPlayer extends few more function for NPC to play
public class NPCPlayer extends Player{
    private int thinkingTime;
    private NPCType npcPlay;
    private NPCFactory getNpc = NPCFactory.getInstance();

    NPCPlayer(int playerNum, String npcType, GamePlayUI ui, int thinkingTime) {
        super(playerNum, ui);
        this.npcPlay = getNpc.getNpc(npcType);
        this.thinkingTime = thinkingTime; 
    }

    // display NPC player text to the GUI and return the selected card given the 
    // NPC Type of the NPC player 
    @Override
    public Card playCard(){
        super.ui.setStatusText("Player " + playerNum + " thinking...");
        GamePlayUI.delay(thinkingTime);
		return npcPlay.play(super.hand);
	}
}

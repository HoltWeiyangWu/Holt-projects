
package oh_heaven.game;

import ch.aplu.jcardgame.*;

public interface NPCType{

    public Hand getPlayableCards(Hand hand);

    public Card play(Hand hand);
}
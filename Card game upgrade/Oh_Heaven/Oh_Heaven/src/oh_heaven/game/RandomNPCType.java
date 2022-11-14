package oh_heaven.game;

import ch.aplu.jcardgame.*;
import java.util.*;

// NPC logic for random NPC type
public class RandomNPCType implements NPCType{
    private final Random random;

    RandomNPCType(int seed){
        random = new Random(seed);
    }

    public Hand getPlayableCards(Hand hand){
        return hand;
    }

    public Card play(Hand hand){
        Hand getHand = getPlayableCards(hand);
        int x = random.nextInt(getHand.getNumberOfCards());
		return getHand.get(x);
    }
}

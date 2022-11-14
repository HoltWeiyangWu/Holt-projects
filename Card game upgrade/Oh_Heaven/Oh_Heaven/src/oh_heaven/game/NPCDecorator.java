package oh_heaven.game;

import java.util.Random;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

// The decoractor abstract class from the decoractor pattern
public abstract class NPCDecorator implements NPCType{
    protected final int LEAD_CARD = 1;
    protected Publisher subject;
    protected NPCType base;
    protected Random random;
    protected Hand getHand;
    protected Suit leadSuit = null;

    NPCDecorator(Publisher subject, NPCType base, int seed){
        this.subject = subject;
        this.base = base;
        this.random = new Random(seed);
    }

    public abstract Hand playableCondition();
    
    // Decorator pattern calling base component 
    public Hand getPlayableCards(Hand hand){
        return base.getPlayableCards(hand);
    }

    // return seleted card give the NPC type with it selection logic 
    public Card play(Hand hand) {
        Hand getHand = getPlayableCards(hand);

        int x = random.nextInt(getHand.getNumberOfCards());
        
        Card card = getHand.get(x);

        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();

        return hand.getCard(suit, rank);
    }
}
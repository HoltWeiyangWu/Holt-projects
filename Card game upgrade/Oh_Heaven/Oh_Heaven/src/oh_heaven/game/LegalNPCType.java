package oh_heaven.game;

// import java.util.*;
import ch.aplu.jcardgame.Hand;

// NPC logic for legal NPC type
public class LegalNPCType extends NPCDecorator implements Subscriber{

    public LegalNPCType(Publisher subject, NPCType base, int seed){
        super(subject, base, seed);
        this.subject.attach(this);
    }
  
    @Override
    public void update() {
        if(subject.getOrder() == LEAD_CARD || subject.getLeading()){
            if (subject.getLead() == null){
                leadSuit = null;
            }else{
                leadSuit = (Suit) subject.getLead().getSuit();
            }
        }
    }

    @Override
    public Hand playableCondition(){
        if(!getHand.extractCardsWithSuit(leadSuit).isEmpty()){
            return getHand.extractCardsWithSuit(leadSuit); 
        }else{
            return getHand;
        }
    }

    // With decorator pattern, get the base component's `getPlayableCards`
    // and added legal play logic to the card selection 
    @Override
    public Hand getPlayableCards(Hand hand){
        getHand = super.getPlayableCards(hand);
        return playableCondition();
        
    }
}

package oh_heaven.game;

import java.util.*;


import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

// NPC logic for smart NPC type
public class SmartNPCType extends NPCDecorator implements Subscriber{
    private Suit trump = null;
    private Card win = null;

    public SmartNPCType(Publisher subject, NPCType base, int seed){
        super(subject, base, seed);
        this.subject.attach(this);
    }
  
    @Override
    public void update() {
        win = subject.getWinning();
        if(subject.getOrder() == LEAD_CARD || subject.getLeading()){
            if (subject.getLead() == null){
                leadSuit = null;
            }else{
                leadSuit = (Suit) subject.getLead().getSuit();
            }
        }
        if (subject.isGetTrump()){
            this.trump = subject.getTrump();
        }
    }

    public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}

    // return a hand of card without trump suit if the given have has other suit 
    // than the trump suit, else return the original/trumps suit hand
    public Hand getHandwithoutTrump(Hand hand){
        Hand trumpHand = hand.extractCardsWithSuit(trump);
        Hand newHand;
        Hand outHand = null;
        boolean first = true;
        if(trumpHand.getNumberOfCards() != hand.getNumberOfCards()){
            for(Suit suit : Suit.values()){
                if(suit != trump){
                    if (first){
                        outHand = hand.extractCardsWithSuit(suit);
                        first = false;
                    }
                    newHand = hand.extractCardsWithSuit(suit);
                    outHand.insert(newHand, false);
                }
            }
            return outHand;
        }else{
            return trumpHand;
        }
    }

    // return a hand of cards that is bigger/smaller then the winning card
    // if the hand does not contain cards that is bigger/smaller than the 
    // winning card, return null
    public Hand findBiggerCard(Hand hand){
        ArrayList<Card> cards = new ArrayList<Card>();
        for(int i = 0; i < hand.getNumberOfCards(); i++){
            // if win is greater add card to cards
            if(rankGreater(win, hand.get(i))){
                cards.add(hand.get(i));
            }
        }
        // if cards size is not equal to hand size, meaning that hand has 
        // other cards than the given bigger/smaller condition 
        if(cards.size() != hand.getNumberOfCards()){
            for(int i = 0; i < cards.size(); i++){
                hand.remove(cards.get(i), false);
            }
            return hand;
        }else{
            return null;
        }
    }

    // return a hand of cards that is smallest for all suit except the trump 
    // suit in the hand
    public Hand sacrifice(Hand hand){
        Hand newHand = getHandwithoutTrump(hand);
        ArrayList<Card> cards = new ArrayList<Card>();

        // get smallest card of every suit except trump suit, if any
        for(Suit suit : Suit.values()){
            Card card;
            if ((card = hand.extractCardsWithSuit(suit).getLast()) != null){
                cards.add(card);
            }
        }
        newHand.removeAll(false);
        for(Card card : cards){
            newHand.insert(card, false);
        }
        return newHand;
    }

    // The smart NPC strategy
    public Hand smartPlayStrategy(Hand hand){
        Hand newHand;
        // the NPC have lead suit, must follow 
        // the hand will only contain the lead suit from legal
        if(!hand.extractCardsWithSuit(leadSuit).isEmpty()){
            if (win.getSuit() == leadSuit && 
                    (newHand = findBiggerCard(hand))!= null){
                // give all cards which is bigger than current winning cards 
                return newHand;
            }else{
                // can't win, so sacrifice small cards
                return sacrifice(hand);
            }
        }else{ // NPC doesn't have lead suit
            if(!hand.extractCardsWithSuit(trump).isEmpty()){ // but have trump suit
                if(win.getSuit() == leadSuit){    
                    // have trump suit, which could win a lead suit winning card
                    return hand.extractCardsWithSuit(trump);
                }else{                                  // winning suit is trump suit     
                    if((newHand = findBiggerCard(hand.extractCardsWithSuit(trump)))
                        != null){
                        // have bigger trump suit cards, which could win
                        return newHand;
                    }else{
                        // sacrifice small non-trump suit cards, if any,
                        return sacrifice(getHandwithoutTrump(hand));
                    }
                }
            }else{ // don't have trump suit
                return sacrifice(hand);
            }
        }
    }

    @Override
    public Hand playableCondition(){
        if(leadSuit == null){
            return getHandwithoutTrump(getHand);
        }else{
            return smartPlayStrategy(getHand);
        }
    }

    // With decorator pattern, get the base component's `getPlayableCards`
    // and added smart play logic to the card selection 
    @Override
    public Hand getPlayableCards(Hand hand) {
        getHand = super.getPlayableCards(hand);
        return playableCondition();
        
    }
}

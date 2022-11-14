package oh_heaven.game;

import java.util.ArrayList;
import java.util.List;

import ch.aplu.jcardgame.Card;

// A SubjectBase from the observer patterns and publishs information to its subscribers
public class Publisher {

    private List<Subscriber> observers = new ArrayList<Subscriber>();
    private Suit trump;
    
    private Card lead;
    private Card win;

    private boolean initTrump;
    private boolean leading;
    
    private int order = 1;
    private int nbPlayers;

    public Publisher(int nbPlayers){
        this.nbPlayers = nbPlayers;
    }

    public Card getLead() {
        return lead;
    }

    public Card getWinning() {
        return win;
    }

    public int getOrder() {
        return order;
    }
    
    public Suit getTrump() {
        return trump;
    }
    
    public boolean isGetTrump() {
        return initTrump;
    }
    
    public boolean getLeading() {
        return leading;
    }

    public void setLead(Card card) {
        this.lead = card;
        notifyAllObservers();
        setOrder();
    }

    public void setWinning(Card card) {
        this.win = card;
        notifyAllObservers();
    }

    public void setNull() {
        this.lead = null;
        this.win = null;
        this.leading = true;
        notifyAllObservers();
        this.leading = false;
    }

    public void setTrump(Suit trump) {
        this.trump = trump;
        this.initTrump = true;
        notifyAllObservers();
        this.initTrump = false;
    }

    private void setOrder(){
        if (++order > nbPlayers){
            order = 1;
        }
    }

    public void attach(Subscriber observer){
        observers.add(observer);		
    }

    public void notifyAllObservers(){
        for (Subscriber observer : observers) {
            observer.update();
        }
    } 	
}

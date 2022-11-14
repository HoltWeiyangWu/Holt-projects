package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.util.*;
import java.awt.Color;
import java.awt.Font;
import java.util.stream.Collectors;

// the UI class handles all the UI related components
public class GamePlayUI extends CardGame{
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};
    private final Location textLocation = new Location(350, 450);
    private final Location trickLocation = new Location(350, 350);
    private final Location[] handLocations = {
        new Location(350, 625),
        new Location(75, 350),
        new Location(350, 75),
        new Location(625, 350)
    };
    private final Location[] scoreLocations = {
        new Location(575, 675),
        new Location(25, 575),
        new Location(575, 25),
        // new Location(650, 575)
        new Location(575, 575)
    };
    private Location hideLocation = new Location(-500, - 500);
    private Location trumpsActorLocation = new Location(50, 50);
    
    private int nbPlayers;
    private Font bigFont = new Font("Serif", Font.BOLD, 36);
    private Actor trumpsActor;
    private Actor[] scoreActors = {null, null, null, null };

    public void setStatus(String string) { setStatusText(string); }

    public void initialise(String version) {
        setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
    }

    public GamePlayUI(int nbPlayers) {
        super(700, 700, 30);
        this.nbPlayers = nbPlayers;
    }

    public void setPlayerScore(Player currPlayer, int currScoreActor){
        scoreActors[currScoreActor] = new TextActor(currPlayer.getText(), Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[currScoreActor], scoreLocations[currScoreActor]);
    }

    public void updateScore(Player currPlayer, int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(currPlayer.getText(), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}

    public void setTrumpActor(Suit trumps){
        trumpsActor = new Actor("sprites/"+trumpImage[trumps.ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);
    }

    public void removeTrumpActor(){
        removeActor(trumpsActor);
    }

    public void updateAllScore(List<Player> players){
        for (int i=0; i <nbPlayers; i++){
			Player currPlayer = players.get(i);
			updateScore(currPlayer, i);
		}
    }

    public void setHands(List<Player> players){
        RowLayout[] layouts = new RowLayout[nbPlayers];
		for (int i = 0; i < nbPlayers; i++) {
			Hand currPlayerHand = players.get(i).getHand();
			layouts[i] = new RowLayout(handLocations[i], handWidth);
			layouts[i].setRotationAngle(90 * i);
			// layouts[i].setStepDelay(10);
			currPlayerHand.setView(this, layouts[i]);
			currPlayerHand.setTargetArea(new TargetArea(trickLocation));
			currPlayerHand.draw();
		}
    }

    public void setTableView(Hand trick, Card selected) {
        trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
		trick.draw();
		selected.setVerso(false);
    }

    public void resetTableView(Hand trick){
        trick.setView(this, new RowLayout(hideLocation, 0));
        trick.draw();
    }

    public void cardTransfer(Card selected, Hand trick){
        selected.transfer(trick, true);
    }

	public void displayGameOver(Set <Integer> winners ){
		String winText;
		if (winners.size() == 1) {
			winText = "Game over. Winner is player: " +
					winners.iterator().next(); 
		}
		else {
			winText = "Game Over. Drawn winners are players: " +
					String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toSet())); 
		}
		addActor(new Actor("sprites/gameover.gif"), textLocation); 
		setStatusText(winText);
	}
}

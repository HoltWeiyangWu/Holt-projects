package oh_heaven.game;

// Oh_Heaven.java

import oh_heaven.utility.PropertiesLoader;

import java.util.*;


@SuppressWarnings("serial")
public class Oh_Heaven{
	private final String version = "1.0";

	public Oh_Heaven(Properties properties)
	{
		GamePlay gamePlay = new GamePlay(properties, version);
		// board.setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		// board.setStatusText("Initializing...");
		
		gamePlay.playGame();
		// refresh();
	}
	
	public static void main(String[] args)
	{
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		final Properties properties;
		if (args == null || args.length == 0) {
			 properties = PropertiesLoader.loadPropertiesFile(null);
		} else {
			     properties = PropertiesLoader.loadPropertiesFile(args[0]);
		}
		
		new Oh_Heaven(properties);
	}

}

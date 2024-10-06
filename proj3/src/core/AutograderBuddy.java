package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.util.ArrayList;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        char[] inputChars = input.toLowerCase().toCharArray();
        String lowerInput = input.toLowerCase();
        boolean saveAndQuit = input.toLowerCase().endsWith(":q");
        int width = 80;
        int height = 30;
        String saveFile = "save.txt";
        long seed = 0;
        ArrayList<Character> moves = new ArrayList<>();
        boolean loadSaved = false;

        if (saveAndQuit) {
            lowerInput = lowerInput.replace(":q", "");
        }


        if (lowerInput.startsWith("n")) {
            int seedEnd = lowerInput.indexOf('s');
            seed = Long.parseLong(input.substring(1, seedEnd));
            //@source https://www.tutorialspoint.com/Searching-characters-
            //in-a-String-in-Java#:~:text=You%20can%20search%20for%20a
            // ,Otherwise%20it%20returns%20%2D1.
            // how to get index of a specific letter or num
            for (int i = seedEnd + 1; i < lowerInput.length(); i++) {
                moves.add(lowerInput.charAt(i));
            }
        } else if (lowerInput.startsWith("l")) {
            loadSaved = true;
            String savedGame = FileUtils.readFile(saveFile);
            String[] parts = savedGame.split(":");
            seed = Long.parseLong(parts[0]);

            for (int i = 1; i < lowerInput.length(); i++) {
                moves.add(lowerInput.charAt(i));
            }


        }

        World world = new World(width, height, seed);
        Avatar avatar = new Avatar(world, saveFile);
        if (!loadSaved) {
            avatar.spawnAvatarAuto();
        } else {
            avatar.loadAvatarAuto();
        }

        for (char c : moves) {
            avatar.moveAvatarAuto(c);
        }
        //System.out.println("Avatar Position after moves: (" + avatar.getX() + ',' + avatar.getY() + ')');

        if (saveAndQuit) {
            String saveData = (world.getSeed()) + ":" + avatar.getX() + "," + avatar.getY();
            FileUtils.writeFile(saveFile, saveData);
        }
        return world.getWorld();
    }



    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}

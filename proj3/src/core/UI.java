package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;


import java.awt.*;


public class UI {
    private TETile[][] world;
    private World worldInstance;
    private static final TETile AVATAR_TILE = Tileset.AVATAR;
    private Avatar avatar;
    private Light light;


    private static final String saveFile = "save.txt";


    public UI(TETile[][] world, int width, int height, long seed, boolean loadSaved, boolean lightOn) {
        this.world = world;
        this.worldInstance = new World(width, height, seed);
        this.avatar = new Avatar(worldInstance, saveFile);
        this.light = new Light(worldInstance, avatar);
        if (!loadSaved) {
            avatar.spawnAvatar();
            light.placeLights(worldInstance);
        } else {
            avatar.loadAvatar();
            light.loadSavedLights();
        }


    }

    public void handleUserInput() {
        hud();
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            if (key == ':') {
                //System.out.println("hey");
                while (true) {
                    if (StdDraw.hasNextKeyTyped()) {
                        char nextKey = StdDraw.nextKeyTyped();
                        //System.out.println("hi");
                        if (nextKey == 'q' || nextKey == 'Q') {
                            saveAndQuit();
                        } else {
                            break;

                        }
                    }
                }
            } else if (key == 'l' || key =='L') {
                light.handleKeyPress();
            } else {
                avatar.moveAvatar(key);
            }
        }
    }


    public void hud() {
        int mouseXPos = (int) StdDraw.mouseX();
        int mouseYPos = (int) StdDraw.mouseY();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, worldInstance.getHeight() - 1, 12, 1);
        if (mouseXPos >= 0 && mouseXPos < world.length && mouseYPos >= 0 && mouseYPos < world[0].length) {
            TETile mouseTile = worldInstance.getTile(mouseXPos, mouseYPos);
            StdDraw.enableDoubleBuffering();
            StdDraw.setPenColor(StdDraw.WHITE);
            String description = "Current Tile: " + mouseTile.description();
            StdDraw.textLeft(0, worldInstance.getHeight() - 1, description);
        } else {
            StdDraw.textLeft(0, worldInstance.getHeight() - 1, "");
        }
        StdDraw.show();
        //@source https://www.cis.upenn.edu/~cis110/15su/lectures/10stddraw.pdf
        // needed help understanding stddraw better and how to make shapes

    }

    private void renderWorld() {
        StdDraw.clear(Color.BLACK);
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                TETile tile = world[x][y];
                Color color = tile.backgroundColor;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
                char character = tile.character;
                if (character != 0) {
                    StdDraw.setPenColor(tile.textColor);
                    StdDraw.text(x + 0.5, y + 0.5, String.valueOf(character));
                }
            }
        }
        StdDraw.show();

    }
    public void saveAndQuit() {
        String saveData = getSaveData();
        //System.out.print("saveData");
        FileUtils.writeFile(saveFile, saveData);

        System.exit(0);

        //@source from edStem - how to quit game
    }

    private String getSaveData() {

        int lightStatus = light.isLightOn() ? 1 : 0;
        //@source chatGPT, how to convert boolean to integer representation
        //System.out.println(world.getSeed());
        StringBuilder lightPositions = new StringBuilder();
        for (Rectangle room : light.roomsWithLights) {
            int lightX = room.x + room.width / 2;
            int lightY = room.y + room.height / 3;
            lightPositions.append(lightX).append(",").append(lightY).append("\n");
        }
        String lightData = lightPositions.toString();
        return (worldInstance.getSeed()) + "\n" + avatar.getX()+ "," + avatar.getY() + "\n" + lightStatus + "\n" + lightData +"\n";

    }



}




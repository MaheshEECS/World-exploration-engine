package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.*;
import utils.*;

import java.awt.*;


public class Main {
    private static final int MENU_WIDTH = 40;
    private static final int MENU_HEIGHT = 20;
    private static final int HUD_WIDTH = 0;
    private static final int HUD_HEIGHT = 0;
    public static final String SAVE_FILE = "save.txt";
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;

    public static void main(String[] args) {
        drawMainMenu();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (key == 'n' || key == 'N') {
                    newWorld();
                } else if (key == 'l' || key == 'L') {
                    loadWorld();
                } else if (key == 'q' || key == 'Q') {
                    quit();
                }
            } else if (StdDraw.isMousePressed()) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                if (mouseX >= 0 && mouseX <= MENU_WIDTH) {
                    if (mouseY <= MENU_HEIGHT * 0.6 && mouseY > MENU_HEIGHT * 0.5) {
                        newWorld();
                    }
                    if (mouseY <= MENU_HEIGHT * 0.6 && mouseY > MENU_HEIGHT * 0.4) {
                        loadWorld();
                    }
                    if (mouseY <= MENU_HEIGHT * 0.6 && mouseY > MENU_HEIGHT * 0.2) {
                        quit();
                    }
                }
            }
        }
    }

    private static void drawMainMenu() {
        StdDraw.setCanvasSize(MENU_WIDTH * 16, MENU_HEIGHT * 16);
        StdDraw.setXscale(0, MENU_WIDTH);
        StdDraw.setYscale(0, MENU_HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monrovia", Font.BOLD, 40));
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.75, "Main Menu");

        StdDraw.setFont(new Font("Monrovia", Font.PLAIN, 20));
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.55, "N: New World");
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.40, "L: Load World");
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT * 0.25, "Q: Quit");
        StdDraw.show();

    }


    private static void newWorld() {
        long seed = getSeedFromInput();
        World world = new World(WIDTH, HEIGHT, seed);
        renderWorld(world.getWorld());
        UI ui = new UI(world.getWorld(), world.getWidth(), world.getHeight(), seed, false, false);

        while (true) {
            ui.handleUserInput();

        }

    }

    public static void loadWorld() {
        if (FileUtils.fileExists(SAVE_FILE)) {
            String savedGame = FileUtils.readFile(SAVE_FILE);
            World world = getWorldFromInput(savedGame);
            String[] parts = savedGame.split("\n");
            String[] info  = parts[1].split(",");
            UI ui = new UI(world.getWorld(), world.getWidth(), world.getHeight(), world.getSeed(), true, true);
            Avatar avatar = new Avatar(world, Main.SAVE_FILE);



            while (true) {
                ui.handleUserInput();
            }

        } else {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT / 2.0, "No saved game found.");
            StdDraw.show();
        }
    }

    private static void quit() {
        System.exit(0);
    }

    private static long getSeedFromInput() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT / 2.0, "Enter seed and press S:");
        StdDraw.show();

        StringBuilder seedBuilder = new StringBuilder();
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            seedBuilder.append(key);
        }
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(key) == 's') {
                    break;
                }
                seedBuilder.append(key);
                StdDraw.clear(Color.BLACK);
                StdDraw.text(MENU_WIDTH / 2.0, MENU_HEIGHT / 2.0, "Seed: " + seedBuilder.toString());
                StdDraw.show();
            }
        }
        return Long.parseLong(seedBuilder.toString());
    }

    public static World getWorldFromInput(String input) {
        String[] parts = input.split("\n");
        long seed = Long.parseLong(parts[0]);
        String[] position = parts[1].split(",");
        int x = Integer.parseInt(position[0]);
        int y = Integer.parseInt(position[1]);
        return new World(WIDTH, HEIGHT, seed, x, y);
    }

    private static void renderWorld(TETile[][] world) {
        TERenderer ter = new TERenderer();
        ter.initialize(world.length, world[0].length, HUD_WIDTH, HUD_HEIGHT);
        ter.renderFrame(world);

    }
}

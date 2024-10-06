package core;
import tileengine.*;
import utils.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Avatar {
    private static final TETile AVATAR_TILE = Tileset.AVATAR;
    private static final TETile FLOOR_TILE = Tileset.FLOOR;
    private static final TETile WALL_TILE = Tileset.WALL;
    private TERenderer avatar;
    private World world;
    private int avatarX;
    private int avatarY;
    private Random random;
    private TETile currentTile;
    private String saveFile;
    private Light light;

    public Avatar(World world, String saveFile) {
        this.world = world;
        this.saveFile = saveFile;
        this.random = new Random();
        avatar = new TERenderer();
        avatar.renderFrame(world.getWorld());

    }

    public void spawnAvatar() {
        random = new Random((int)world.getSeed());
        do {
            avatarX = random.nextInt(world.getWidth());
            avatarY = random.nextInt(world.getHeight());
        } while (world.getTile(avatarX, avatarY) != Tileset.FLOOR);

        currentTile = world.getTile(avatarX,avatarY);
        world.setTile(avatarX, avatarY, AVATAR_TILE);

        avatar.renderFrame(world.getWorld());


    }

    public void loadAvatar() {
        if (FileUtils.fileExists(saveFile)) {
            try {
                String savedData = new String(Files.readAllBytes(Paths.get(saveFile)));
                String[] parts = savedData.split("\n");
                long seed = Long.parseLong(parts[0]);
                String[] position = parts[1].split(",");
                avatarX = Integer.parseInt(position[0]);
                avatarY = Integer.parseInt(position[1]);


                currentTile = world.getTile(avatarX,avatarY);
                world.setTile(avatarX, avatarY, AVATAR_TILE);




                avatar.renderFrame(world.getWorld());



            } catch (IOException e) {
                System.out.println("Error loading saved state: " + e.getMessage());
            }
        } else {
            spawnAvatar();
        }

    }

    public void moveAvatar(char key) {
        int dx = 0, dy = 0;
        if (key == 'w' || key == 'W') {
            dy = 1;
        } else if (key == 's' || key == 'S') {
            dy = -1;
        } else if (key == 'a' || key == 'A') {
            dx = -1;
        } else if (key == 'd' || key == 'D') {
            dx = 1;
        } else {
            return;
        }

        int newX = avatarX + dx;
        int newY = avatarY + dy;

        if (world.isValidPosition(newX, newY)) {
            TETile newTile = world.getTile(newX, newY);

            System.out.println(world.isLightOn());
            if (!world.isLightOn()) {
                if (currentTile == Tileset.LIGHT1 || currentTile == Tileset.LIGHT2 || currentTile == Tileset.LIGHT3) {
                    currentTile = Tileset.FLOOR;
                }
            }
            world.setTile(avatarX, avatarY, currentTile);
            currentTile = newTile;
            world.setTile(newX, newY, AVATAR_TILE);

            avatarX = newX;
            avatarY = newY;

            avatar.renderFrame(world.getWorld());
        }
    }

    public void moveAvatarAuto(char key) {
        int dx = 0, dy = 0;
        if (key == 'w' || key == 'W') {
            dy = 1;
        } else if (key == 's' || key == 'S') {
            dy = -1;
        } else if (key == 'a' || key == 'A') {
            dx = -1;
        } else if (key == 'd' || key == 'D') {
            dx = 1;
        }

        int newX = avatarX + dx;
        int newY = avatarY + dy;

        if (world.isValidPosition(newX, newY)) {
            world.setTile(avatarX, avatarY, FLOOR_TILE);
            avatarX = newX;
            avatarY = newY;
            world.setTile(avatarX, avatarY, AVATAR_TILE);
        }

    }


    public void saveAndQuit() {
        String saveData = getSaveData();

        FileUtils.writeFile(saveFile, saveData);
        System.exit(0);

        //@source from edStem - how to quit game
    }

    private String getSaveData() {
        int lightStatus = light.isLightOn() ? 1 : 0;
        //@source chatGPT, how to convert boolean to integer representation
        //System.out.println(world.getSeed());
        return (world.getSeed()) + ":" + avatarX + "," + avatarY + lightStatus;

    }

    public void save() {
        String saveData = getSaveData();
        FileUtils.writeFile(saveFile, saveData);
    }
    public int getX() {
        return avatarX;
    }
    public int getY() {
        return avatarY;
    }

    public void loadAvatarAuto() {
        if (FileUtils.fileExists(saveFile)) {
            try {
                String savedData = new String(Files.readAllBytes(Paths.get(saveFile)));
                String[] parts = savedData.split(":");
                long seed = Long.parseLong(parts[0]);
                String[] position = parts[1].split(",");
                avatarX = Integer.parseInt(position[0]);
                avatarY = Integer.parseInt(position[1]);

                world.setTile(avatarX, avatarY, AVATAR_TILE);



            } catch (IOException e) {
                System.out.println("Error loading saved state: " + e.getMessage());
            }
        } else {
            spawnAvatarAuto();
        }


    }
    public void spawnAvatarAuto() {
        random = new Random(world.getSeed());
        do {
            avatarX = random.nextInt(world.getWidth());
            avatarY = random.nextInt(world.getHeight());
        } while (world.getTile(avatarX, avatarY) != Tileset.FLOOR);


        world.setTile(avatarX, avatarY, AVATAR_TILE);



    }






}

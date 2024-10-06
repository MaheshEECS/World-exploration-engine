package core;
import edu.princeton.cs.algs4.StdDraw;
import net.sf.saxon.expr.flwor.Tuple;
import tileengine.*;
import utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.awt.*;
import java.util.List;

public class Light {
    private static final TETile LIGHT_TILE = Tileset.LIGHT;
    private static final TETile WALL_TILE = Tileset.WALL;
    public String saveFile = "save.txt";
    private World world;
    private Avatar avatar;
    private Random random;
    public Set<Rectangle> roomsWithLights;
    private int lightX;
    private int lightY;
    boolean isLightOut;
    boolean loadSaved;
    private List<Point> tiles1 = new ArrayList<>();
    private List<Point> tiles2 = new ArrayList<>();
    private List<Point> tiles3 = new ArrayList<>();
    private List<Point> lightSources = new ArrayList<>();

    public Light(World world, Avatar avatar) {
        this.world = world;
        this.avatar = avatar;
        this.random = new Random();
        this.roomsWithLights = new HashSet<>();
        this.isLightOut = false;
    }

    public void handleKeyPress() {
        isLightOut = !isLightOut;
        if (isLightOut) {
            turnLightsOn();
        } else {
            turnOffLights();

        }
        TERenderer lights = new TERenderer();
        lights.initialize(world.getWidth(), world.getHeight());
        lights.renderFrame(world.getWorld());


    }


    public void placeLights(World world) {
        int numLights = 0;
        for (Rectangle room : world.getallRooms()) {
            if (numLights >= world.getallRooms().size()) {
                break;
            }
            if (!roomsWithLights.contains(room)) {
                do {
                    lightX = room.x + room.width / 2;
                    lightY = room.y + room.height / 3;
                } while (isNextToLight(lightX, lightY));

                world.setTile(lightX, lightY, Tileset.LIGHT);
                roomsWithLights.add(room);
                numLights++;
            }
        }


        TERenderer lights = new TERenderer();
        lights.initialize(world.getWidth(), world.getHeight());
        lights.renderFrame(world.getWorld());
    }


    public int distanceFromLight(int x, int y, int lx, int ly) {
        return Math.abs(x - lightX) + Math.abs(y - lightY);
//        return Math.max(Math.abs(x - lightX),Math.abs(y - lightY)) ;

    }

    public boolean canBeIlluminated(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int x = startX;
        int y = startY;
        int n = 1 + dx + dy;
        int x_inc = (endX > startX) ? 1 : -1;
        int y_inc = (endY > startY) ? 1 : -1;
        int error = dx - dy;
        dx = 2;
        dy= 2;

        for (; n > 0; --n) {
            if (world.getTile(x, y) == WALL_TILE) {
                return false;
            }

            if (error > 0) {
                x += x_inc;
                error -= dy;
            } else {
                y += y_inc;
                error += dx;
            }
        }
        return true;
    }

    public void turnLightsOn() {
        System.out.println("turning lights on");
        world.turnLightOn();
        if (loadSaved) {
            for (Point lights : lightSources) {
                lightX = lights.x;
                lightY = lights.y;
                world.setTile(lightX, lightY,Tileset.LIGHTSON);
//                Set<Point> invalid_light = new HashSet<Point>();
                for (int x = lightX - 1; x <= lightX - 3; x--) {
                    for (int y = lightY - 1; y <= lightY - 3; y--) {
                        if (world.isValidPosition(x, y) && canBeIlluminated(x,y, lightX, lightY)) {
                            int distance = distanceFromLight(x, y, lightX, lightY);
                            if (distance == 0) {
                                continue;
                            }
                            if (distance == 1) {
                                tiles1.add(new Point(x, y));
                            } else if (distance == 2) {
                                tiles2.add(new Point(x, y));
                            } else if (distance == 3) {
                                tiles3.add(new Point(x, y));
                            }
                        }

                    }
                }
            }

        } else {
            for (Rectangle room : roomsWithLights) {
                int lx = room.x + room.width / 2;
                int ly = room.y + room.height / 3;

                world.setTile(lx, ly, Tileset.LIGHTSON);
                lightX = lx;
                lightY = ly;
                for (int x = lightX - 3; x <= lightX + 3; x++) {
                    for (int y = lightY - 3; y <= lightY + 3; y++) {
                        if (!world.isValidPosition(x, y)) {
                            continue;
                        }
                        int distance = distanceFromLight(x, y, lightX, lightY);
                        if (distance == 0) {
                            continue;
                        }
                        if (distance == 1) {
                            tiles1.add(new Point(x, y));
                        } else if (distance == 2) {
                            tiles2.add(new Point(x, y));
                        } else if (distance == 3) {
                            tiles3.add(new Point(x, y));
                        }
                    }
                }
            }
        }

        for (Point tile : tiles1) {
            world.setTile(tile.x, tile.y, Tileset.LIGHT1);
            world.setTile(avatar.getX(), avatar.getY(), Tileset.AVATAR_LIGHT1);
        }
        for (Point tile : tiles2) {
            world.setTile(tile.x, tile.y, Tileset.LIGHT2);
            world.setTile(avatar.getX(), avatar.getY(), Tileset.AVATAR_LIGHT2);
        }
        for (Point tile : tiles3) {
            world.setTile(tile.x, tile.y, Tileset.LIGHT3);
            world.setTile(avatar.getX(), avatar.getY(), Tileset.AVATAR_LIGHT3);
        }

        TERenderer lights = new TERenderer();
        lights.initialize(world.getWidth(), world.getHeight());
        lights.renderFrame(world.getWorld());
        System.out.println("lights are on");

    }

    public void turnOffLights() {
        world.turnLightOff();
        if (loadSaved) {
            for (Point lights : lightSources) {
                lightX = lights.x;
                lightY = lights.y;
                world.setTile(lightX, lightY, Tileset.LIGHT);
                for (int x = lightX - 3; x <= lightX + 3; x++) {
                    for (int y = lightY - 3; y <= lightY + 3; y++) {
                        if (!world.isValidPosition(x, y)) {
                            continue;
                        }
                        int distance = distanceFromLight(x, y, lightX, lightY);
                        if (distance == 0) {
                            continue;
                        }
                        if (distance == 1) {
                            tiles1.add(new Point(x, y));
                        } else if (distance == 2) {
                            tiles2.add(new Point(x, y));
                        } else if (distance == 3) {
                            tiles3.add(new Point(x, y));
                        }
                    }
                }
            }
        }


        for (Rectangle room : roomsWithLights) {
            world.setTile(room.x + room.width / 2, room.y + room.height / 3, Tileset.LIGHT);
        }
        for (Point tile : tiles1) {
            world.setTile(tile.x, tile.y, Tileset.FLOOR);
        }
        for (Point tile : tiles2) {
            world.setTile(tile.x, tile.y, Tileset.FLOOR);
        }
        for (Point tile : tiles3) {
            world.setTile(tile.x, tile.y, Tileset.FLOOR);
        }
        TERenderer lights = new TERenderer();
        lights.initialize(world.getWidth(), world.getHeight());
        lights.renderFrame(world.getWorld());

    }
    public void loadSavedLights() {
        boolean lightsOn = false;
        if (FileUtils.fileExists(saveFile)) {
            try {
                String savedData = new String(Files.readAllBytes(Paths.get(saveFile)));
                loadSaved = true;
                String[] data = savedData.split("\n");
                String seed = data[0];
                int lightStatus = Integer.parseInt(data[2]);
                System.out.println(lightStatus);
                lightsOn = (lightStatus == 1);
                isLightOut = lightsOn;
                System.out.println(lightsOn);


                for (int i = 3; i < data.length; i++) {
                    String[] xy = data[i].split(",");
                    int x = Integer.parseInt(xy[0]);
                    int y = Integer.parseInt(xy[1]);
                    Point point = new Point(x, y);
                    lightSources.add(point);
                }
                System.out.println(lightSources);

                // Set tiles for each light source
                for (Point light : lightSources) {
                    world.setTile(light.x, light.y, Tileset.LIGHT);
                }

                TERenderer lights = new TERenderer();
                lights.initialize(world.getWidth(), world.getHeight(), 0, 0);
                lights.renderFrame(world.getWorld());
                //turnLightsOn();

            } catch (IOException e) {
                System.out.println("Error loading saved state: " + e.getMessage());
            }
        } else {
            placeLights(world);
        }
        if (lightsOn) {
            System.out.println("Lights on = True");
            turnLightsOn();
        } else {
            turnOffLights();
        }
    }
    public int getLightX() {
        return lightX;
    }
    public int getLightY() {
        return lightY;
    }

    public boolean isLightOn() {
        return isLightOut;
    }


    public boolean isLight(int x, int y) {
        return world.getTile(x, y) == Tileset.LIGHT;
    }

    public boolean isNextToLight(int lightX, int lightY) {
        return world.getTile(lightX - 1, lightY) == LIGHT_TILE ||
                world.getTile(lightX + 1, lightY) == LIGHT_TILE ||
                world.getTile(lightX, lightY - 1) == LIGHT_TILE ||
                world.getTile(lightX, lightY + 1) == LIGHT_TILE;
    }
}

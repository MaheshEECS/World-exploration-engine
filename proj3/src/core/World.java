package core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import tileengine.TETile;
import tileengine.Tileset;

public class World {
    private static final TETile FLOOR_TILE = Tileset.FLOOR;
    private static final TETile WALL_TILE = Tileset.WALL;
    private static final TETile NOTHING_TILE = Tileset.NOTHING;
    private static final int ROOM_MIN_SIZE = 4;
    private static final int ROOM_MAX_SIZE = 10;
    private static final int MAX_ROOMS = 15;

    private int width;
    private int height;
    private long seed;
    private TETile[][] world;
    private Random random;
    private List<Rectangle> rooms = new ArrayList<>();
    private boolean isLightOn;

    public World(int width, int height, long seed) {
        this(width, height, seed, 0, 0);
    }


    public World(int width, int height, long seed, int avatarX, int avatarY) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.rooms = new ArrayList<>();
        this.isLightOn = false;
        createWorld();
    }

    public void createWorld() {
        world = new TETile[width][height];
        random = new Random(seed);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = NOTHING_TILE;
            }
        }

        rooms = createRooms();
        createHallways(rooms);
        connectRooms(rooms);
        checkMissing();
    }

    private List<Rectangle> createRooms() {
        List<Rectangle> generatedRooms = new ArrayList<>();
        for (int i = 0; i < MAX_ROOMS; i++) {
            int roomWidth = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE + 1) + ROOM_MIN_SIZE;
            int roomHeight = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE + 1) + ROOM_MIN_SIZE;
            int roomX = random.nextInt(width - roomWidth - 1);
            int roomY = random.nextInt(height - roomHeight - 1);
            Rectangle room = new Rectangle(roomX, roomY, roomWidth, roomHeight);
            boolean overlaps = false;
            for (Rectangle existingRoom : rooms) {
                if (room.intersects(existingRoom)) {
                    overlaps = true;
                    break;
                }
            }
            if (!overlaps) {
                generatedRooms.add(room);
                for (int x = roomX + 1; x < roomX + roomWidth - 1; x++) {
                    for (int y = roomY + 1; y < roomY + roomHeight - 1; y++) {
                        world[x][y] = FLOOR_TILE;
                    }
                }
                for (int x = roomX; x < roomX + roomWidth; x++) {
                    world[x][roomY] = WALL_TILE; // Top wall
                    world[x][roomY + roomHeight - 1] = WALL_TILE; // Bottom wall
                }
                for (int y = roomY; y < roomY + roomHeight; y++) {
                    world[roomX][y] = WALL_TILE; // Left wall
                    world[roomX + roomWidth - 1][y] = WALL_TILE; // Right wall
                }
            }
        }
        return generatedRooms;
    }
    public boolean isLightOn() {
        return isLightOn;
    }

    public void turnLightOff() {
        isLightOn = false;
    }

    public void turnLightOn() {
        isLightOn = true;
    }

    private void createHallways(List<Rectangle> roomsHallway) {
        for (int i = 0; i < roomsHallway.size() - 1; i++) {
            Rectangle room1 = roomsHallway.get(i);
            Rectangle room2 = roomsHallway.get(i + 1);
            int x1 = room1.x + room1.width / 2;
            int y1 = room1.y + room1.height / 2;
            int x2 = room2.x + room2.width / 2;
            int y2 = room2.y + room2.height / 2;

            while (x1 != x2) {
                if (world[x1][y1] != FLOOR_TILE) {
                    world[x1][y1] = FLOOR_TILE;

                }

                if (world[x1][y1 - 1] != FLOOR_TILE) {
                    world[x1][y1 - 1] = WALL_TILE;
                }
                if (world[x1][y1 + 1] != FLOOR_TILE) {
                    world[x1][y1 + 1] = WALL_TILE;
                }
                if (x1 < x2) {
                    x1++;
                } else {
                    x1--;
                }
            }
            while (y1 != y2) {
                if (world[x1][y1] != FLOOR_TILE) {
                    world[x1][y1] = FLOOR_TILE;
                }

                if (world[x1 - 1][y1] != FLOOR_TILE) {
                    world[x1 - 1][y1] = WALL_TILE;
                }
                if (world[x1 + 1][y1] != FLOOR_TILE) {
                    world[x1 + 1][y1] = WALL_TILE;
                }
                if (y1 < y2) {
                    y1++;
                } else {
                    y1--;
                }
            }
            world[x2][y2] = WALL_TILE;
        }
    }

    private void connectRooms(List<Rectangle> roomsHallway) {
        for (int i = 0; i < roomsHallway.size() - 1; i++) {
            Rectangle room1 = roomsHallway.get(i);
            Rectangle room2 = roomsHallway.get((i + 1) % roomsHallway.size());
            int x1 = room1.x + room1.width / 2;
            int y1 = room1.y + room1.height / 2;
            int x2 = room2.x + room2.width / 2;
            int y2 = room2.y + room2.height / 2;
            while (x1 != x2) {
                world[x1][y1] = FLOOR_TILE;
                if (x1 < x2) {
                    x1++;
                } else {
                    x1--;
                }
            }
            while (y1 != y2) {
                world[x1][y1] = FLOOR_TILE;
                y1 += (y1 < y2) ? 1 : -1;
            }
        }
    }
    public void checkMissing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (world[x][y] == FLOOR_TILE) {
                    replaceAdjacentNothingWithWall(x, y);
                }
            }
        }
    }

    public void replaceAdjacentNothingWithWall(int x, int y) {
        if (x > 0 && world[x - 1][y] == Tileset.NOTHING) {
            world[x - 1][y] = WALL_TILE;
        }
        if (x < width - 1 && world[x + 1][y] == Tileset.NOTHING) {
            world[x + 1][y] = WALL_TILE;
        }
        if (y > 0 && world[x][y - 1] == Tileset.NOTHING) {
            world[x][y - 1] = WALL_TILE;
        }
        if (y < height - 1 && world[x][y + 1] == Tileset.NOTHING) {
            world[x][y + 1] = WALL_TILE;
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public static TETile getWallTile() {
        return WALL_TILE;
    }

    public TETile getFloorTile() {
        return FLOOR_TILE;
    }

    // For putting the avatar in a room
    public Rectangle getRandomRoom() {
        if (rooms.isEmpty()) {
            return null;
        }
        int i = random.nextInt(rooms.size());
        return rooms.get(i);
    }

    public boolean isWalkable(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && world[x][y] == FLOOR_TILE;
    }

    public boolean isValidPosition(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
//        return getTile(x,y) == Tileset.FLOOR;
        return getTile(x, y) != Tileset.WALL && getTile(x, y) != Tileset.LIGHT && getTile(x,y) != Tileset.NOTHING;
    }

    public void setTile(int x, int y, TETile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            world[x][y] = tile;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSeed() {
        return seed;
    }


    public TETile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return world[x][y];
        }
        return null;
    }
    public List<Rectangle> getallRooms() {
        return rooms;
    }


}



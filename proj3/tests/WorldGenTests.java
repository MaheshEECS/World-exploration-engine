import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;
import static com.google.common.truth.Truth.assertThat;

public class WorldGenTests {
    @Test
    public void differentInputs() {
        String [] seeds = {"n5197880843569031643s", "n455857754086099036s", "n1009835137506199904s", "n2760636605756985376s"};
        for (String seed : seeds) {
            TETile[][] tiles = AutograderBuddy.getWorldFromInput(seed);
            TERenderer ter = new TERenderer();
            ter.initialize(tiles.length, tiles[0].length);
            ter.renderFrame(tiles);
            StdDraw.pause(5000);
        }

    }
    @Test
    public void basicTest() {
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000);
    }

    @Test
    public void basicInteractivityTest() {

        String input = "n123swasdwasd";
        TETile[][] tiles = AutograderBuddy.getWorldFromInput(input);
        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(100000);
    }

    @Test
    public void basicSaveTest() {

        String inputSaveAndQuit = "n123swasd:q";
        TETile[][] tilesSaveAndQuit = AutograderBuddy.getWorldFromInput(inputSaveAndQuit);
        String inputNoSave = "lwasd";
        TETile[][] tilesNoSave = AutograderBuddy.getWorldFromInput(inputNoSave);

    }
    @Test
    public void multipleInputs() {
        String input = "n29658350093875swawwadwass:q";
        TETile[][] tiles = AutograderBuddy.getWorldFromInput(input);
        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000);

        String input2 = "lwa";
        TETile[][] tiles2 = AutograderBuddy.getWorldFromInput(input2);
        TERenderer tile = new TERenderer();
        tile.initialize(tiles2.length, tiles2[0].length);
        tile.renderFrame(tiles2);
        StdDraw.pause(5000);

        String input3 = "n29658350093875swawwadwasswa";
        TETile[][] tiles3 = AutograderBuddy.getWorldFromInput(input3);
        TERenderer tile2 = new TERenderer();
        tile2.initialize(tiles3.length, tiles3[0].length);
        tile2.renderFrame(tiles3);
        StdDraw.pause(5000);


        assertThat(tiles2).isEqualTo(tiles3);
    }
    @Test
    public void test() {
        String input3 = "n2965835009387542179swawwadwasswa";
        TETile[][] tiles3 = AutograderBuddy.getWorldFromInput(input3);
        TERenderer tile2 = new TERenderer();
        tile2.initialize(tiles3.length, tiles3[0].length);
        tile2.renderFrame(tiles3);
        StdDraw.pause(10000);
    }

}

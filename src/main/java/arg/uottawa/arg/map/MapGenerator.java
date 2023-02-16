package arg.uottawa.arg.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MapGenerator extends MapRenderer {

    URL url;
    BufferedImage img;

    public MapGenerator() {
        try {
            url = new URL("https://dl.dropboxusercontent.com/s/moxyj8fwsjktmb4/QR.png?dl=0");
            img = MapPalette.resizeImage(ImageIO.read(url));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        canvas.drawImage(0, 0, img);
    }
}

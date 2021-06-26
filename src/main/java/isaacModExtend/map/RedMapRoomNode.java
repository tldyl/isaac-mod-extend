package isaacModExtend.map;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RedMapRoomNode extends MapRoomNode {
    public RedMapRoomNode(int x, int y) {
        super(x, y);
        this.color = Color.RED;
    }

    @Override
    public void addEdge(MapEdge e) {
        try {
            Field edges = MapRoomNode.class.getDeclaredField("edges");
            edges.setAccessible(true);
            List<MapEdge> edgeList = (ArrayList<MapEdge>) edges.get(this);
            edgeList.add(e);
        } catch (NoSuchFieldException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }
}

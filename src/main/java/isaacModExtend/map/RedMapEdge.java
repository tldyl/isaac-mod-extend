package isaacModExtend.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.map.MapEdge;

public class RedMapEdge extends MapEdge {
    public RedMapEdge(int srcX, int srcY, float srcOffsetX, float srcOffsetY, int dstX, int dstY, float dstOffsetX, float dstOffsetY, boolean isBoss) {
        super(srcX, srcY, srcOffsetX, srcOffsetY, dstX, dstY, dstOffsetX, dstOffsetY, isBoss);
    }

    @Override
    public void render(SpriteBatch sb) {
        this.color = Color.RED;
        super.render(sb);
    }
}

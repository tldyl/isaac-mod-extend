package isaacModExtend.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.IsaacModExtend;
import utils.Point;

public class BloodTearPoofEffect extends AbstractGameEffect {
    private AnimatedActor tearPoof;

    BloodTearPoofEffect(Point pos, float scale, int size) {
        this.tearPoof = new AnimatedActor(IsaacModExtend.getResourcePath("effects/blood_tear_poof.xml"));
        tearPoof.scale = (scale / 8.0F) * (float) size;
        tearPoof.xPosition = (float) pos.x - 8.0F * tearPoof.scale;
        tearPoof.yPosition = (float) pos.y - 8.0F * tearPoof.scale;
        tearPoof.setCurAnimation("Poof");
    }

    @Override
    public void update() {
        tearPoof.update();
        this.isDone = tearPoof.isCurAnimationDone();
    }

    @Override
    public void render(SpriteBatch sb) {
        tearPoof.render(sb);
    }

    @Override
    public void dispose() {
        this.tearPoof.dispose();
    }
}

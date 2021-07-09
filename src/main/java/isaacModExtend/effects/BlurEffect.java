package isaacModExtend.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import isaacModExtend.IsaacModExtend;

public class BlurEffect extends AbstractGameEffect {
    private Texture img;
    private float a;
    private FrameBuffer frameBuffer;

    public BlurEffect(float alpha, float duration) {
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        this.img = IsaacModExtend.getCreatureSnapshot(AbstractDungeon.player, this.frameBuffer);
        this.a = alpha;
        this.duration = duration;
        this.startingDuration = this.duration;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.img == null) {
            this.isDone = true;
            return;
        }
        sb.setColor(1, 1, 1, this.a * (this.duration / this.startingDuration));
        sb.draw(this.img, 0, 0,
                this.img.getWidth(), this.img.getHeight(),
                0, 0,
                this.img.getWidth(), this.img.getHeight(),
                false, true);
    }

    @Override
    public void dispose() {
        this.frameBuffer.dispose();
    }
}

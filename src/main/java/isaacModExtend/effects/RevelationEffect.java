package isaacModExtend.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import isaacModExtend.IsaacModExtend;

public class RevelationEffect extends AbstractGameEffect {
    private Texture img = new Texture(IsaacModExtend.getResourcePath("effects/revelation/revelation.png"));
    private float a;
    private float x;
    private float y;

    public RevelationEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.a = 1.0F;
        this.duration = 1.8F;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
            dispose();
        }
        if (this.duration > 0.6F) {
            this.a = (float) (Math.sin((1.8F - this.duration) * 31.4159F) * 0.3F) + 0.7F;
        } else {
            this.a = this.duration / 0.6F;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, this.a);
        sb.draw(this.img, this.x, this.y - 20);
    }

    @Override
    public void dispose() {
        this.img.dispose();
    }
}

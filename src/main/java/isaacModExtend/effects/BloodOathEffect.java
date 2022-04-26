package isaacModExtend.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import isaacModExtend.IsaacModExtend;

public class BloodOathEffect extends AbstractGameEffect {
    private boolean playSfx = false;
    private Texture img;
    private float y;
    private float startY;
    private float a;

    public BloodOathEffect() {
        this.duration = 1.5F;
        this.startingDuration = this.duration;
        this.renderBehind = true;
        this.img = new Texture(IsaacModExtend.getResourcePath("effects/bloodOath/bloodOath.png"));
        this.y = AbstractDungeon.player.drawY + AbstractDungeon.player.hb.height;
        this.startY = this.y;
        this.a = 1.0F;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, this.a);
        sb.draw(this.img, AbstractDungeon.player.hb.cX - 34.0F, this.y);
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration > 0.8F) {
            this.y = Interpolation.exp10In.apply(this.startY, Settings.HEIGHT * 0.65F, (this.startingDuration - this.duration) / 0.7F);
        } else if (this.duration > 0.5F) {
            this.y = Interpolation.exp10Out.apply(Settings.HEIGHT * 0.65F, AbstractDungeon.player.hb.cY, (0.8F - this.duration) / 0.3F);
        } else {
            this.y = Interpolation.exp10In.apply(AbstractDungeon.player.hb.cY, this.startY, (0.5F - this.duration) / 0.5F);
            this.a = Interpolation.linear.apply(1.0F, 0.0F, (0.5F - this.duration) / 0.5F);
        }
        if (this.duration <= 0.5F && !playSfx) {
            CardCrawlGame.sound.play("RELIC_BLOOD_OATH_STAB");
            this.img.dispose();
            this.img = new Texture(IsaacModExtend.getResourcePath("effects/bloodOath/bloodOath_bloodied.png"));
            playSfx = true;
        }
        if (this.duration <= 0) {
            this.isDone = true;
            dispose();
        }
    }

    @Override
    public void dispose() {
        this.img.dispose();
    }
}

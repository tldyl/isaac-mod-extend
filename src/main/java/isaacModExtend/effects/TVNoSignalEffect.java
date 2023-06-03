package isaacModExtend.effects;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import isaacModExtend.IsaacModExtend;

public class TVNoSignalEffect extends AbstractGameEffect {
    private ShaderProgram shader;
    private float mixLevel = 0.2F;
    private final Texture screenShot;
    private final boolean lastEffect;

    public TVNoSignalEffect(float duration, Texture screenShot, boolean lastEffect) {
        this.shader = new ShaderProgram(
                Gdx.files.internal(getShaderPath("tv_no_signal/tv_no_signal.vsh")).readString(),
                Gdx.files.internal(getShaderPath("tv_no_signal/tv_no_signal.fsh")).readString());
        if (!shader.isCompiled()) {
            throw new RuntimeException(shader.getLog());
        }
        this.duration = this.startingDuration = duration;
        this.screenShot = screenShot;
        this.lastEffect = lastEffect;
    }

    private String getShaderPath(String path) {
        return "IsaacShader/" + path;
    }

    @Override
    public void update() {
        if (this.duration <= 0) {
            this.isDone = true;
            return;
        }
        if (this.duration == this.startingDuration) {
            CardCrawlGame.sound.stop("DEATH_CARD_MIX");
            CardCrawlGame.sound.playA("DEATH_CARD_MIX", -0.5F);
        }
        if (this.startingDuration - this.duration < 0.25F) {
            this.mixLevel = Interpolation.linear.apply(0.3F, 1.0F, (this.startingDuration - this.duration) / 0.25F);
        } else if (this.startingDuration - this.duration < 0.5F) {
            this.mixLevel = Interpolation.linear.apply(1.0F, lastEffect ? 0.0F : 0.3F, (this.startingDuration - this.duration - 0.25F) / 0.25F);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.shader == null) return;
        sb.setShader(shader);
        CardCrawlGame.psb.setShader(shader);
        shader.setUniformf("mix_level", this.mixLevel);
        shader.setUniformf("duration", this.duration / this.startingDuration);
        if (screenShot != null && this.startingDuration - this.duration > 0.25F) {
            IsaacModExtend.renderPostProcessRegion = false;
            sb.draw(screenShot, 0, 0, screenShot.getWidth(), screenShot.getHeight(), 0, 0, screenShot.getWidth(), screenShot.getHeight(), false, true);
        }
        this.duration -= Gdx.graphics.getDeltaTime();
    }

    @Override
    public void dispose() {
        if (this.shader != null) {
            SpriteBatch sb = ReflectionHacks.getPrivate(Gdx.app.getApplicationListener(), CardCrawlGame.class, "sb");
            if (sb.getShader() == this.shader) sb.setShader(null);
            if (CardCrawlGame.psb.getShader() == this.shader) CardCrawlGame.psb.setShader(null);
            this.shader.dispose();
            this.shader = null;
        }
        IsaacModExtend.renderPostProcessRegion = true;
    }
}

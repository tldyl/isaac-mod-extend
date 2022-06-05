package isaacModExtend.effects;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class RetroVisionEffect extends AbstractGameEffect {
    private ShaderProgram shader;
    private float granularity = 1;
    private float[] resolution;

    public RetroVisionEffect() {
        this.shader = new ShaderProgram(
                Gdx.files.internal(getShaderPath("retroVision/retroVision.vsh")).readString(),
                Gdx.files.internal(getShaderPath("retroVision/retroVision.fsh")).readString());
        if (!shader.isCompiled()) {
            throw new RuntimeException(shader.getLog());
        }
        this.duration = this.startingDuration = 10.0F;
        this.resolution = new float[]{Gdx.graphics.getWidth(), Gdx.graphics.getHeight()};
    }

    private String getShaderPath(String path) {
        return "IsaacShader/" + path;
    }

    @Override
    public void update() {
        if (this.duration <= 0) this.isDone = true;
        if (this.startingDuration - this.duration < 1.0F) {
            this.granularity = Interpolation.exp10Out.apply(this.startingDuration - this.duration) * 7.5F * Settings.scale;
        } else if (this.duration <= 1.0F) {
            this.granularity = Interpolation.exp10Out.apply(this.duration) * 7.5F * Settings.scale;
        }
        if (this.granularity < 1) this.granularity = 1;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.shader == null) return;
        sb.setShader(shader);
        CardCrawlGame.psb.setShader(shader);
        shader.setUniformf("granularity", this.granularity);
        shader.setUniform2fv("u_resolution", this.resolution, 0, 2);
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
    }
}

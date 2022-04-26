package isaacModExtend.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.IsaacModExtend;
import utils.Point;

public class BloodTearEffect extends AbstractGameEffect {
    private float v; //速度
    private float angle; //方向(弧度制)
    private AnimatedActor tear;
    private float scale;
    private int size;

    public BloodTearEffect(Point startPos, Point targetPos, float velocity, float sustain, float scale, int size) {
        this.v = velocity;
        this.duration = sustain;
        this.tear = new AnimatedActor(IsaacModExtend.getResourcePath("effects/blood_tear.xml"));
        tear.xPosition = (float) startPos.x;
        tear.yPosition = (float) startPos.y;
        tear.scale = scale;
        double x = targetPos.x - startPos.x;
        double y = targetPos.y - startPos.y;
        this.angle = MathUtils.atan2((float) y, (float)x);
        if (size < 1) size = 1;
        if (size > 13) size = 13;
        tear.setCurAnimation("BloodTear" + size);
        this.scale = scale;
        this.size = size;
    }

    public BloodTearEffect(Point startPos, float angleRad, float velocity, float sustain, float scale, int size) {
        this.v = velocity;
        this.duration = sustain;
        this.tear = new AnimatedActor(IsaacModExtend.getResourcePath("effects/blood_tear.xml"));
        tear.xPosition = (float) startPos.x;
        tear.yPosition = (float) startPos.y;
        tear.scale = scale;
        this.angle = angleRad;
        if (size < 1) size = 1;
        if (size > 13) size = 13;
        tear.setCurAnimation("BloodTear" + size);
        this.scale = scale;
        this.size = size;
    }

    @Override
    public void update() {
        tear.xPosition += v * Gdx.graphics.getDeltaTime() * 60.0F * MathUtils.cos(angle);
        tear.yPosition += v * Gdx.graphics.getDeltaTime() * 60.0F * MathUtils.sin(angle);
        tear.update();
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
            dispose();
            Point endPos = new Point(tear.xPosition, tear.yPosition);
            AbstractDungeon.effectsQueue.add(new BloodTearPoofEffect(endPos, scale, size));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        tear.render(sb);
    }

    @Override
    public void dispose() {
        this.tear.dispose();
    }
}

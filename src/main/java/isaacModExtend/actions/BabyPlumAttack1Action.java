package isaacModExtend.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.effects.BloodTearEffect;
import utils.Point;

public class BabyPlumAttack1Action extends AbstractGameAction {
    private AnimatedActor animation;
    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private float effectTimer = 0.03F;
    private float angleRad = MathUtils.PI * 1.8F / 3.0F;
    private float angleDelta = MathUtils.PI / 5.0F;
    private float velocity = 5.0F;

    public BabyPlumAttack1Action(AnimatedActor animation, float targetX, float targetY) {
        this.duration = 2.0F;
        this.animation = animation;
        this.startX = animation.xPosition;
        this.startY = animation.yPosition;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void update() {
        if (duration > 1.0F) {
            animation.xPosition = Interpolation.exp5Out.apply(startX, targetX, 2.0F - this.duration);
            animation.yPosition = Interpolation.exp5Out.apply(startY, targetY, 2.0F - this.duration);
            effectTimer -= Gdx.graphics.getDeltaTime();
            if (effectTimer <= 0 && angleRad > -1.6F * MathUtils.PI) {
                Point startPos = new Point(animation.xPosition - (velocity - 4.0F) * 16.0F * Settings.scale, animation.yPosition + 32.0F * animation.scale);
                AbstractDungeon.effectList.add(new BloodTearEffect(startPos, angleRad, this.velocity, 2.2F, 2.0F * Settings.scale, 9));
                angleRad -= angleDelta;
                angleDelta -= MathUtils.PI / 56.0F;
                if (angleDelta < MathUtils.PI / 9.0F) {
                    angleDelta = MathUtils.PI / 9.0F;
                }
                velocity -= 9.0F * Gdx.graphics.getDeltaTime();
                if (velocity < 4.0F) {
                    velocity = 4.0F;
                }
                effectTimer = 0.03F;
            }
        } else {
            animation.xPosition = Interpolation.exp5Out.apply(targetX, startX, 1.0F - this.duration);
            animation.yPosition = Interpolation.exp5Out.apply(targetY, startY, 1.0F - this.duration);
        }
        this.tickDuration();
    }
}

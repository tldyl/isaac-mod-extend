package isaacModExtend.actions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.effects.BloodTearEffect;
import utils.Point;

public class SirenSlashAttackAction extends AbstractGameAction {
    private AnimatedActor animation;
    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private boolean tearEffect;

    public SirenSlashAttackAction(AnimatedActor animation, float targetX, float targetY, boolean tearEffect) {
        this.duration = 1.6F;
        this.animation = animation;
        this.startX = animation.xPosition;
        this.startY = animation.yPosition;
        this.targetX = targetX;
        this.targetY = targetY;
        this.tearEffect = tearEffect;
    }

    @Override
    public void update() {
        if (duration > 1.0F) {
            animation.xPosition = Interpolation.exp5Out.apply(startX, targetX, 1.6F - this.duration);
            animation.yPosition = Interpolation.exp5Out.apply(startY, targetY, 1.6F - this.duration);
            if (duration == 1.6F && tearEffect) {
                float angleRad = MathUtils.PI * 0.95F;
                for (int i=0;i<5;i++) {
                    Point startPos = new Point(animation.xPosition - 16.0F * Settings.scale, animation.yPosition + 32.0F * animation.scale * Settings.scale);
                    AbstractDungeon.effectList.add(new BloodTearEffect(startPos, angleRad, 18.0F, 1.2F, 2.0F, 9));
                    angleRad += MathUtils.PI / 24.0F;
                }
            }
        } else {
            animation.xPosition = Interpolation.exp5Out.apply(targetX, startX, 1.0F - this.duration);
            animation.yPosition = Interpolation.exp5Out.apply(targetY, startY, 1.0F - this.duration);
        }
        this.tickDuration();
    }
}

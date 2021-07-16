package isaacModExtend.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.effects.BloodTearEffect;
import utils.Point;

public class BabyPlumAttack3Action extends AbstractGameAction {
    private AnimatedActor animation;
    private float v = 0.0F;
    private float distX = 1.0F;
    private float distY = 1.0F;
    private float targetX;
    private float targetY;
    private float startX;
    private float startY;
    private boolean t = false;
    private boolean endAttack = false;
    private float effectTimer = 0.03F;
    private boolean playSfx = false;

    public BabyPlumAttack3Action(AnimatedActor animation, float targetX, float targetY) {
        this.duration = MathUtils.random(4.0F, 4.3F);
        this.animation = animation;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void update() {
        if (duration > 1.0F) {
            if (!playSfx) {
                CardCrawlGame.sound.playAndLoop("BOSS_BABY_PLUM_BUBBLE_LOOP");
                playSfx = true;
            }
            if (!animation.flipX) {
                if ("Attack3Loop".equals(animation.getCurAnimationName())) {
                    distX = 1.0F;
                    distY = 1.0F;
                    if (animation.xPosition >= Settings.WIDTH - 64.0F * animation.scale) {
                        animation.flipX = true;
                    }
                    if (animation.yPosition >= Settings.HEIGHT - 64.0F * animation.scale && !endAttack) {
                        animation.setCurAnimation("Attack3BackLoop");
                    }
                } else if ("Attack3BackLoop".equals(animation.getCurAnimationName())) {
                    distX = 1.0F;
                    distY = -1.0F;
                    if (animation.xPosition >= Settings.WIDTH - 64.0F * animation.scale) {
                        animation.flipX = true;
                    }
                    if (animation.yPosition <= 0 && !endAttack) {
                        animation.setCurAnimation("Attack3Loop");
                    }
                }
            } else {
                if ("Attack3Loop".equals(animation.getCurAnimationName())) {
                    distX = -1.0F;
                    distY = 1.0F;
                    if (animation.xPosition <= 0) {
                        animation.flipX = false;
                    }
                    if (animation.yPosition >= Settings.HEIGHT - 64.0F * animation.scale && !endAttack) {
                        animation.setCurAnimation("Attack3BackLoop");
                    }
                } else if ("Attack3BackLoop".equals(animation.getCurAnimationName())) {
                    distX = -1.0F;
                    distY = -1.0F;
                    if (animation.xPosition <= 0) {
                        animation.flipX = false;
                    }
                    if (animation.yPosition <= 0 && !endAttack) {
                        animation.setCurAnimation("Attack3Loop");
                    }
                }
            }
            animation.xPosition += v * animation.scale * distX;
            animation.yPosition += v * animation.scale * distY;
        } else {
            if (!t) {
                startX = animation.xPosition;
                startY = animation.yPosition;
                t = true;
            }
            animation.xPosition = Interpolation.exp5Out.apply(startX, targetX, 1.0F - duration);
            animation.yPosition = Interpolation.exp5Out.apply(startY, targetY, 1.0F - duration);
        }
        if (duration > 2.0F) {
            v += Gdx.graphics.getDeltaTime() * 10.0F;
            if (v > Gdx.graphics.getDeltaTime() * 360.0F) {
                v = Gdx.graphics.getDeltaTime() * 360.0F;
            }
        } else {
            if (!endAttack) {
                animation.setCurAnimation("Attack3End");
                endAttack = true;
                CardCrawlGame.sound.stop("BOSS_BABY_PLUM_BUBBLE_LOOP");
            }
            v -= Gdx.graphics.getDeltaTime() * 10.0F;
            if (v <= 0) {
                v = 0;
            }
        }
        if (duration > 1.7F) {
            effectTimer -= Gdx.graphics.getDeltaTime();
            if (effectTimer <= 0) {
                effectTimer = 0.03F;
                Point startPos = new Point(animation.xPosition, animation.yPosition + 32.0F * animation.scale);
                Point targetPos = new Point(animation.xPosition - (v + MathUtils.random(2 * v)) * distX, animation.yPosition + 32.0F * animation.scale - (2 * (v + MathUtils.random(v))) * distY);
                AbstractDungeon.effectList.add(new BloodTearEffect(startPos, targetPos, MathUtils.random(0.6F * v), MathUtils.random(2.0F, 3.0F), 2.0F * Settings.scale, MathUtils.random(3, 13)));
            }
        }
        this.tickDuration();
    }
}

package isaacModExtend.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.effects.BlurEffect;

import java.lang.reflect.Field;

public class SuplexRushAction extends AbstractGameAction {
    private AbstractPlayer p;
    private int phase = 0;
    private float startX;
    private float startY;
    private boolean called = false;

    public SuplexRushAction(AbstractCreature target) {
        this.target = target;
        this.duration = 0.0F;
        this.p = AbstractDungeon.player;
        startX = p.drawX;
        startY = p.drawY;
    }

    @Override
    public void update() {
        this.duration += Gdx.graphics.getDeltaTime();
        switch (this.phase) {
            case 0:
                if (!called) {
                    CardCrawlGame.sound.play("RELIC_SUPLEX_RUSH", 0.1F);
                    called = true;
                }
                p.movePosition(startX + Interpolation.linear.apply(this.duration * 3.33F) * (target.drawX - startX), startY);
                AbstractDungeon.effectsQueue.add(new BlurEffect(0.8F, 0.25F));
                if (this.duration >= 0.3F) {
                    movePosition(target.drawX, p.drawY + 64.0F * Settings.scale, target);
                    this.phase = 1;
                    this.duration = 0.0F;
                    called = false;
                }
                break;
            case 1:
                if (!called) {
                    CardCrawlGame.sound.play("RELIC_SUPLEX_LIFT");
                    called = true;
                }
                if (this.duration >= 1.0F) {
                    this.phase = 2;
                    this.duration = 0.0F;
                    called = false;
                }
                break;
            case 2:
                if (!called) {
                    float vY = 1000.0F * Settings.scale;
                    p.useJumpAnimation();
                    p.flipVertical = true;
                    this.target.useJumpAnimation();
                    this.target.flipVertical = true;
                    movePosition(target.drawX, p.drawY - 64.0F * Settings.scale, target);
                    try {
                        Field field = AbstractCreature.class.getDeclaredField("vY");
                        field.setAccessible(true);
                        field.setFloat(p, vY);
                        field.setFloat(this.target, vY);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    called = true;
                }
                if (this.duration >= 1.6F) {
                    p.flipVertical = false;
                    this.target.flipVertical = false;
                    IsaacModExtend.addToBot(new DamageAction(this.target, new DamageInfo(p, 50, DamageInfo.DamageType.NORMAL)));
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.XLONG, true);
                    CardCrawlGame.sound.play("RELIC_SUPLEX_SMASH");
                    this.phase = 3;
                    this.duration = 0.0F;
                    called = false;
                }
                break;
            case 3:
                if (!called) {
                    p.flipHorizontal = !p.flipHorizontal;
                    movePosition(target.drawX, p.drawY, target);
                    called = true;
                }
                p.movePosition(startX + Interpolation.linear.apply((0.5F - this.duration) * 2.0F) * (target.drawX - startX), startY);
                AbstractDungeon.effectsQueue.add(new BlurEffect(0.8F, 0.25F));
                if (this.duration >= 0.5F) {
                    p.flipHorizontal = !p.flipHorizontal;
                    p.movePosition(startX, startY);
                    this.isDone = true;
                }
        }
    }

    public static void movePosition(float x, float y, AbstractCreature creature) {
        creature.drawX = x;
        creature.drawY = y;
        creature.dialogX = creature.drawX + 0.0F * Settings.scale;
        creature.dialogY = creature.drawY + 170.0F * Settings.scale;
        creature.animX = 0.0F;
        creature.animY = 0.0F;
        creature.hb.move(creature.drawX + creature.hb_x + creature.animX, creature.drawY + creature.hb_y + creature.hb_h / 2.0F);
        creature.healthHb.move(creature.hb.cX, creature.hb.cY - creature.hb_h / 2.0F - creature.healthHb.height / 2.0F);
    }
}

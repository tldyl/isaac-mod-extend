package isaacModExtend.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.effects.BlurEffect;

public class MarsRushAction extends AbstractGameAction {
    private float startX;
    private float startY;
    private AbstractPlayer p;
    private boolean t = false;
    private float deltaY = 15;
    private float blurTimer = 0.02F;

    public MarsRushAction() {
        this.duration = 1.0F;
        this.p = AbstractDungeon.player;
        startX = p.drawX;
        startY = p.drawY;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.blurTimer -= Gdx.graphics.getDeltaTime();
        if (this.duration > 0.5F) {
            p.movePosition(startX + Interpolation.linear.apply((1.0F - this.duration) * 2.0F) * (Settings.WIDTH * 0.9F - startX), startY);
            if (blurTimer <= 0) {
                AbstractDungeon.effectsQueue.add(new BlurEffect(0.8F, 0.35F));
                blurTimer = 0.02F;
            }
        } else {
            if (!t) {
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.LONG, false);
                IsaacModExtend.addToBot(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(32, true), DamageInfo.DamageType.THORNS, AttackEffect.SLASH_HORIZONTAL));
                t = true;
            }
            p.movePosition(startX + Interpolation.exp5In.apply(this.duration * 2.0F) * (Settings.WIDTH * 0.9F - startX), p.drawY + deltaY);
            deltaY--;
            if (p.drawY < startY) p.movePosition(p.drawX, startY);
        }
        if (this.duration <= 0) {
            p.movePosition(startX, startY);
            this.isDone = true;
        }
    }
}

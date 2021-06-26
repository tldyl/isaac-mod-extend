package isaacModExtend.relics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import isaacModExtend.IsaacModExtend;
import relics.TheBattery;
import relics.abstracrt.ChargeableRelic;

public class Larynx extends ChargeableRelic {
    public static final String ID = IsaacModExtend.makeID("Larynx");
    public static final String IMG_PATH = "relics/larynx.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public Larynx() {
        super(ID, IMG, RelicTier.UNCOMMON, LandingSound.FLAT, 12);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        super.onEquip();
        this.counter = 1;
    }

    @Override
    public void onRightClick() {
        if (isUsable()) {
            AbstractPlayer p = AbstractDungeon.player;
            this.addToBot(new VFXAction(p, new ShockWaveEffect(p.hb.cX, p.hb.cY, Settings.RED_TEXT_COLOR, ShockWaveEffect.ShockWaveType.ADDITIVE), 0.8F));
            AbstractDungeon.topLevelEffects.add(new BorderFlashEffect(new Color(1.0F, 0.1F, 0.05F, 0.0F)));
            int tmp = this.counter;
            if (p.hasRelic(TheBattery.ID) && this.counter > this.maxCharge) {
                tmp -= this.maxCharge;
            }
            if (tmp < 6) {
                CardCrawlGame.sound.play("RELIC_LARYNX_SHOUT_1");
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.LONG, false);
            } else if (tmp < 10) {
                CardCrawlGame.sound.play("RELIC_LARYNX_SHOUT_2");
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.LONG, false);
            } else {
                CardCrawlGame.sound.play("RELIC_LARYNX_SHOUT_3");
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.XLONG, false);
            }
            addToBot(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(3 * this.counter, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE, true));
            resetCharge();
            this.stopPulse();
        }
    }

    @Override
    public void resetCharge() {
        if (AbstractDungeon.player.hasRelic(TheBattery.ID)) {
            this.counter -= this.maxCharge;
        } else {
            this.counter = 0;
        }
    }

    @Override
    public boolean isUsable() {
        return this.counter > 0 && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    @Override
    public void wasHPLost(int damageAmount) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && damageAmount > 0) {
            if (!this.isMaxCharge()) {
                this.flash();
                this.counter++;
            }
        }
    }
}

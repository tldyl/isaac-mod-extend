package isaacModExtend.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import isaacModExtend.effects.BloodOathEffect;
import isaacModExtend.relics.BloodOath;

public class BloodOathAction extends AbstractGameAction {
    private BloodOath bloodOath;
    private boolean called = false;
    private int hpLoss = 0;

    public BloodOathAction(BloodOath bloodOath) {
        this.duration = 1.0F;
        this.source = AbstractDungeon.player;
        this.bloodOath = bloodOath;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.source.currentHealth <= 1) {
            this.isDone = true;
            return;
        }
        if (!called) {
            this.hpLoss = this.source.currentHealth - 1;
            called = true;
            AbstractDungeon.effectList.add(new BloodOathEffect());
        }
        if (this.duration <= 0) {
            this.isDone = true;
            AbstractDungeon.topLevelEffects.add(new BorderFlashEffect(new Color(1.0F, 0.1F, 0.05F, 0.0F)));
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.source.hb.cX, this.source.hb.cY, AttackEffect.SLASH_VERTICAL, true));
            AbstractDungeon.effectList.add(new StrikeEffect(this.source, this.source.hb.cX, this.source.hb.cY, this.hpLoss));
            bloodOath.strengthCounter = (this.hpLoss / 10 + (this.hpLoss % 10 > 0 ? 1 : 0)) * 2;
            bloodOath.dexterityCounter = this.hpLoss / 10 + (this.hpLoss % 10 > 0 ? 1 : 0);
            source.currentHealth = 1;
            source.healthBarUpdatedEvent();
            addToBot(new ApplyPowerAction(source, source, new StrengthPower(source, bloodOath.strengthCounter)));
            addToBot(new ApplyPowerAction(source, source, new DexterityPower(source, bloodOath.dexterityCounter)));
            addToBot(new GainBlockAction(source, bloodOath.strengthCounter));
        }
    }
}

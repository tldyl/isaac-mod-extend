package isaacModExtend.relics;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import relics.abstracrt.ChargeableRelic;

import java.util.HashMap;
import java.util.Map;

public class GlassCannon extends ChargeableRelic implements CustomSavable<Map<String, Boolean>> {
    public static final String ID = IsaacModExtend.makeID("GlassCannon");
    public static final String IMG_PATH = "relics/glassCannon.png";
    private static final String BROKEN_IMG_PATH = "relics/glassCannon_broken.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private static final Texture BROKEN_IMG = new Texture(IsaacModExtend.getResourcePath(BROKEN_IMG_PATH));
    private boolean isBroken = false;
    private boolean isUsed = false;

    public GlassCannon() {
        super(ID, IMG, RelicTier.UNCOMMON, LandingSound.SOLID, 1);
        BROKEN_IMG.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public String getUpdatedDescription() {
        return isBroken ? this.DESCRIPTIONS[1] : this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        super.onEquip();
        this.counter = 1;
    }

    @Override
    public void onRightClick() {
        if (this.isUsable()) {
            this.flash();

            if (this.counter >= 4) {
                this.isBroken = false;
                this.setTexture(IMG);
                this.maxCharge = 1;
                this.counter = 1;
                this.beginLongPulse();
                refreshDescriptions();
            } else {
                this.isUsed = true;
                CardCrawlGame.sound.play("GLASS_CANNON_FIRE");
                addToBot(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(30, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.LONG, false);
                resetCharge();
                this.stopPulse();
            }
        }
    }

    @Override
    public boolean isUsable() {
        return this.counter >= this.maxCharge && (isBroken || AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.owner != AbstractDungeon.player && info.type == DamageInfo.DamageType.NORMAL && this.isUsed && damageAmount > 0) {
            this.flash();
            this.isBroken = true;
            this.isUsed = false;
            CardCrawlGame.sound.play("GLASS_BREAK_01");
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.maxCharge = 4;
            this.setTexture(BROKEN_IMG);
            resetCharge();
            refreshDescriptions();
            return damageAmount * 3;
        }
        return damageAmount;
    }

    private void refreshDescriptions() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public Map<String, Boolean> onSave() {
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("isBroken", this.isBroken);
        ret.put("isUsed", this.isUsed);
        return ret;
    }

    @Override
    public void onLoad(Map<String, Boolean> data) {
        if (data != null) {
            this.isBroken = data.getOrDefault("isBroken", false);
            this.isUsed = data.getOrDefault("isUsed", false);
        } else {
            this.isBroken = false;
            this.isUsed = false;
        }
        if (this.isBroken) {
            this.setTexture(BROKEN_IMG);
            this.maxCharge = 4;
            refreshDescriptions();
        }
    }
}

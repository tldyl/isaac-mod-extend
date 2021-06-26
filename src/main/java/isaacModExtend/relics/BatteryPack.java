package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import cards.Battery;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import isaacModExtend.IsaacModExtend;
import relics.TheBattery;
import relics.abstracrt.ChargeableRelic;

public class BatteryPack extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("BatteryPack");
    public static final String IMG_PATH = "relics/batteryPack.png";

    public BatteryPack() {
        super(ID, new Texture(IsaacModExtend.getResourcePath(IMG_PATH)),
                RelicTier.SHOP, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        this.flash();
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof ChargeableRelic) {
                ChargeableRelic chargeableRelic = (ChargeableRelic) relic;
                if (AbstractDungeon.player.hasRelic(TheBattery.ID)) {
                    chargeableRelic.counter = chargeableRelic.maxCharge * 2;
                } else {
                    chargeableRelic.counter = chargeableRelic.maxCharge;
                }
            }
        }
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Battery(), Settings.WIDTH / 2.0F - AbstractCard.RAW_W * Settings.scale, Settings.HEIGHT / 2.0F, true));
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Battery(), Settings.WIDTH / 2.0F - AbstractCard.RAW_W * Settings.scale, Settings.HEIGHT / 2.0F, true));
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Battery(), Settings.WIDTH / 2.0F - AbstractCard.RAW_W * Settings.scale, Settings.HEIGHT / 2.0F, true));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }
}

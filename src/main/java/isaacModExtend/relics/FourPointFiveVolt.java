package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import relics.abstracrt.ChargeableRelic;

public class FourPointFiveVolt extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("FourPointFiveVolt");
    public static final String IMG_PATH = "relics/fourPointFiveVolt.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public FourPointFiveVolt() {
        super(ID, IMG, RelicTier.SHOP, LandingSound.CLINK);
    }

    @Override
    public void onEquip() {
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster && (info.type == DamageInfo.DamageType.NORMAL || info.type == DamageInfo.DamageType.THORNS)) {
            this.counter += target.lastDamageTaken;
            if (this.counter >= 70) {
                this.flash();
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof ChargeableRelic) {
                        ChargeableRelic chargeableRelic = (ChargeableRelic) relic;
                        if (chargeableRelic.isMaxCharge()) continue;
                        chargeableRelic.counter++;
                        break;
                    }
                }
                this.counter %= 70;
            }
        }
    }
}

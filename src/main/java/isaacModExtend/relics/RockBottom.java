package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.OnAnyPowerAppliedRelic;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.IsaacModExtend;

public class RockBottom extends CustomRelic implements OnAnyPowerAppliedRelic {
    public static final String ID = IsaacModExtend.makeID("RockBottom");
    public static final String IMG_PATH = "relics/rockBottom.png";

    public RockBottom() {
        super(ID, new Texture(IsaacModExtend.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public boolean onAnyPowerApply(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (target instanceof AbstractPlayer) {
            if (power.type == AbstractPower.PowerType.DEBUFF) {
                this.flash();
                return false;
            } else {
                if (power.amount < 0 && power.canGoNegative) this.flash();
                return power.amount >= 0 || !power.canGoNegative;
            }
        }
        return true;
    }

    @Override
    public int onAnyPowerApplyStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        if ((power instanceof StrengthPower || power instanceof DexterityPower || power instanceof FocusPower) && target instanceof AbstractPlayer) {
            if (stackAmount < 0) this.flash();
            return Math.max(stackAmount, 0);
        }
        return stackAmount;
    }
}

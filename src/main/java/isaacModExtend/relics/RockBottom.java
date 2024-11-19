package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.OnAnyPowerAppliedRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.IsaacModExtend;

import java.util.ArrayList;
import java.util.List;

public class RockBottom extends CustomRelic implements OnAnyPowerAppliedRelic, CustomSavable<List<Integer>> {
    public static final String ID = IsaacModExtend.makeID("RockBottom");
    public static final String IMG_PATH = "relics/rockBottom.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    private List<Integer> sdfValues = new ArrayList<>();

    public RockBottom() {
        super(ID, IMG, RelicTier.RARE, LandingSound.HEAVY);
        sdfValues.add(0);
        sdfValues.add(0);
        sdfValues.add(0);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if (sdfValues.stream().anyMatch(v -> v > 0)) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
        }
        if (sdfValues.get(0) > 0) {
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, sdfValues.get(0))));
        }
        if (sdfValues.get(1) > 0) {
            addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, sdfValues.get(1))));
        }
        if (sdfValues.get(2) > 0) {
            addToBot(new ApplyPowerAction(p, p, new FocusPower(p, sdfValues.get(2))));
        }
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
            if (stackAmount < 0) {
                this.flash();
            } else {
                if (power instanceof StrengthPower) {
                    sdfValues.set(0, Math.max(sdfValues.get(0), stackAmount));
                }
                if (power instanceof DexterityPower) {
                    sdfValues.set(1, Math.max(sdfValues.get(1), stackAmount));
                }
                if (power instanceof FocusPower) {
                    sdfValues.set(2, Math.max(sdfValues.get(2), stackAmount));
                }
            }
            return Math.max(stackAmount, 0);
        }
        return stackAmount;
    }

    @Override
    public List<Integer> onSave() {
        return sdfValues;
    }

    @Override
    public void onLoad(List<Integer> data) {
        if (data != null && data.size() == 3) {
            sdfValues = data;
        } else {
            sdfValues.add(0);
            sdfValues.add(0);
            sdfValues.add(0);
        }
    }
}

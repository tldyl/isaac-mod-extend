package isaacModExtend.relics.birthrightRelics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.Birthright;

public class DefectBirthrightRelic extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("DefectBirthrightRelic");
    public static boolean energyRecharge = true;

    public DefectBirthrightRelic() {
        super(ID, "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStart() {
        energyRecharge = true;
    }

    @Override
    public void onEnergyRecharge() {
        AbstractPlayer p = AbstractDungeon.player;
        if (!energyRecharge) {
            if (p.hasRelic(Birthright.ID)) {
                AbstractRelic relic = p.getRelic(Birthright.ID);
                relic.flash();
                addToBot(new RelicAboveCreatureAction(p, relic));
                addToBot(new DrawCardAction(1));
                energyRecharge = true;
            }
        }
    }
}

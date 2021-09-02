package isaacModExtend.relics.birthrightRelics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.Birthright;

public class WatcherBirthrightRelic extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("WatcherBirthrightRelic");

    public WatcherBirthrightRelic() {
        super(ID, "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onChangeStance(AbstractStance prevStance, AbstractStance newStance) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasRelic(Birthright.ID)) {
            AbstractRelic relic = p.getRelic(Birthright.ID);
            relic.flash();
            addToBot(new RelicAboveCreatureAction(p, relic));
            addToBot(new ApplyPowerAction(p, p, new MantraPower(p, 1)));
        }
    }
}

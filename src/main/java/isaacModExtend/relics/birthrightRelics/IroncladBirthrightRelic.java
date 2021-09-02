package isaacModExtend.relics.birthrightRelics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.Birthright;

public class IroncladBirthrightRelic extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("IroncladBirthrightRelic");

    public IroncladBirthrightRelic() {
        super(ID, "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        if (AbstractDungeon.player.hasRelic(Birthright.ID)) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(Birthright.ID);
            if (relic.counter < 0) {
                relic.counter = 0;
            }
        }
    }

    @Override
    public void onExhaust(AbstractCard card) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasRelic(Birthright.ID)) {
            AbstractRelic relic = p.getRelic(Birthright.ID);
            relic.counter++;
            if (relic.counter >= 3) {
                relic.flash();
                addToBot(new RelicAboveCreatureAction(p, relic));
                addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, 1)));
                relic.counter = 0;
            }
        }
    }
}

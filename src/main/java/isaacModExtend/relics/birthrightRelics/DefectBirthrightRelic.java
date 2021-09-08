package isaacModExtend.relics.birthrightRelics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.Birthright;

public class DefectBirthrightRelic extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("DefectBirthrightRelic");

    public DefectBirthrightRelic() {
        super(ID, "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasRelic(Birthright.ID) && drawnCard.type == AbstractCard.CardType.POWER) {
            AbstractRelic relic = p.getRelic(Birthright.ID);
            relic.flash();
            addToBot(new RelicAboveCreatureAction(p, relic));
            drawnCard.selfRetain = true;
            drawnCard.superFlash();
        }
    }
}

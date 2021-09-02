package isaacModExtend.relics.birthrightRelics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.Birthright;

public class SilentBirthrightRelic extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("SilentBirthrightRelic");
    public static AbstractCard targetCard = null;

    public SilentBirthrightRelic() {
        super(ID, "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onManualDiscard() {
        AbstractPlayer p = AbstractDungeon.player;
        if (targetCard != null && (targetCard.exhaust || targetCard.exhaustOnUseOnce || targetCard.purgeOnUse || targetCard.isEthereal)) {
            if (p.hasRelic(Birthright.ID)) {
                AbstractRelic relic = p.getRelic(Birthright.ID);
                relic.flash();
                addToBot(new RelicAboveCreatureAction(p, relic));
                p.discardPile.removeCard(targetCard);
                AbstractDungeon.getCurrRoom().souls.remove(targetCard);
                targetCard.exhaustOnUseOnce = true;
                AbstractDungeon.player.limbo.group.add(targetCard);
                targetCard.current_y = -200.0F * Settings.scale;
                targetCard.target_x = (float)Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
                targetCard.target_y = (float)Settings.HEIGHT / 2.0F;
                targetCard.targetAngle = 0.0F;
                targetCard.lighten(false);
                targetCard.drawScale = 0.12F;
                targetCard.targetDrawScale = 0.75F;
                targetCard.applyPowers();
                this.addToBot(new UnlimboAction(targetCard));
                this.addToBot(new NewQueueCardAction(targetCard, AbstractDungeon.getRandomMonster(), false, true));
            }
            targetCard = null;
        }
    }
}

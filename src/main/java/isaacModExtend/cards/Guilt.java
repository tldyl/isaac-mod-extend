package isaacModExtend.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;

public class Guilt extends CustomCard {
    public static final String ID = IsaacModExtend.makeID("Guilt");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/guilt.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.CURSE;
    private static final CardRarity RARITY = CardRarity.CURSE;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = -2;

    public Guilt() {
        super(ID, NAME, IsaacModExtend.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.CURSE, RARITY, TARGET);
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            this.addToTop(new DamageAction(p, new DamageInfo(p, this.magicNumber, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public void triggerWhenDrawn() {
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card.cardID.equals(this.cardID) && card != this) {
                addToBot(new ExhaustSpecificCardAction(this, AbstractDungeon.player.hand, true));
                addToBot(new ExhaustSpecificCardAction(card, AbstractDungeon.player.hand, true));
                addToBot(new MakeTempCardInHandAction(new Regret()));
            }
        }
    }

    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        this.magicNumber = this.baseMagicNumber = AbstractDungeon.player.hand.size();
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

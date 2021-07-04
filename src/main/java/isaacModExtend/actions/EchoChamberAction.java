package isaacModExtend.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.List;

public class EchoChamberAction extends AbstractGameAction {
    private List<AbstractCard> cards;

    public EchoChamberAction(List<AbstractCard> cards) {
        this.cards = cards;
        this.duration = 0.1F;
    }

    @Override
    public void update() {
        for (AbstractCard card : this.cards) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                    if (m != null) {
                        AbstractCard tmp = card.makeStatEquivalentCopy();
                        tmp.uuid = card.uuid;
                        tmp.calculateCardDamage(m);
                        tmp.use(AbstractDungeon.player, m);
                    }
                    isDone = true;
                }
            });
        }
        this.isDone = true;
    }
}

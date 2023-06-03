package isaacModExtend.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.PreSetMoveIntent;

public class DerangedPower extends TwoAmountPower implements PreSetMoveIntent {
    private static final PowerStrings powerString;
    public static final String POWER_ID = IsaacModExtend.makeID("DerangedPower");
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public DerangedPower(AbstractMonster owner, int initialAmount) {
        this.ID = POWER_ID;
        this.name = NAME;
        this.img = ImageMaster.loadImage("images/powers/DeliriousPower.png");
        this.amount = initialAmount;
        this.amount2 = initialAmount;
        this.owner = owner;
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount2, this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount > 0) {
            return;
        }
        this.amount2 += stackAmount;
        if (this.amount2 < 4) {
            this.amount2 = 4;
        }
        if (this.amount > this.amount2) {
            this.amount = this.amount2;
        }
        updateDescription();
    }

    @Override
    public boolean receivePreSetMove() {
        this.flashWithoutSound();
        this.amount--;
        if (this.amount <= 0) {
            this.flash();
            this.amount = this.amount2;
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    ((AbstractMonster) DerangedPower.this.owner).takeTurn();
                    isDone = true;
                }
            });
            updateDescription();
            return true;
        }
        if (this.amount == 1) {
            addToBot(new TalkAction(this.owner, DESCRIPTIONS[1]));
        }
        updateDescription();
        return false;
    }

    static {
        powerString = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerString.NAME;
        DESCRIPTIONS = powerString.DESCRIPTIONS;
    }
}

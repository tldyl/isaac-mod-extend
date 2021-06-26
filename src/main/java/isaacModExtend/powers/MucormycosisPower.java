package isaacModExtend.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import isaacModExtend.IsaacModExtend;

import java.util.UUID;

public class MucormycosisPower extends AbstractPower {
    public static final String POWER_ID = IsaacModExtend.makeID("MucormycosisPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public MucormycosisPower(AbstractMonster owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID + UUID.randomUUID().toString();
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        updateDescription();
        loadRegion("sporeCloud");
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void onInitialApplication() {
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(m, owner, new PoisonPower(m, owner, 4)));
            }
        }
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (MucormycosisPower.this.amount <= 0) {
                    MucormycosisPower.this.flash();
                    addToBot(new DamageAction(owner, new DamageInfo(owner, 5, DamageInfo.DamageType.NORMAL), AttackEffect.POISON));
                    for (int i=0;i<2;i++) {
                        AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                        addToBot(new ApplyPowerAction(m, owner, new MucormycosisPower(m, 2)));
                    }
                    addToBot(new RemoveSpecificPowerAction(owner, owner, MucormycosisPower.this.ID));
                }
                isDone = true;
            }
        });
    }

    @Override
    public void reducePower(int reduceAmount) {
        this.fontScale = 8.0F;
        this.amount -= reduceAmount;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

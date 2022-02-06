package isaacModExtend.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.monsters.BabyPlum;
import isaacModExtend.monsters.pet.BabyPlumPet;
import patches.player.PlayerAddFieldsPatch;

public class FranticPower extends AbstractPower {
    public static final String POWER_ID = IsaacModExtend.makeID("FranticPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FranticPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.loadRegion("vulnerable");
        this.type = PowerType.DEBUFF;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        AbstractMonster monster = AbstractDungeon.getMonsters().getMonster(BabyPlum.ID);
        if (monster != null) {
            ((BabyPlum) monster).refreshMultiAttackIntent();
            monster.createIntent();
        }
        monster = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player).getMonster(BabyPlumPet.ID);
        if (monster != null) {
            ((BabyPlumPet) monster).refreshMultiAttackIntent();
            monster.createIntent();
        }
    }

    @Override
    public void onDeath() {
        atStartOfTurn();
    }

    @Override
    public void onRemove() {
        atStartOfTurn();
    }

    @Override
    public void onGainedBlock(float blockAmount) {
        if (blockAmount > 0) {
            atStartOfTurn();
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

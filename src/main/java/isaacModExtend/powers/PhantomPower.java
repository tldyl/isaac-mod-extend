package isaacModExtend.powers;

import actions.CreateIntentAction;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.PreSetMoveIntent;
import isaacModExtend.monsters.Delirium;
import isaacModExtend.patches.AbstractMonsterPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhantomPower extends TwoAmountPower {
    private static final PowerStrings powerString;
    public static final String POWER_ID = IsaacModExtend.makeID("PhantomPower");
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private final List<AbstractMonster> monsterPool = new ArrayList<>();
    private final AbstractMonster initialOwner;

    public PhantomPower(AbstractCreature owner, int initialAmount) {
        this.ID = POWER_ID;
        this.name = NAME;
        this.loadRegion("time");
        this.amount = initialAmount;
        this.amount2 = initialAmount;
        this.owner = owner;
        this.initialOwner = (AbstractMonster) owner;
        this.updateDescription();
        initMonsterPool();
    }

    private void initMonsterPool() {
        List<String> encounterKeys = new ArrayList<>();
        for (HashMap map : CardCrawlGame.metricData.damage_taken) {
            encounterKeys.add((String) map.get("enemies"));
        }
        for (String key : encounterKeys) {
            MonsterGroup monsterGroup = MonsterHelper.getEncounter(key);
            monsterPool.addAll(monsterGroup.monsters);
        }
    }

    private AbstractMonster getMonster() {
        if (this.owner != this.initialOwner && AbstractDungeon.monsterRng.randomBoolean(0.15F)) {
            return this.initialOwner;
        }
        if (monsterPool.isEmpty()) {
            initMonsterPool();
        }
        return monsterPool.remove(AbstractDungeon.monsterRng.random(monsterPool.size() - 1));
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount > 0) {
            return;
        }
        this.amount2 += stackAmount;
        if (this.amount2 < 3) {
            this.amount2 = 3;
        }
        if (this.amount > this.amount2) {
            this.amount = this.amount2;
        }
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount2, this.amount);
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return this.calculateDamageTakenAmount(damage);
    }

    private float calculateDamageTakenAmount(float damage) {
        return owner instanceof Delirium ? damage : damage / 2.0F;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        this.amount--;
        if (this.amount <= 0) {
            this.flash();
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractMonster monster = getMonster();
                    monster.maxHealth = PhantomPower.this.owner.maxHealth;
                    monster.currentHealth = PhantomPower.this.owner.currentHealth;
                    monster.type = AbstractMonster.EnemyType.BOSS;
                    AbstractDungeon.getCurrRoom().monsters.monsters.clear();
                    AbstractDungeon.getCurrRoom().monsters.add(monster);
                    AbstractMonster oldOwner = (AbstractMonster) PhantomPower.this.owner;
                    PhantomPower.this.owner = monster;
                    monster.showHealthBar();
                    monster.healthBarUpdatedEvent();
                    AbstractMonsterPatch.PatchSetMove.preventNextInvoke = false;
                    monster.rollMove();
                    if (ReflectionHacks.getPrivate(monster, AbstractMonster.class, "move") == null) {
                        ReflectionHacks.setPrivate(monster, AbstractMonster.class, "move", new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.UNKNOWN, 0, 1, false));
                    }
                    monster.usePreBattleAction();
                    oldOwner.powers.forEach(power -> PhantomPower.this.owner.addPower(power));
                    addToTop(new CreateIntentAction((AbstractMonster) PhantomPower.this.owner));
                    addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            oldOwner.powers.clear();
                            isDone = true;
                        }
                    });
                    PhantomPower.this.owner.powers.forEach(power -> {
                        power.owner = PhantomPower.this.owner;
                        if (power instanceof PreSetMoveIntent) {
                            AbstractMonsterPatch.PatchSetMove.preventNextInvoke |= ((PreSetMoveIntent) power).receivePreSetMove();
                        }
                    });
                    isDone = true;
                }
            });
            this.amount = this.amount2;
        }
        this.updateDescription();
    }

    static {
        powerString = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerString.NAME;
        DESCRIPTIONS = powerString.DESCRIPTIONS;
    }
}

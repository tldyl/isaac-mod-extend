package isaacModExtend.powers;

import actions.CreateIntentAction;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.Anm2WaitAction;
import isaacModExtend.effects.TVNoSignalEffect;
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
    public static final List<Texture> screenShots = new ArrayList<>();
    private float effectTimer = MathUtils.random(30.0F, 120.0F);

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

    @Override
    public void update(int slot) {
        super.update(slot);
        if (!owner.isDying) {
            effectTimer -= Gdx.graphics.getDeltaTime();
            if (effectTimer <= 0) {
                IsaacModExtend.globalEffect.add(new TVNoSignalEffect(0.8F, screenShots.isEmpty() ? null : screenShots.get(MathUtils.random(screenShots.size() - 1)), false));
                effectTimer = MathUtils.random(30.0F, 120.0F);
                IsaacModExtend.addToBot(new Anm2WaitAction(0.8F));
                IsaacModExtend.addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        while (screenShots.size() > 10) {
                            screenShots.remove(0).dispose();
                        }
                        isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void onInitialApplication() {
        atStartOfTurn();
    }

    @Override
    public void atStartOfTurn() {
        IsaacModExtend.screenShotRequests.add(screenShots::add);
        if (MathUtils.randomBoolean(0.4F)) {
            IsaacModExtend.addToBot(new Anm2WaitAction(MathUtils.random(2.0F, 15.0F)));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    IsaacModExtend.screenShotRequests.add(screenShots::add);
                    isDone = true;
                }
            });
        }
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
        if (this.owner != this.initialOwner && AbstractDungeon.monsterRng.randomBoolean(0.2F)) {
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
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        this.amount--;
        if (this.amount <= 0) {
            this.flash();
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    transformToRandomEnemy(false);
                    if (MathUtils.randomBoolean()) {
                        CardCrawlGame.sound.play("HELL_PORTAL");
                    } else {
                        CardCrawlGame.sound.play("HELL_PORTAL_2");
                    }
                    isDone = true;
                }
            });
            this.amount = this.amount2;
        }
        if (this.amount == 1) {
            addToBot(new TalkAction(this.owner, DESCRIPTIONS[1]));
        }
        this.updateDescription();
    }

    private void transformToRandomEnemy(boolean isDying) {
        AbstractMonster monster = isDying ? initialOwner : getMonster();
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
        addToTop(new CreateIntentAction((AbstractMonster) PhantomPower.this.owner));
        if (isDying) {
            ((AbstractMonster) this.owner).die();
        } else {
            AbstractDungeon.player.relics.forEach(relic -> relic.onSpawnMonster((AbstractMonster) this.owner));
        }
    }

    @Override
    public void onDeath() {
        AbstractDungeon.getCurrRoom().cannotLose = false;
        if (!(owner instanceof Delirium)) {
            transformToRandomEnemy(true);
            return;
        }
        if (screenShots.isEmpty()) {
            IsaacModExtend.clearAllActions();
            IsaacModExtend.globalEffect.add(new TVNoSignalEffect(3.0F, null, false));
            IsaacModExtend.addToBot(new Anm2WaitAction(3.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(2.0F, null, false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(2.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(2.0F, null, false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(2.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(1.0F, null, false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(1.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(1.0F, null, false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(1.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(1.0F, null, true));
                    isDone = true;
                }
            });
        } else {
            IsaacModExtend.globalEffect.add(new TVNoSignalEffect(3.0F, screenShots.get(0), false));
            IsaacModExtend.addToBot(new Anm2WaitAction(3.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    int progress = Math.min((int) (screenShots.size() * 0.3), screenShots.size() - 1);
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(2.0F, screenShots.get(progress), false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(2.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    int progress = Math.min((int) (screenShots.size() * 0.5), screenShots.size() - 1);
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(2.0F, screenShots.get(progress), false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(2.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    int progress = Math.min((int) (screenShots.size() * 0.7), screenShots.size() - 1);
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(1.0F, screenShots.get(progress), false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(1.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    int progress = Math.min((int) (screenShots.size() * 0.8), screenShots.size() - 1);
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(1.0F, screenShots.get(progress), false));
                    isDone = true;
                }
            });
            IsaacModExtend.addToBot(new Anm2WaitAction(1.0F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    IsaacModExtend.globalEffect.add(new TVNoSignalEffect(1.0F, null, true));
                    isDone = true;
                }
            });
        }
        IsaacModExtend.addToBot(new Anm2WaitAction(0.1F));
        IsaacModExtend.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (Texture texture : screenShots) {
                    texture.dispose();
                }
                screenShots.clear();
                isDone = true;
            }
        });
    }

    static {
        powerString = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerString.NAME;
        DESCRIPTIONS = powerString.DESCRIPTIONS;
    }
}

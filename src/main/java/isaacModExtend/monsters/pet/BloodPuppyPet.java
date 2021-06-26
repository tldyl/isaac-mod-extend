package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.*;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.BloodPuppyRushAction;
import isaacModExtend.relics.BloodPuppy;
import monsters.Intent.Move;
import monsters.abstracrt.AbstractPet;

import java.util.Iterator;

@SuppressWarnings("Duplicates")
public class BloodPuppyPet extends AbstractPet {
    public static final String ID = IsaacModExtend.makeID("BloodPuppyPet");
    public static final String NAME;
    private int phase = 0;
    private int[] phaseDmg = new int[]{6, 23, 46};
    private boolean leftSide = true;
    private float originalX;
    private float originalY;

    public BloodPuppyPet(float x, float y) {
        super(NAME, ID, 200, -8.0F, 10.0F, 58.0F, 38.0F, null, x, y);
        this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy1.png"));
        this.setHp(200);
        this.damage.add(new DamageInfo(this, this.phaseDmg[0], DamageInfo.DamageType.THORNS));
        this.damage.add(new DamageInfo(this, this.phaseDmg[1], DamageInfo.DamageType.THORNS));
        this.damage.add(new DamageInfo(this, this.phaseDmg[2], DamageInfo.DamageType.THORNS));
        this.setMove((byte)Move.ATTACK.id, Intent.ATTACK, this.phaseDmg[0]);
        this.currentHealth = 1;
        this.originalX = this.hb.cX;
        this.originalY = this.hb.cY;
    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        switch (this.phase) {
            case 0:
                AbstractMonster m = null;
                for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!monster.isDeadOrEscaped()) {
                        m = monster;
                        break;
                    }
                }
                if (m != null) {
                    IsaacModExtend.addToBot(new VFXAction(new BiteEffect(m.hb.cX, m.hb.cY)));
                    IsaacModExtend.addToBot(new DamageAction(m, this.damage.get(0)));
                    AbstractMonster finalM = m;
                    IsaacModExtend.addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            BloodPuppyPet.this.heal(finalM.lastDamageTaken);
                            if (BloodPuppyPet.this.currentHealth >= 100) {
                                phase = 1;
                                BloodPuppyPet.this.img.dispose();
                                BloodPuppyPet.this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy2.png"));
                                updateHitbox(-8.0F, 10.0F, 68.0F, 51.0F);
                            }
                            isDone = true;
                        }
                    });
                }
                break;
            case 1:
                addToBot(new SFXAction("RELIC_BLOOD_PUPPY_RUSH_2"));
                for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!monster.isDeadOrEscaped()) {
                        if (leftSide) {
                            IsaacModExtend.addToBot(new VFXAction(new BiteEffect(monster.hb.cX, monster.hb.cY)));
                            IsaacModExtend.addToBot(new DamageAction(monster, this.damage.get(1)));
                            IsaacModExtend.addToBot(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    BloodPuppyPet.this.heal(monster.lastDamageTaken);
                                    if (BloodPuppyPet.this.currentHealth >= 200) {
                                        phase = 2;
                                        BloodPuppyPet.this.img.dispose();
                                        BloodPuppyPet.this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy3.png"));
                                        updateHitbox(-8.0F, 10.0F, 96.0F, 56.0F);
                                    }
                                    isDone = true;
                                }
                            });
                        } else {
                            IsaacModExtend.addToTop(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    BloodPuppyPet.this.heal(monster.lastDamageTaken);
                                    if (BloodPuppyPet.this.currentHealth >= 200) {
                                        phase = 2;
                                        BloodPuppyPet.this.img.dispose();
                                        BloodPuppyPet.this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy3.png"));
                                        updateHitbox(-8.0F, 10.0F, 96.0F, 56.0F);
                                    }
                                    isDone = true;
                                }
                            });
                            IsaacModExtend.addToTop(new DamageAction(monster, this.damage.get(1)));
                            IsaacModExtend.addToTop(new VFXAction(new BiteEffect(monster.hb.cX, monster.hb.cY)));
                        }
                        IsaacModExtend.addToBot(new AbstractGameAction() {
                            @Override
                            public void update() {
                                if (p.hasRelic(BloodPuppy.ID)) {
                                    p.getRelic(BloodPuppy.ID).onTrigger(monster);
                                }
                                isDone = true;
                            }
                        });
                    }
                }
                if (leftSide) {
                    IsaacModExtend.addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            BloodPuppyPet.this.heal(p.lastDamageTaken);
                            isDone = true;
                        }
                    });
                    IsaacModExtend.addToTop(new DamageAction(p, this.damage.get(1)));
                    IsaacModExtend.addToTop(new VFXAction(new BiteEffect(p.hb.cX, p.hb.cY)));
                } else {
                    IsaacModExtend.addToBot(new VFXAction(new BiteEffect(p.hb.cX, p.hb.cY)));
                    IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1)));
                    IsaacModExtend.addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            BloodPuppyPet.this.heal(p.lastDamageTaken);
                            isDone = true;
                        }
                    });
                }
                IsaacModExtend.addToTop(new BloodPuppyRushAction(this));
                break;
            case 2:
                addToBot(new SFXAction("RELIC_BLOOD_PUPPY_RUSH_3"));
                for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!monster.isDeadOrEscaped()) {
                        if (leftSide) {
                            IsaacModExtend.addToBot(new VFXAction(new BiteEffect(monster.hb.cX, monster.hb.cY)));
                            IsaacModExtend.addToBot(new RemoveAllBlockAction(monster, this));
                            IsaacModExtend.addToBot(new DamageAction(monster, this.damage.get(2)));
                            IsaacModExtend.addToBot(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    BloodPuppyPet.this.heal(monster.lastDamageTaken);
                                    isDone = true;
                                }
                            });
                        } else {
                            IsaacModExtend.addToTop(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    BloodPuppyPet.this.heal(monster.lastDamageTaken);
                                    isDone = true;
                                }
                            });
                            IsaacModExtend.addToTop(new DamageAction(monster, this.damage.get(2)));
                            IsaacModExtend.addToTop(new RemoveAllBlockAction(monster, this));
                            IsaacModExtend.addToTop(new VFXAction(new BiteEffect(monster.hb.cX, monster.hb.cY)));
                        }
                        IsaacModExtend.addToBot(new AbstractGameAction() {
                            @Override
                            public void update() {
                                if (p.hasRelic(BloodPuppy.ID)) {
                                    p.getRelic(BloodPuppy.ID).onTrigger(monster);
                                }
                                isDone = true;
                            }
                        });
                    }
                }
                if (leftSide) {
                    IsaacModExtend.addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            BloodPuppyPet.this.heal(p.lastDamageTaken);
                            isDone = true;
                        }
                    });
                    IsaacModExtend.addToTop(new DamageAction(p, this.damage.get(2)));
                    IsaacModExtend.addToTop(new VFXAction(new BiteEffect(p.hb.cX, p.hb.cY)));
                } else {
                    IsaacModExtend.addToBot(new VFXAction(new BiteEffect(p.hb.cX, p.hb.cY)));
                    IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(2)));
                    IsaacModExtend.addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            BloodPuppyPet.this.heal(p.lastDamageTaken);
                            isDone = true;
                        }
                    });
                }
                IsaacModExtend.addToTop(new BloodPuppyRushAction(this));
                break;
        }
        IsaacModExtend.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (phase > 0) leftSide = !leftSide;
                BloodPuppyPet.this.flipHorizontal = !leftSide;
                isDone = true;
            }
        });
        IsaacModExtend.addToBot(new RollMoveAction(this));
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.output > 0 && this.hasPower("IntangiblePlayer")) {
            info.output = 1;
        }

        int damageAmount = info.output;
        if (!this.isDying && !this.isEscaping) {
            if (damageAmount < 0) {
                damageAmount = 0;
            }

            boolean hadBlock = true;
            if (this.currentBlock == 0) {
                hadBlock = false;
            }

            boolean weakenedToZero = damageAmount == 0;
            damageAmount = this.decrementBlock(info, damageAmount);
            Iterator var5;
            AbstractRelic r;
            if (info.owner == AbstractDungeon.player) {
                for(var5 = AbstractDungeon.player.relics.iterator(); var5.hasNext(); damageAmount = r.onAttackToChangeDamage(info, damageAmount)) {
                    r = (AbstractRelic)var5.next();
                }
            }

            AbstractPower p;
            if (info.owner != null) {
                for(var5 = info.owner.powers.iterator(); var5.hasNext(); damageAmount = p.onAttackToChangeDamage(info, damageAmount)) {
                    p = (AbstractPower)var5.next();
                }
            }

            for(var5 = this.powers.iterator(); var5.hasNext(); damageAmount = p.onAttackedToChangeDamage(info, damageAmount)) {
                p = (AbstractPower)var5.next();
            }

            if (info.owner == AbstractDungeon.player) {
                var5 = AbstractDungeon.player.relics.iterator();

                while(var5.hasNext()) {
                    r = (AbstractRelic)var5.next();
                    r.onAttack(info, damageAmount, this);
                }
            }

            var5 = this.powers.iterator();

            while(var5.hasNext()) {
                p = (AbstractPower)var5.next();
                p.wasHPLost(info, damageAmount);
            }

            if (info.owner != null) {
                var5 = info.owner.powers.iterator();

                while(var5.hasNext()) {
                    p = (AbstractPower)var5.next();
                    p.onAttack(info, damageAmount, this);
                }
            }

            for(var5 = this.powers.iterator(); var5.hasNext(); damageAmount = p.onAttacked(info, damageAmount)) {
                p = (AbstractPower)var5.next();
            }

            this.lastDamageTaken = Math.min(damageAmount, this.currentHealth);
            boolean probablyInstantKill = this.currentHealth == 0;
            if (damageAmount > 0) {
                if (info.owner != this) {
                    this.useStaggerAnimation();
                }

                if (damageAmount >= 99 && !CardCrawlGame.overkill) {
                    CardCrawlGame.overkill = true;
                }

                this.currentHealth -= damageAmount;
                if (!probablyInstantKill) {
                    AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, damageAmount));
                }

                if (this.currentHealth < 0) {
                    this.currentHealth = 0;
                }

                this.healthBarUpdatedEvent();
            } else if (!probablyInstantKill) {
                if (weakenedToZero && this.currentBlock == 0) {
                    if (hadBlock) {
                        AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[30]));
                    } else {
                        AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, 0));
                    }
                } else if (Settings.SHOW_DMG_BLOCK) {
                    AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[30]));
                }
            }

            if (this.currentHealth <= 0) {
                this.die(false);

                if (this.currentBlock > 0) {
                    this.loseBlock();
                    AbstractDungeon.effectList.add(new HbBlockBrokenEffect(this.hb.cX - this.hb.width / 2.0F + BLOCK_ICON_X, this.hb.cY - this.hb.height / 2.0F + BLOCK_ICON_Y));
                }
            }

        }
    }

    @Override
    public void die(boolean triggerRelics) {
        this.currentHealth = 1;
        this.isDying = false;
        this.isDead = false;
        this.phase = 0;
        this.img.dispose();
        this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy1.png"));
        updateHitbox(-8.0F, 10.0F, 58.0F, 38.0F);
    }

    @Override
    protected void getMove(int i) {
        this.setMove((byte)Move.ATTACK.id, Intent.ATTACK, this.phaseDmg[this.phase]);
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
        if (phase > 2) this.phase = 2;
        if (phase < 0) this.phase = 0;
        this.img.dispose();
        switch (this.phase) {
            case 0:
                this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy1.png"));
                updateHitbox(-8.0F, 10.0F, 58.0F, 38.0F);
                break;
            case 1:
                this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy2.png"));
                updateHitbox(-8.0F, 10.0F, 68.0F, 51.0F);
                break;
            case 2:
                this.img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy3.png"));
                updateHitbox(-8.0F, 10.0F, 96.0F, 56.0F);
                break;
        }
    }

    public void setImg(Texture img) {
        this.img = img;
    }

    public boolean isLeftSide() {
        return leftSide;
    }

    public float getOriginalX() {
        return originalX;
    }

    public float getOriginalY() {
        return originalY;
    }

    @Override
    public void refreshHitboxLocation() {
        this.hb.move(this.drawX + this.hb_x + this.animX, this.drawY + this.hb_y + this.hb_h / 2.0F);
        this.healthHb.move(this.hb.cX, this.hb.cY - this.hb_h / 2.0F - this.healthHb.height / 2.0F);
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

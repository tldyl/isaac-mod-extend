package isaacModExtend.monsters.pet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.BabyPlumAttack1Action;
import isaacModExtend.actions.BabyPlumAttack3Action;
import isaacModExtend.effects.BloodTearEffect;
import isaacModExtend.powers.BarrageBlockadePower;
import isaacModExtend.powers.FranticPower;
import isaacModExtend.powers.GoodbyePower;
import monsters.abstracrt.AbstractPet;
import utils.Point;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class BabyPlumPet extends AbstractPet {
    public static final String ID = IsaacModExtend.makeID("BabyPlum");
    public static final String NAME;
    private AnimatedActor animation;
    private boolean resetPosition = true;
    private boolean renderHealthBar = false;

    public BabyPlumPet(float x, float y) {
        super(NAME, ID, 234, -8.0F, 10.0F, 58.0F, 38.0F, null, x, y);
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(250);
        } else {
            this.setHp(234);
        }
        this.animation = new AnimatedActor(IsaacModExtend.getResourcePath("monsters/908.000_baby plum.xml"));
        animation.scale = Settings.scale * 4.0F;
        animation.setCurAnimation("Descend");
        animation.addTriggerEvent("0", a -> { //Explosion
            switch (animation.getCurAnimationName()) {
                case "Death":
                    CardCrawlGame.sound.play("BOSS_BABY_PLUM_DEATH");
                    break;
            }
        });
        animation.addTriggerEvent("1", a -> {
            switch (animation.getCurAnimationName()) {
                case "Attack1": //转一圈
                    animation.flipX = !animation.flipX;
                    IsaacModExtend.addToBot(new BabyPlumAttack1Action(this.animation, this.animation.xPosition + 384.0F * Settings.scale, this.animation.yPosition + 128.0F * Settings.scale));
                    addToBot(new AbstractGameAction() {
                        float duration = 1.5F;
                        boolean t = false;
                        List<AbstractGameAction> actions = new ArrayList<>();

                        @Override
                        public void update() {
                            duration -= Gdx.graphics.getDeltaTime();
                            if (duration <= 1.4F && !t) {
                                int multi = 5;
                                if (AbstractDungeon.ascensionLevel >= 4) multi++;
                                for (int i=0;i<multi;i++) {
                                    actions.add(new AbstractGameAction() {
                                        @Override
                                        public void update() {
                                            AbstractMonster m = AbstractDungeon.getRandomMonster();
                                            if (m != null) {
                                                addToBot(new DamageAction(m, damage.get(0), AttackEffect.BLUNT_LIGHT));
                                            }
                                            isDone = true;
                                        }
                                    });
                                }
                                t = true;
                            }
                            if (actions.size() > 0) {
                                actions.get(0).update();
                                if (actions.get(0).isDone) {
                                    actions.remove(0);
                                }
                            }
                            if (duration <= 0) {
                                actions.clear();
                                resetPosition = true;
                                animation.flipX = !animation.flipX;
                                isDone = true;
                            }
                        }
                    });
                    break;
                case "Attack2": //砸地

                case "Death":
                    CardCrawlGame.sound.play("MEAT_JUMPS_0");
                    break;
                case "Attack3":
                    animation.setCurAnimation("Attack3Loop");
                    IsaacModExtend.addToBot(new BabyPlumAttack3Action(this.animation, this.hb.cX - 44.0F * Settings.scale, this.hb.cY - 64.0F * animation.scale));
                    break;
                case "Descend": //降落
                    animation.setCurAnimation("Appear");
                    addToBot(new AbstractGameAction() {
                        boolean t = true;

                        @Override
                        public void update() {
                            if (t) {
                                t = false;
                                duration = 2.3F;
                            }
                            tickDuration();
                        }
                    });
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            renderHealthBar = true;
                            isDone = true;
                        }
                    });
                    break;
                case "Leave": //离开
                    IsaacModExtend.addToBot(new AbstractGameAction() {
                        float duration = BabyPlumPet.this.escapeTimer;

                        @Override
                        public void update() {
                            BabyPlumPet.this.animation.xPosition += 200.0F * Gdx.graphics.getDeltaTime();
                            duration -= Gdx.graphics.getDeltaTime();
                            if (duration <= 0) {
                                IsaacModExtend.isPlumFluteUnlocked = true;
                                IsaacModExtend.saveSettings();
                                isDone = true;
                            }
                        }
                    });
                    break;
            }
        });
        animation.addTriggerEvent("2", a -> { //Sound
            CardCrawlGame.sound.play("MEAT_JUMPS_0");
        });
        animation.addTriggerEvent("3", a -> {
            resetPosition = false;
            IsaacModExtend.addToBot(new AbstractGameAction() {
                float duration = 0.9F;
                float startY = BabyPlumPet.this.hb.cY - 64.0F * animation.scale;
                boolean playEffect = false;

                @Override
                public void update() {
                    if (duration > 0.6F) {
                        BabyPlumPet.this.animation.yPosition = Interpolation.exp10In.apply(startY, startY + 256.0F * Settings.scale, (0.9F - duration) / 0.3F);
                    } else if (duration > 0.3F) {
                        BabyPlumPet.this.animation.yPosition = startY + 256.0F * Settings.scale;
                    } else {
                        BabyPlumPet.this.animation.yPosition = Interpolation.exp10Out.apply(startY + 256.0F * Settings.scale, startY + 64.0F * Settings.scale, (0.3F - duration) / 0.3F);
                        if (!playEffect) {
                            playEffect = true;
                            float angleRad = MathUtils.PI / 3.0F;
                            for (int i=0;i<6;i++) {
                                Point startPos = new Point(animation.xPosition, animation.yPosition - 56.0F * animation.scale);
                                AbstractDungeon.effectList.add(new BloodTearEffect(startPos, angleRad, 5.0F, 1.8F, 2.0F * Settings.scale, 9));
                                angleRad -= MathUtils.PI / 3.0F;
                            }
                            angleRad = MathUtils.PI / 2.0F;
                            for (int i=0;i<12;i++) {
                                Point startPos = new Point(animation.xPosition, animation.yPosition - 56.0F * animation.scale);
                                AbstractDungeon.effectList.add(new BloodTearEffect(startPos, angleRad, 3.0F, 1.8F, 2.0F * Settings.scale, 9));
                                angleRad -= MathUtils.PI / 6.0F;
                            }
                        }
                    }
                    duration -= Gdx.graphics.getDeltaTime();
                    if (duration <= 0) {
                        resetPosition = true;
                        isDone = true;
                    }
                }
            });
        });
        this.disposables.add(animation);
        this.deathTimer = 1.3F;
        if (AbstractDungeon.ascensionLevel >= 4) {
            this.damage.add(new DamageInfo(this, 3, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 26, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 4, DamageInfo.DamageType.NORMAL));
        } else {
            this.damage.add(new DamageInfo(this, 3, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 24, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 3, DamageInfo.DamageType.NORMAL));
        }
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new BarrageBlockadePower(this)));
        addToBot(new ApplyPowerAction(this, this, new GoodbyePower(this, 3)));
    }

    @Override
    public void update() {
        super.update();
        if (resetPosition) {
            animation.xPosition = this.hb.cX - 44.0F * Settings.scale;
            animation.yPosition = this.hb.cY - 64.0F * animation.scale;
        }
        animation.update();
        if (animation.isCurAnimationDone() && !this.isDying && !this.isEscaping) {
            animation.setCurAnimation("Idle");
        }
    }

    @Override
    public void renderHealth(SpriteBatch sb) {
        if (renderHealthBar) {
            super.renderHealth(sb);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Method method;
        if (!this.isDead && !this.escaped) {
            if (resetPosition) {
                animation.flipX = this.flipHorizontal;
                animation.flipY = this.flipVertical;
            }
            animation.render(sb);

            if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic("Runic Dome") && this.intent != AbstractMonster.Intent.NONE && !Settings.hideCombatElements) {
                try {
                    method = AbstractMonster.class.getDeclaredMethod("renderIntentVfxBehind", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                    method = AbstractMonster.class.getDeclaredMethod("renderIntent", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                    method = AbstractMonster.class.getDeclaredMethod("renderIntentVfxAfter", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                    method = AbstractMonster.class.getDeclaredMethod("renderDamageRange", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            this.hb.render(sb);
            this.intentHb.render(sb);
            this.healthHb.render(sb);
        }

        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            try {
                method = AbstractMonster.class.getDeclaredMethod("renderName", SpriteBatch.class);
                method.setAccessible(true);
                method.invoke(this, sb);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void takeTurn() {
        if (this.isDeadOrEscaped()) return;
        switch (this.nextMove) {
            case 0:
                resetPosition = false;
                animation.setCurAnimation("Attack1");
                break;
            case 1:
                addToBot(new AbstractGameAction() {
                    float duration = 1.0F;

                    @Override
                    public void update() {
                        if (duration == 1.0F) {
                            resetPosition = false;
                            animation.setCurAnimation("Attack2");
                        }
                        duration -= Gdx.graphics.getDeltaTime();
                        if (duration <= 0) {
                            isDone = true;
                        }
                    }
                });
                addToBot(new DamageRandomEnemyAction(damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            case 2:
                addToBot(new AbstractGameAction() {
                    float duration = 5.0F;
                    boolean t = false;
                    List<AbstractGameAction> actions = new ArrayList<>();

                    @Override
                    public void update() {
                        if (duration == 5.0F) {
                            resetPosition = false;
                            animation.setCurAnimation("Attack3");
                        }
                        if (duration <= 2.7F && !t) {
                            int multi = 5;
                            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                                if (!m.isDeadOrEscaped() && m.hasPower(FranticPower.POWER_ID)) {
                                    AbstractPower power = m.getPower(FranticPower.POWER_ID);
                                    multi += power.amount;
                                }
                            }
                            for (int i=0;i<multi;i++) {
                                actions.add(new AbstractGameAction() {
                                    @Override
                                    public void update() {
                                        AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                                        if (target != null) {
                                            addToBot(new DamageAction(target, BabyPlumPet.this.damage.get(0), AttackEffect.BLUNT_LIGHT));
                                        }
                                        isDone = true;
                                    }
                                });
                            }
                            t = true;
                        }
                        if (actions.size() > 0) {
                            actions.get(0).update();
                            if (actions.get(0).isDone) {
                                actions.remove(0);
                            }
                        }
                        duration -= Gdx.graphics.getDeltaTime();
                        if (duration <= 0) {
                            actions.clear();
                            isDone = true;
                        }
                    }
                });
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        resetPosition = true;
                        isDone = true;
                    }
                });
                break;
            case 3:
                this.animation.setCurAnimation("Descend");
                break;
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        if (hasPower(GoodbyePower.POWER_ID)) {
            AbstractPower power = getPower(GoodbyePower.POWER_ID);
            if (power.amount == 1) {
                setMove((byte) 0, Intent.ESCAPE);
                return;
            }
        }
        if (lastMove((byte) 0)) {
            setMove((byte) 1, Intent.ATTACK, this.damage.get(1).base);
        } else if (lastMove((byte) 1)) {
            int multi = 5;
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped() && m.hasPower(FranticPower.POWER_ID)) {
                    AbstractPower power = m.getPower(FranticPower.POWER_ID);
                    multi += power.amount;
                }
            }
            setMove((byte) 2, Intent.ATTACK, this.damage.get(2).base, multi, true);
        } else {
            if (AbstractDungeon.ascensionLevel >= 4) {
                setMove((byte) 0, Intent.ATTACK, this.damage.get(0).base, 6, true);
            } else {
                setMove((byte) 0, Intent.ATTACK, this.damage.get(0).base, 5, true);
            }
        }
    }

    @Override
    public void die() {
        super.die();
        this.animation.setCurAnimation("Death");
    }

    @Override
    public void escape() {
        this.type = EnemyType.NORMAL;
        super.escape();
        resetPosition = false;
        this.animation.setCurAnimation("Leave");
    }

    @SpireOverride
    protected void updateEscapeAnimation() {
        if (this.escapeTimer != 0.0F) {
            this.escapeTimer -= Gdx.graphics.getDeltaTime();
        }

        if (this.escapeTimer < 0.0F) {
            this.escaped = true;
            if (AbstractDungeon.getMonsters().areMonstersDead() && !AbstractDungeon.getCurrRoom().isBattleOver && !AbstractDungeon.getCurrRoom().cannotLose) {
                AbstractDungeon.getCurrRoom().endBattle();
            }
        }
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

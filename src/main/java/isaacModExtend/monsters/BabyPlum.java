package isaacModExtend.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.BabyPlumAttack1Action;
import isaacModExtend.actions.BabyPlumAttack3Action;
import isaacModExtend.cards.optionCards.InNoMood;
import isaacModExtend.cards.optionCards.OfCourse;
import isaacModExtend.effects.BloodTearEffect;
import isaacModExtend.powers.BarrageBlockadePower;
import isaacModExtend.powers.FranticPower;
import isaacModExtend.powers.GoodbyePower;
import isaacModExtend.relics.PlumFlute;
import utils.Point;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class BabyPlum extends AbstractAnm2Monster {
    public static final String ID = IsaacModExtend.makeID("BabyPlum");
    public static final String NAME;
    private boolean renderHealthBar = false;
    private boolean shouldRefreshIntent = false;

    public BabyPlum(float offsetX, float offsetY) {
        super(NAME, ID, 234, -8.0F, 10.0F, 256.0F, 256.0F, null, offsetX, offsetY);
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(250);
        } else {
            this.setHp(234);
        }
        this.animation = new AnimatedActor(IsaacModExtend.getResourcePath("monsters/908.000_baby plum.xml"));
        animation.scale = 4.0F;
        animation.setCurAnimation("Descend");
        animation.addTriggerEvent("0", a -> { //Explosion
            switch (animation.getCurAnimationName()) {
                case "Death":
                    CardCrawlGame.sound.play("BOSS_BABY_PLUM_DEATH");
                    break;
            }
        });
        animation.addTriggerEvent("1", a -> { //Shoot
            switch (animation.getCurAnimationName()) {
                case "Attack1": //转一圈
                    IsaacModExtend.addToBot(new BabyPlumAttack1Action(this.animation, this.animation.xPosition - 384.0F * Settings.scale, this.animation.yPosition + 128.0F * Settings.scale));
                    addToBot(new AbstractGameAction() {
                        float duration = 1.5F;
                        boolean t = false;
                        List<AbstractGameAction> actions = new ArrayList<>();

                        @Override
                        public void update() {
                            duration -= Gdx.graphics.getDeltaTime();
                            if (duration <= 1.4F && !t) {
                                int multi = 4;
                                if (AbstractDungeon.ascensionLevel >= 4) multi++;
                                for (int i=0;i<multi;i++) {
                                    actions.add(new DamageAction(AbstractDungeon.player, damage.get(0), AttackEffect.BLUNT_LIGHT));
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
                    IsaacModExtend.addToBot(new BabyPlumAttack3Action(this.animation, this.hb.cX, this.hb.cY));
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
                        float duration = BabyPlum.this.escapeTimer;

                        @Override
                        public void update() {
                            BabyPlum.this.animation.xPosition += 200.0F * Gdx.graphics.getDeltaTime();
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
        animation.addTriggerEvent("3", a -> { //Jump
            resetPosition = false;
            IsaacModExtend.addToBot(new AbstractGameAction() {
                float duration = 0.9F;
                boolean playEffect = false;

                @Override
                public void update() {
                    if (duration <= 0.3F) {
                        if (!playEffect) {
                            playEffect = true;
                            float angleRad = MathUtils.PI / 3.0F;
                            for (int i=0;i<6;i++) {
                                Point startPos = new Point(animation.xPosition, animation.yPosition);
                                AbstractDungeon.effectList.add(new BloodTearEffect(startPos, angleRad, 5.0F, 1.8F, 2.0F, 9));
                                angleRad -= MathUtils.PI / 3.0F;
                            }
                            angleRad = MathUtils.PI / 2.0F;
                            for (int i=0;i<12;i++) {
                                Point startPos = new Point(animation.xPosition, animation.yPosition);
                                AbstractDungeon.effectList.add(new BloodTearEffect(startPos, angleRad, 3.0F, 1.8F, 2.0F, 9));
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
        this.type = EnemyType.BOSS;
        this.deathTimer = 1.3F;
        if (AbstractDungeon.ascensionLevel >= 4) {
            this.damage.add(new DamageInfo(this, 3, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 16, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 3, DamageInfo.DamageType.NORMAL));
        } else {
            this.damage.add(new DamageInfo(this, 3, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 14, DamageInfo.DamageType.NORMAL));
            this.damage.add(new DamageInfo(this, 2, DamageInfo.DamageType.NORMAL));
        }
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.silenceBGMInstantly();
        ArrayList<AbstractCard> playChoices = new ArrayList<>();
        playChoices.add(new OfCourse());
        playChoices.add(new InNoMood());
        addToBot(new ChooseOneAction(playChoices));
        addToBot(new ApplyPowerAction(this, this, new BarrageBlockadePower(this)));
    }

    @Override
    public void update() {
        super.update();
        if (resetPosition) {
            animation.xPosition = this.hb.cX + this.animX;
            animation.yPosition = this.hb.cY + this.animY;
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
    public void takeTurn() {
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
                addToBot(new DamageAction(AbstractDungeon.player, damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                if (AbstractDungeon.ascensionLevel >= 19) {
                    addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 1, true)));
                }
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
                            int multi = 4;
                            if (AbstractDungeon.player.hasPower(FranticPower.POWER_ID)) {
                                AbstractPower power = AbstractDungeon.player.getPower(FranticPower.POWER_ID);
                                multi += power.amount;
                            }
                            for (int i=0;i<multi;i++) {
                                actions.add(new DamageAction(AbstractDungeon.player, damage.get(2), AttackEffect.BLUNT_LIGHT));
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
        shouldRefreshIntent = false;
        if (hasPower(GoodbyePower.POWER_ID)) {
            AbstractPower power = getPower(GoodbyePower.POWER_ID);
            if (power.amount == 1) {
                setMove((byte) 0, Intent.ESCAPE);
                return;
            }
        }
        if (lastMove((byte) 0)) {
            if (AbstractDungeon.ascensionLevel >= 19) {
                setMove((byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(1).base);
            } else {
                setMove((byte) 1, Intent.ATTACK, this.damage.get(1).base);
            }
        } else if (lastMove((byte) 1)) {
            shouldRefreshIntent = true;
            refreshMultiAttackIntent();
        } else {
            if (AbstractDungeon.ascensionLevel >= 4) {
                setMove((byte) 0, Intent.ATTACK, this.damage.get(0).base, 5, true);
            } else {
                setMove((byte) 0, Intent.ATTACK, this.damage.get(0).base, 4, true);
            }
        }
    }

    public void refreshMultiAttackIntent() {
        if (shouldRefreshIntent) {
            int multi = 5;
            if (AbstractDungeon.player.hasPower(FranticPower.POWER_ID)) {
                AbstractPower power = AbstractDungeon.player.getPower(FranticPower.POWER_ID);
                multi += power.amount;
            }
            setMove((byte) 2, Intent.ATTACK, this.damage.get(2).base, --multi, true);
        }
    }

    @Override
    public void die() {
        super.die();
        this.animation.setCurAnimation("Death");
        this.onBossVictoryLogic();
    }

    @Override
    public void escape() {
        this.type = EnemyType.NORMAL;
        super.escape();
        resetPosition = false;
        this.animation.setCurAnimation("Leave");
        AbstractDungeon.combatRewardScreen.clear();
        AbstractDungeon.getCurrRoom().addRelicToRewards(new PlumFlute());
        IsaacModExtend.isPlumFluteUnlocked = true;
        IsaacModExtend.saveSettings();
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

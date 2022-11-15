package isaacModExtend.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.Anm2SwitchAction;
import isaacModExtend.actions.Anm2WaitAction;
import isaacModExtend.actions.SirenDestroyPetsAction;
import isaacModExtend.actions.SirenSlashAttackAction;
import isaacModExtend.monsters.pet.BloodPuppyPet;
import isaacModExtend.powers.FamiliarBarrierPower;
import isaacModExtend.relics.RockBottom;
import patches.player.PlayerAddFieldsPatch;
import relics.NineLifeCat;
import utils.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Siren extends AbstractAnm2Monster {
    public static final String ID = IsaacModExtend.makeID("Siren");
    public static final String NAME;
    private static final String[] MOVES;
    private List<String> animationQueue = new ArrayList<>();
    private String[] familiarList = new String[] {
            "Hairball",
            "Incubus",
            "Leech",
            "ScapeGoat",
            "Succubus",
            BloodPuppyPet.ID
    };
    private List<AbstractMonster> controlledPets = new ArrayList<>();
    private List<AbstractMonster> allHelpers = new ArrayList<>();
    private List<AbstractMonster> availablePets = new ArrayList<>();
    private Map<Integer, Point> controlledPetsSlot = new HashMap<>();
    private Map<String, Integer> controlledPetsSlotIndex = new HashMap<>();
    private List<Point> controlledPetsPosition = new ArrayList<>();

    private boolean stopLoop = false;
    boolean useSuicideSlam = false;

    public Siren(float offsetX, float offsetY) {
        super(NAME, ID, 666, -8.0F, 10.0F, 384.0F, 384.0F, null, offsetX, offsetY);
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(666);
        } else {
            this.setHp(600);
        }
        this.animation = new AnimatedActor(IsaacModExtend.getResourcePath("monsters/904.000_siren.xml"));
        animation.scale = 4.0F;
        animation.setCurAnimation("Appear");
        animation.addTriggerEvent("0", a -> { //Explosion
            switch (animation.getCurAnimationName()) {
                case "Death":
                    animation.dispose();
                    animation = new AnimatedActor(IsaacModExtend.getResourcePath("monsters/904.001_siren skull.xml"));
                    animation.scale = 4.0F;
                    animation.setCurAnimation("Appear");
                    break;
            }
        });
        animation.addTriggerEvent("1", a -> { //Sound
            switch (animation.getCurAnimationName()) {
                case "Attack2BStart":
                    CardCrawlGame.sound.play("BOSS_SIREN_SING_" + MathUtils.random(1, 3));
                    CardCrawlGame.sound.play("BOSS_SIREN_SINGINGATTACK");
                    break;
                case "SlashLeft":
                    CardCrawlGame.sound.play("BOSS_SIREN_LUNGEATTACK");
                    break;
                case "Attack1Start":
                    CardCrawlGame.sound.play("BOSS_SIREN_SCREAM_ATTACK_" + MathUtils.random(1, 3));
                    CardCrawlGame.sound.play("BOSS_SIREN_SCREAM_ATTACK_LAYER");
                    break;
                case "Recall":
                    CardCrawlGame.sound.play("BOSS_SIREN_MINIONSBLACKSMOKE");
                    break;
                case "Revive":
                    if (AbstractDungeon.player.hasRelic(NineLifeCat.ID)) {
                        AbstractRelic relic = AbstractDungeon.player.getRelic(NineLifeCat.ID);
                        IsaacModExtend.addToBot(new RelicAboveCreatureAction(this, relic));
                        addToBot(new HealAction(this, this, this.maxHealth / 10));
                    }
                    break;
            }
        });
        animation.addTriggerEvent("2", a -> { //Shoot
            switch (animation.getCurAnimationName()) {
                case "SlashLeft":
                    Siren.this.resetPosition = false;
                    IsaacModExtend.addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            Siren.this.resetPosition = true;
                            isDone = true;
                        }
                    });
                    IsaacModExtend.addToTop(new SirenSlashAttackAction(this.animation, this.animation.xPosition - 384.0F * Settings.scale, this.animation.yPosition + 32.0F * Settings.scale, !Siren.this.useSuicideSlam));
                    break;
                case "Attack1Start":

                    break;
            }
        });
        this.disposables.add(animation);
        this.type = EnemyType.BOSS;
        this.deathTimer = 1.3F;
        this.damage.add(new DamageInfo(this, 3));
        this.damage.add(new DamageInfo(this, 2));
        this.damage.add(new DamageInfo(this, 12));
        controlledPetsSlot.put(0, null);
        controlledPetsSlot.put(1, null);
        controlledPetsSlot.put(2, null);
        controlledPetsPosition.add(new Point(this.hb.cX - this.hb_w / 2.0 - 32 * Settings.scale, this.hb.cY - 32 * Settings.scale));
        controlledPetsPosition.add(new Point(this.hb.cX + this.hb_w / 2.0 + 32 * Settings.scale, this.hb.cY - 32 * Settings.scale));
        controlledPetsPosition.add(new Point(this.hb.cX, this.hb.cY + 280 * Settings.scale));
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        addToBot(new ApplyPowerAction(this, this, new FamiliarBarrierPower(this, allHelpers)));
        IsaacModExtend.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.getCurrRoom().playBgmInstantly("ISAAC_BOSS_INTRO");
                isDone = true;
            }
        });
        IsaacModExtend.addToBot(new Anm2WaitAction(5.8F));
        IsaacModExtend.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.music.silenceTempBgmInstantly();
                AbstractDungeon.getCurrRoom().playBgmInstantly("ISAAC_BOSS_LOOP");
                isDone = true;
            }
        });
    }

    @Override
    public void update() {
        super.update();
        if (resetPosition) {
            animation.xPosition = this.hb.cX + this.animX;
            animation.yPosition = this.hb.cY + this.animY;
        }
        animation.update();
        if (animation.isCurAnimationDone() && !this.isDying && !this.isEscaping && !this.stopLoop) {
            if (animationQueue.size() > 0) {
                animation.setCurAnimation(animationQueue.remove(0));
            } else {
                animation.setCurAnimation("Idle");
            }
        }
    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        switch (this.nextMove) {
            case 0: //唱歌(strong debuff)
                this.animation.setCurAnimation("Attack2BStart");
                this.animationQueue.add("Attack2BLoop");
                addToBot(new Anm2WaitAction(2.0F));
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        while (controlledPets.size() < 3 && availablePets.size() > 0) {
                            AbstractMonster m = availablePets.get(AbstractDungeon.miscRng.random(availablePets.size() - 1));
                            availablePets.remove(m);
                            PlayerAddFieldsPatch.f_minions.get(p).monsters.remove(m);
                            controlledPets.add(m);
                            for (int i = 0; i < 3; i++) {
                                if (controlledPetsSlot.get(i) == null) {
                                    controlledPetsSlot.put(i, controlledPetsPosition.remove(0));
                                    controlledPetsSlotIndex.put(m.id, i);
                                    Point point = controlledPetsSlot.get(i);
                                    AbstractMonster sirenHelper = new SirenHelper(0, 0, m);
                                    sirenHelper.drawX = (float) point.x;
                                    sirenHelper.drawY = (float) point.y;
                                    addToBot(new SpawnMonsterAction(sirenHelper, true, -99));
                                    allHelpers.add(sirenHelper);
                                    break;
                                }
                            }
                        }
                        isDone = true;
                    }
                });
                addToBot(new Anm2SwitchAction(this.animation, "Attack2BEnd"));
                addToBot(new ApplyPowerAction(p, this, new WeakPower(p, 2, true)));
                break;
            case 1: //带跟班滑翔(unknown)
                if (!this.lastMove((byte) 1)) {
                    this.animation.setCurAnimation("HoverStart");
                    this.animationQueue.add("HoverLoop");
                } else {
                    this.animation.setCurAnimation("HoverLoop");
                }
                int block = 0;
                for (AbstractMonster m1 : AbstractDungeon.getMonsters().monsters) {
                    if (m1 != this && !m1.isDeadOrEscaped()) {
                        if (AbstractDungeon.ascensionLevel >= 19) {
                            addToBot(new ApplyPowerAction(m1, Siren.this, new StrengthPower(m1, 5)));
                            addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 1)));
                        } else {
                            addToBot(new ApplyPowerAction(m1, Siren.this, new StrengthPower(m1, 3)));
                        }
                        block += 8;
                    }
                }
                if (AbstractDungeon.ascensionLevel >= 19) {
                    addToBot(new GainBlockAction(this, 24));
                    addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 3)));
                } else {
                    if (block > 0) {
                        addToBot(new GainBlockAction(this, block));
                    } else {
                        addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 2)));
                    }
                }
                break;
            case 2: //猛击销毁跟班(unknown)
                this.animation.setCurAnimation("HoverEnd");
                this.animationQueue.add("SlashLeft");
                addToBot(new Anm2WaitAction(0.7F));
                allHelpers.clear();
                for (AbstractMonster m1 : AbstractDungeon.getMonsters().monsters) {
                    if (m1 instanceof SirenHelper && !m1.isDeadOrEscaped()) {
                        allHelpers.add(m1);
                    }
                }
                addToBot(new SirenDestroyPetsAction(0.4F, allHelpers));
                for (AbstractMonster m1 : allHelpers) {
                    addToBot(new DamageAction(p, m1.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    addToBot(new DamageAction(m1, new DamageInfo(this, m1.currentHealth, DamageInfo.DamageType.HP_LOSS)));
                }
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        Siren.this.useSuicideSlam = false;
                        isDone = true;
                    }
                });
                break;
            case 3: //冲刺(attack)
                if (this.lastMoveBefore((byte) 4)) {
                    this.stopLoop = false;
                    this.animation.setCurAnimation("TeleportEnd");
                    this.animationQueue.add("SlashLeft");
                    addToBot(new Anm2WaitAction(1.3F));
                } else {
                    this.animation.setCurAnimation("SlashLeft");
                    addToBot(new Anm2WaitAction(0.35F));
                }
                addToBot(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                addToBot(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                addToBot(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                addToBot(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                addToBot(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                if (this.lastMoveBefore((byte) 4)) {
                    addToBot(new RemoveSpecificPowerAction(p, this, SlowPower.POWER_ID));
                }
                break;
            case 4: //化为泥淖(debuff)
                this.stopLoop = true;
                this.animation.setCurAnimation("Teleport");
                addToBot(new ApplyPowerAction(this, this, new IntangiblePower(this, 1)));
                addToBot(new ApplyPowerAction(p, this, new SlowPower(p, 0)));
                break;
            case 5: //尖啸(attack defend)
                this.animation.setCurAnimation("Attack1Start");
                this.animationQueue.add("Attack1Loop");
                addToBot(new Anm2WaitAction(2.0F));
                addToBot(new Anm2SwitchAction(this.animation, "Attack1End"));
                IsaacModExtend.addToBot(new Anm2WaitAction(1.3F));
                IsaacModExtend.addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.XLONG, false);
                        isDone = true;
                    }
                });
                IsaacModExtend.addToBot(new VFXAction(p, new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.RED_TEXT_COLOR, ShockWaveEffect.ShockWaveType.ADDITIVE), 0.0F));
                IsaacModExtend.addToBot(new VFXAction(p, new BorderFlashEffect(new Color(1.0F, 0.1F, 0.05F, 0.0F)), 0.0F));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                IsaacModExtend.addToBot(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                if (AbstractDungeon.ascensionLevel >= 19) {
                    IsaacModExtend.addToBot(new GainBlockAction(this, 30));
                } else {
                    IsaacModExtend.addToBot(new GainBlockAction(this, 20));
                }
                if (this.hasPower(StrengthPower.POWER_ID)) {
                    AbstractPower power = this.getPower(StrengthPower.POWER_ID);
                    if (power.amount < 0) {
                        IsaacModExtend.addToBot(new RemoveSpecificPowerAction(this, this, StrengthPower.POWER_ID));
                        IsaacModExtend.addToBot(new RemoveSpecificPowerAction(this, this, "Shackled"));
                    }
                }
                addToBot(new ApplyPowerAction(p, this, new StrengthPower(p, -6)));
                if (!p.hasPower(ArtifactPower.POWER_ID) && !p.hasRelic(RockBottom.ID)) {
                    addToBot(new ApplyPowerAction(p, this, new GainStrengthPower(p, 6)));
                }
                break;
            case 6: //召唤助手(unknown)
                this.animation.setCurAnimation("Recall");
                while (controlledPets.size() < 3) {
                    for (int i = 0; i < 3; i++) {
                        if (controlledPetsSlot.get(i) == null) {
                            controlledPetsSlot.put(i, controlledPetsPosition.remove(0));
                            Point point = controlledPetsSlot.get(i);
                            AbstractMonster sirenHelper = new SirenHelper(0, 0, null);
                            controlledPetsSlotIndex.put(sirenHelper.id, i);
                            sirenHelper.drawX = (float) point.x;
                            sirenHelper.drawY = (float) point.y;
                            addToBot(new SpawnMonsterAction(sirenHelper, true, -99));
                            controlledPets.add(sirenHelper);
                            allHelpers.add(sirenHelper);
                            break;
                        }
                    }
                }
                break;
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.output > 0 && this.hasPower("Intangible")) {
            info.output = 1;
        }

        super.damage(info);
    }

    public void returnControlledPet(String id) {
        AbstractMonster toRemove = null;
        for (AbstractMonster m : controlledPets) {
            if (m.id.equals(id)) {
                controlledPetsPosition.add(controlledPetsSlot.remove(controlledPetsSlotIndex.remove(id)));
                if (!(m instanceof SirenHelper)) {
                    PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player).monsters.add(m);
                } else {
                    allHelpers.remove(m);
                }
                toRemove = m;
                break;
            }
        }
        if (toRemove != null) controlledPets.remove(toRemove);
    }

    @Override
    protected void getMove(int aiRng) {
        if (this.lastMove((byte) 4)) {
            setMove((byte) 3, Intent.ATTACK, 3, 5, true);
            return;
        }
        if (!this.lastTwoMoves((byte) 1) && controlledPets.size() > 0 && aiRng < 30) {
            setMove((byte) 1, Intent.DEFEND_BUFF);
            return;
        }
        if (aiRng < 60 && allHelpers.size() > 0) {
            useSuicideSlam = true;
            for (AbstractMonster m : allHelpers) {
                addToBot(new RollMoveAction(m));
            }
            setMove((byte) 2, Intent.UNKNOWN);
            return;
        }
        MonsterGroup pets = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);
        availablePets.clear();
        for (String id : familiarList) {
            AbstractMonster monster = pets.getMonster(id);
            if (monster != null && !monster.isDeadOrEscaped()) {
                availablePets.add(monster);
            }
        }
        if (controlledPets.size() < 2) {
            if (!this.lastMove((byte) 0) && !this.lastMoveBefore((byte) 0) && availablePets.size() > 0 && aiRng < 40) {
                setMove(MOVES[0], (byte) 0, Intent.MAGIC);
                return;
            } else if (aiRng < 50 && !this.lastMove((byte) 6) && !this.lastMoveBefore((byte) 6)) {
                setMove((byte) 6, Intent.UNKNOWN);
                return;
            }
        }
        if (aiRng < 60) {
            setMove((byte) 3, Intent.ATTACK, 3, 5, true);
        } else if (aiRng < 80 && !this.lastMoveBefore((byte) 4)) {
            setMove(MOVES[1], (byte) 4, Intent.DEBUFF);
        } else {
            setMove(MOVES[2], (byte) 5, Intent.ATTACK_DEFEND, 2, 9, true);
        }
    }

    @Override
    public void die() {
        if (controlledPets.size() > 0 && AbstractDungeon.player.hasRelic(NineLifeCat.ID)) {
            AbstractRelic nineLifeCat = AbstractDungeon.player.getRelic(NineLifeCat.ID);
            if (nineLifeCat.counter > 0) {
                nineLifeCat.counter--;
                animation.setCurAnimation("Teleport");
                animationQueue.add("Revive");
                return;
            }
        }
        super.die();
        this.animation.setCurAnimation("Death");
        this.onBossVictoryLogic();
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDead && !m.isDying) {
                AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
                AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
                AbstractDungeon.actionManager.addToTop(new VFXAction(m, new InflameEffect(m), 0.2F));
            }
        }
    }

    @Override
    protected void onBossVictoryLogic() {
        if (Settings.FAST_MODE) {
            this.deathTimer += 0.7F;
        } else {
            ++this.deathTimer;
        }

        AbstractDungeon.scene.fadeInAmbiance();
        if (AbstractDungeon.getCurrRoom().event == null) {
            ++AbstractDungeon.bossCount;
            StatsScreen.incrementBossSlain();
            if (GameActionManager.turn <= 1) {
                UnlockTracker.unlockAchievement("YOU_ARE_NOTHING");
            }

            if (GameActionManager.damageReceivedThisCombat - GameActionManager.hpLossThisCombat <= 0) {
                UnlockTracker.unlockAchievement("PERFECT");
                ++CardCrawlGame.perfect;
            }
        }

        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.silenceBGMInstantly();
        CardCrawlGame.sound.play("BOSS_VICTORY_STINGER");
        CardCrawlGame.music.playTempBgmInstantly("ISAAC_BOSS_BEATEN", false);

        for (AbstractBlight b : AbstractDungeon.player.blights) {
            b.onBossDefeat();
        }
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
    }
}

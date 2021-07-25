package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.ShelledParasite;
import com.megacrit.cardcrawl.monsters.city.SnakePlant;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import com.megacrit.cardcrawl.stances.*;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.patches.RewardItemPatch;
import monsters.Monstro;
import relics.AnarchistCookbook;
import relics.Damocles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMTrainer extends CustomRelic implements CustomSavable<Map<String, List<Integer>>> {
    public static final String ID = IsaacModExtend.makeID("TMTrainer");
    public static final String IMG_PATH = "relics/tmTrainer.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    private static List<Runnable> totalEffects = new ArrayList<>();
    private Map<String, List<Integer>> effects = new HashMap<>();

    public TMTrainer() {
        super(ID, IMG, RelicTier.RARE, LandingSound.CLINK);
        effects.put("onEvokeOrb", new ArrayList<>());
        effects.put("onPlayCard", new ArrayList<>());
        effects.put("onPreviewObtainCard", new ArrayList<>());
        effects.put("onObtainCard", new ArrayList<>());
        effects.put("onGainGold", new ArrayList<>());
        effects.put("onLoseGold", new ArrayList<>());
        effects.put("onSpendGold", new ArrayList<>());
        effects.put("onEquip", new ArrayList<>());
        effects.put("onUnequip", new ArrayList<>());
        effects.put("atPreBattle", new ArrayList<>());
        effects.put("atBattleStart", new ArrayList<>());
        effects.put("onSpawnMonster", new ArrayList<>());
        effects.put("atBattleStartPreDraw", new ArrayList<>());
        effects.put("atTurnStart", new ArrayList<>());
        effects.put("atTurnStartPostDraw", new ArrayList<>());
        effects.put("onPlayerEndTurn", new ArrayList<>());
        effects.put("onBloodied", new ArrayList<>());
        effects.put("onNotBloodied", new ArrayList<>());
        effects.put("onManualDiscard", new ArrayList<>());
        effects.put("onUseCard", new ArrayList<>());
        effects.put("onVictory", new ArrayList<>());
        effects.put("onMonsterDeath", new ArrayList<>());
        effects.put("onBlockBroken", new ArrayList<>());
        effects.put("onPlayerGainBlock", new ArrayList<>());
        effects.put("onPlayerGainedBlock", new ArrayList<>());
        effects.put("onPlayerHeal", new ArrayList<>());
        effects.put("onMeditate", new ArrayList<>());
        effects.put("onEnergyRecharge", new ArrayList<>());
        effects.put("onEnterRestRoom", new ArrayList<>());
        effects.put("onRefreshHand", new ArrayList<>());
        effects.put("onShuffle", new ArrayList<>());
        effects.put("onAttack", new ArrayList<>());
        effects.put("onAttacked", new ArrayList<>());
        effects.put("onAttackedToChangeDamage", new ArrayList<>());
        effects.put("onAttackToChangeDamage", new ArrayList<>());
        effects.put("onExhaust", new ArrayList<>());
        effects.put("onEnterRoom", new ArrayList<>());
        effects.put("justEnteredRoom", new ArrayList<>());
        effects.put("onCardDraw", new ArrayList<>());
        effects.put("onChestOpen", new ArrayList<>());
        effects.put("onChestOpenAfter", new ArrayList<>());
        effects.put("onMasterDeckChange", new ArrayList<>());
        effects.put("atDamageModify", new ArrayList<>());
        effects.put("onUsePotion", new ArrayList<>());
        effects.put("onChangeStance", new ArrayList<>());
        effects.put("onLoseHp", new ArrayList<>());
        effects.put("onLoseHpLast", new ArrayList<>());
        effects.put("wasHPLost", new ArrayList<>());
        if (AbstractDungeon.miscRng != null) {
            for (List<Integer> list : effects.values()) {
                int effectAmt = AbstractDungeon.miscRng.random(1);
                if (AbstractDungeon.miscRng.randomBoolean(0.15F)) {
                    for (int i = 0; i < effectAmt; i++) {
                        list.add(AbstractDungeon.miscRng.random(totalEffects.size() - 1));
                    }
                }
            }
        }
        this.tips.clear();
        this.tips.add(new PowerTip(getUpdatedDescription(), this.description));
        this.initializeTips();
        this.flavorText = getUpdatedDescription();
    }

    @Override
    public String getUpdatedDescription() {
        int length = MathUtils.random(1, 30);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<length;i++) {
            sb.append(this.DESCRIPTIONS[0].charAt(MathUtils.random(this.DESCRIPTIONS[0].length() - 1)));
        }
        return sb.toString();
    }

    @Override
    public void playLandingSFX() {
        HashMap<String, Sfx> map = null;
        try {
            Field field = SoundMaster.class.getDeclaredField("map");
            field.setAccessible(true);
            map = (HashMap<String, Sfx>) field.get(CardCrawlGame.sound);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (map != null) {
            CardCrawlGame.sound.play((String) map.keySet().toArray()[MathUtils.random(map.size() - 1)], 0.8F);
        }
    }

    @Override
    public void onEvokeOrb(AbstractOrb ammo) {
        for (Integer i : effects.get("onEvokeOrb")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        for (Integer i : effects.get("onPlayCard")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onPreviewObtainCard(AbstractCard c) {
        for (Integer i : effects.get("onPreviewObtainCard")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        for (Integer i : effects.get("onObtainCard")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onGainGold() {
        for (Integer i : effects.get("onGainGold")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onLoseGold() {
        for (Integer i : effects.get("onLoseGold")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onSpendGold() {
        for (Integer i : effects.get("onSpendGold")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onEquip() {
        CardCrawlGame.music.silenceBGMInstantly();
        CardCrawlGame.music.silenceTempBgmInstantly();
        for (Integer i : effects.get("onEquip")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onUnequip() {
        for (Integer i : effects.get("onUnequip")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void atPreBattle() {
        for (Integer i : effects.get("atPreBattle")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void atBattleStart() {
        for (Integer i : effects.get("atBattleStart")) {
            totalEffects.get(i).run();
        }
    }

    private boolean spawnedMonster = false;

    @Override
    public void onSpawnMonster(AbstractMonster monster) {
        if (!spawnedMonster) {
            for (Integer i : effects.get("onSpawnMonster")) {
                totalEffects.get(i).run();
            }
            spawnedMonster = true;
        }
    }

    @Override
    public void atBattleStartPreDraw() {
        for (Integer i : effects.get("atBattleStartPreDraw")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void atTurnStart() {
        spawnedMonster = false;
        refreshHandCounter = 0;
        for (Integer i : effects.get("atTurnStart")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void atTurnStartPostDraw() {
        for (Integer i : effects.get("atTurnStartPostDraw")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onPlayerEndTurn() {
        for (Integer i : effects.get("onPlayerEndTurn")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onBloodied() {
        for (Integer i : effects.get("onBloodied")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onNotBloodied() {
        for (Integer i : effects.get("onNotBloodied")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onManualDiscard() {
        for (Integer i : effects.get("onManualDiscard")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        for (Integer i : effects.get("onUseCard")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onVictory() {
        for (Integer i : effects.get("onVictory")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        for (Integer i : effects.get("onMonsterDeath")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onBlockBroken(AbstractCreature m) {
        for (Integer i : effects.get("onBlockBroken")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public int onPlayerGainBlock(int blockAmount) {
        for (Integer i : effects.get("onPlayerGainBlock")) {
            totalEffects.get(i).run();
        }
        return blockAmount;
    }

    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        for (Integer i : effects.get("onPlayerGainedBlock")) {
            totalEffects.get(i).run();
        }
        return MathUtils.floor(blockAmount);
    }

    @Override
    public int onPlayerHeal(int healAmount) {
        for (Integer i : effects.get("onPlayerHeal")) {
            if (i != 10) totalEffects.get(i).run();
        }
        return healAmount;
    }

    @Override
    public void onMeditate() {
        for (Integer i : effects.get("onMeditate")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onEnergyRecharge() {
        for (Integer i : effects.get("onEnergyRecharge")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onEnterRestRoom() {
        for (Integer i : effects.get("onEnterRestRoom")) {
            totalEffects.get(i).run();
        }
    }

    private int refreshHandCounter = 0;

    @Override
    public void onRefreshHand() {
        if (refreshHandCounter < 5) {
            for (Integer i : effects.get("onRefreshHand")) {
                totalEffects.get(i).run();
            }
            refreshHandCounter++;
        }
    }

    @Override
    public void onShuffle() {
        for (Integer i : effects.get("onShuffle")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {

    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        return damageAmount;
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        for (Integer i : effects.get("onAttackedToChangeDamage")) {
            totalEffects.get(i).run();
        }
        return damageAmount;
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        return damageAmount;
    }

    @Override
    public void onExhaust(AbstractCard card) {
        for (Integer i : effects.get("onExhaust")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        for (Integer i : effects.get("onEnterRoom")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        for (Integer i : effects.get("justEnteredRoom")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        for (Integer i : effects.get("onCardDraw")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onChestOpen(boolean bossChest) {
        for (Integer i : effects.get("onChestOpen")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onChestOpenAfter(boolean bossChest) {
        for (Integer i : effects.get("onChestOpenAfter")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onMasterDeckChange() {
        for (Integer i : effects.get("onMasterDeckChange")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        return damage;
    }

    @Override
    public void onUsePotion() {
        for (Integer i : effects.get("onUsePotion")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onChangeStance(AbstractStance prevStance, AbstractStance newStance) {
        for (Integer i : effects.get("onChangeStance")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public void onLoseHp(int damageAmount) {
        for (Integer i : effects.get("onLoseHp")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public int onLoseHpLast(int damageAmount) {
        return damageAmount;
    }

    @Override
    public void wasHPLost(int damageAmount) {
        for (Integer i : effects.get("wasHPLost")) {
            totalEffects.get(i).run();
        }
    }

    @Override
    public Map<String, List<Integer>> onSave() {
        return effects;
    }

    @Override
    public void onLoad(Map<String, List<Integer>> randomEffects) {
        if (randomEffects != null) {
            effects = randomEffects;
        }
    }

    private static boolean isInCombat() {
        return AbstractDungeon.getCurrRoom().monsters != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    private static void spawnMonster(AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                m.init();
                AbstractDungeon.getCurrRoom().monsters.add(m);
                m.showHealthBar();
                m.usePreBattleAction();
                m.createIntent();
                isDone = true;
            }
        });
    }

    static {
        totalEffects.add(() -> {
            if (isInCombat()) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped()) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new VulnerablePower(m, 1, false)));
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new VulnerablePower(p, 2, false)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped()) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new WeakPower(m, 1, false)));
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new WeakPower(p, 3, false)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new FrailPower(p, 2, false)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, 4));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped()) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, 3));
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                if (m != null) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, 5, DamageInfo.DamageType.THORNS)));
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(999, true), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.LIGHTNING));
            }
        });
        totalEffects.add(() -> AbstractDungeon.player.damage(new DamageInfo(null, 7, DamageInfo.DamageType.NORMAL)));
        totalEffects.add(() -> AbstractDungeon.player.heal(10));
        totalEffects.add(() -> {
            if (isInCombat()) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped()) {
                        m.heal(20);
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                spawnMonster(new Cultist(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                spawnMonster(new ShelledParasite(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F)));
                spawnMonster(new FungiBeast(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) spawnMonster(new SnakePlant(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F)));
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                if (AbstractDungeon.miscRng.randomBoolean()) {
                    spawnMonster(new AcidSlime_S(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F), AbstractDungeon.miscRng.random(4)));
                } else {
                    spawnMonster(new SpikeSlime_S(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F), 0));
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                spawnMonster(new Sentry(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                spawnMonster(new Monstro(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F), 0.5F));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                spawnMonster(new Monstro(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F), 0.5F));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                spawnMonster(new Monstro(MathUtils.random(-200.0F, 400.0F), MathUtils.random(-100.0F, 400.0F), 0.5F));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                spawnMonster(new ApologySlime());
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, 10)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, -3)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                if (m != null) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 10)));
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                if (m != null) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, -10)));
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                AbstractDungeon.actionManager.addToBottom(new PlayTopCardAction(m, false));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new DiscardAction(p, p, 1, true));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(5));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DrawReductionPower(AbstractDungeon.player, 1)));
            }
        });
        totalEffects.add(() -> Settings.isDebug = !Settings.isDebug);
        totalEffects.add(() -> {
            MapRoomNode currNode = AbstractDungeon.getCurrMapNode();
            MapRoomNode nextNode = new MapRoomNode(0, currNode.y + 1);
            AbstractDungeon.nextRoom = nextNode;
            nextNode.room = new TrueVictoryRoom();
            nextNode.room.setMapSymbol("?");
            currNode.addEdge(new MapEdge(currNode.x, currNode.y, nextNode.x, nextNode.y));
            nextNode.addParent(currNode);
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped()) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new PoisonPower(m, p, 9)));
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PoisonPower(p, p, 1)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                if (m != null) {
                    for (AbstractPower power : m.powers) {
                        if (power.type == AbstractPower.PowerType.BUFF) {
                            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(m, m, power));
                        }
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                for (AbstractPower power : p.powers) {
                    if (power.type == AbstractPower.PowerType.BUFF) {
                        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p, p, power));
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                if (m != null) {
                    for (AbstractPower power : m.powers) {
                        if (power.type == AbstractPower.PowerType.DEBUFF) {
                            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(m, m, power));
                        }
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                for (AbstractPower power : p.powers) {
                    if (power.type == AbstractPower.PowerType.DEBUFF) {
                        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p, p, power));
                    }
                }
            }
        });
        totalEffects.add(() -> AbstractDungeon.player.increaseMaxHp(10, true));
        totalEffects.add(() -> {
            if (isInCombat()) {
                IsaacModExtend.addToBot(new VFXAction(new WhirlwindEffect(new Color(1.0F, 0.9F, 0.4F, 1.0F), true)));
                IsaacModExtend.addToBot(new SkipEnemiesTurnAction());
            }
        });
        totalEffects.add(() -> IsaacModExtend.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.relics.add(0, new Damocles());
                AbstractDungeon.player.reorganizeRelics();
                isDone = true;
            }
        }));
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractDungeon.actionManager.addToBottom(new IncreaseMaxOrbAction(3));
            }
        });
        totalEffects.add(() -> IsaacModExtend.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.relics.add(0, AbstractDungeon.returnRandomRelic(RewardItemPatch.PatchConstructor.returnRandomRelicTier()));
                AbstractDungeon.player.reorganizeRelics();
                isDone = true;
            }
        }));
        totalEffects.add(() -> {
            if (isInCombat()) {
                IsaacModExtend.addToBot(new ChangeStanceAction(DivinityStance.STANCE_ID));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                IsaacModExtend.addToBot(new ChangeStanceAction(WrathStance.STANCE_ID));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                IsaacModExtend.addToBot(new ChangeStanceAction(CalmStance.STANCE_ID));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                IsaacModExtend.addToBot(new ChangeStanceAction(NeutralStance.STANCE_ID));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AnarchistCookbook relic = new AnarchistCookbook();
                relic.onRightClick();
                relic.img.dispose();
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractCard card = new Burn();
                card.upgrade();
                IsaacModExtend.addToBot(new MakeTempCardInHandAction(card, false));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(2));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractDungeon.actionManager.actions.clear();
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
                if (m != null) {
                    AbstractDungeon.actionManager.addToBottom(new InstantKillAction(m));
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BufferPower(p, 3)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped()) {
                        AbstractDungeon.actionManager.addToBottom(new StunMonsterAction(m, AbstractDungeon.player, 3));
                    }
                }
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 99)));
            }
        });
        totalEffects.add(() -> {
           for (AbstractRelic relic : AbstractDungeon.player.relics) {
               IsaacModExtend.addToBot(new AbstractGameAction() {
                   private float duration = 0.1F;

                   @Override
                   public void update() {
                       if (duration == 0.1F) {
                           relic.flash();
                       }
                       duration -= Gdx.graphics.getDeltaTime();
                       if (duration <= 0) {
                           isDone = true;
                       }
                   }
               });
           }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractDungeon.actionManager.actions.clear();
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DexterityPower(p, 10)));
            }
        });
        totalEffects.add(() -> {
            if (isInCombat()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new FocusPower(p, 2)));
            }
        });
    }
}

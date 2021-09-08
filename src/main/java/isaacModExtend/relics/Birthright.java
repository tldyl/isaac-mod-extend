package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.birthrightRelics.EmptyBirthrightRelic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Birthright extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("Birthright");
    public static final String IMG_PATH = "relics/birthright.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    public static final Map<Class<? extends AbstractPlayer>, AbstractRelic> birthrightEffects = new HashMap<>();
    private AbstractRelic delegate;

    public Birthright() {
        super(ID, IMG, RelicTier.SHOP, LandingSound.FLAT);
        Class<? extends AbstractPlayer> playerCls = null;
        if (AbstractDungeon.player != null) {
            playerCls = AbstractDungeon.player.getClass();
        }
        this.delegate = birthrightEffects.getOrDefault(playerCls, new EmptyBirthrightRelic());
        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
        this.flavorText = this.delegate.flavorText;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public String getUpdatedDescription() {
        return this.delegate == null ? this.DESCRIPTIONS[0] : this.delegate.getUpdatedDescription();
    }

    @Override
    public void onEvokeOrb(AbstractOrb ammo) {
        this.delegate.onEvokeOrb(ammo);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        this.delegate.onPlayCard(c, m);
    }

    @Override
    public void onPreviewObtainCard(AbstractCard c) {
        this.delegate.onPreviewObtainCard(c);
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        this.delegate.onObtainCard(c);
    }

    @Override
    public void onGainGold() {
        this.delegate.onGainGold();
    }

    @Override
    public void onLoseGold() {
        this.delegate.onLoseGold();
    }

    @Override
    public void onSpendGold() {
        this.delegate.onSpendGold();
    }

    @Override
    public void onEquip() {
        this.delegate.onEquip();
    }

    @Override
    public void onUnequip() {
        this.delegate.onUnequip();
    }

    @Override
    public void atPreBattle() {
        this.delegate.atPreBattle();
    }

    @Override
    public void atBattleStart() {
        this.delegate.atBattleStart();
    }

    @Override
    public void onSpawnMonster(AbstractMonster monster) {
        this.delegate.onSpawnMonster(monster);
    }

    @Override
    public void atBattleStartPreDraw() {
        this.delegate.atBattleStartPreDraw();
    }

    @Override
    public void atTurnStart() {
        this.delegate.atTurnStart();
    }

    @Override
    public void atTurnStartPostDraw() {
        this.delegate.atTurnStartPostDraw();
    }

    @Override
    public void onPlayerEndTurn() {
        this.delegate.onPlayerEndTurn();
    }

    @Override
    public void onBloodied() {
        this.delegate.onBloodied();
    }

    @Override
    public void onNotBloodied() {
        this.delegate.onNotBloodied();
    }

    @Override
    public void onManualDiscard() {
        this.delegate.onManualDiscard();
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        this.delegate.onUseCard(targetCard, useCardAction);
    }

    @Override
    public void onVictory() {
        this.delegate.onVictory();
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        this.delegate.onMonsterDeath(m);
    }

    @Override
    public void onBlockBroken(AbstractCreature m) {
        this.delegate.onBlockBroken(m);
    }

    @Override
    public int onPlayerGainBlock(int blockAmount) {
        return this.delegate.onPlayerGainBlock(blockAmount);
    }

    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        return this.delegate.onPlayerGainedBlock(blockAmount);
    }

    @Override
    public int onPlayerHeal(int healAmount) {
        return this.delegate.onPlayerHeal(healAmount);
    }

    @Override
    public void onMeditate() {
        this.delegate.onMeditate();
    }

    @Override
    public void onEnergyRecharge() {
        this.delegate.onEnergyRecharge();
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        this.delegate.addCampfireOption(options);
    }

    @Override
    public boolean canUseCampfireOption(AbstractCampfireOption option) {
        return this.delegate.canUseCampfireOption(option);
    }

    @Override
    public void onRest() {
        this.delegate.onRest();
    }

    @Override
    public void onRitual() {
        this.delegate.onRitual();
    }

    @Override
    public void onEnterRestRoom() {
        this.delegate.onEnterRestRoom();
    }

    @Override
    public void onRefreshHand() {
        this.delegate.onRefreshHand();
    }

    @Override
    public void onShuffle() {
        this.delegate.onShuffle();
    }

    @Override
    public void onSmith() {
        this.delegate.onSmith();
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        this.delegate.onAttack(info, damageAmount, target);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        return this.delegate.onAttacked(info, damageAmount);
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        return this.delegate.onAttackedToChangeDamage(info, damageAmount);
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        return this.delegate.onAttackToChangeDamage(info, damageAmount);
    }

    @Override
    public void onExhaust(AbstractCard card) {
        this.delegate.onExhaust(card);
    }

    @Override
    public void onTrigger() {
        this.delegate.onTrigger();
    }

    @Override
    public void onTrigger(AbstractCreature target) {
        this.delegate.onTrigger(target);
    }

    @Override
    public boolean checkTrigger() {
        return this.delegate.checkTrigger();
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        this.delegate.onEnterRoom(room);
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        this.delegate.justEnteredRoom(room);
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        this.delegate.onCardDraw(drawnCard);
    }

    @Override
    public void onChestOpen(boolean bossChest) {
        this.delegate.onChestOpen(bossChest);
    }

    @Override
    public void onChestOpenAfter(boolean bossChest) {
        this.delegate.onChestOpenAfter(bossChest);
    }

    @Override
    public void onDrawOrDiscard() {
        this.delegate.onDrawOrDiscard();
    }

    @Override
    public void onMasterDeckChange() {
        this.delegate.onMasterDeckChange();
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        return this.delegate.atDamageModify(damage, c);
    }

    @Override
    public int changeNumberOfCardsInReward(int numberOfCards) {
        return this.delegate.changeNumberOfCardsInReward(numberOfCards);
    }

    @Override
    public int changeRareCardRewardChance(int rareCardChance) {
        return this.delegate.changeRareCardRewardChance(rareCardChance);
    }

    @Override
    public int changeUncommonCardRewardChance(int uncommonCardChance) {
        return this.delegate.changeUncommonCardRewardChance(uncommonCardChance);
    }

    @Override
    public boolean canPlay(AbstractCard card) {
        return this.delegate.canPlay(card);
    }

    @Override
    public int getPrice() {
        return super.getPrice();
    }

    @Override
    public boolean canSpawn() {
        return this.delegate.canSpawn();
    }

    @Override
    public void onUsePotion() {
        this.delegate.onUsePotion();
    }

    @Override
    public void onChangeStance(AbstractStance prevStance, AbstractStance newStance) {
        this.delegate.onChangeStance(prevStance, newStance);
    }

    @Override
    public void onLoseHp(int damageAmount) {
        this.delegate.onLoseHp(damageAmount);
    }

    @Override
    public int onLoseHpLast(int damageAmount) {
        return this.delegate.onLoseHpLast(damageAmount);
    }

    @Override
    public void wasHPLost(int damageAmount) {
        this.delegate.wasHPLost(damageAmount);
    }
}

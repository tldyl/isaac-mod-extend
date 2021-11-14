package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import isaacModExtend.IsaacModExtend;

public class Hemoptysis extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("Hemoptysis");
    public static final String IMG_PATH = "relics/hemoptysis.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    private boolean applyVulnerable = false;
    private int lastDamage;

    public Hemoptysis() {
        super(ID, IMG, RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        applyVulnerable = true;
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (applyVulnerable && targetCard.type == AbstractCard.CardType.ATTACK) {
            this.flash();
            CardCrawlGame.sound.play("BABY_BRIM_" + MathUtils.random(1, 4), 0.0F);
            if (useCardAction.target != null) {
                addToBot(new RelicAboveCreatureAction(useCardAction.target, this));
                addToBot(new ApplyPowerAction(useCardAction.target, AbstractDungeon.player, new VulnerablePower(useCardAction.target, 3, false)));
            } else {
                for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                    if (!monster.isDeadOrEscaped()) {
                        addToBot(new RelicAboveCreatureAction(monster, this));
                        addToBot(new ApplyPowerAction(monster, AbstractDungeon.player, new VulnerablePower(monster, 3, false)));
                    }
                }
            }
            applyVulnerable = false;
        }
        if (targetCard.type == AbstractCard.CardType.ATTACK) {
            if (useCardAction.target instanceof AbstractMonster) {
                targetCard.calculateCardDamage((AbstractMonster) useCardAction.target);
            }
            lastDamage = targetCard.damage;
        }
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        if (m.hasPower(VulnerablePower.POWER_ID)) {
            this.flash();
            CardCrawlGame.sound.play("DEATH_BURST_LARGE_" + MathUtils.random(0, 1), 0.1F);
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDeadOrEscaped()) {
                    addToBot(new RelicAboveCreatureAction(monster, this));
                }
            }
            addToBot(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(lastDamage, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDeadOrEscaped()) {
                    addToBot(new ApplyPowerAction(monster, AbstractDungeon.player, new VulnerablePower(monster, 2, false)));
                }
            }
        }
    }
}

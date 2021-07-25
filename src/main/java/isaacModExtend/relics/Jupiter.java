package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import isaacModExtend.IsaacModExtend;

public class Jupiter extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("Jupiter");
    public static final String IMG_PATH = "relics/jupiter.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private boolean givePoison = false;

    public Jupiter() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void onEquip() {
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        this.flash();
        this.counter = 0;
        addToBot(new RelicAboveCreatureAction(p, this));
        addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, -2)));
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        AbstractPlayer p = AbstractDungeon.player;
        if (card.type == AbstractCard.CardType.ATTACK) {
            if (this.counter > 0) {
                this.flash();
                this.counter--;
                addToBot(new RelicAboveCreatureAction(p, this));
                addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, -1)));
                givePoison = true;
            } else {
                givePoison = false;
            }
        } else if (card.type == AbstractCard.CardType.SKILL) {
            this.flash();
            this.counter++;
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, 1)));
            givePoison = false;
        } else {
            givePoison = false;
        }
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster&& info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0 && givePoison) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;
            addToBot(new RelicAboveCreatureAction(target, this));
            addToBot(new ApplyPowerAction(target, p, new PoisonPower(target, p, 3)));
        }
    }

    @Override
    public void onVictory() {
        this.counter = -1;
    }
}

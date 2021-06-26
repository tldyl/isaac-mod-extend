package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import isaacModExtend.IsaacModExtend;

public class Venus extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("Venus");
    public static final String IMG_PATH = "relics/venus.png";

    public Venus() {
        super(ID, new Texture(IsaacModExtend.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.flash();
        AbstractDungeon.player.increaseMaxHp(7, true);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            addToBot(new RelicAboveCreatureAction(m, this));
            addToBot(new ApplyPowerAction(m, p, new WeakPower(m, 3, false)));
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != p && damageAmount > 0) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(info.owner, this));
            addToBot(new ApplyPowerAction(info.owner, p, new StrengthPower(info.owner, -3)));
        }
        return damageAmount;
    }
}

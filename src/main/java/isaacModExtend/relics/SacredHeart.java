package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.powers.SacredHeartPower;

public class SacredHeart extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("SacredHeart");
    public static final String IMG_PATH = "relics/sacredHeart.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public SacredHeart() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.flash();
        AbstractDungeon.player.increaseMaxHp(7, true);
        AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, 1)));
    }

    @Override
    public void atTurnStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if (!p.hasPower(SacredHeartPower.POWER_ID)) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new SacredHeartPower(p)));
        }
    }

    @Override
    public int getPrice() {
        return 350;
    }
}

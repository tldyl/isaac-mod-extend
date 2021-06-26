package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DoubleDamagePower;
import isaacModExtend.IsaacModExtend;
import patches.ui.SoulHeartPatch;

public class CrownOfLight extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("CrownOfLight");
    public static final String IMG_PATH = "relics/crownOfLight.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private boolean activated = true;

    public CrownOfLight() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.flash();
        SoulHeartPatch.soulHeart += 20;
    }

    @Override
    public void atBattleStart() {
        activated = true;
        AbstractPlayer p = AbstractDungeon.player;
        if (p.currentHealth == p.maxHealth) {
            this.grayscale = false;
        }
    }

    @Override
    public void atTurnStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.currentHealth == p.maxHealth && activated) {
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new DoubleDamagePower(p, 1, false)));
        }
    }

    @Override
    public void onLoseHp(int damageAmount) {
        if (!this.grayscale) {
            this.flash();
            this.grayscale = true;
            this.activated = false;
        }
    }

    @Override
    public int getPrice() {
        return 350;
    }
}

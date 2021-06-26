package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import isaacModExtend.IsaacModExtend;

public class TheWafer extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("TheWafer");
    public static final String IMG_PATH = "relics/theWafer.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public TheWafer() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount > 6) {
            this.flash();
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            return 6;
        } else {
            return damageAmount;
        }
    }

    @Override
    public int getPrice() {
        return 350;
    }
}

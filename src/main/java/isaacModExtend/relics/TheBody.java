package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import isaacModExtend.IsaacModExtend;

public class TheBody extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("TheBody");
    public static final String IMG_PATH = "relics/theBody.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public TheBody() {
        super(ID, IMG, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.flash();
        AbstractDungeon.player.increaseMaxHp(21, true);
    }
}

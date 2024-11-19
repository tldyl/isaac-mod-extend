package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import isaacModExtend.IsaacModExtend;
import patches.ui.SoulHeartPatch;

public class TheRelic extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("TheRelic");
    public static final String IMG_PATH = "relics/theRelic.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public TheRelic() {
        super(ID, IMG, RelicTier.COMMON, LandingSound.SOLID);
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onVictory() {
        this.counter++;
        if (this.counter == 4) this.beginLongPulse();
        if (this.counter >= 5) {
            this.flash();
            SoulHeartPatch.soulHeart += 14;
            this.stopPulse();
            this.counter = 0;
        }
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.floorNum >= 48;
    }
}

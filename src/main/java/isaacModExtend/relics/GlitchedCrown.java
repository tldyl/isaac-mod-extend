package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import isaacModExtend.IsaacModExtend;

public class GlitchedCrown extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("GlitchedCrown");
    public static final String IMG_PATH = "relics/glitchedCrown.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public GlitchedCrown() {
        super(ID, IMG, RelicTier.BOSS, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }
}

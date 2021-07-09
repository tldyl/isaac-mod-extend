package isaacModExtend.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import patches.player.PlayerMethodPatches;

@SuppressWarnings("unused")
public class PlayerMethodPatchesPatch {
    @SpirePatch(
            clz = PlayerMethodPatches.RenderPatch.class,
            method = "Prefix"
    )
    public static class PatchRenderPatch {
        public static SpireReturn<Void> Prefix(AbstractPlayer _instance, SpriteBatch sb) {
            return SpireReturn.Return();
        }
    }
}

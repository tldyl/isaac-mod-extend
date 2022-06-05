package isaacModExtend.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import isaacModExtend.IsaacModExtend;

public class CardCrawlGamePatch {
    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "render"
    )
    public static class PatchRender {
        public static void Prefix(CardCrawlGame cardCrawlGame) {
            if (IsaacModExtend.globalFrame != null) {
                IsaacModExtend.globalFrame.begin();
            }
        }

        public static void Postfix(CardCrawlGame cardCrawlGame) {
            if (IsaacModExtend.globalFrame != null) {
                SpriteBatch sb = ReflectionHacks.getPrivate(Gdx.app.getApplicationListener(), CardCrawlGame.class, "sb");
                IsaacModExtend.globalFrame.end();
                Texture img = IsaacModExtend.globalFrame.getColorBufferTexture();
                IsaacModExtend.globalFrame.begin();
                sb.begin();
                sb.setColor(Color.WHITE);
                for (AbstractGameEffect effect : IsaacModExtend.globalEffect) {
                    effect.render(sb);
                    sb.draw(img, 0, 0,
                            img.getWidth(), img.getHeight(),
                            0, 0,
                            img.getWidth(), img.getHeight(),
                            false, true);
                }
                sb.end();
                IsaacModExtend.globalFrame.end();
                sb.begin();
                sb.setColor(Color.WHITE);
                sb.draw(img, 0, 0,
                        img.getWidth(), img.getHeight(),
                        0, 0,
                        img.getWidth(), img.getHeight(),
                        false, true);
                sb.setShader(null);
                sb.end();
            }
        }
    }
}

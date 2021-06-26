package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.TMTrainer;

@SuppressWarnings("unused")
public class AbstractRelicPatch {
    @SpirePatch(
            clz = AbstractRelic.class,
            method = "instantObtain",
            paramtypez = {
                    AbstractPlayer.class,
                    int.class,
                    boolean.class
            }
    )
    @SpirePatch(
            clz = AbstractRelic.class,
            method = "instantObtain",
            paramtypez = {}
    )
    public static class PatchInstantObtain {
        public static SpireReturn<Void> Prefix(AbstractRelic relic) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(TMTrainer.ID) && !(relic instanceof TMTrainer)) {
                new TMTrainer().instantObtain();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "obtain"
    )
    public static class PatchObtain {
        public static SpireReturn<Void> Prefix(AbstractRelic relic) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(TMTrainer.ID) && !(relic instanceof TMTrainer)) {
                AbstractRelic tmTrainer = new TMTrainer();
                tmTrainer.spawn(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F);
                int row = AbstractDungeon.player.relics.size();
                tmTrainer.targetX = 64.0F * Settings.scale + (float)row * 72.0F * Settings.scale;
                tmTrainer.targetY = Settings.isMobile ? (float)Settings.HEIGHT - 132.0F * Settings.scale : (float)Settings.HEIGHT - 102.0F * Settings.scale;
                tmTrainer.obtain();
                tmTrainer.isDone = false;
                AbstractDungeon.getCurrRoom().relics.remove(relic);
                AbstractDungeon.getCurrRoom().relics.add(tmTrainer);
                IsaacModExtend.addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        relic.isObtained = false;
                        relic.isDone = true;
                        AbstractDungeon.player.reorganizeRelics();
                        isDone = true;
                    }
                });
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}

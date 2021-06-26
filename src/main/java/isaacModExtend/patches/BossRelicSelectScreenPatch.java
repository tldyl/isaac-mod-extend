package isaacModExtend.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import isaacModExtend.relics.GlitchedCrown;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class BossRelicSelectScreenPatch {
    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = SpirePatch.CLASS
    )
    public static class AddFieldPatch {
        public static SpireField<List<List<AbstractRelic>>> relics = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = "open"
    )
    public static class PatchOpen {
        private static final float SLOT_1_X;
        private static final float SLOT_1_Y;
        private static final float SLOT_2_X;
        private static final float SLOT_2_Y;
        private static final float SLOT_3_X;
        private static float[][] slots = new float[3][2];

        static {
            SLOT_1_X = (float)Settings.WIDTH / 2.0F + 4.0F * Settings.scale;
            SLOT_1_Y = AbstractDungeon.floorY + 360.0F * Settings.scale;
            SLOT_2_X = (float)Settings.WIDTH / 2.0F - 116.0F * Settings.scale;
            SLOT_2_Y = AbstractDungeon.floorY + 225.0F * Settings.scale;
            SLOT_3_X = (float)Settings.WIDTH / 2.0F + 124.0F * Settings.scale;
            slots[0][0] = SLOT_1_X;
            slots[0][1] = SLOT_1_Y;
            slots[1][0] = SLOT_2_X;
            slots[1][1] = SLOT_2_Y;
            slots[2][0] = SLOT_3_X;
            slots[2][1] = SLOT_2_Y;
        }

        public static void Prefix(BossRelicSelectScreen screen, ArrayList<AbstractRelic> chosenRelics) {
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                List<List<AbstractRelic>> relics = AddFieldPatch.relics.get(screen);
                relics.clear();
                relics.add(new ArrayList<>());
                relics.add(new ArrayList<>());
                relics.add(new ArrayList<>());
                int slot = 0;
                for (List<AbstractRelic> relicList : relics) {
                    for (int i = 0; i < 4; i++) {
                        AbstractRelic relic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS);
                        relic.spawn(slots[slot][0], slots[slot][1]);
                        relic.hb.move(relic.currentX, relic.currentY);
                        relicList.add(relic);
                    }
                    slot++;
                }
            }
        }
    }

    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = "update"
    )
    public static class PatchUpdate {
        private static float switchTimer = 0.25F;

        public static void Prefix(BossRelicSelectScreen screen) {
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                switchTimer -= Gdx.graphics.getDeltaTime();
                if (switchTimer <= 0) {
                    switchTimer = 0.25F;
                    List<List<AbstractRelic>> relics = AddFieldPatch.relics.get(screen);
                    for (int i=0;i<3;i++) {
                        relics.get(i).add(screen.relics.get(i));
                        screen.relics.set(i, relics.get(i).remove(0));
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = "relicObtainLogic"
    )
    public static class PatchRelicObtainLogic {
        public static void Prefix(BossRelicSelectScreen screen, AbstractRelic relic) {
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                List<List<AbstractRelic>> relics = AddFieldPatch.relics.get(screen);
                for (List<AbstractRelic> relicList : relics) {
                    StoreRelicPatch.PatchPurchaseRelic.putRelicsBackToPools(relicList);
                }
            }
        }
    }
}

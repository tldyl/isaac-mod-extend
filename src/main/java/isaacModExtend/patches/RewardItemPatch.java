package isaacModExtend.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import isaacModExtend.relics.GlitchedCrown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class RewardItemPatch {
    @SpirePatch(
            clz = RewardItem.class,
            method = SpirePatch.CLASS
    )
    public static class AddFieldPatch {
        public static SpireField<List<AbstractRelic>> relics = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(
            clz = RewardItem.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractRelic.class
            }
    )
    public static class PatchConstructor {
        public static void Prefix(RewardItem item, AbstractRelic relic) {
            if (AbstractDungeon.player == null) return;
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                List<AbstractRelic> relics = AddFieldPatch.relics.get(item);
                for (int i = 0; i < 4; i++) {
                    AbstractRelic.RelicTier tier = returnRandomRelicTier();
                    relics.add(AbstractDungeon.returnRandomRelic(tier));
                }
            }
        }

        public static AbstractRelic.RelicTier returnRandomRelicTier() {
            int roll = AbstractDungeon.relicRng.random(0, 99);
            if (ModHelper.isModEnabled("Elite Swarm")) {
                roll += 10;
            }

            if (roll < 50) {
                return AbstractRelic.RelicTier.COMMON;
            } else {
                return roll > 82 ? AbstractRelic.RelicTier.RARE : AbstractRelic.RelicTier.UNCOMMON;
            }
        }
    }

    @SpirePatch(
            clz = RewardItem.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static Map<RewardItem, Float> switchTimer = new HashMap<>();

        public static void Prefix(RewardItem item) {
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID) && item.type == RewardItem.RewardType.RELIC) {
                switchTimer.put(item, switchTimer.getOrDefault(item, 0.2F) - Gdx.graphics.getDeltaTime());
                if (switchTimer.get(item) <= 0) {
                    switchTimer.put(item, 0.2F);
                    List<AbstractRelic> relics = AddFieldPatch.relics.get(item);
                    relics.add(item.relic);
                    item.relic = relics.remove(0);
                    item.text = item.relic.name;
                }
            }
        }
    }

    @SpirePatch(
            clz = RewardItem.class,
            method = "claimReward"
    )
    public static class PatchClaimReward {
        public static void Prefix(RewardItem item) {
            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID && item.type == RewardItem.RewardType.RELIC && AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                StoreRelicPatch.PatchPurchaseRelic.putRelicsBackToPools(AddFieldPatch.relics.get(item));
                PatchUpdate.switchTimer.clear();
            }
        }
    }
}

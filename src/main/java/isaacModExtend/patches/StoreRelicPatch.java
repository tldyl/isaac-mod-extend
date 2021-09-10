package isaacModExtend.patches;

import blood.BloodStoreRelic;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;
import isaacModExtend.relics.GlitchedCrown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class StoreRelicPatch {
    @SpirePatch(
            clz = StoreRelic.class,
            method = SpirePatch.CLASS
    )
    public static class AddFieldPatch {
        public static SpireField<List<AbstractRelic>> relics = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(
            clz = StoreRelic.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Prefix(StoreRelic storeRelic, AbstractRelic relic, int slot, ShopScreen screenRef) {
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                List<AbstractRelic> relics = AddFieldPatch.relics.get(storeRelic);
                if (relic.tier != AbstractRelic.RelicTier.SHOP) {
                    for (int i = 0; i < 4; i++) {
                        AbstractRelic.RelicTier tier = RewardItemPatch.PatchConstructor.returnRandomRelicTier();
                        relics.add(AbstractDungeon.returnRandomRelic(tier));
                    }
                } else {
                    for (int i = 0; i < 4; i++) {
                        AbstractRelic.RelicTier tier = RewardItemPatch.PatchConstructor.returnRandomRelicTier();
                        relics.add(AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.SHOP));
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = StoreRelic.class,
            method = "update"
    )
    @SpirePatch(
            clz = BloodStoreRelic.class,
            method = "update"
    )
    public static class PatchUpdate {
        static Map<StoreRelic, Float> switchTimer = new HashMap<>();

        public static void Prefix(StoreRelic storeRelic, float rugY) {
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                switchTimer.put(storeRelic, switchTimer.getOrDefault(storeRelic, 0.2F) - Gdx.graphics.getDeltaTime());
                if (switchTimer.get(storeRelic) <= 0) {
                    switchTimer.put(storeRelic, 0.2F);
                    List<AbstractRelic> relics = AddFieldPatch.relics.get(storeRelic);
                    relics.add(storeRelic.relic);
                    storeRelic.relic = relics.remove(0);
                    if (storeRelic instanceof BloodStoreRelic) {
                        ((BloodStoreRelic) storeRelic).relic = storeRelic.relic;
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = StoreRelic.class,
            method = "purchaseRelic"
    )
    public static class PatchPurchaseRelic {
        public static void Prefix(StoreRelic storeRelic) {
            if (AbstractDungeon.player.hasRelic(GlitchedCrown.ID)) {
                if (AbstractDungeon.player.gold >= storeRelic.price && !AbstractDungeon.player.hasRelic("The Courier")) {
                    putRelicsBackToPools(AddFieldPatch.relics.get(storeRelic));
                    PatchUpdate.switchTimer.clear();
                } else if (AbstractDungeon.player.gold >= storeRelic.price) {
                    PatchUpdate.switchTimer.remove(storeRelic);
                }
            }
        }

        static void putRelicsBackToPools(List<AbstractRelic> relics) {
            for (AbstractRelic relic : relics) {
                switch (relic.tier) {
                    case COMMON:
                        AbstractDungeon.commonRelicPool.add(relic.relicId);
                        break;
                    case UNCOMMON:
                        AbstractDungeon.uncommonRelicPool.add(relic.relicId);
                        break;
                    case RARE:
                        AbstractDungeon.rareRelicPool.add(relic.relicId);
                        break;
                    case BOSS:
                        AbstractDungeon.bossRelicPool.add(relic.relicId);
                        break;
                    case SHOP:
                        AbstractDungeon.shopRelicPool.add(relic.relicId);
                        break;
                }
            }
        }
    }
}

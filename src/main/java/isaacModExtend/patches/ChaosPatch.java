package isaacModExtend.patches;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import mymod.IsaacMod;
import relics.Chaos;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unused")
public class ChaosPatch implements CustomSavable<Map<String, List<String>>> {
    private Map<String, List<String>> pools = new HashMap<>();

    public ChaosPatch() {
        BaseMod.addSaveField("chaosPools", this);
    }

    @Override
    public Map<String, List<String>> onSave() {
        pools.clear();
        pools.put("devilOnlyRelics", IsaacMod.devilOnlyRelics);

        List<String> angelOnlyRelics = new ArrayList<>();
        for (AbstractRelic relic : IsaacModExtend.angelOnlyRelics) {
            angelOnlyRelics.add(relic.relicId);
        }
        pools.put("angelOnlyRelics", angelOnlyRelics);

        List<String> planetariumRelics = new ArrayList<>();
        for (AbstractRelic relic : IsaacModExtend.planetariumRelics) {
            planetariumRelics.add(relic.relicId);
        }
        pools.put("planetariumRelics", planetariumRelics);
        return pools;
    }

    @Override
    public void onLoad(Map<String, List<String>> pools) {
        if (pools != null) {
            if (pools.containsKey("devilOnlyRelics")) {
                IsaacMod.devilOnlyRelics = pools.get("devilOnlyRelics");
            }
            if (pools.containsKey("angelOnlyRelics")) {
                IsaacModExtend.angelOnlyRelics.clear();
                for (String relicId : pools.get("angelOnlyRelics")) {
                    IsaacModExtend.angelOnlyRelics.add(RelicLibrary.getRelic(relicId).makeCopy());
                }
            }
            if (pools.containsKey("planetariumRelics")) {
                IsaacModExtend.planetariumRelics.clear();
                for (String relicId : pools.get("planetariumRelics")) {
                    IsaacModExtend.planetariumRelics.add(RelicLibrary.getRelic(relicId).makeCopy());
                }
            }
        }
    }

    @SpirePatch(
            clz = Chaos.class,
            method = "onEquip"
    )
    public static class PatchOnEquip {
        public static SpireReturn<Void> Prefix(Chaos chaos) {
            Set<String> all = new LinkedHashSet<>();
            for (AbstractRelic relic : RelicLibrary.redList) {
                all.add(relic.relicId);
            }
            for (AbstractRelic relic : RelicLibrary.greenList) {
                all.add(relic.relicId);
            }
            for (AbstractRelic relic : RelicLibrary.blueList) {
                all.add(relic.relicId);
            }
            for (AbstractRelic relic : RelicLibrary.whiteList) {
                all.add(relic.relicId);
            }
            try {
                Field field = BaseMod.class.getDeclaredField("customRelicLists");
                field.setAccessible(true);
                HashMap<AbstractCard.CardColor, ArrayList<AbstractRelic>> customRelicLists = (HashMap<AbstractCard.CardColor, ArrayList<AbstractRelic>>)field.get(null);
                for (Map.Entry<AbstractCard.CardColor, ArrayList<AbstractRelic>> e : customRelicLists.entrySet()) {
                    for (AbstractRelic relic : e.getValue()) {
                        all.add(relic.relicId);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            for (AbstractRelic relic : IsaacModExtend.angelOnlyRelics) {
                all.add(relic.relicId);
            }
            for (AbstractRelic relic : IsaacModExtend.planetariumRelics) {
                all.add(relic.relicId);
            }
            all.addAll(AbstractDungeon.commonRelicPool);
            AbstractDungeon.commonRelicPool.clear();
            all.addAll(AbstractDungeon.uncommonRelicPool);
            AbstractDungeon.uncommonRelicPool.clear();
            all.addAll(AbstractDungeon.rareRelicPool);
            AbstractDungeon.rareRelicPool.clear();
            all.addAll(AbstractDungeon.bossRelicPool);
            AbstractDungeon.bossRelicPool.clear();
            all.addAll(AbstractDungeon.shopRelicPool);
            AbstractDungeon.shopRelicPool.clear();
            all.addAll(IsaacMod.devilOnlyRelics);
            IsaacMod.devilOnlyRelics.clear();
            IsaacModExtend.angelOnlyRelics.clear();
            IsaacModExtend.planetariumRelics.clear();
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                all.remove(relic.relicId);
            }
            List<String> allList = new ArrayList<>(all);
            Collections.shuffle(allList, new Random(AbstractDungeon.miscRng.randomLong()));
            int n = all.size() / 8;
            int m = all.size() % 8;
            int[] relicPoolSize = new int[]{n + m, n, n, n, n, n, n, n};
            int index = 0;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                addAndRemove(allList, AbstractDungeon.commonRelicPool);
            }
            index++;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                addAndRemove(allList, AbstractDungeon.uncommonRelicPool);
            }
            index++;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                addAndRemove(allList, AbstractDungeon.rareRelicPool);
            }
            index++;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                addAndRemove(allList, AbstractDungeon.bossRelicPool);
            }
            index++;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                addAndRemove(allList, AbstractDungeon.shopRelicPool);
            }
            index++;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                addAndRemove(allList, IsaacMod.devilOnlyRelics);
            }
            index++;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                IsaacModExtend.angelOnlyRelics.add(RelicLibrary.getRelic(allList.remove(0)).makeCopy());
            }
            index++;
            for(int i = 0; i < relicPoolSize[index]; ++i) {
                IsaacModExtend.planetariumRelics.add(RelicLibrary.getRelic(allList.remove(0)).makeCopy());
            }
            return SpireReturn.Return(null);
        }

        private static void addAndRemove(List<String> all, List<String> toAdd) {
            if (all.size() > 0) {
                toAdd.add(all.remove(0));
            }
        }
    }
}

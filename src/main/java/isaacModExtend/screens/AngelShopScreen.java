package isaacModExtend.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;
import isaacModExtend.IsaacModExtend;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AngelShopScreen extends ShopScreen {
    public AngelShopScreen() {
        super();
    }

    @SpireOverride
    protected void initRelics() {
        try {
            Field field = ShopScreen.class.getDeclaredField("relics");
            field.setAccessible(true);
            List<StoreRelic> relics = (List<StoreRelic>) field.get(this);
            relics.clear();
            List<AbstractRelic> angelRelics = new ArrayList<>(IsaacModExtend.angelOnlyRelics);
            List<AbstractRelic> toRemove = new ArrayList<>();
            for (AbstractRelic relic : angelRelics) {
                if (AbstractDungeon.player.hasRelic(relic.relicId)) {
                    toRemove.add(relic);
                }
            }
            angelRelics.removeAll(toRemove);
            angelRelics = IsaacModExtend.getRandomAngelOnlyRelics(AbstractDungeon.merchantRng, 3, angelRelics);
            while (angelRelics.size() < 3) {
                angelRelics.add(new Circlet());
            }
            for (int i=0;i<3;i++) {
                relics.add(new StoreRelic(angelRelics.get(i), i, this));
                if (!Settings.isDailyRun) {
                    relics.get(i).price *= AbstractDungeon.merchantRng.random(0.95F, 1.05F);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

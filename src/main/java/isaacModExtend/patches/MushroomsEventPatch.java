package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.exordium.Mushrooms;
import isaacModExtend.relics.Mucormycosis;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class MushroomsEventPatch {
    @SpirePatch(
            clz = Mushrooms.class,
            method = "buttonEffect"
    )
    public static class PatchButtonEffect {
        public static void Prefix(Mushrooms event, int buttonPressed) {
            if (buttonPressed == 0) {
                try {
                    Field field = Mushrooms.class.getDeclaredField("screenNum");
                    field.setAccessible(true);
                    int screenNum = field.getInt(event);
                    if (screenNum == 2) {
                        if (!AbstractDungeon.player.hasRelic(Mucormycosis.ID)) {
                            AbstractDungeon.getCurrRoom().addRelicToRewards(new Mucormycosis());
                            AbstractDungeon.commonRelicPool.remove(Mucormycosis.ID);
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.interfaces.NeutralCreature;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class AbstractPlayerPatch {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "updateSingleTargetInput"
    )
    public static class PatchUpdateSingleTargetInput {
        @SpireInsertPatch(rloc = 26)
        public static void Insert(AbstractPlayer p) {
            for (AbstractRelic relic : p.relics) {
                if (relic instanceof NeutralCreature) {
                    NeutralCreature neutralCreature = (NeutralCreature) relic;
                    AbstractCreature creature = neutralCreature.getInstance();
                    if (creature != null && neutralCreature.isEnemy()) {
                        creature.hb.update();
                        if (creature.hb.hovered) {
                            try {
                                Field field = AbstractPlayer.class.getDeclaredField("hoveredMonster");
                                field.setAccessible(true);
                                field.set(p, creature);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}

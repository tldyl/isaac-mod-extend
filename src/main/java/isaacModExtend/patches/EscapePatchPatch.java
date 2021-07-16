package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import patches.monster.MonsterEscapePatch;

@SuppressWarnings("unused")
public class EscapePatchPatch {
    @SpirePatch(
            clz = MonsterEscapePatch.EscapePatch.class,
            method = "Prefix"
    )
    public static class PatchPrefix {
        public static SpireReturn<SpireReturn> Prefix(AbstractMonster monster) {
            monster.type = AbstractMonster.EnemyType.NORMAL;
            return SpireReturn.Continue();
        }
    }
}

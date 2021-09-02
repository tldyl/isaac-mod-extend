package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import isaacModExtend.relics.birthrightRelics.SilentBirthrightRelic;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class DiscardActionPatch {
    @SpirePatch(
            clz = DiscardAction.class,
            method = "update"
    )
    public static class PatchUpdate1 {
        @SpireInsertPatch(rloc = 16, localvars = {"c"})
        public static void Insert1(DiscardAction action, @ByRef(type = "cards.AbstractCard") AbstractCard[] _c) {
            AbstractCard c = _c[0];
            SilentBirthrightRelic.targetCard = c;
        }

        @SpireInsertPatch(rloc = 29, localvars = {"c"})
        public static void Insert2(DiscardAction action, @ByRef(type = "cards.AbstractCard") AbstractCard[] _c) {
            Insert1(action, _c);
        }

        @SpireInsertPatch(rloc = 56, localvars = {"c"})
        public static void Insert3(DiscardAction action, @ByRef(type = "cards.AbstractCard") AbstractCard[] _c) {
            Insert1(action, _c);
        }
    }

    @SpirePatch(
            clz = DiscardSpecificCardAction.class,
            method = "update"
    )
    public static class PatchUpdate2 {
        @SpireInsertPatch(rloc = 6)
        public static void Insert2(DiscardSpecificCardAction action) {
            try {
                Field field = DiscardSpecificCardAction.class.getDeclaredField("targetCard");
                field.setAccessible(true);
                SilentBirthrightRelic.targetCard = (AbstractCard) field.get(action);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

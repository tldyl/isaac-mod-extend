package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public class PaleReimuZhsFixPatch {
    @SpirePatch(
            cls = "paleoftheancients.reimu.monsters.Reimu",
            method = "engageFantasyHeaven",
            optional = true
    )
    public static class PatchEngageFantasyHeaven {
        @SpireInsertPatch(rloc = 8)
        public static SpireReturn<Void> Insert(Object reimu) {
            if (Settings.language == Settings.GameLanguage.ZHS) {
                try {
                    Constructor fantasyHeavenActionClassConstructor = Class.forName("paleoftheancients.reimu.actions.FantasyHeavenAction").getConstructor(Class.forName("paleoftheancients.reimu.monsters.Reimu"));
                    Object fantasyHeavenAction = fantasyHeavenActionClassConstructor.newInstance(reimu);
                    Constructor waitOnFantasyHeavenActionConstructor = Class.forName("paleoftheancients.reimu.actions.WaitOnFantasyHeavenAction").getConstructor(Class.forName("paleoftheancients.reimu.actions.FantasyHeavenAction"));
                    Object waitOnFantasyHeavenAction = waitOnFantasyHeavenActionConstructor.newInstance(fantasyHeavenAction);
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) waitOnFantasyHeavenAction);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}

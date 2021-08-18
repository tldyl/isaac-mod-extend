package isaacModExtend.patches;

import basemod.abstracts.CustomRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.*;
import mymod.IsaacMod;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class IsaacModPatch {
    private static void addRelics() {
        IsaacMod.relics.add(RedStew.ID);
        IsaacMod.relics.add(Mucormycosis.ID);
        IsaacMod.relics.add(GoldenRazor.ID);
        IsaacMod.relics.add(Sulfur.ID);
        IsaacMod.relics.add(RockBottom.ID);
        IsaacMod.relics.add(BloodPuppy.ID);
        IsaacMod.relics.add(Sol.ID);
        IsaacMod.relics.add(Luna.ID);
        IsaacMod.relics.add(Mercurius.ID);
        IsaacMod.relics.add(Venus.ID);
        IsaacMod.relics.add(Terra.ID);
        IsaacMod.relics.add(Mars.ID);
        IsaacMod.relics.add(Jupiter.ID);
        IsaacMod.relics.add(Neptunus.ID);
        IsaacMod.relics.add(FourPointFiveVolt.ID);
        IsaacMod.relics.add(BatteryPack.ID);
        IsaacMod.relics.add(Suplex.ID);
        IsaacMod.relics.add(Larynx.ID);
        IsaacMod.devilOnlyRelics.add(BloodOath.ID);
        IsaacMod.relics.add(RKey.ID);
        IsaacMod.relics.add(Eraser.ID);
        IsaacMod.relics.add(GlitchedCrown.ID);
        IsaacMod.relics.add(KeepersSack.ID);
        IsaacMod.relics.add(TMTrainer.ID);
        IsaacMod.relics.add(RedKey.ID);
        IsaacMod.relics.add(TheWafer.ID);
        IsaacMod.relics.add(TheRelic.ID);
        IsaacMod.relics.add(SacredHeart.ID);
        IsaacMod.relics.add(HolyGrail.ID);
        IsaacMod.relics.add(TheBody.ID);
        IsaacMod.relics.add(CrownOfLight.ID);
        IsaacMod.relics.add(Revelation.ID);
        IsaacMod.relics.add(HolyMantle.ID);
        IsaacMod.relics.add(AlabasterBox.ID);
        IsaacMod.relics.add(TheStairway.ID);
        IsaacMod.relics.add(BookOfVirtues.ID);
        IsaacMod.relics.add(EchoChamber.ID);
        IsaacMod.relics.add(PlumFlute.ID);
    }

    @SpirePatch(
            clz = IsaacMod.class,
            method = "receiveEditRelics"
    )
    public static class PatchReceiveEditRelics {
        public static void Prefix(IsaacMod mod) {
            addRelics();
        }
    }

    @SpirePatch(
            clz = IsaacMod.class,
            method = "preSettings"
    )
    public static class PatchPreSettings {
        public static void Postfix(IsaacMod mod) {
            addRelics();
            try {
                Field receivedCardField = IsaacMod.class.getDeclaredField("receivedCard");
                receivedCardField.setAccessible(true);
                receivedCardField.setBoolean(null, !IsaacModExtend.enableStartCardReward);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(
            clz = IsaacMod.class,
            method = "getRelic"
    )
    public static class PatchGetRelic {
        public static SpireReturn<AbstractRelic> Prefix(String name) {
            try {
                Class c = Class.forName("relics." + name);
                return SpireReturn.Return((CustomRelic)c.newInstance());
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException var3) {
                try {
                    Class c = Class.forName("isaacModExtend.relics." + name.replace(IsaacModExtend.makeID(""), ""));
                    return SpireReturn.Return((CustomRelic)c.newInstance());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    return SpireReturn.Return(null);
                }
            }
        }
    }
}

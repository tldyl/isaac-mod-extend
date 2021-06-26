package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.rooms.AngelRoom;
import relics.HushsDoor;

@SuppressWarnings("unused")
public class HushsDoorPatch {
    @SpirePatch(
            clz = HushsDoor.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static int chanceToAngel;
        public static boolean dealWithDevil = false;

        @SpireInsertPatch(rloc = 22, localvars = {"chanceToDevil"})
        public static void Insert(HushsDoor hushsDoor, @ByRef int[] chanceToDevil) {
            if (dealWithDevil) {
                chanceToAngel = 0;
            } else {
                chanceToAngel = Math.min(chanceToDevil[0], 50);
            }
            if (AbstractDungeon.aiRng.random(0, 99) < chanceToAngel) {
                chanceToDevil[0] = 0;
                AbstractRoom angelRoom = new AngelRoom();
                AbstractDungeon.currMapNode.setRoom(angelRoom);
                angelRoom.onPlayerEntry();
            }
        }
    }
}

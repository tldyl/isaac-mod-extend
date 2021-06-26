package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.relics.Mercurius;

@SuppressWarnings("unused")
public class MapRoomNodePatch {
    @SpirePatch(
            clz = MapRoomNode.class,
            method = "update"
    )
    public static class PatchUpdate {
        private static AbstractRoom.RoomPhase phase;

        public static void Prefix(MapRoomNode node) {
            if (AbstractDungeon.player.hasRelic(Mercurius.ID)) {
                phase = AbstractDungeon.getCurrRoom().phase;
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            }
        }

        public static void Postfix(MapRoomNode node) {
            if (AbstractDungeon.player.hasRelic(Mercurius.ID)) {
                AbstractDungeon.getCurrRoom().phase = phase;
            }
        }
    }
}

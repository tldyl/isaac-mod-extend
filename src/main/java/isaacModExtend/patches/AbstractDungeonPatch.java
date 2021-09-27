package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.PostGenerateDungeonMapSubscriber;
import isaacModExtend.relics.Luna;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class AbstractDungeonPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "generateRoomTypes"
    )
    public static class PatchGenerateRoomTypes {
        public static void Prefix(ArrayList<AbstractRoom> roomList, int availableRoomCount) {
            if (AbstractDungeon.player.hasRelic(Luna.ID)) {
                try {
                    Field field = AbstractDungeon.class.getDeclaredField("eventRoomChance");
                    field.setAccessible(true);
                    float eventRoomChance = field.getFloat(null);
                    eventRoomChance *= 2.0F;
                    field.setFloat(null, eventRoomChance);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getCurrRoom"
    )
    public static class PatchGetCurrRoom {
        public static SpireReturn<AbstractRoom> Prefix() {
            if (AbstractDungeon.currMapNode == null) {
                return SpireReturn.Return(new EmptyRoom());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getMonsters"
    )
    public static class PatchGetMonsters {
        public static SpireReturn<MonsterGroup> Prefix() {
            if (AbstractDungeon.getCurrRoom().monsters == null) {
                AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new AbstractMonster[]{});
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "generateMap"
    )
    public static class PatchGenerateMap {
        public static void Postfix() {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof PostGenerateDungeonMapSubscriber) {
                    ((PostGenerateDungeonMapSubscriber) relic).receivePostGenerateDungeonMap();
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeRelicList"
    )
    public static class PatchInitializeRelicList {
        public static boolean shouldInitializeRelicList = true;

        public static SpireReturn<Void> Prefix(AbstractDungeon dungeon) {
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    shouldInitializeRelicList = true;
                    isDone = true;
                }
            });
            return shouldInitializeRelicList ? SpireReturn.Continue() : SpireReturn.Return(null);
        }
    }
}

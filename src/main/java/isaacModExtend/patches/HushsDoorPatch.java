package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.monsters.pet.SatanicBibleWisp;
import isaacModExtend.rooms.AngelRoom;
import patches.player.PlayerAddFieldsPatch;
import relics.HushsDoor;
import rewards.BlackHeart;
import rewards.SoulHeart;

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
            MonsterGroup minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);
            for (AbstractMonster m : minions.monsters) {
                if (m instanceof SatanicBibleWisp) {
                    chanceToDevil[0] += 1;
                }
            }
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

    @SpirePatch(
            clz = HushsDoor.class,
            method = "onVictory"
    )
    public static class PatchOnVictory {
        @SpireInsertPatch(rloc = 9)
        public static SpireReturn<Void> Insert(HushsDoor hushsDoor) {
            int rnd = AbstractDungeon.aiRng.random(0, 99);
            if (rnd < 15) {
                AbstractDungeon.getCurrRoom().rewards.add(new SoulHeart());
            } else if (rnd < 20) {
                AbstractDungeon.getCurrRoom().rewards.add(new BlackHeart());
            }
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = HushsDoor.class,
            method = "onPlayerEndTurn"
    )
    public static class PatchOnPlayerEndTurn {
        public static SpireReturn<Void> Prefix(HushsDoor hushsDoor) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p.currentHealth <= 0) {
                p.currentHealth = 0;
                SuicideKingPatch.usingSuicideKing = true;
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, new DamageInfo(p, 0, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.NONE));
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        SuicideKingPatch.usingSuicideKing = false;
                        isDone = true;
                    }
                });
            }
            return SpireReturn.Return(null);
        }
    }
}

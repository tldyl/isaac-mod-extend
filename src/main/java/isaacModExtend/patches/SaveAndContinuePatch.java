package isaacModExtend.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.shop.ShopScreen;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.monsters.pet.SoulWisp;
import isaacModExtend.relics.BookOfVirtues;
import patches.player.PlayerAddFieldsPatch;

@SuppressWarnings("unused")
public class SaveAndContinuePatch {
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "deleteSave"
    )
    public static class PatchDeleteSave {
        public static void Prefix(AbstractPlayer p) {
            HushsDoorPatch.PatchUpdate.chanceToAngel = 50;
            HushsDoorPatch.PatchUpdate.dealWithDevil = false;
            for (SoulWisp wisp : BookOfVirtues.soulWisps) {
                BaseMod.unsubscribe(wisp);
            }
            for (int i=0;i<SoulWisp.wispAmount;i++) {
                SoulWisp.wispAlive[i] = false;
            }
            BookOfVirtues.soulWisps.clear();
            MonsterGroup minions = PlayerAddFieldsPatch.f_minions.get(p);
            for (AbstractMonster m : minions.monsters) {
                m.dispose();
            }
            minions.monsters.clear();
            if (!AbstractDungeon.shopScreen.getClass().getSimpleName().equals("ShopScreen")) {
                AbstractDungeon.shopScreen = new ShopScreen();
            }
            IsaacModExtend.initPlanetariumRelics();
            IsaacModExtend.initAngelOnlyRelics();
        }
    }
}

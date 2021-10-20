package isaacModExtend.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.shop.ShopScreen;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.monsters.pet.SoulWisp;
import isaacModExtend.relics.BookOfVirtues;
import patches.player.PlayerAddFieldsPatch;

import java.io.*;

@SuppressWarnings("unused")
public class SaveAndContinuePatch {
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "save"
    )
    public static class PatchSave {
        public static void Postfix(SaveFile save) {
            String filepath = AbstractDungeon.player.getSaveFilePath();
            if (Settings.isBeta) {
                filepath = filepath + "BETA";
            }
            File saveFile = new File(filepath);
            File rewindFile = new File(filepath + "REWIND");
            if (rewindFile.exists()) {
                rewindFile.delete();
            }
            try {
                rewindFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            copyFile(saveFile, rewindFile);
        }
    }

    public static boolean copyFile(File src, File dst) {
        if (src.exists()) {
            byte[] buf = new byte[512];
            try (InputStream is = new FileInputStream(src);
                 OutputStream os = new FileOutputStream(dst)) {
                int len;
                while ((len = is.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

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

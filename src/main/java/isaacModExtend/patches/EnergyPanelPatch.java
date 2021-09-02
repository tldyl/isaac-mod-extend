package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import isaacModExtend.relics.Birthright;
import isaacModExtend.relics.birthrightRelics.DefectBirthrightRelic;

@SuppressWarnings("unused")
public class EnergyPanelPatch {
    @SpirePatch(
            clz = EnergyPanel.class,
            method = "addEnergy"
    )
    public static class PatchAddEnergy {
        public static void Prefix(int e) {
            if (e > 0) {
                DefectBirthrightRelic.energyRecharge = false;
                if (AbstractDungeon.player.hasRelic(Birthright.ID)) {
                    AbstractDungeon.player.getRelic(Birthright.ID).onEnergyRecharge();
                }
            }
        }
    }
}

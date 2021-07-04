package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import isaacModExtend.interfaces.NeutralCreature;

@SuppressWarnings("unused")
public class DamageAllEnemiesPatch {
    @SpirePatch(
            clz = DamageAllEnemiesAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 27)
        public static void Insert1(DamageAllEnemiesAction action) {
            boolean playMusic = true;
            if (action.damage.length == 0) return;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof NeutralCreature) {
                    NeutralCreature neutralCreature = (NeutralCreature) relic;
                    if (neutralCreature.isEnemy()) {
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(neutralCreature.getInstance().hb.cX, neutralCreature.getInstance().hb.cY, action.attackEffect, playMusic));
                        playMusic = false;
                    }
                }
            }
        }

        @SpireInsertPatch(rloc = 51)
        public static void Insert2(DamageAllEnemiesAction action) {
            if (action.damage.length == 0) return;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof NeutralCreature) {
                    NeutralCreature neutralCreature = (NeutralCreature) relic;
                    if (neutralCreature.isEnemy()) {
                        int damage = action.damage[0];
                        neutralCreature.getInstance().damage(new DamageInfo(action.source, damage, action.damageType));
                    }
                }
            }
        }
    }
}

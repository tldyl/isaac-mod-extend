package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.interfaces.NeutralCreature;
import patches.ui.SoulHeartPatch;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class AbstractPlayerPatch {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "updateSingleTargetInput"
    )
    public static class PatchUpdateSingleTargetInput {
        @SpireInsertPatch(rloc = 26)
        public static void Insert(AbstractPlayer p) {
            for (AbstractRelic relic : p.relics) {
                if (relic instanceof NeutralCreature) {
                    NeutralCreature neutralCreature = (NeutralCreature) relic;
                    AbstractCreature creature = neutralCreature.getInstance();
                    if (creature != null && neutralCreature.isEnemy()) {
                        creature.hb.update();
                        if (creature.hb.hovered) {
                            try {
                                Field field = AbstractPlayer.class.getDeclaredField("hoveredMonster");
                                field.setAccessible(true);
                                field.set(p, creature);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage"
    )
    public static class PatchDamage {
        @SpireInsertPatch(rloc = 85, localvars = {"damageAmount"})
        public static void Insert(AbstractPlayer p, DamageInfo info, @ByRef int[] damageAmount) {
            if (info.owner == null || AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT ||
                    (info.owner != null && info.owner.powers.size() == 0 && info.owner != p)) {
                if (SoulHeartPatch.blackHeart > 0) {
                    if ((info.owner != null && info.owner.powers.size() == 0 && info.owner != p) || AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                        if (damageAmount[0] > SoulHeartPatch.blackHeart % 10) {
                            SoulHeartPatch.blackHeart = SoulHeartPatch.blackHeart <= damageAmount[0] ? 0 : SoulHeartPatch.blackHeart - damageAmount[0];
                            if (AbstractDungeon.getMonsters() != null) {
                                AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(40, false), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));
                            }
                        } else {
                            SoulHeartPatch.blackHeart -= damageAmount[0];
                        }
                    } else {
                        SoulHeartPatch.blackHeart = SoulHeartPatch.blackHeart <= damageAmount[0] ? 0 : SoulHeartPatch.blackHeart - damageAmount[0];
                    }
                    damageAmount[0] = 0;
                } else if (SoulHeartPatch.soulHeart > 0) {
                    SoulHeartPatch.soulHeart = SoulHeartPatch.soulHeart <= damageAmount[0] ? 0 : SoulHeartPatch.soulHeart - damageAmount[0];
                    damageAmount[0] = 0;
                }
            }
        }
    }
}

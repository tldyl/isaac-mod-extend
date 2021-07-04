package isaacModExtend.patches;

import cards.tempCards.EpicFetusAttack;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

@SuppressWarnings("unused")
public class EpicFetusAttackPatch {
    @SpirePatch(
            clz = EpicFetusAttack.class,
            method = "use"
    )
    public static class PatchUse {
        public static SpireReturn<Void> Prefix(EpicFetusAttack card, AbstractPlayer p, AbstractMonster m) {
            if (m != null) {
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new WeightyImpactEffect(m.hb.cX, m.hb.cY)));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.8F));
            }
            int[] damage = DamageInfo.createDamageMatrix(card.damage, false);
            for (int i=0;i<AbstractDungeon.getCurrRoom().monsters.monsters.size();i++) {
                AbstractMonster monster = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
                if (!monster.isDeadOrEscaped()) {
                    if (monster != m) {
                        damage[i] = Math.round(damage[i] / 2.0F);
                    }
                }
            }
            AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, damage, card.damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE, true));
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = EpicFetusAttack.class,
            method = "upgrade"
    )
    public static class PatchUpgrade {
        public static void Postfix(EpicFetusAttack card) {
            card.upgraded = false;
        }
    }
}

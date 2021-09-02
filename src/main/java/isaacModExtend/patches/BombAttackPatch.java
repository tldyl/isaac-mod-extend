package isaacModExtend.patches;

import cards.Bomb;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class BombAttackPatch {
    @SpirePatch(
            clz = Bomb.class,
            method = "use"
    )
    public static class PatchUse {
        public static SpireReturn<Void> Prefix(AbstractCard card, AbstractPlayer p, AbstractMonster m) {
            AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, card.multiDamage, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));
            try {
                Field field = Bomb.class.getDeclaredField("used");
                field.setAccessible(true);
                field.setBoolean(card, true);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return SpireReturn.Return(null);
        }
    }
}

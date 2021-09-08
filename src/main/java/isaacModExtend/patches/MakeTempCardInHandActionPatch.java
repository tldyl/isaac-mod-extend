package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Defect;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.relics.Birthright;

import static isaacModExtend.patches.MakeTempCardInHandActionPatch.PatchConstructor.Prefix;

@SuppressWarnings("unused")
public class MakeTempCardInHandActionPatch {
    @SpirePatch(
            clz = MakeTempCardInHandAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractCard.class,
                    boolean.class
            }
    )
    @SpirePatch(
            clz = MakeTempCardInHandAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractCard.class,
                    int.class
            }
    )
    public static class PatchConstructor {
        public static void Prefix(MakeTempCardInHandAction action, AbstractCard card) {
            AbstractPlayer p = AbstractDungeon.player;
            if (card.type == AbstractCard.CardType.POWER && p instanceof Defect && p.hasRelic(Birthright.ID)) {
                AbstractRelic relic = p.getRelic(Birthright.ID);
                relic.onCardDraw(card);
            }
        }
    }

    @SpirePatch(
            clz = DiscoveryAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 26, localvars = {"disCard"})
        public static void Insert(DiscoveryAction action, @ByRef(type = "cards.AbstractCard") AbstractCard[] _disCard) {
            AbstractCard card = _disCard[0];
            Prefix(null, card);
        }
    }
}

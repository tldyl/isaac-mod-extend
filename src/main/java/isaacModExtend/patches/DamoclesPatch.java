package isaacModExtend.patches;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import mymod.IsaacMod;
import relics.Damocles;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DamoclesPatch {
    @SpirePatch(
            clz = Damocles.class,
            method = SpirePatch.CLASS
    )
    public static class AddField implements CustomSavable<List<Boolean>> {
        public static SpireField<Boolean> dieNextTime = new SpireField<>(() -> false);

        private AddField() {
            BaseMod.addSaveField("dieNextTime", this);
        }

        @Override
        public List<Boolean> onSave() {
            if (AbstractDungeon.player.getRelic(Damocles.ID) == null) {
                return new ArrayList<>();
            }
            List<Boolean> ret = new ArrayList<>();
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(Damocles.ID)) {
                    ret.add(dieNextTime.get(relic));
                }
            }
            return ret;
        }

        @Override
        public void onLoad(List<Boolean> dieNextTimeList) {
            if (AbstractDungeon.player.getRelic(Damocles.ID) == null) {
                return;
            }
            if (dieNextTimeList != null) {
                int index = 0;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic.relicId.equals(Damocles.ID)) {
                        AddField.dieNextTime.set(relic, dieNextTimeList.get(index));
                        if (dieNextTimeList.get(index)) {
                            relic.beginLongPulse();
                        }
                        index++;
                    }
                    if (index >= dieNextTimeList.size()) break;
                }
            }
        }

        static {
            new AddField();
        }
    }

    @SpirePatch(
            clz = Damocles.class,
            method = "onLoseHp"
    )
    public static class PatchOnLoseHp {
        public static SpireReturn<Void> Prefix(Damocles relic, int damageAmount) {
            return SpireReturn.Return();
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "onAttacked"
    )
    public static class PatchOnAttacked {
        public static SpireReturn<Integer> Prefix(AbstractRelic relic, DamageInfo info, int damageAmount) {
            if (!relic.relicId.equals(Damocles.ID) || AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT || damageAmount <= 0) {
                return SpireReturn.Continue();
            }
            if (info.owner != null && info.owner != AbstractDungeon.player) {
                double chance = (double) relic.counter / 100.0D;
                if (AddField.dieNextTime.get(relic)) {
                    AbstractPlayer p = AbstractDungeon.player;
                    p.currentHealth = 0;
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, new DamageInfo(p, 1, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                    IsaacMod.removeRelics.add("Damocles");
                    IsaacModExtend.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, relic));
                }
                if (AbstractDungeon.eventRng.randomBoolean((float) chance)) {
                    AddField.dieNextTime.set(relic, true);
                    relic.beginLongPulse();
                }
                relic.setCounter(relic.counter + 4);
            }
            return SpireReturn.Return(damageAmount);
        }
    }

    @SpirePatch(
            clz = Damocles.class,
            method = "getUpdatedDescription"
    )
    public static class PatchGetUpdatedDescription {
        public static SpireReturn<String> Prefix(Damocles relic) {
            return SpireReturn.Return(CardCrawlGame.languagePack.getRelicStrings(IsaacModExtend.makeID(Damocles.ID)).DESCRIPTIONS[0]);
        }
    }
}

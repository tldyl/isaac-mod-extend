package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import events.HidenRoomEvent;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.Larynx;
import mymod.IsaacMod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class HiddenRoomEventPatch {
    public static final String[] OPTIONS;
    private static final EventStrings eventStrings;
    public static final String[] DESCRIPTIONS;
    private static final String DIALOG_2;
    private static final String DIALOG_3;

    @SpirePatch(
            clz = HidenRoomEvent.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        @SpireInsertPatch(rloc = 20)
        public static void Insert(HidenRoomEvent event) {
            if (AbstractDungeon.player.hasRelic(Larynx.ID)) {
                event.imageEventText.setDialogOption(OPTIONS[0]);
            } else {
                event.imageEventText.setDialogOption(OPTIONS[1], true);
            }
        }
    }

    @SpirePatch(
            clz = HidenRoomEvent.class,
            method = "buttonEffect"
    )
    public static class PatchButtonEffect {
        public static SpireReturn<Void> Prefix(HidenRoomEvent event, int buttonPressed) {
            try {
                Field field = HidenRoomEvent.class.getDeclaredField("screen");
                field.setAccessible(true);
                Enum screen = (Enum) field.get(event);
                int gold;
                Method method;
                switch (screen.name()) {
                    case "INTRO":
                        switch (buttonPressed) {
                            case 3:
                                AbstractRelic relic = AbstractDungeon.player.getRelic(Larynx.ID);
                                if (relic != null) {
                                    relic.counter = 0;
                                }
                                if (AbstractDungeon.eventRng.randomBoolean(0.25F)) {
                                    method = HidenRoomEvent.class.getDeclaredMethod("battle");
                                    method.setAccessible(true);
                                    method.invoke(event);
                                    return SpireReturn.Return();
                                } else {
                                    if (AbstractDungeon.eventRng.randomBoolean(0.85F)) {
                                        event.imageEventText.updateBodyText(DIALOG_2);
                                        if (AbstractDungeon.eventRng.randomBoolean(0.35F)) {
                                            IsaacMod.obtain(AbstractDungeon.player, AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON), false);
                                        } else {
                                            gold = AbstractDungeon.eventRng.random(40, 100);
                                            AbstractDungeon.player.gainGold(gold);

                                            for(int i = 0; i < gold; ++i) {
                                                AbstractDungeon.effectList.add(new GainPennyEffect(AbstractDungeon.player, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, true));
                                            }
                                        }
                                        method = HidenRoomEvent.class.getDeclaredMethod("leave");
                                        method.setAccessible(true);
                                        method.invoke(event);
                                    } else {
                                        event.imageEventText.updateBodyText(DIALOG_3);
                                        method = HidenRoomEvent.class.getDeclaredMethod("leave");
                                        method.setAccessible(true);
                                        method.invoke(event);
                                    }
                                    return SpireReturn.Return();
                                }
                            case 4:
                                AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, 15, DamageInfo.DamageType.HP_LOSS));
                                if (AbstractDungeon.eventRng.randomBoolean(0.25F)) {
                                    method = HidenRoomEvent.class.getDeclaredMethod("battle");
                                    method.setAccessible(true);
                                    method.invoke(event);
                                    return SpireReturn.Return();
                                } else {
                                    if (AbstractDungeon.eventRng.randomBoolean(0.85F)) {
                                        event.imageEventText.updateBodyText(DIALOG_2);
                                        if (AbstractDungeon.eventRng.randomBoolean(0.35F)) {
                                            IsaacMod.obtain(AbstractDungeon.player, AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON), false);
                                        } else {
                                            gold = AbstractDungeon.eventRng.random(40, 100);
                                            AbstractDungeon.player.gainGold(gold);

                                            for(int i = 0; i < gold; ++i) {
                                                AbstractDungeon.effectList.add(new GainPennyEffect(AbstractDungeon.player, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, true));
                                            }
                                        }

                                        method = HidenRoomEvent.class.getDeclaredMethod("leave");
                                        method.setAccessible(true);
                                        method.invoke(event);
                                    } else {
                                        event.imageEventText.updateBodyText(DIALOG_3);
                                        method = HidenRoomEvent.class.getDeclaredMethod("leave");
                                        method.setAccessible(true);
                                        method.invoke(event);
                                    }

                                    return SpireReturn.Return();
                                }
                        }
                }
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return SpireReturn.Continue();
        }
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(IsaacModExtend.makeID("HiddenRoomEvent"));
        OPTIONS = eventStrings.OPTIONS;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
    }
}

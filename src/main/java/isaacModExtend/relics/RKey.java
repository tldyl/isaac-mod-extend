package isaacModExtend.relics;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.vfx.GameSavedEffect;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.patches.AbstractDungeonPatch;
import relics.abstracrt.ClickableRelic;

import java.lang.reflect.Field;

public class RKey extends ClickableRelic {
    public static final String ID = IsaacModExtend.makeID("RKey");
    public static final String IMG_PATH = "relics/rKey.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public RKey() {
        super(ID, IMG, RelicTier.BOSS, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onRightClick() {
        if (!AbstractDungeon.player.isDead) {
            AbstractDungeon.resetPlayer();
            AbstractDungeon.player.movePosition(Settings.WIDTH * 0.25F, AbstractDungeon.floorY);
            CardCrawlGame.nextDungeon = "Exordium";
            AbstractDungeon.isDungeonBeaten = true;

            CardCrawlGame.music.fadeOutBGM();
            CardCrawlGame.music.fadeOutTempBGM();
            AbstractDungeon.fadeOut();
            AbstractDungeon.topLevelEffects.clear();
            AbstractDungeon.actionManager.actions.clear();
            AbstractDungeon.effectList.clear();
            AbstractDungeon.effectsQueue.clear();
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.floorNum = 1;
            AbstractDungeon.actNum = 0;
            AbstractDungeon.id = Exordium.ID;
            AbstractDungeon.player.masterDeck.removeCard(AscendersBane.ID);
            AbstractDungeon.dungeonMapScreen.open(true);
            AbstractDungeonPatch.PatchInitializeRelicList.shouldInitializeRelicList = false;
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractDungeon.player.relics.remove(RKey.this);
                    AbstractDungeon.player.reorganizeRelics();
                    SaveFile saveFile = new SaveFile(SaveFile.SaveType.ENTER_ROOM);
                    SaveAndContinue.save(saveFile);
                    AbstractDungeon.effectList.add(new GameSavedEffect());
                    AbstractDungeon.bossRelicPool.remove(RKey.ID);
                    IsaacModExtend.removeRelicFromVoid(RKey.this);
                    isDone = true;
                }
            });

            if (Loader.isModLoaded("actlikeit")) {
                try {
                    Class<?> cls = Class.forName("actlikeit.savefields.BehindTheScenesActNum");
                    Field field = cls.getDeclaredField("bc");
                    field.setAccessible(true);
                    Object bc = field.get(null);
                    field = cls.getDeclaredField("actNum");
                    field.setAccessible(true);
                    field.set(bc, 0);
                } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

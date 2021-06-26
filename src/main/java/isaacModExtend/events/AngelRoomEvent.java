package isaacModExtend.events;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import isaacModExtend.IsaacModExtend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AngelRoomEvent extends AbstractEvent {
    public static final String ID = IsaacModExtend.makeID("AngelRoomEvent");
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;

    private int screenNum = 0;
    private List<AbstractRelic> relics = new ArrayList<>();
    private int choices;
    private static TextureAtlas.AtlasRegion roomBg = new TextureAtlas.AtlasRegion(new Texture(IsaacModExtend.getResourcePath("events/angelRoomBg.png")),0 , 0, 1920, 1080);

    public AngelRoomEvent() {
        this.body = DESCRIPTIONS[0];
        RoomEventDialog.optionList.clear();
        choices = AbstractDungeon.eventRng.random(1, 4);
        List<AbstractRelic> relics1 = new ArrayList<>(IsaacModExtend.angelOnlyRelics);
        List<AbstractRelic> toRemove = new ArrayList<>();
        for (AbstractRelic relic : relics1) {
            if (AbstractDungeon.player.hasRelic(relic.relicId)) {
                toRemove.add(relic);
            }
        }
        relics1.removeAll(toRemove);
        relics1 = IsaacModExtend.getRandomAngelOnlyRelics(AbstractDungeon.miscRng, choices, relics1);
        for (int i=0;i<choices;i++) {
            AbstractRelic relic;
            if (i < relics1.size()) {
                relic = relics1.get(i);
            } else {
                relic = new Circlet();
            }
            relics.add(relic);
            this.roomEventText.addDialogOption(OPTIONS[0].replace("!R!", relic.name.replace(" ", " #g")), relic);
        }
        this.roomEventText.addDialogOption(OPTIONS[1]);
        this.hasDialog = true;
        this.hasFocus = true;
    }

    @Override
    public void onEnterRoom() {
        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.playTempBGM("ANGEL_ROOM");
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                boolean obtainedRelic = false;
                for (int i=0;i<this.choices;i++) {
                    if (i == buttonPressed) {
                        AbstractRelic relic = relics.get(i).makeCopy();
                        relic.spawn(Settings.WIDTH / 2, Settings.HEIGHT / 2);
                        relic.obtain();
                        relic.isObtained = true;
                        AbstractDungeon.commonRelicPool.remove(relic.relicId);
                        AbstractDungeon.uncommonRelicPool.remove(relic.relicId);
                        AbstractDungeon.rareRelicPool.remove(relic.relicId);
                        AbstractDungeon.bossRelicPool.remove(relic.relicId);
                        AbstractDungeon.shopRelicPool.remove(relic.relicId);
                        this.roomEventText.updateBodyText(DESCRIPTIONS[1]);
                        obtainedRelic = true;
                        break;
                    }
                }
                if (!obtainedRelic) {
                    screenNum = 1;
                    this.roomEventText.updateBodyText(DESCRIPTIONS[2]);
                }
                RoomEventDialog.optionList.clear();
                this.roomEventText.addDialogOption(OPTIONS[1]);
                screenNum = 1;
                break;
            case 1:
            default:
                if (Loader.isModLoaded("actlikeit")) {
                    try {
                        Constructor constructor = Class.forName("actlikeit.events.GetForked").getConstructor(boolean.class);
                        AbstractEvent getForked = (AbstractEvent) constructor.newInstance(false);
                        AbstractRoom room = new EventRoom() {
                            @Override
                            public void onPlayerEntry() {
                                AbstractDungeon.overlayMenu.proceedButton.hide();
                                event = getForked;
                                event.onEnterRoom();
                            }
                        };
                        AbstractDungeon.currMapNode.setRoom(room);
                        room.onPlayerEntry();
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    CardCrawlGame.music.fadeOutBGM();
                    CardCrawlGame.music.fadeOutTempBGM();
                    AbstractDungeon.fadeOut();
                    AbstractDungeon.isDungeonBeaten = true;
                    AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
                    AbstractDungeon.currMapNode.room = new EmptyRoom();
                }
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        sb.setColor(1, 1, 1, 1);
        sb.draw(roomBg.getTexture(), 0, 0, 0.0F, 0.0F, (float) roomBg.packedWidth, (float) roomBg.packedHeight, Settings.WIDTH / 1920.0F, Settings.HEIGHT / 1080.0F, 0.0F, roomBg.getRegionX(), roomBg.getRegionY(), roomBg.getRegionWidth(), roomBg.getRegionHeight(), false, false);
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
    }
}

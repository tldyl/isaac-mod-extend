package isaacModExtend.events;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;

public class Planetarium extends AbstractEvent {
    public static final String ID = IsaacModExtend.makeID("Planetarium");
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;

    private int screenNum = 0;
    private static TextureAtlas.AtlasRegion roomBg = new TextureAtlas.AtlasRegion(new Texture(IsaacModExtend.getResourcePath("events/planetariumBg.png")),0 , 0, 1920, 1080);

    public Planetarium() {
        this.body = DESCRIPTIONS[0];
        this.roomEventText.clear();
        this.roomEventText.addDialogOption(OPTIONS[0]);
        this.roomEventText.addDialogOption(OPTIONS[1]);
        this.hasDialog = true;
        this.hasFocus = true;
    }

    @Override
    public void onEnterRoom() {
        CardCrawlGame.music.playTempBGM("PLANETARIUM");
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        screenNum = 1;
                        AbstractRelic relic = IsaacModExtend.getRandomPlanetariumRelic(AbstractDungeon.miscRng);
                        relic.spawn(Settings.WIDTH / 2, Settings.HEIGHT / 2);
                        relic.obtain();
                        relic.isObtained = true;
                        this.roomEventText.updateBodyText(DESCRIPTIONS[1]);
                        break;
                    case 1:
                        screenNum = 1;
                        this.roomEventText.updateBodyText(DESCRIPTIONS[2]);
                        break;
                }
                this.roomEventText.updateDialogOption(0, OPTIONS[1]);
                this.roomEventText.removeDialogOption(1);
                break;
            case 1:
            default:
                this.openMap();
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

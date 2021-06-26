package isaacModExtend;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.IncenseBurner;
import isaacModExtend.events.Planetarium;
import isaacModExtend.relics.*;
import relics.Habit;
import relics.TheBible;
import relics.Void;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpireInitializer
public class IsaacModExtend implements EditStringsSubscriber,
                                       AddAudioSubscriber,
                                       PostInitializeSubscriber,
                                       EditKeywordsSubscriber,
                                       PostUpdateSubscriber {

    private static List<AbstractGameAction> actionList = new ArrayList<>();
    private static List<AbstractRelic> planetariumRelics = new ArrayList<>();
    private static SpriteBatch sb;
    public static final List<AbstractRelic> angelOnlyRelics = new ArrayList<>();

    public static void initialize() {
        new IsaacModExtend();
    }

    public static String makeID(String name) {
        return "IsaacExt:" + name;
    }

    public static String getResourcePath(String resource) {
        return "IsaacImages/" + resource;
    }

    public static String getLanguageString() {
        String language;
        switch (Settings.language) {
            case ZHS:
                language = "zhs";
                break;
            default:
                language = "eng";
        }
        return language;
    }

    private IsaacModExtend() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receiveEditStrings() {
        String language = getLanguageString();
        String relicStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-RelicStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        String powerStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-PowerStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
        String monsterStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-MonsterStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(MonsterStrings.class, monsterStrings);
        String eventStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-EventStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio("RELIC_BLOOD_PUPPY_RUSH_2", "IsaacAudio/sfx/relic_blood_puppy_rush_2.wav");
        BaseMod.addAudio("RELIC_BLOOD_PUPPY_RUSH_3", "IsaacAudio/sfx/relic_blood_puppy_rush_3.wav");
        BaseMod.addAudio("RELIC_MARS_RUSH_1", "IsaacAudio/sfx/relic_mars_rush_1.wav");
        BaseMod.addAudio("RELIC_MARS_RUSH_2", "IsaacAudio/sfx/relic_mars_rush_2.wav");
        BaseMod.addAudio("RELIC_SUPLEX_RUSH", "IsaacAudio/sfx/relic_suplex_rush.wav");
        BaseMod.addAudio("RELIC_SUPLEX_LIFT", "IsaacAudio/sfx/relic_suplex_lift.wav");
        BaseMod.addAudio("RELIC_SUPLEX_SMASH", "IsaacAudio/sfx/relic_suplex_smash.wav");
        BaseMod.addAudio("RELIC_LARYNX_SHOUT_1", "IsaacAudio/sfx/relic_larynx_shout_1.wav");
        BaseMod.addAudio("RELIC_LARYNX_SHOUT_2", "IsaacAudio/sfx/relic_larynx_shout_2.wav");
        BaseMod.addAudio("RELIC_LARYNX_SHOUT_3", "IsaacAudio/sfx/relic_larynx_shout_3.wav");
        BaseMod.addAudio("RELIC_BLOOD_OATH_STAB", "IsaacAudio/sfx/relic_blood_oath_stab.wav");
        BaseMod.addAudio("RELIC_RED_KEY", "IsaacAudio/sfx/relic_red_key.wav");
        BaseMod.addAudio("RELIC_REVELATION", "IsaacAudio/sfx/relic_revelation.wav");
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addEvent(Planetarium.ID, Planetarium.class);

        planetariumRelics.add(new Sol());
        planetariumRelics.add(new Luna());
        planetariumRelics.add(new Mercurius());
        planetariumRelics.add(new Venus());
        planetariumRelics.add(new Terra());
        planetariumRelics.add(new Mars());
        planetariumRelics.add(new Jupiter());

        planetariumRelics.add(new Neptunus());
        try {
            Field field = CardCrawlGame.class.getDeclaredField("sb");
            field.setAccessible(true);
            sb = (SpriteBatch) field.get(Gdx.app.getApplicationListener());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        angelOnlyRelics.add(new Void());
        angelOnlyRelics.add(new TheBible());
        angelOnlyRelics.add(new TheWafer());
        angelOnlyRelics.add(new Habit());
        angelOnlyRelics.add(new IncenseBurner());
        angelOnlyRelics.add(new TheRelic());
        angelOnlyRelics.add(new SacredHeart());
        angelOnlyRelics.add(new HolyGrail());
        angelOnlyRelics.add(new TheBody());
        angelOnlyRelics.add(new CrownOfLight());
        angelOnlyRelics.add(new Revelation());
        angelOnlyRelics.add(new HolyMantle());
        angelOnlyRelics.add(new TheStairway());
    }

    public static AbstractRelic getRandomPlanetariumRelic(Random rng) {
        return planetariumRelics.get(rng.random(planetariumRelics.size() - 1)).makeCopy();
    }

    public static List<AbstractRelic> getRandomAngelOnlyRelics(Random rng, int amount, List<AbstractRelic> relics) {
        if (amount >= relics.size()) {
            return new ArrayList<>(relics);
        } else {
            List<AbstractRelic> ret = new ArrayList<>();
            List<AbstractRelic> cpy = new ArrayList<>(relics);
            for (int i=0;i<amount;i++) {
                ret.add(cpy.remove(rng.random(cpy.size() - 1)));
            }
            return ret;
        }
    }

    @Override
    public void receiveEditKeywords() {
        final Gson gson = new Gson();
        String language;
        language = getLanguageString();
        final String json = Gdx.files.internal("localization/" + language + "/IsaacExt-KeywordStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    public static void addToBot(AbstractGameAction action) {
        actionList.add(action);
    }

    public static void addToTop(AbstractGameAction action) {
        actionList.add(0, action);
    }

    @Override
    public void receivePostUpdate() {
        if (actionList.size() > 0) {
            actionList.get(0).update();
            if (actionList.get(0).isDone) {
                actionList.remove(0);
            }
        }
    }

    public static Texture getPlayerSnapshot(FrameBuffer frameBuffer) {
        frameBuffer.begin();
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(16384);
        if (sb != null && AbstractDungeon.player != null) {
            sb.begin();
            boolean t = Settings.hideCombatElements;
            Settings.hideCombatElements = true;
            AbstractDungeon.player.render(sb);
            Settings.hideCombatElements = t;
            sb.end();
        }
        frameBuffer.end();
        return frameBuffer.getColorBufferTexture();
    }
}

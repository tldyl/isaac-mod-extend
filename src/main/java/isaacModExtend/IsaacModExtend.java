package isaacModExtend;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.AbstractDailyMod;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.IncenseBurner;
import helpers.MinionHelper;
import isaacModExtend.cards.Guilt;
import isaacModExtend.daily.mods.Challenge45;
import isaacModExtend.events.Planetarium;
import isaacModExtend.monsters.BabyPlum;
import isaacModExtend.relics.*;
import monsters.Monstro;
import patches.player.PlayerAddFieldsPatch;
import relics.Habit;
import relics.TheBible;
import relics.Void;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpireInitializer
public class IsaacModExtend implements EditStringsSubscriber,
                                       AddAudioSubscriber,
                                       PostInitializeSubscriber,
                                       EditKeywordsSubscriber,
                                       PostUpdateSubscriber,
                                       PostRenderSubscriber,
                                       EditCardsSubscriber,
                                       PostDungeonInitializeSubscriber{

    private static List<AbstractGameAction> actionList = new ArrayList<>();
    private static List<AbstractRelic> planetariumRelics = new ArrayList<>();
    private static SpriteBatch sb;
    private static boolean enableMonstro = true;
    public static boolean isPlumFluteUnlocked = false;
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
        String cardStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-CardStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
        String powerStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-PowerStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
        String monsterStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-MonsterStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(MonsterStrings.class, monsterStrings);
        String eventStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-EventStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
        String uiStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-UIStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
        String runModStrings = Gdx.files.internal("localization/" + language + "/IsaacExt-RunModStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RunModStrings.class, runModStrings);
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
        BaseMod.addAudio("SOUL_WISP_IGNITE", "IsaacAudio/sfx/soul_wisp_ignite.wav");
        BaseMod.addAudio("SOUL_WISP_EXTINCT", "IsaacAudio/sfx/soul_wisp_extinct.wav");
        BaseMod.addAudio("BOSS_BABY_PLUM_DEATH", "IsaacAudio/sfx/boss_baby_plum_death.wav");
        BaseMod.addAudio("MEAT_JUMPS_0", "IsaacAudio/sfx/meat_jumps_0.wav");
        BaseMod.addAudio("BOSS_BABY_PLUM_BUBBLE_LOOP", "IsaacAudio/sfx/boss_baby_plum_bubble_loop.wav");
        BaseMod.addAudio("RELIC_PLUM_FLUTE", "IsaacAudio/sfx/relic_plum_flute.wav");
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
        angelOnlyRelics.add(new BookOfVirtues());

        BaseMod.addMonster("Monstro" , Monstro.NAME, () -> new MonsterGroup(new AbstractMonster[]{
                new Monstro(-50, 0)
        }));
        BaseMod.addMonster("2_Monstro" , Monstro.NAME, () -> new MonsterGroup(new AbstractMonster[]{
                new Monstro(-150.0F, 0.0F),
                new Monstro(150.0F, 0.0F, 1)
        }));
        BaseMod.addMonster("4_Monstro" , Monstro.NAME, () -> new MonsterGroup(new AbstractMonster[]{
                new Monstro(-300.0F, 0.0F, 0.5F),
                new Monstro(-100.0F, 0.0F, 0.5F, 1),
                new Monstro(100F, 0.0F, 0.5F),
                new Monstro(300.0F, 0.0F, 0.5F, 1)
        }));
        BaseMod.addMonster("BabyPlum", BabyPlum.NAME, () -> new MonsterGroup(new AbstractMonster[]{
                new BabyPlum(-50, 0)
        }));
        loadSettings();
        if (enableMonstro) {
            BaseMod.addBoss(Exordium.ID, "Monstro", getResourcePath("map/monstro.png"), getResourcePath("map/monstroOutline.png"));
            BaseMod.addBoss(TheCity.ID, "2_Monstro", getResourcePath("map/monstro.png"), getResourcePath("map/monstroOutline.png"));
            BaseMod.addBoss(TheBeyond.ID, "4_Monstro", getResourcePath("map/monstro.png"), getResourcePath("map/monstroOutline.png"));
        }
        BaseMod.addBoss(Exordium.ID, "BabyPlum", getResourcePath("map/babyPlum.png"), getResourcePath("map/babyPlumOutline.png"));
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModPanel"));
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton enableMonstroOption = new ModLabeledToggleButton(uiStrings.TEXT[0], 350.0F, 700.0F, Color.WHITE, FontHelper.buttonLabelFont, enableMonstro, settingsPanel, (me) -> {},
            (me) -> {
                enableMonstro = me.enabled;
                saveSettings();
            }
        );
        settingsPanel.addUIElement(enableMonstroOption);
        BaseMod.registerModBadge(ImageMaster.loadImage(getResourcePath("ui/badge.png")), "IsaacMod Extend", "Everyone", "TODO", settingsPanel);
        try {
            Field field = ModHelper.class.getDeclaredField("starterMods");
            field.setAccessible(true);
            HashMap<String, AbstractDailyMod> mods = (HashMap<String, AbstractDailyMod>) field.get(null);
            mods.put(Challenge45.ID, new Challenge45());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        try {
            SpireConfig config = new SpireConfig("IsaacModExtend", "settings");
            config.setBool("enableMonstro", enableMonstro);
            config.setBool("isPlumFluteUnlocked", isPlumFluteUnlocked);
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadSettings() {
        try {
            SpireConfig config = new SpireConfig("IsaacModExtend", "settings");
            config.load();
            if (config.has("enableMonstro")) {
                enableMonstro = config.getBool("enableMonstro");
            }
            if (config.has("isPlumFluteUnlocked")) {
                isPlumFluteUnlocked = config.getBool("isPlumFluteUnlocked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            while (ret.size() < amount) {
                AbstractRelic relic;
                if (cpy.size() > 0) {
                    relic = cpy.remove(rng.random(cpy.size() - 1));
                } else {
                    relic = new Circlet();
                }
                if (relic.canSpawn()) ret.add(relic);
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

    public static Texture getCreatureSnapshot(AbstractCreature creature, FrameBuffer frameBuffer) {
        frameBuffer.begin();
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(16384);
        if (sb != null && creature != null) {
            sb.begin();
            boolean t = Settings.hideCombatElements;
            Settings.hideCombatElements = true;
            creature.render(sb);
            Settings.hideCombatElements = t;
            sb.end();
        }
        frameBuffer.end();
        return frameBuffer.getColorBufferTexture();
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.player != null) {
            MonsterGroup minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);
            switch (AbstractDungeon.getCurrRoom().phase) {
                case COMBAT:
                    if (MinionHelper.hasMinions(AbstractDungeon.player)) {
                        minions.render(sb);
                    }
            }
        }
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new Guilt());
    }

    @Override
    public void receivePostDungeonInitialize() {
        if (ModHelper.isModEnabled("IsaacExt:Challenge45")) {
            new TMTrainer().instantObtain(AbstractDungeon.player, 0, true);
            AbstractRelic relic = AbstractDungeon.player.relics.get(0);
            AbstractDungeon.player.relics.set(0, AbstractDungeon.player.relics.get(AbstractDungeon.player.relics.size() - 1));
            AbstractDungeon.player.relics.set(AbstractDungeon.player.relics.size() - 1, relic);
            AbstractDungeon.player.reorganizeRelics();
        }
    }
}

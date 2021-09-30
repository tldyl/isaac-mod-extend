package isaacModExtend.relics;

import basemod.abstracts.CustomBottleRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.patches.ChaosPatch;
import relics.abstracrt.ChargeableRelic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Metronome extends ChargeableRelic implements CustomSavable<String> {
    public static final String ID = IsaacModExtend.makeID("Metronome");
    public static final String IMG_PATH = "relics/metronome.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    private AbstractRelic relic;
    private Function<AbstractRelic, Boolean> relicFilter = relic -> {
        if (relic instanceof BottledFlame || relic instanceof BottledLightning || relic instanceof BottledTornado || relic instanceof CustomBottleRelic) {
            return false;
        }
        return !(relic instanceof TinyHouse) &&
                !(relic instanceof CallingBell) &&
                !(relic instanceof Orrery) &&
                !(relic instanceof Cauldron) &&
                !(relic instanceof Metronome);
    };

    public Metronome() {
        super(ID, IMG, RelicTier.COMMON, LandingSound.CLINK, 1);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onRightClick() {
        if (this.isUsable()) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;
            addToBot(new RelicAboveCreatureAction(p, this));
            onEnterRoom(AbstractDungeon.getCurrRoom());
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    if (AbstractDungeon.relicRng.randomBoolean(0.75F)) {
                        RelicTier tier = returnRandomRelicTier();
                        relic = AbstractDungeon.returnRandomRelic(tier);
                        while (!relicFilter.apply(relic)) {
                            relic = AbstractDungeon.returnRandomRelic(tier);
                        }
                    } else {
                        List<String> allRelics = new ArrayList<>(ChaosPatch.PatchOnEquip.getAllRelics());
                        relic = RelicLibrary.getRelic(allRelics.get(AbstractDungeon.relicRng.random(allRelics.size() - 1)));
                        while (!relicFilter.apply(relic)) {
                            relic = RelicLibrary.getRelic(allRelics.get(AbstractDungeon.relicRng.random(allRelics.size() - 1)));
                        }
                        relic = relic.makeCopy();
                    }
                    relic.instantObtain();
                    isDone = true;
                }
            });
            this.resetCharge();
        }
    }

    private RelicTier returnRandomRelicTier() {
        int roll = AbstractDungeon.relicRng.random(0, 99);

        if (roll < 40) {
            return RelicTier.COMMON;
        } else if (roll < 55) {
            return RelicTier.UNCOMMON;
        } else if (roll < 80) {
            return RelicTier.RARE;
        } else if (roll < 85) {
            return RelicTier.BOSS;
        } else {
            return RelicTier.SHOP;
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (relic != null) {
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractDungeon.player.loseRelic(relic.relicId);
                    relic = null;
                    isDone = true;
                }
            });
        }
    }

    @Override
    public String onSave() {
        return relic != null ? relic.relicId : null;
    }

    @Override
    public void onLoad(String s) {
        if (s != null) {
            relic = RelicLibrary.getRelic(s);
        }
    }
}

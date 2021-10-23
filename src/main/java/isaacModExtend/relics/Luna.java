package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import isaacModExtend.IsaacModExtend;
import patches.ui.SoulHeartPatch;

public class Luna extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = IsaacModExtend.makeID("Luna");
    public static final String IMG_PATH = "relics/luna.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private int obtainedActNum;

    public Luna() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.counter = 0;
        obtainedActNum = AbstractDungeon.actNum;
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (room instanceof EventRoom) {
            this.beginLongPulse();
            this.counter++;
            SoulHeartPatch.soulHeart += 5;
        }
    }

    @Override
    public void atBattleStart() {
        if (this.counter > 0) {
            this.flash();
            this.stopPulse();
            AbstractPlayer p = AbstractDungeon.player;
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.counter)));
            this.counter = 0;
        }
    }

    public boolean changeEventRoomChance() {
        return this.obtainedActNum != AbstractDungeon.actNum;
    }

    @Override
    public Integer onSave() {
        return this.obtainedActNum;
    }

    @Override
    public void onLoad(Integer i) {
        if (i != null) {
            this.obtainedActNum = i;
        } else {
            this.obtainedActNum = AbstractDungeon.actNum;
        }
    }
}

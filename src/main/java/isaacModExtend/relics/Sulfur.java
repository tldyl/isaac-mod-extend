package isaacModExtend.relics;

import cards.BloodyBrimstone;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import relics.abstracrt.ChargeableRelic;

public class Sulfur extends ChargeableRelic {
    public static final String ID = IsaacModExtend.makeID("Sulfur");
    public static final String IMG_PATH = "relics/sulfur.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public Sulfur() {
        super(ID, IMG, RelicTier.UNCOMMON, LandingSound.CLINK, 3);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onRightClick() {
        if (this.isUsable()) {
            this.flash();
            addToBot(new MakeTempCardInHandAction(new BloodyBrimstone()));
            resetCharge();
            this.stopPulse();
        }
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }
}

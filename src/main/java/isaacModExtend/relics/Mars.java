package isaacModExtend.relics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.MarsRushAction;
import relics.abstracrt.ChargeableRelic;

public class Mars extends ChargeableRelic {
    public static final String ID = IsaacModExtend.makeID("Mars");
    public static final String IMG_PATH = "relics/mars.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public Mars() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.CLINK, 3);
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
            addToBot(new ApplyPowerAction(p, p, new InvinciblePower(p, 0)));
            addToBot(new SFXAction("RELIC_MARS_RUSH_" + MathUtils.random(1, 2)));
            addToBot(new MarsRushAction());
            addToBot(new RemoveSpecificPowerAction(p, p, InvinciblePower.POWER_ID));
            resetCharge();
            this.stopPulse();
        }
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }
}

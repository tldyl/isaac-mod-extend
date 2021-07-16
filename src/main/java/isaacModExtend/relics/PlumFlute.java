package isaacModExtend.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import helpers.SummonHelper;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.monsters.pet.BabyPlumPet;
import monsters.abstracrt.AbstractPet;
import relics.abstracrt.ChargeableRelic;
import utils.Point;
import utils.Utils;

public class PlumFlute extends ChargeableRelic {
    public static final String ID = IsaacModExtend.makeID("PlumFlute");
    public static final String IMG_PATH = "relics/plumFlute.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public PlumFlute() {
        super(ID, IMG, RelicTier.COMMON, LandingSound.FLAT, 4);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onRightClick() {
        if (isUsable()) {
            this.flash();
            Point center = new Point((double)(AbstractDungeon.player.hb.x - 1200.0F), (double)(AbstractDungeon.player.hb_y + 170.0F));
            double angle = 0.314D;
            Point point = Utils.getCirclePoint(center, angle, 420.0D);
            CardCrawlGame.sound.play("RELIC_PLUM_FLUTE");
            AbstractPet babyPlum = new BabyPlumPet((float) point.x + MathUtils.random(128.0F) * Settings.scale, (float) point.y);
            addToBot(new AbstractGameAction() {
                float duration = 1.5F;

                @Override
                public void update() {
                    duration -= Gdx.graphics.getDeltaTime();
                    if (duration <= 0) {
                        SummonHelper.summonMinion(babyPlum);
                        babyPlum.createIntent();
                        isDone = true;
                    }
                }
            });

            this.resetCharge();
        }
    }

    @Override
    public boolean isUsable() {
        return this.counter >= this.maxCharge && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    @Override
    public boolean canSpawn() {
        return IsaacModExtend.isPlumFluteUnlocked;
    }
}

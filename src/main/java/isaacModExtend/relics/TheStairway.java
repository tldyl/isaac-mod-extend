package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.screens.AngelShopScreen;
import screen.BloodShopScreen;

public class TheStairway extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("TheStairway");
    public static final String IMG_PATH = "relics/theStairway.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public TheStairway() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (room instanceof ShopRoom && !(AbstractDungeon.shopScreen instanceof BloodShopScreen)) {
            if (AbstractDungeon.miscRng.randomBoolean()) {
                this.flash();
                this.beginLongPulse();
                if (!(AbstractDungeon.shopScreen instanceof AngelShopScreen)) AbstractDungeon.shopScreen = new AngelShopScreen();
            } else if (!AbstractDungeon.shopScreen.getClass().getSimpleName().equals("ShopScreen")) {
                AbstractDungeon.shopScreen = new ShopScreen();
                this.stopPulse();
            }
        } else {
            this.stopPulse();
        }
    }
}

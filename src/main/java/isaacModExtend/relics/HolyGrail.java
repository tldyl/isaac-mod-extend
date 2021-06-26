package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import isaacModExtend.IsaacModExtend;
import powers.FlightPower;

public class HolyGrail extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("HolyGrail");
    public static final String IMG_PATH = "relics/holyGrail.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public HolyGrail() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.flash();
        AbstractDungeon.player.increaseMaxHp(7, true);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;
        addToBot(new RelicAboveCreatureAction(p, this));
        addToBot(new ApplyPowerAction(p, p, new FlightPower(p, 1)));
    }

    @Override
    public int getPrice() {
        return 300;
    }
}

package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;

public class RedStew extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("RedStew");
    public static final String IMG_PATH = "relics/redStew.png";

    public RedStew() {
        super(ID, new Texture(IsaacModExtend.getResourcePath(IMG_PATH)),
                RelicTier.SHOP, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.counter = 22;
        AbstractDungeon.player.increaseMaxHp(7, true);
        AbstractDungeon.player.heal(15);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        this.counter -= 2;
        if (this.counter <= 0) {
            this.counter = -2;
            this.usedUp();
        }
    }

    @Override
    public void atBattleStart() {
        if (!this.usedUp) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, this.counter)));
        }
    }

    @Override
    public void onPlayerEndTurn() {
        this.counter -= 2;
        if (!this.usedUp) {
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, -Math.min(2, this.counter))));
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
        if (this.counter <= 0) {
            this.counter = -2;
            this.usedUp();
        }
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        if (!m.hasPower(MinionPower.POWER_ID) && !this.usedUp) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 1)));
            this.counter++;
        }
    }
}

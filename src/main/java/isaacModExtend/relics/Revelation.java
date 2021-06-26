package isaacModExtend.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.effects.RevelationEffect;
import patches.ui.SoulHeartPatch;
import powers.FlightPower;
import relics.abstracrt.ClickableRelic;

public class Revelation extends ClickableRelic {
    public static final String ID = IsaacModExtend.makeID("Revelation");
    public static final String IMG_PATH = "relics/revelation.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public Revelation() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.flash();
        SoulHeartPatch.soulHeart += 20;
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;
        addToBot(new RelicAboveCreatureAction(p, this));
        addToBot(new ApplyPowerAction(p, p, new FlightPower(p, 1)));
    }

    @Override
    public void onRightClick() {
        AbstractPlayer p = AbstractDungeon.player;
        if (EnergyPanel.getCurrentEnergy() > 0 && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.flash();
            p.useFastAttackAnimation();
            AbstractDungeon.effectList.add(new RevelationEffect(p.hb.cX + p.hb.width * 0.5F, p.hb.cY));
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
            CardCrawlGame.sound.play("RELIC_REVELATION");
            for (int i=0;i<15;i++) {
                addToBot(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(EnergyPanel.getCurrentEnergy(), true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE, true));
            }
            EnergyPanel.setEnergy(0);
        }
    }

    @Override
    public int getPrice() {
        return 350;
    }
}

package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SporeCloudPower;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.powers.MucormycosisPower;

public class Mucormycosis extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("Mucormycosis");
    public static final String IMG_PATH = "relics/mucormycosis.png";

    public Mucormycosis() {
        super(ID, new Texture(IsaacModExtend.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster && AbstractDungeon.cardRandomRng.random(1.0F) < 0.25F && info.type == DamageInfo.DamageType.NORMAL) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(target, this));
            if (AbstractDungeon.cardRandomRng.randomBoolean()) {
                addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new MucormycosisPower((AbstractMonster) target, 2)));
            } else {
                addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new SporeCloudPower(target, 1)));
            }
        }
    }
}

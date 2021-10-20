package isaacModExtend.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import isaacModExtend.IsaacModExtend;

import java.util.List;

public class FamiliarBarrierPower extends AbstractPower {
    public static final String POWER_ID = IsaacModExtend.makeID("FamiliarBarrierPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    private List<AbstractMonster> controlledPets;

    public FamiliarBarrierPower(AbstractCreature owner, List<AbstractMonster> controlledPets) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(IsaacModExtend.getResourcePath("powers/BarrageBlockade84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(IsaacModExtend.getResourcePath("powers/BarrageBlockade32.png")), 0, 0, 32, 32);
        this.controlledPets = controlledPets;
        updateDescription();
    }

    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.HP_LOSS) {
            for (AbstractMonster m : controlledPets) {
                if (!m.isDeadOrEscaped()) {
                    this.flash();
                    info.output /= 2;
                    if (damageAmount % 2 != 0) info.output++;
                    m.damage(info);
                    return damageAmount / 2;
                }
            }
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

package isaacModExtend.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.HP_LOSS) {
            for (AbstractMonster m : controlledPets) {
                if (!m.isDeadOrEscaped()) {
                    this.flash();
                    if (AbstractDungeon.ascensionLevel >= 19) {
                        info.output *= 3;
                    }
                    if (damageAmount % 2 != 0) info.output++;
                    m.damage(info);
                    break;
                }
            }
        }
        return damageAmount;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type != DamageInfo.DamageType.HP_LOSS) {
            for (AbstractMonster m : controlledPets) {
                if (!m.isDeadOrEscaped()) {
                    if (AbstractDungeon.ascensionLevel >= 19) {
                        return damage * 0.25F;
                    }
                    return damage / 2;
                }
            }
        }
        return damage;
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], AbstractDungeon.ascensionLevel >= 19 ? 75 : 50);
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

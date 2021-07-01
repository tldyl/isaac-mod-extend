package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import isaacModExtend.IsaacModExtend;
import monsters.Intent.Move;

public class NecronomiconSoulWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("NecronomiconSoulWisp");
    public static final String NAME;

    public NecronomiconSoulWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/necronomiconSoulWisp.png"));
    }

    @Override
    public void die() {
        super.die();
        addToBot(new DamageAllEnemiesAction(this, DamageInfo.createDamageMatrix(40, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, true));
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

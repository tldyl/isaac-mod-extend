package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import isaacModExtend.IsaacModExtend;
import monsters.Intent.Move;

public class TheBibleWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("TheBibleWisp");
    public static final String NAME;

    public TheBibleWisp() {
        super();
        this.name = NAME;
        this.id = ID;
        this.setHp(18);
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/theBibleWisp.png"));
    }

    @Override
    protected void setDamage() {
        this.damage.add(new DamageInfo(this, 14, DamageInfo.DamageType.NORMAL));
        this.setMove((byte)Move.ATTACK.id, Intent.ATTACK, 14);
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

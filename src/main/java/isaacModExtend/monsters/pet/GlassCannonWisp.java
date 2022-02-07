package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import isaacModExtend.IsaacModExtend;
import monsters.Intent.Move;

public class GlassCannonWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("GlassCannonWisp");
    public static final String NAME;

    public GlassCannonWisp() {
        super();
        this.name = NAME;
        this.id = ID;
        this.setHp(1);
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/glassCannonWisp.png"));
    }

    @Override
    protected void setDamage() {
        this.damage.add(new DamageInfo(this, 12, DamageInfo.DamageType.NORMAL));
        this.setMove((byte)Move.ATTACK.id, Intent.ATTACK, 12);
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

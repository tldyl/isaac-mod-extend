package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import isaacModExtend.IsaacModExtend;

public class GuppysPawWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("GuppysPawWisp");
    public static final String NAME;

    public GuppysPawWisp() {
        super();
        this.name = NAME;
        this.id = ID;
        this.setHp(24);
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/guppysPawWisp.png"));
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

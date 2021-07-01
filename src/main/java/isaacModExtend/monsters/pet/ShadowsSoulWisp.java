package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.BufferPower;
import isaacModExtend.IsaacModExtend;

public class ShadowsSoulWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("ShadowsSoulWisp");
    public static final String NAME;

    public ShadowsSoulWisp() {
        super();
        this.setHp(1);
        this.name = NAME;
        this.id = ID;
    }

    protected float getWidth() {
        return 34.0F;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/shadowsSoulWisp.png"));
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new BufferPower(this, 1)));
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

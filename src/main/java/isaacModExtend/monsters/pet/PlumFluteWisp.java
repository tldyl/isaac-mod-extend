package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.powers.BarrageBlockadePower;

public class PlumFluteWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("PlumFluteWisp");
    public static final String NAME;

    public PlumFluteWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new BarrageBlockadePower(this)));
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/plumFluteWisp.png"));
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

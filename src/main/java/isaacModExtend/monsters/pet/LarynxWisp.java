package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.IsaacModExtend;

public class LarynxWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("LarynxWisp");
    public static final String NAME;
    private int initialStrength = 0;

    public LarynxWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/larynxWisp.png"));
    }

    @Override
    public void usePreBattleAction() {
        if (this.initialStrength > 0) {
            addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, this.initialStrength)));
        }
    }

    @Override
    public void takeTurn() {
        addToBot(new AnimateFastAttackAction(this));
        AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
        if (m != null) {
            addToBot(new DamageAction(m, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
        }
    }

    public LarynxWisp setInitialStrength(int initialStrength) {
        this.initialStrength = initialStrength;
        return this;
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

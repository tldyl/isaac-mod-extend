package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;

public class YumHeartWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("YumHeartWisp");
    public static final String NAME;

    public YumHeartWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/yumHeartWisp.png"));
    }

    @Override
    public void takeTurn() {
        addToBot(new AnimateFastAttackAction(this));
        AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
        if (m != null) {
            addToBot(new DamageAction(m, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public void die() {
        super.die();
        if (AbstractDungeon.monsterRng.randomBoolean(0.25F)) {
            AbstractDungeon.player.heal(5);
        }
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

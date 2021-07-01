package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.effects.BlueFireAttackEffect;

public class BlankCardSoulWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("BlankCardSoulWisp");
    public static final String NAME;
    private static final MonsterStrings monsterStrings;

    public BlankCardSoulWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/blankCardSoulWisp.png"));
    }

    @Override
    public void takeTurn() {
        addToBot(new AnimateFastAttackAction(this));
        AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
        if (m != null) {
            addToBot(new SFXAction("ATTACK_FIRE"));
            addToBot(new VFXAction(new BlueFireAttackEffect(m.hb.cX, m.hb.cY, true)));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    m.damage(BlankCardSoulWisp.this.damage.get(0));
                    if (m.isDeadOrEscaped()) {
                        if (AbstractDungeon.miscRng.randomBoolean(0.15F)) {
                            AbstractDungeon.getCurrRoom().addCardToRewards();
                            addToBot(new TextAboveCreatureAction(BlankCardSoulWisp.this, monsterStrings.DIALOG[0]));
                        }
                    }
                    isDone = true;
                }
            });
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

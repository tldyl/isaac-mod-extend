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
import isaacModExtend.relics.RedKey;

public class RedKeyWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("RedKeyWisp");
    public static final String NAME;

    public RedKeyWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/redKeyWisp.png"));
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
    public void usePreBattleAction() {
        if (AbstractDungeon.aiRng.randomBoolean(0.1F)) {
            if (AbstractDungeon.player.hasRelic(RedKey.ID)) {
                CardCrawlGame.sound.play("RELIC_RED_KEY");
                RedKey redKey = (RedKey) AbstractDungeon.player.getRelic(RedKey.ID);
                redKey.createNewRoom(AbstractDungeon.getCurrMapNode(), false);
            }
        }
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

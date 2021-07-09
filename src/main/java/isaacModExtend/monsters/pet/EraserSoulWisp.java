package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.relics.Eraser;

public class EraserSoulWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("EraserSoulWisp");
    public static final String NAME;
    private DamageInfo info;

    public EraserSoulWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/eraserSoulWisp.png"));
    }

    @Override
    public void damage(DamageInfo info) {
        this.info = info;
        super.damage(info);
    }

    @Override
    public void die() {
        super.die();
        if (AbstractDungeon.player.hasRelic(Eraser.ID)) {
            if (this.info != null && this.info.owner != null) {
                Eraser eraser = (Eraser) AbstractDungeon.player.getRelic(Eraser.ID);
                eraser.eraseEnemy(info.owner.id);
            }
        }
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

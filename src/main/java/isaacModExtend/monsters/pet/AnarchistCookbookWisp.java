package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.TheBombPower;
import isaacModExtend.IsaacModExtend;
import monsters.abstracrt.AbstractPet;
import patches.player.PlayerAddFieldsPatch;
import powers.TheMonsterBombPower;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AnarchistCookbookWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("AnarchistCookbookWisp");
    public static final String NAME;

    public AnarchistCookbookWisp() {
        super();
        this.name = NAME;
        this.id = ID;
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/anarchistCookbookWisp.png"));
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
        List<AbstractCreature> list = new ArrayList<>();
        list.add(AbstractDungeon.player);
        list.addAll(AbstractDungeon.getMonsters().monsters);
        MonsterGroup minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);
        list.addAll(minions.monsters);
        int index = Utils.randomCombineRng(list.size(), 1)[0];
        if (list.get(index) instanceof AbstractMonster && !(list.get(index) instanceof AbstractPet)) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(list.get(index), list.get(index), new TheMonsterBombPower(list.get(index), 3, 40)));
        } else if (list.get(index) instanceof AbstractPet) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(list.get(index), list.get(index), new TheBombPower(list.get(index), 3, 40)));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(list.get(index), list.get(index), new TheBombPower(list.get(index), 3, 40)));
        }
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

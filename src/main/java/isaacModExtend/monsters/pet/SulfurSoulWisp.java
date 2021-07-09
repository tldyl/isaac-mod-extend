package isaacModExtend.monsters.pet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;
import isaacModExtend.IsaacModExtend;

import java.lang.reflect.Field;

public class SulfurSoulWisp extends SoulWisp {
    public static final String ID = IsaacModExtend.makeID("SulfurSoulWisp");
    public static final String NAME;

    public SulfurSoulWisp() {
        super();
        this.name = NAME;
        this.id = ID;
        this.setHp(9);
    }

    @Override
    protected void renderDamageRange(SpriteBatch sb) {
        BobEffect bobEffect;
        Color intentColor;
        try {
            Field field = AbstractMonster.class.getDeclaredField("bobEffect");
            field.setAccessible(true);
            bobEffect = (BobEffect) field.get(this);
            field = AbstractMonster.class.getDeclaredField("intentColor");
            field.setAccessible(true);
            intentColor = (Color) field.get(this);
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, "10%", this.intentHb.cX - 30.0F * Settings.scale, this.intentHb.cY + bobEffect.y - 12.0F * Settings.scale, intentColor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/sulfurSoulWisp.png"));
    }

    @Override
    public void takeTurn() {
        addToBot(new AnimateFastAttackAction(this));
        int[] damage = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
        for (int i=0;i<damage.length;i++) {
            damage[i] = (int) Math.ceil(AbstractDungeon.getCurrRoom().monsters.monsters.get(i).maxHealth * 0.1);
        }
        addToBot(new DamageAllEnemiesAction(this, damage, DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.FIRE, true));
        addToBot(new DamageAction(this, new DamageInfo(this, 3, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.NONE));
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

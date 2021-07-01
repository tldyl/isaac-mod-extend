package isaacModExtend.monsters.pet;

import basemod.BaseMod;
import basemod.interfaces.OnPlayerDamagedSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.effects.BlueFireAttackEffect;
import monsters.Intent.Move;
import monsters.abstracrt.AbstractPet;
import utils.Point;
import utils.Utils;

public class SoulWisp extends AbstractPet implements OnPlayerDamagedSubscriber {
    public static final String ID = IsaacModExtend.makeID("SoulWisp");
    public static final String NAME;

    public static int wispAmount = 26;
    public static int wispAliveAmount = 0;
    public static boolean[] wispAlive = new boolean[wispAmount];

    private int index;

    public SoulWisp() {
        super(NAME, ID, 6, -8.0F, 10.0F, 20.0F, 42.0F, null, 0, 0);
        this.img = loadImage();
        this.setHp(6);
        setDamage();
        Point center = new Point(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY);
        for (int i=0;i<wispAmount;i++) {
            if (!wispAlive[i]) {
                this.index = i;
                wispAlive[i] = true;
                break;
            }
        }
        double angle = 2 * Math.PI * this.index / (wispAmount - 6);
        Point point = Utils.getCirclePoint(center, angle, 220);
        this.drawX = (float) point.x;
        this.drawY = (float) point.y;
        this.hb_w = getWidth() * Settings.scale;
        this.hb_h = getHeight() * Settings.xScale;
        this.refreshHitboxLocation();
        this.refreshIntentHbLocation();
        wispAliveAmount++;
        BaseMod.subscribe(this);
        this.createIntent();
    }

    protected float getWidth() {
        return 30.0F;
    }

    protected float getHeight() {
        return 56.0F;
    }

    protected void setDamage() {
        this.damage.add(new DamageInfo(this, 6, DamageInfo.DamageType.NORMAL));
        this.setMove((byte)Move.ATTACK.id, Intent.ATTACK, 6);
    }

    protected Texture loadImage() {
        return new Texture(IsaacModExtend.getResourcePath("monsters/soulWisp.png"));
    }

    protected void setHp(int minHp, int maxHp) {
        this.currentHealth = minHp;
        this.maxHealth = this.currentHealth;
    }

    @Override
    public void takeTurn() {
        addToBot(new AnimateFastAttackAction(this));
        AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
        if (m != null) {
            addToBot(new SFXAction("ATTACK_FIRE"));
            addToBot(new VFXAction(new BlueFireAttackEffect(m.hb.cX, m.hb.cY, true)));
            addToBot(new DamageAction(m, this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        wispAlive[index] = false;
        wispAliveAmount--;
        super.die(false);
        CardCrawlGame.sound.play("SOUL_WISP_EXTINCT");
        IsaacModExtend.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                BaseMod.unsubscribe(SoulWisp.this);
                isDone = true;
            }
        });
    }

    @Override
    protected void getMove(int i) {

    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }

    @Override
    public int receiveOnPlayerDamaged(int damageAmount, DamageInfo damageInfo) {
        damageAmount -= AbstractDungeon.player.currentBlock;
        if (damageInfo.owner != AbstractDungeon.player && damageInfo.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
            for (int i=0;i<wispAmount;i++) {
                if (wispAlive[i] && this.index == i) {
                    int tmpHealth = this.currentHealth;
                    this.damage(new DamageInfo(damageInfo.owner, Math.min(damageAmount, this.currentHealth)));
                    if (damageAmount >= tmpHealth) {
                        return damageAmount - tmpHealth;
                    } else {
                        return 0;
                    }
                }
            }
        }
        damageAmount += AbstractDungeon.player.currentBlock;
        return damageAmount;
    }
}

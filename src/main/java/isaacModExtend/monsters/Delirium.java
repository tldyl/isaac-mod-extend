package isaacModExtend.monsters;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ReactivePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.anm2player.AnimatedActor;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.powers.DerangedPower;
import isaacModExtend.powers.PhantomPower;
import monsters.Intent.Move;

public class Delirium extends AbstractAnm2Monster {
    public static final String ID = IsaacModExtend.makeID("Delirium");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    private int reduceDerangedCount = 0;
    private boolean playDeathSfx = false;

    public Delirium(float x, float y) {
        super(NAME, "Delirium", 10000, -8.0F, 0.0F, 360.0F, 240.0F, null, x, y);
        this.img = new Texture("images/monsters/Delirium.png");
        this.type = EnemyType.BOSS;
        this.setHp(10000);
        this.setMove((byte) Move.UNKNOWN.id, Intent.UNKNOWN);
        this.animation = new AnimatedActor(IsaacModExtend.getResourcePath("monsters/412.000_delirium.xml"));
        this.animation.addTriggerEvent("0", animation1 -> {});
        this.animation.addTriggerEvent("1", animation1 -> CardCrawlGame.sound.play("BOSS_BABY_PLUM_DEATH"));
        this.animation.scale = 3.0F;
        this.disposables.add(animation);
        this.deathTimer = 10.0F;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new ReactivePower(this)));
        addToBot(new ApplyPowerAction(this, this, new PhantomPower(this, 13)));
        addToBot(new ApplyPowerAction(this, this, new DerangedPower(this, 6)));
    }

    @Override
    public void update() {
        super.update();
        if (resetPosition) {
            animation.xPosition = this.hb.cX + this.animX;
            animation.yPosition = this.hb.cY + this.animY;
        }
        animation.update();
        if (animation.isCurAnimationDone() && !this.isDying && !this.isEscaping) {
            animation.setCurAnimation("Blink");
        }
        if (this.isDying) {
            if (this.deathTimer > 1.0F) {
                if (animation.isCurAnimationDone()) {
                    animation.setCurAnimation("Idle");
                }
            } else {
                if (animation.getCurAnimationName().equals("Idle")) {
                    animation.setCurAnimation("Scream");
                }
            }
            if (this.deathTimer < 4.0F && !playDeathSfx) {
                CardCrawlGame.sound.playA("ANGRY_GURGLE_1", -0.9F);
                playDeathSfx = true;
            }
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 0:
                if (hasPower(DerangedPower.POWER_ID)) {
                    addToBot(new ApplyPowerAction(this, this, new DerangedPower(this, -1)));
                }
                break;
            case 1:
                if (hasPower(PhantomPower.POWER_ID)) {
                    addToBot(new ApplyPowerAction(this, this, new PhantomPower(this, -2)));
                }
                break;
            case 2:
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 20)));
                break;
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int aiRng) {
        if (reduceDerangedCount < 3 && aiRng < 50) {
            setMove((byte) 0, Intent.UNKNOWN);
            reduceDerangedCount++;
        } else {
            if (this.currentHealth <= this.maxHealth * 0.5F && aiRng < 50) {
                setMove((byte) 2, Intent.BUFF);
            } else {
                setMove((byte) 1, Intent.UNKNOWN);
            }
        }
    }
}

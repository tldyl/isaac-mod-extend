package isaacModExtend.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import demoMod.anm2player.AnimatedActor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractAnm2Monster extends AbstractMonster {
    protected AnimatedActor animation;
    protected boolean resetPosition = true;

    public AbstractAnm2Monster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractAnm2Monster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractAnm2Monster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void render(SpriteBatch sb) {
        Method method;
        if (!this.isDead && !this.escaped) {
            if (resetPosition) {
                animation.flipX = this.flipHorizontal;
                animation.flipY = this.flipVertical;
            }
            animation.render(sb);

            if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic("Runic Dome") && this.intent != AbstractMonster.Intent.NONE && !Settings.hideCombatElements) {
                try {
                    method = AbstractMonster.class.getDeclaredMethod("renderIntentVfxBehind", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                    method = AbstractMonster.class.getDeclaredMethod("renderIntent", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                    method = AbstractMonster.class.getDeclaredMethod("renderIntentVfxAfter", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                    method = AbstractMonster.class.getDeclaredMethod("renderDamageRange", SpriteBatch.class);
                    method.setAccessible(true);
                    method.invoke(this, sb);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            this.hb.render(sb);
            this.intentHb.render(sb);
            this.healthHb.render(sb);
        }

        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            try {
                method = AbstractMonster.class.getDeclaredMethod("renderName", SpriteBatch.class);
                method.setAccessible(true);
                method.invoke(this, sb);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}

package isaacModExtend.interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface AbstractTargetingRelic extends SpecialActiveItem {
    void setHidden(boolean hidden);

    boolean isHidden();

    AbstractCreature getHoveredCreature();

    void setHoveredCreature(AbstractCreature creature);

    default void close() {
        this.setHidden(true);
    }

    void onLeftClick();

    default void updateTargetMode() {
        if (AbstractDungeon.isScreenUp || (float)InputHelper.mY > (float)Settings.HEIGHT - 80.0F * Settings.scale || AbstractDungeon.player.hoveredCard != null || (float)InputHelper.mY < 140.0F * Settings.scale) {
            GameCursor.hidden = false;
            this.close();
        }
        this.setHoveredCreature(null);
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster.hb.hovered && !monster.isDying) {
                this.setHoveredCreature(monster);
                break;
            }
        }
        if (InputHelper.justClickedLeft) {
            InputHelper.justClickedLeft = false;
            if (this.getHoveredCreature() != null) {
                AbstractPlayer p = AbstractDungeon.player;
                if (p.hasPower("Surrounded")) {
                    p.flipHorizontal = this.getHoveredCreature().drawX < p.drawX;
                }
                this.onLeftClick();
            }

            GameCursor.hidden = false;
            this.close();
        }
    }

    Vector2 getControlPoint();

    Vector2[] getPoints();

    void setControlPoint(Vector2 controlPoint);

    float getArrowScale();

    void setArrowScale(float arrowScale);

    float getArrowScaleTimer();

    void setArrowScaleTimer(float arrowScaleTimer);

    float getCurrentX();

    void setCurrentX(float currentX);

    float getCurrentY();

    void setCurrentY(float currentY);

    default void renderArrows(SpriteBatch sb) {
        if (!this.isHidden()) {
            this.renderTargetingUi(sb);
            if (this.getHoveredCreature() != null) {
                this.getHoveredCreature().renderReticle(sb);
            }
        }
    }

    default void renderTargetingUi(SpriteBatch sb) {
        float x = (float)InputHelper.mX;
        float y = (float)InputHelper.mY;
        this.setControlPoint(new Vector2(this.getCurrentX() - (x - this.getCurrentX()) / 4.0F, this.getCurrentY() + (y - this.getCurrentY() - 40.0F * Settings.scale) / 2.0F));
        if (this.getHoveredCreature() == null) {
            this.setArrowScale(Settings.scale);
            this.setArrowScaleTimer(0);
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
        } else {
            this.setArrowScaleTimer(this.getArrowScaleTimer() + Gdx.graphics.getDeltaTime());
            if (this.getArrowScaleTimer() > 1.0F) {
                this.setArrowScaleTimer(1.0F);
            }

            this.setArrowScale(Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, this.getArrowScaleTimer()));
            sb.setColor(new Color(1.0F, 0.2F, 0.3F, 1.0F));
        }

        Vector2 tmp = new Vector2(this.getControlPoint().x - x, this.getControlPoint().y - y);
        tmp.nor();
        this.drawCurvedLine(sb, new Vector2(this.getCurrentX(), this.getCurrentY()), new Vector2(x, y), this.getControlPoint());
        sb.draw(ImageMaster.TARGET_UI_ARROW, x - 128.0F, y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, this.getArrowScale(), this.getArrowScale(), tmp.angle() + 90.0F, 0, 0, 256, 256, false, false);
    }

    default void drawCurvedLine(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control) {
        float radius = 7.0F * Settings.scale;

        for(int i = 0; i < this.getPoints().length - 1; ++i) {
            this.getPoints()[i] = Bezier.quadratic(this.getPoints()[i], (float)i / 20.0F, start, control, end, new Vector2());
            radius += 0.4F * Settings.scale;
            float angle;
            Vector2 tmp;
            if (i != 0) {
                tmp = new Vector2(this.getPoints()[i - 1].x - this.getPoints()[i].x, this.getPoints()[i - 1].y - this.getPoints()[i].y);
                angle = tmp.nor().angle() + 90.0F;
            } else {
                tmp = new Vector2(this.getControlPoint().x - this.getPoints()[i].x, this.getControlPoint().y - this.getPoints()[i].y);
                angle = tmp.nor().angle() + 270.0F;
            }

            sb.draw(ImageMaster.TARGET_UI_CIRCLE, this.getPoints()[i].x - 64.0F, this.getPoints()[i].y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, radius / 18.0F, radius / 18.0F, angle, 0, 0, 128, 128, false, false);
        }
    }
}

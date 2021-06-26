package isaacModExtend.relics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.SuplexRushAction;
import isaacModExtend.interfaces.AbstractTargetingRelic;
import relics.abstracrt.ChargeableRelic;

public class Suplex extends ChargeableRelic implements AbstractTargetingRelic {
    public static final String ID = IsaacModExtend.makeID("Suplex");
    public static final String IMG_PATH = "relics/suplex.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private boolean hidden = true;
    private AbstractCreature hoveredCreature;
    private Vector2 controlPoint;
    private float arrowScaleTimer;
    private float arrowScale;
    private Vector2[] points = new Vector2[20];

    public Suplex() {
        super(ID, IMG, RelicTier.UNCOMMON, LandingSound.CLINK, 3);
        for(int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        super.onEquip();
    }

    @Override
    public void onRightClick() {
        if (this.isUsable()) {
            this.setHidden(false);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!this.isHidden()) {
            this.updateTargetMode();
        }
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public AbstractCreature getHoveredCreature() {
        return this.hoveredCreature;
    }

    @Override
    public void setHoveredCreature(AbstractCreature creature) {
        this.hoveredCreature = creature;
    }

    @Override
    public void onLeftClick() {
        addToBot(new SuplexRushAction(this.hoveredCreature));
        resetCharge();
        this.stopPulse();
    }

    @Override
    public Vector2 getControlPoint() {
        return this.controlPoint;
    }

    @Override
    public Vector2[] getPoints() {
        return this.points;
    }

    @Override
    public void setControlPoint(Vector2 controlPoint) {
        this.controlPoint = controlPoint;
    }

    @Override
    public float getArrowScale() {
        return this.arrowScale;
    }

    @Override
    public void setArrowScale(float arrowScale) {
        this.arrowScale = arrowScale;
    }

    @Override
    public float getArrowScaleTimer() {
        return this.arrowScaleTimer;
    }

    @Override
    public void setArrowScaleTimer(float arrowScaleTimer) {
        this.arrowScaleTimer = arrowScaleTimer;
    }

    @Override
    public float getCurrentX() {
        return this.currentX;
    }

    @Override
    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    @Override
    public float getCurrentY() {
        return this.currentY;
    }

    @Override
    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        super.renderInTopPanel(sb);
        if (!this.isHidden()) {
            this.renderArrows(sb);
        }
    }
}

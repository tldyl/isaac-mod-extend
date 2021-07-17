package isaacModExtend.relics;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.AbstractTargetingRelic;
import isaacModExtend.interfaces.SpecialActiveItem;
import relics.abstracrt.ClickableRelic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Eraser extends ClickableRelic implements CustomSavable<Map<String, Object>>,
                                                      AbstractTargetingRelic,
                                                      SpecialActiveItem {
    public static final String ID = IsaacModExtend.makeID("Eraser");
    public static final String IMG_PATH = "relics/eraser.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private String actName;
    private List<String> erasedMonsters = new ArrayList<>();
    private boolean hidden = true;
    private AbstractCreature hoveredCreature;
    private Vector2 controlPoint;
    private float arrowScaleTimer;
    private float arrowScale;
    private Vector2[] points = new Vector2[20];

    public Eraser() {
        super(ID, IMG, RelicTier.SHOP, LandingSound.FLAT);
        for(int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }
    }

    @Override
    public void onRightClick() {
        if (this.counter > 0 && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
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
    public void onEquip() {
        super.onEquip();
        this.counter = 1;
        this.actName = AbstractDungeon.name;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        if (this.erasedMonsters.size() > 0) this.flash();
        eraseEnemy();
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);
        if (!AbstractDungeon.name.equals(this.actName)) {
            this.counter++;
        }
        this.actName = AbstractDungeon.name;
    }

    @Override
    public Map<String, Object> onSave() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("actName", this.actName);
        ret.put("erasedMonsters", this.erasedMonsters);
        return ret;
    }

    @Override
    public void onLoad(Map<String, Object> map) {
        if (map != null) {
            this.actName = (String) map.getOrDefault("actName", null);
            this.erasedMonsters = (List<String>) map.getOrDefault("erasedMonsters", new ArrayList<>());
        }
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
        AbstractMonster m = (AbstractMonster) this.hoveredCreature;
        if (m != null) {
            this.counter--;
            this.flash();
            eraseEnemy(m.id);
            if (AbstractDungeon.player.hasRelic(BookOfVirtues.ID)) {
                BookOfVirtues bookOfVirtues = (BookOfVirtues) AbstractDungeon.player.getRelic(BookOfVirtues.ID);
                bookOfVirtues.use(ID);
            }
        }
    }

    public void eraseEnemy(String id) {
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                if (monster.id.equals(id)) {
                    if (monster.type != AbstractMonster.EnemyType.BOSS || this.erasedMonsters.contains(monster.id)) {
                        IsaacModExtend.addToBot(new RelicAboveCreatureAction(monster, this));
                        addToBot(new InstantKillAction(monster));
                        if (!this.erasedMonsters.contains(monster.id)) this.erasedMonsters.add(monster.id);
                    } else {
                        monster.damage(new DamageInfo(AbstractDungeon.player, 15, DamageInfo.DamageType.THORNS));
                        if (monster.isDying) {
                            IsaacModExtend.addToBot(new RelicAboveCreatureAction(monster, this));
                            this.erasedMonsters.add(monster.id);
                        }
                    }
                }
            }
        }
    }

    private void eraseEnemy() {
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                if (this.erasedMonsters.contains(monster.id)) {
                    IsaacModExtend.addToBot(new RelicAboveCreatureAction(monster, this));
                    addToBot(new InstantKillAction(monster));
                }
            }
        }
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
        return this.hb.cX;
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

    @Override
    public void preUse() {

    }

    @Override
    public boolean triggered() {
        return false;
    }
}

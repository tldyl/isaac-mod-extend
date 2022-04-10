package isaacModExtend.relics;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.powers.LazarusRagsPower;
import relics.abstracrt.ResurrectRelic;

import java.util.HashMap;
import java.util.Map;

public class LazarusRags extends ResurrectRelic implements CustomSavable<Map<String, Object>> {
    public static final String ID = IsaacModExtend.makeID("LazarusRags");
    public static final String IMG_PATH = "relics/lazarusRags.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private int strengthCounter = 0;

    private String lastDungeonId = "";

    public LazarusRags() {
        super(ID, IMG, RelicTier.RARE, LandingSound.FLAT, -9); //复活优先级最高
        this.counter = 1;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public int onResurrect() {
        this.counter--;
        this.strengthCounter++;
        AbstractPlayer p = AbstractDungeon.player;
        p.decreaseMaxHealth(10);
        if (p.maxHealth < 10) {
            p.maxHealth = 10;
            p.currentHealth = p.maxHealth;
            p.healthBarUpdatedEvent();
        }
        this.flash();
        this.stopPulse();
        addToBot(new RelicAboveCreatureAction(p, this));
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, 1)));
        addToBot(new ApplyPowerAction(p, p, new LazarusRagsPower(p)));
        GameActionManager.hpLossThisCombat = 0; //重置恶魔房开启概率
        GameActionManager.damageReceivedThisCombat = 0;
        return 10;
    }

    @Override
    public boolean canResurrect() {
        return this.counter > 0;
    }

    @Override
    public void atBattleStart() {
        if (this.strengthCounter > 0) {
            AbstractPlayer p = AbstractDungeon.player;
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.strengthCounter)));
        }
    }

    @Override
    public void atTurnStart() {
        if (this.counter == 0) {
            AbstractPlayer p = AbstractDungeon.player;
            if (!p.hasPower(LazarusRagsPower.POWER_ID)) {
                if (this.strengthCounter == 0) {
                    this.flash();
                    addToBot(new RelicAboveCreatureAction(p, this));
                }
                addToBot(new ApplyPowerAction(p, p, new LazarusRagsPower(p)));
            }
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (!lastDungeonId.equals(AbstractDungeon.id) && this.counter < 1) {
            this.counter++;
        }
        lastDungeonId = AbstractDungeon.id;
        if (this.counter > 0) {
            this.beginLongPulse();
        }
    }

    @Override
    public Map<String, Object> onSave() {
        Map<String, Object> data = new HashMap<>();
        data.put("lastDungeonId", this.lastDungeonId);
        data.put("strengthCounter", this.strengthCounter);
        return data;
    }

    @Override
    public void onLoad(Map<String, Object> save) {
        this.lastDungeonId = (String) save.getOrDefault("lastDungeonId", "");
        this.strengthCounter = ((Double) save.getOrDefault("strengthCounter", 0)).intValue();
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
        float offsetX = ReflectionHacks.getPrivateStatic(AbstractRelic.class, "offsetX");
        if (this.strengthCounter > -1) {
            if (inTopPanel) {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.strengthCounter), offsetX + this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.RED);
            } else {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.strengthCounter), this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.RED);
            }
        }
        if (this.counter > -1) {
            if (inTopPanel) {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.counter), offsetX + this.currentX + 30.0F * Settings.scale, this.currentY + 21.0F * Settings.scale, Color.WHITE);
            } else {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.counter), this.currentX + 30.0F * Settings.scale, this.currentY + 21.0F * Settings.scale, Color.WHITE);
            }
        }
    }
}

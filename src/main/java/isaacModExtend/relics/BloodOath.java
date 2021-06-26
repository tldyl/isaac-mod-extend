package isaacModExtend.relics;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.BloodOathAction;
import relics.abstracrt.DevilRelic;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BloodOath extends DevilRelic implements CustomSavable<Map<String, Object>> {
    public static final String ID = IsaacModExtend.makeID("BloodOath");
    public static final String IMG_PATH = "relics/bloodOath.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    public int strengthCounter = 0;
    public int dexterityCounter = 0;
    private String actName;
    private boolean shouldBloodDry = false;

    public BloodOath() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        this.actName = AbstractDungeon.name;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if (this.shouldBloodDry) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new BloodOathAction(this));
            this.shouldBloodDry = false;
        }
        if (this.strengthCounter > 0) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.strengthCounter)));
            addToBot(new GainBlockAction(p, this.strengthCounter));
        }
        if (this.dexterityCounter > 0) {
            addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.dexterityCounter)));
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);
        if (!AbstractDungeon.name.equals(this.actName)) {
            this.shouldBloodDry = true;
            this.strengthCounter = 0;
            this.dexterityCounter = 0;
        }
        this.actName = AbstractDungeon.name;
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
        float offsetX = 0.0F;
        try {
            Field field = AbstractRelic.class.getDeclaredField("offsetX");
            field.setAccessible(true);
            offsetX = field.getFloat(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (this.strengthCounter > -1) {
            if (inTopPanel) {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.strengthCounter), offsetX + this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.RED);
            } else {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.strengthCounter), this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.RED);
            }
        }
        if (this.dexterityCounter > -1) {
            if (inTopPanel) {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.dexterityCounter), offsetX + this.currentX + 30.0F * Settings.scale, this.currentY + 21.0F * Settings.scale, Color.GREEN);
            } else {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.dexterityCounter), this.currentX + 30.0F * Settings.scale, this.currentY + 21.0F * Settings.scale, Color.GREEN);
            }
        }
    }

    @Override
    public Map<String, Object> onSave() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("strengthCounter", this.strengthCounter);
        ret.put("dexterityCounter", this.dexterityCounter);
        ret.put("shouldBloodDry", this.shouldBloodDry);
        ret.put("actName", this.actName);
        return ret;
    }

    @Override
    public void onLoad(Map<String, Object> maps) {
        if (maps != null) {
            this.strengthCounter = (int)((Double) maps.getOrDefault("strengthCounter", 0)).doubleValue();
            this.dexterityCounter = (int)((Double) maps.getOrDefault("dexterityCounter", 0)).doubleValue();
            this.shouldBloodDry = (Boolean) maps.getOrDefault("shouldBloodDry", false);
            this.actName = (String) maps.getOrDefault("actName", null);
        }
    }
}

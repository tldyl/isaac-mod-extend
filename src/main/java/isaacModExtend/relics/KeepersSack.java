package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class KeepersSack extends CustomRelic implements CustomSavable<Map<String, Integer>> {
    public static final String ID = IsaacModExtend.makeID("KeepersSack");
    public static final String IMG_PATH = "relics/keepersSack.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    public int strengthCounter = 0;
    public int dexterityCounter = 0;
    public int focusCounter = 0;

    public KeepersSack() {
        super(ID, IMG, RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onSpendGold() {
        this.flash();
        switch (AbstractDungeon.miscRng.random(2)) {
            case 0:
                this.strengthCounter++;
                break;
            case 1:
                this.dexterityCounter++;
                break;
            case 2:
                this.focusCounter++;
                break;
        }
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if (this.strengthCounter > 0 || this.dexterityCounter > 0 || this.focusCounter > 0) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
        }
        if (this.strengthCounter > 0) {
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.strengthCounter)));
        }
        if (this.dexterityCounter > 0) {
            addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.dexterityCounter)));
        }
        if (this.focusCounter > 0) {
            addToBot(new ApplyPowerAction(p, p, new FocusPower(p, this.focusCounter)));
        }
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
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.dexterityCounter), offsetX + this.currentX + 30.0F * Settings.scale, this.currentY + 18.0F * Settings.scale, Color.GREEN);
            } else {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.dexterityCounter), this.currentX + 30.0F * Settings.scale, this.currentY + 18.0F * Settings.scale, Color.GREEN);
            }
        }
        if (this.focusCounter > -1) {
            if (inTopPanel) {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.focusCounter), offsetX + this.currentX + 30.0F * Settings.scale, this.currentY + 43.0F * Settings.scale, Color.BLUE);
            } else {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.focusCounter), this.currentX + 30.0F * Settings.scale, this.currentY + 43.0F * Settings.scale, Color.BLUE);
            }
        }
    }

    @Override
    public Map<String, Integer> onSave() {
        Map<String, Integer> ret = new HashMap<>();
        ret.put("strengthCounter", this.strengthCounter);
        ret.put("dexterityCounter", this.dexterityCounter);
        ret.put("focusCounter", this.focusCounter);
        return ret;
    }

    @Override
    public void onLoad(Map<String, Integer> maps) {
        if (maps != null) {
            this.strengthCounter = maps.getOrDefault("strengthCounter", 0);
            this.dexterityCounter = maps.getOrDefault("dexterityCounter", 0);
            this.focusCounter = maps.getOrDefault("focusCounter", 0);
        }
    }
}

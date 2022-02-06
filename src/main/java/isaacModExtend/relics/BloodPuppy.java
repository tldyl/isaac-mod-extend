package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import helpers.SummonHelper;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.SuplexRushAction;
import isaacModExtend.interfaces.NeutralCreature;
import isaacModExtend.monsters.pet.BloodPuppyPet;
import utils.Point;
import utils.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BloodPuppy extends CustomRelic implements NeutralCreature, CustomSavable<Map<String, Integer>> {
    public static final String ID = IsaacModExtend.makeID("BloodPuppy");
    public static final String IMG_PATH = "relics/bloodPuppy.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private BloodPuppyPet bloodPuppyPet;
    private Map<String, Integer> saveData = new HashMap<>();

    public BloodPuppy() {
        super(ID, IMG, RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.counter = 0;
    }

    @Override
    public void atBattleStart() {
        Point center = new Point((double)(AbstractDungeon.player.hb.x - 1200.0F), (double)(AbstractDungeon.player.hb_y + 170.0F));
        double angle = -0.517D;
        Point point = Utils.getCirclePoint(center, angle, 250.0D);
        if (this.bloodPuppyPet == null) {
            this.bloodPuppyPet = new BloodPuppyPet((float) point.x, (float) point.y);
            bloodPuppyPet.setPhase(saveData.getOrDefault("phase", 0));
            bloodPuppyPet.currentHealth = saveData.getOrDefault("health", 1);
        }
        this.bloodPuppyPet.setLeftSide(true);
        SuplexRushAction.movePosition(Settings.WIDTH * 0.75F + (float) point.x * Settings.xScale, AbstractDungeon.floorY + (float) point.y * Settings.yScale, this.bloodPuppyPet);
        this.bloodPuppyPet.powers.clear();
        SummonHelper.summonMinion(this.bloodPuppyPet);
    }

    @Override
    public void onTrigger(AbstractCreature target) {
        if (bloodPuppyPet.getPhase() > 0) {
            this.counter += target.lastDamageTaken;
            if (this.counter >= 100) {
                this.flash();
                AbstractDungeon.player.heal(20 * bloodPuppyPet.getPhase() * (this.counter - (this.counter % 100)) / 100);
                this.counter %= 100;
            }
        }
    }

    @Override
    public boolean isEnemy() {
        return bloodPuppyPet != null && bloodPuppyPet.getPhase() > 0;
    }

    @Override
    public AbstractCreature getInstance() {
        return bloodPuppyPet;
    }

    @Override
    public Map<String, Integer> onSave() {
        Map<String, Integer> saveData;
        if (bloodPuppyPet != null) {
            saveData = new HashMap<>();
            saveData.put("health", bloodPuppyPet.currentHealth);
            saveData.put("phase", bloodPuppyPet.getPhase());
        } else {
            saveData = this.saveData;
        }
        return saveData;
    }

    @Override
    public void onLoad(Map<String, Integer> data) {
        saveData = data;
    }

    @Override
    public Type savedType() {
        return new TypeToken<Map<String, Integer>>(){}.getType();
    }
}

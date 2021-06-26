package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import helpers.SummonHelper;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.NeutralCreature;
import isaacModExtend.monsters.pet.BloodPuppyPet;
import isaacModExtend.saves.BloodPuppySaveData;
import utils.Point;
import utils.Utils;

import java.lang.reflect.Type;

public class BloodPuppy extends CustomRelic implements NeutralCreature, CustomSavable<BloodPuppySaveData> {
    public static final String ID = IsaacModExtend.makeID("BloodPuppy");
    public static final String IMG_PATH = "relics/bloodPuppy.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private BloodPuppyPet bloodPuppyPet;
    private BloodPuppySaveData saveData = new BloodPuppySaveData();

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
            if (saveData.phase > 0) {
                bloodPuppyPet.setPhase(saveData.phase);
                bloodPuppyPet.currentHealth = saveData.health;
            }
        }
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
        return bloodPuppyPet.getPhase() > 0;
    }

    @Override
    public AbstractCreature getInstance() {
        return bloodPuppyPet;
    }

    @Override
    public BloodPuppySaveData onSave() {
        BloodPuppySaveData saveData;
        if (bloodPuppyPet != null) {
            saveData = new BloodPuppySaveData();
            saveData.health = bloodPuppyPet.currentHealth;
            saveData.phase = bloodPuppyPet.getPhase();
        } else {
            saveData = this.saveData;
        }
        return saveData;
    }

    @Override
    public void onLoad(BloodPuppySaveData data) {
        if (data != null) {
            saveData = data;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<BloodPuppySaveData>(){}.getType();
    }
}

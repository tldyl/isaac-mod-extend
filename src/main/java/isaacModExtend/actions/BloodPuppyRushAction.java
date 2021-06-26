package isaacModExtend.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.monsters.pet.BloodPuppyPet;

public class BloodPuppyRushAction extends AbstractGameAction {
    private BloodPuppyPet pet;
    private Texture img;
    private float startX;
    private float targetX;

    public BloodPuppyRushAction(BloodPuppyPet pet) {
        this.pet = pet;
        this.duration = 0.5F;
        this.startDuration = this.duration;
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration) {
            switch (pet.getPhase()) {
                case 1:
                    img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy2Rush.png"));
                    break;
                case 2:
                    img = new Texture(IsaacModExtend.getResourcePath("monsters/bloodPuppy3Rush.png"));
                    break;
            }
            pet.setImg(img);
            if (pet.isLeftSide()) {
                targetX = Settings.WIDTH * 0.9F;
            } else {
                targetX = pet.getOriginalX();
            }
            startX = pet.hb.cX;
        }
        float delta = Gdx.graphics.getDeltaTime();
        this.duration -= delta;
        float dist = (targetX - startX) * (delta / this.startDuration);
        pet.drawX += dist;
        pet.refreshHitboxLocation();
        if (this.duration <= 0) {
            pet.setPhase(pet.getPhase());
            this.isDone = true;
        }
    }
}

package isaacModExtend.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import demoMod.anm2player.AnimatedActor;

public class Anm2SwitchAction extends AbstractGameAction {
    private AnimatedActor animation;
    private String animName;

    public Anm2SwitchAction(AnimatedActor animation, String animName) {
        this.animation = animation;
        this.animName = animName;
    }

    @Override
    public void update() {
        this.animation.setCurAnimation(animName);
        this.isDone = true;
    }
}

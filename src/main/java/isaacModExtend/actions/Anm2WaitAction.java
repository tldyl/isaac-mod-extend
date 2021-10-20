package isaacModExtend.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class Anm2WaitAction extends AbstractGameAction {
    public Anm2WaitAction(float dur) {
        this.duration = dur;
    }

    @Override
    public void update() {
        this.tickDuration();
    }
}

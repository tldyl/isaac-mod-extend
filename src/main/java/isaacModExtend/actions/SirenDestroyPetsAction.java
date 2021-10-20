package isaacModExtend.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.List;

public class SirenDestroyPetsAction extends AbstractGameAction {
    private List<AbstractMonster> controlledPets;
    private float dist = Float.MIN_VALUE;

    public SirenDestroyPetsAction(float dur, List<AbstractMonster> controlledPets) {
        this.duration = dur;
        this.startDuration = dur;
        this.controlledPets = controlledPets;
        for (AbstractMonster m : controlledPets) {
            if (m.drawX > dist) {
                dist = m.drawX;
            }
        }
    }

    @Override
    public void update() {
        if (controlledPets.size() == 0) {
            isDone = true;
            return;
        }
        float deltaX = Gdx.graphics.getDeltaTime() * dist / this.startDuration;
        for (AbstractMonster m : controlledPets) {
            m.drawX -= deltaX;
        }
        this.tickDuration();
    }
}

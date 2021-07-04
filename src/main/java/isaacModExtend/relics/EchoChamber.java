package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.actions.EchoChamberAction;

import java.util.ArrayList;
import java.util.List;

public class EchoChamber extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("EchoChamber");
    public static final String IMG_PATH = "relics/echoChamber.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private List<AbstractCard> cardsToRepeat = new ArrayList<>();
    private StringBuilder sb = new StringBuilder();

    public EchoChamber() {
        super(ID, IMG, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        List<AbstractCard> cardsPlayedThisTurn = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        if (cardsPlayedThisTurn.size() == 0) {
            if (cardsToRepeat.size() > 0) {
                this.flash();
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            }
            IsaacModExtend.addToBot(new EchoChamberAction(cardsToRepeat));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    cardsToRepeat.add(c);
                    if (sb.length() == 0) {
                        sb.append(" NL #g").append(c.name.replace(" ", " #g"));
                    } else {
                        sb.append(" -> #g").append(c.name.replace(" ", " #g"));
                    }
                    EchoChamber.this.description = getUpdatedDescription() + sb.toString();
                    EchoChamber.this.tips.clear();
                    EchoChamber.this.tips.add(new PowerTip(EchoChamber.this.name, EchoChamber.this.description));
                    EchoChamber.this.initializeTips();
                    isDone = true;
                }
            });
        }
    }

    @Override
    public void onVictory() {
        this.cardsToRepeat.clear();
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
        sb.delete(0, sb.length());
    }
}

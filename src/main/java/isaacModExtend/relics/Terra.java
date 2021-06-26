package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.IsaacModExtend;

public class Terra extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("Terra");
    public static final String IMG_PATH = "relics/terra.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public Terra() {
        super(ID, IMG, RelicTier.SPECIAL, LandingSound.HEAVY);
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        this.flash();
        addToBot(new RelicAboveCreatureAction(p, this));
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, 1)));
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            IsaacModExtend.addToBot(new WaitAction(0.15F));
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    Terra.this.counter++;
                    if (Terra.this.counter == 2) {
                        Terra.this.flash();
                        Terra.this.beginLongPulse();
                    } else if (Terra.this.counter > 2) {
                        Terra.this.flash();
                        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, Terra.this));
                        Terra.this.stopPulse();
                        Terra.this.counter = 0;
                    }
                    isDone = true;
                }
            });

        }
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        if (this.counter >= 2) {
            return 2.0F * damage;
        } else {
            return 0.5F * damage;
        }
    }
}

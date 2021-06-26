package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import isaacModExtend.IsaacModExtend;

import java.util.ArrayList;
import java.util.List;

public class Sol extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("Sol");
    public static final String IMG_PATH = "relics/sol.png";

    public Sol() {
        super(ID, new Texture(IsaacModExtend.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        this.counter = 0;
    }

    @Override
    public void atBattleStart() {
        if (this.counter > 0) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.counter)));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onVictory() {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;
            IsaacModExtend.addToBot(new RelicAboveCreatureAction(p, this));
            this.counter += 2;
            p.currentHealth = p.maxHealth;
            AbstractDungeon.effectList.add(new HealEffect(p.hb.cX, p.hb.cY, p.maxHealth));
            p.healthBarUpdatedEvent();
            List<AbstractCard> toRemove = new ArrayList<>();
            for (AbstractCard card : p.masterDeck.group) {
                if (card.type == AbstractCard.CardType.CURSE) {
                    AbstractDungeon.effectList.add(new PurgeCardEffect(card));
                    toRemove.add(card);
                }
            }
            for (AbstractCard card : toRemove) {
                p.masterDeck.removeCard(card);
            }
        }
    }
}

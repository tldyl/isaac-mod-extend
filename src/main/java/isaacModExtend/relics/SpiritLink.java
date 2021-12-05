package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import isaacModExtend.IsaacModExtend;

import java.util.HashMap;
import java.util.Map;

public class SpiritLink extends CustomRelic implements CustomSavable<Map<String, Integer>> {
    public static final String ID = IsaacModExtend.makeID("SpiritLink");
    public static final String IMG_PATH = "relics/spiritLink.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private Map<String, Integer> savedPower = new HashMap<>();

    public SpiritLink() {
        super(ID, IMG, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction useCardAction) {
        if (card.type == AbstractCard.CardType.POWER) {
            AbstractCard removeLater = null;
            for (AbstractCard card1 : AbstractDungeon.player.masterDeck.group) {
                if (card1.uuid.equals(card.uuid)) {
                    this.flash();
                    useCardAction.exhaustCard = true;
                    card.purgeOnUse = true;
                    if (savedPower.containsKey(card.cardID)) {
                        if (savedPower.get(card.cardID) < card.timesUpgraded) {
                            savedPower.put(card.cardID, card.timesUpgraded);
                        }
                    } else {
                        savedPower.put(card.cardID, card.timesUpgraded);
                    }
                    removeLater = card1;
                    break;
                }
            }
            if (removeLater != null) {
                AbstractDungeon.player.masterDeck.removeCard(removeLater);
            }
        }
    }

    @Override
    public void atBattleStart() {
        if (savedPower.size() > 0) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            for (Map.Entry<String, Integer> e : savedPower.entrySet()) {
                String id = e.getKey();
                AbstractCard card = CardLibrary.getCard(id).makeCopy();
                for (int i=0;i<e.getValue();i++) {
                    card.upgrade();
                }
                card.use(AbstractDungeon.player, AbstractDungeon.getRandomMonster());
            }
        }
    }

    @Override
    public Map<String, Integer> onSave() {
        return savedPower;
    }

    @Override
    public void onLoad(Map<String, Integer> map) {
        if (map != null) {
            this.savedPower = map;
        }
    }
}

package isaacModExtend.cards.optionCards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.monsters.BabyPlum;
import isaacModExtend.powers.SoftheartedPower;

public class InNoMood extends AbstractCard {
    public static final String ID = IsaacModExtend.makeID("InNoMood");
    private static final CardStrings cardStrings;
    private static final String IMG_URL = IsaacModExtend.getResourcePath("cards/optionCards/inNoMood.png");
    private static final Texture IMG = new Texture(IMG_URL);
    private AbstractMonster owner;

    public InNoMood() {
        super(ID, cardStrings.NAME, IMG_URL, -2, cardStrings.DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.NONE);
        this.portrait = new TextureAtlas.AtlasRegion(IMG, 0, 0, 250, 190);
        if (AbstractDungeon.getCurrMapNode() != null) {
            this.owner = AbstractDungeon.getCurrRoom().monsters.getMonster(BabyPlum.ID);
        }
    }

    @Override
    public void onChoseThisOption() {
        if (this.owner != null) {
            CardCrawlGame.music.playTempBgmInstantly("BOSS_BOTTOM", true);
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SoftheartedPower()));
        }
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public AbstractCard makeCopy() {
        return new InNoMood();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    }
}

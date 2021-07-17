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
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.monsters.BabyPlum;
import isaacModExtend.powers.GoodbyePower;
import isaacModExtend.powers.PlayTimePower;

public class OfCourse extends AbstractCard {
    public static final String ID = IsaacModExtend.makeID("OfCourse");
    private static final CardStrings cardStrings;
    private static final String IMG_URL = IsaacModExtend.getResourcePath("cards/optionCards/ofCourse.png");
    private static final Texture IMG = new Texture(IMG_URL);
    private AbstractMonster owner;

    public OfCourse() {
        super(ID, cardStrings.NAME, IMG_URL, -2, cardStrings.DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.NONE);
        this.portrait = new TextureAtlas.AtlasRegion(IMG, 0, 0, 250, 190);
        if (AbstractDungeon.getCurrMapNode() != null) {
            this.owner = AbstractDungeon.getCurrRoom().monsters.getMonster(BabyPlum.ID);
        }
    }

    @Override
    public void onChoseThisOption() {
        if (this.owner != null) {
            CardCrawlGame.music.playTempBgmInstantly("ARCADE", true);
            addToBot(new ApplyPowerAction(this.owner, this.owner, new PlayTimePower(this.owner)));
            addToBot(new ApplyPowerAction(this.owner, this.owner, new GoodbyePower(this.owner, 7)));
            if (AbstractDungeon.ascensionLevel < 19) {
                addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -1)));
            }
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
        return new OfCourse();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    }
}

package isaacModExtend.daily.mods;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.daily.mods.AbstractDailyMod;
import com.megacrit.cardcrawl.localization.RunModStrings;
import isaacModExtend.IsaacModExtend;

public class Challenge45 extends AbstractDailyMod {
    public static final String ID = IsaacModExtend.makeID("Challenge45");
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath("ui/runMods/challenge45.png"));
    private static final RunModStrings runModStrings = CardCrawlGame.languagePack.getRunModString(ID);

    public Challenge45() {
        super(ID, runModStrings.NAME, runModStrings.DESCRIPTION, IsaacModExtend.getResourcePath("ui/runMods/challenge45.png"), false);
        this.img = IMG;
    }
}

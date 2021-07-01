package isaacModExtend.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.SpecialActiveItem;
import isaacModExtend.patches.HushsDoorPatch;
import patches.ui.SoulHeartPatch;
import relics.abstracrt.ClickableRelic;

import java.util.ArrayList;
import java.util.List;

public class AlabasterBox extends ClickableRelic implements SpecialActiveItem {
    public static final String ID = IsaacModExtend.makeID("AlabasterBox");
    public static final String IMG_PATH = "relics/alabasterBox.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private boolean triggered = false;

    public AlabasterBox() {
        super(ID, IMG, RelicTier.SHOP, LandingSound.HEAVY);
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void update() {
        super.update();
        if ((SoulHeartPatch.soulHeart > 0 || SoulHeartPatch.blackHeart > 0) && this.counter < 30) {
            this.flash();
            this.counter += SoulHeartPatch.soulHeart;
            SoulHeartPatch.soulHeart = 0;
            if (this.counter >= 30) {
                SoulHeartPatch.soulHeart += this.counter - 30;
                this.counter = 30;
                this.beginLongPulse();
                return;
            }
            this.counter += SoulHeartPatch.blackHeart;
            SoulHeartPatch.blackHeart = 0;
            if (this.counter >= 30) {
                SoulHeartPatch.blackHeart += this.counter - 30;
                this.counter = 30;
                this.beginLongPulse();
            }
        }
    }

    @Override
    public void onRightClick() {
        if (this.counter >= 30) {
            SoulHeartPatch.soulHeart += 15;
            if (HushsDoorPatch.PatchUpdate.dealWithDevil) {
                SoulHeartPatch.soulHeart -= 5;
            }
            List<AbstractRelic> tmp = new ArrayList<>(IsaacModExtend.angelOnlyRelics);
            List<AbstractRelic> toRemove = new ArrayList<>();
            for (AbstractRelic relic : tmp) {
                if (AbstractDungeon.player.hasRelic(relic.relicId)) {
                    toRemove.add(relic);
                }
            }
            tmp.removeAll(toRemove);
            final List<AbstractRelic> tmp1 = IsaacModExtend.getRandomAngelOnlyRelics(AbstractDungeon.miscRng, HushsDoorPatch.PatchUpdate.dealWithDevil ? 1 : 2, tmp);
            IsaacModExtend.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractRelic relic : tmp1) {
                        AbstractDungeon.commonRelicPool.remove(relic.relicId);
                        AbstractDungeon.uncommonRelicPool.remove(relic.relicId);
                        AbstractDungeon.rareRelicPool.remove(relic.relicId);
                        AbstractDungeon.bossRelicPool.remove(relic.relicId);
                        AbstractDungeon.shopRelicPool.remove(relic.relicId);
                        relic.instantObtain();
                    }
                    AbstractDungeon.player.relics.remove(AlabasterBox.this);
                    AbstractDungeon.player.reorganizeRelics();
                    isDone = true;
                }
            });
        }
    }

    @Override
    public void preUse() {
        if (this.counter >= 30) {
            triggered = true;
        }
    }

    @Override
    public boolean triggered() {
        return triggered;
    }
}

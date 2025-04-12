package isaacModExtend.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import isaacModExtend.IsaacModExtend;

import java.util.List;
import java.util.stream.Collectors;

public class VanishingTwin extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("VanishingTwin");
    public static final String IMG_PATH = "relics/vanishingTwin.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));

    public boolean triggered = false;

    public VanishingTwin() {
        super(ID, IMG, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && counter != AbstractDungeon.actNum) {
            this.flash();
            counter = AbstractDungeon.actNum;
            triggered = true;
            MonsterGroup bossEncounter = MonsterHelper.getEncounter(AbstractDungeon.bossKey);
            List<AbstractMonster> bosses = bossEncounter.monsters.stream().filter(m -> m.type == AbstractMonster.EnemyType.BOSS).collect(Collectors.toList());
            if (!bosses.isEmpty()) {
                AbstractMonster bossToSpawn = bosses.get(AbstractDungeon.monsterRng.random(bosses.size() - 1));
                bossToSpawn.currentHealth *= 0.75F;
                bossToSpawn.healthBarUpdatedEvent();
                bossToSpawn.drawX -= 260.0F * Settings.scale;
                bossToSpawn.rollMove();
                bossToSpawn.createIntent();
                addToBot(new SpawnMonsterAction(bossToSpawn, false));
                addToBot(new ApplyPowerAction(bossToSpawn, bossToSpawn, new SlowPower(bossToSpawn, 0)));
                addToBot(new RelicAboveCreatureAction(bossToSpawn, this));
            }
        }
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
    }

    @Override
    public void onVictory() {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && triggered) {
            this.flash();
        }
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum <= 2;
    }
}

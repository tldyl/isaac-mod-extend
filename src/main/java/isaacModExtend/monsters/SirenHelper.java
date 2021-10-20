package isaacModExtend.monsters;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.IsaacModExtend;

import java.util.UUID;

public final class SirenHelper extends AbstractMonster {
    public static final String ID = IsaacModExtend.makeID("SirenHelper");
    public static final String NAME;
    private boolean mimicked = false;
    private Siren siren;

    public SirenHelper(float offsetX, float offsetY, AbstractMonster mimicSource) {
        super(NAME, ID + UUID.randomUUID().toString(), 40, -8.0F, 10.0F, 36.0F, 44.0F, IsaacModExtend.getResourcePath("monsters/sirenHelper.png"), offsetX, offsetY);
        if (mimicSource != null) {
            setHp(mimicSource.maxHealth);
            this.currentHealth = mimicSource.currentHealth;
            this.name = mimicSource.name;
            this.img = ReflectionHacks.getPrivate(mimicSource, AbstractMonster.class, "img");
            this.id = mimicSource.id;
            this.mimicked = true;
            this.hb.width = this.hb_w = mimicSource.hb.width;
            this.hb.height = this.hb_h = mimicSource.hb.height;
            this.flipHorizontal = true;
            this.refreshHitboxLocation();
            this.refreshIntentHbLocation();
        }
        this.damage.add(new DamageInfo(this, 6));
        this.damage.add(new DamageInfo(this, 12));
        siren = (Siren) AbstractDungeon.getMonsters().getMonster(Siren.ID);
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 0:
                addToBot(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case 1:
                break;
        }
    }

    @Override
    protected void getMove(int aiRng) {
        if (siren != null && siren.useSuicideSlam) {
            setMove((byte) 1, Intent.ATTACK, this.damage.get(1).base);
        } else {
            setMove((byte) 0, Intent.ATTACK, this.damage.get(0).base);
        }
    }

    @Override
    public void die() {
        super.die();
        if (siren != null) {
            siren.returnControlledPet(this.id);
        }
    }

    @Override
    public void dispose() {
        if (!this.mimicked) {
            super.dispose();
        }
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
    }
}

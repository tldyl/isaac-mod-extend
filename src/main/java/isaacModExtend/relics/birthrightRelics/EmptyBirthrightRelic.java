package isaacModExtend.relics.birthrightRelics;

import basemod.abstracts.CustomRelic;
import isaacModExtend.IsaacModExtend;

public class EmptyBirthrightRelic extends CustomRelic {
    public static final String ID = IsaacModExtend.makeID("EmptyBirthrightRelic");

    public EmptyBirthrightRelic() {
        super(ID, "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }
}

package isaacModExtend.interfaces;

import basemod.interfaces.ISubscriber;
import relics.abstracrt.ClickableRelic;

public interface PreRightClickRelicSubscriber extends ISubscriber {
    void preRightClickRelic(ClickableRelic relic);
}

package isaacModExtend.rooms;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.EventRoom;
import isaacModExtend.events.AngelRoomEvent;

public class AngelRoom extends EventRoom {
    public void onPlayerEntry() {
        AbstractDungeon.overlayMenu.proceedButton.hide();
        this.event = new AngelRoomEvent();
        this.event.onEnterRoom();
    }
}

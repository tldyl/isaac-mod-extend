package isaacModExtend.patches;

import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.TempMusic;

@SuppressWarnings("unused")
public class TempMusicPatch {
    @SpirePatch(
            clz = TempMusic.class,
            method = "getSong"
    )
    public static class PatchGetSong {
        public static SpireReturn<Music> Prefix(TempMusic tempMusic, String key) {
            if (key.equals("PLANETARIUM")) {
                return SpireReturn.Return(MainMusic.newMusic("IsaacAudio/music/planetarium.ogg"));
            }
            if (key.equals("ANGEL_ROOM")) {
                return SpireReturn.Return(MainMusic.newMusic("IsaacAudio/music/angel_room.ogg"));
            }
            if (key.equals("ARCADE")) {
                return SpireReturn.Return(MainMusic.newMusic("IsaacAudio/music/arcade.ogg"));
            }
            return SpireReturn.Continue();
        }
    }
}

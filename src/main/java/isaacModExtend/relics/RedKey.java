package isaacModExtend.relics;

import basemod.CustomEventRoom;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.PostGenerateDungeonMapSubscriber;
import isaacModExtend.map.RedMapEdge;
import isaacModExtend.map.RedMapRoomNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import relics.abstracrt.ChargeableRelic;
import room.BloodShopRoom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedKey extends ChargeableRelic implements CustomSavable<Map<String, Integer>>, PostGenerateDungeonMapSubscriber {
    public static final String ID = IsaacModExtend.makeID("RedKey");
    public static final String IMG_PATH = "relics/redKey.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    private Map<String, Integer> saveData = new HashMap<>();
    private static final Logger log = LogManager.getLogger(RedKey.class);
    private String currDungeon;
    private int floorNum;

    public RedKey() {
        super(ID, IMG, RelicTier.RARE, LandingSound.FLAT, 4);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onRightClick() {
        if (isUsable()) {
            this.flash();
            CardCrawlGame.sound.play("RELIC_RED_KEY");
            createNewRoom(AbstractDungeon.getCurrMapNode(), false);
            this.resetCharge();
            this.stopPulse();
        }
    }

    public void createNewRoom(MapRoomNode currNode, boolean loadingData) {
        List<MapRoomNode> availableNodes = new ArrayList<>();
        for (MapRoomNode node : AbstractDungeon.map.get(Math.min(currNode.y + 1, AbstractDungeon.map.size() - 1))) {
            if (node.getRoom() == null) {
                availableNodes.add(node);
            }
        }
        if (availableNodes.isEmpty()) {
            availableNodes.addAll(AbstractDungeon.map.get(Math.min(currNode.y + 1, AbstractDungeon.map.size() - 1)));
        }
        MapRoomNode newNode = availableNodes.get(AbstractDungeon.mapRng.random(availableNodes.size() - 1));
        newNode = new RedMapRoomNode(newNode.x, newNode.y);
        AbstractDungeon.map.get(newNode.y).set(newNode.x, newNode);
        boolean connectToBoss = false;
        if (newNode.y == AbstractDungeon.map.size() - 1) {
            connectToBoss = true;
        }
        newNode.addParent(currNode);
        MapEdge edge = new RedMapEdge(currNode.x, currNode.y, 0, 0, newNode.x, newNode.y, 0, 0, false);
        currNode.addEdge(edge);
        if (connectToBoss) {
            edge = new RedMapEdge(newNode.x, newNode.y, 0, 0, 3, newNode.y + 1, 0, 0, true);
            newNode.addEdge(edge);
        } else {
            MapRoomNode nextNode;
            availableNodes.clear();
            for (MapRoomNode node : AbstractDungeon.map.get(newNode.y + 1)) {
                if (node.getRoom() != null) {
                    availableNodes.add(node);
                }
            }
            nextNode = availableNodes.get(AbstractDungeon.mapRng.random(availableNodes.size() - 1));
            availableNodes.remove(nextNode);
            edge = new RedMapEdge(newNode.x, newNode.y, 0, 0, nextNode.x, nextNode.y, 0, 0, false);
            newNode.addEdge(edge);
            nextNode.addParent(newNode);
            if (!availableNodes.isEmpty() && AbstractDungeon.mapRng.randomBoolean()) {
                nextNode = availableNodes.get(AbstractDungeon.mapRng.random(availableNodes.size() - 1));
                edge = new RedMapEdge(newNode.x, newNode.y, 0, 0, nextNode.x, nextNode.y, 0, 0, false);
                newNode.addEdge(edge);
                nextNode.addParent(newNode);
            }
        }
        int roll = AbstractDungeon.mapRng.random(99);
        if (roll < 5) {
            newNode.room = new BloodShopRoom();
        } else if (roll < 15) {
            newNode.room = new TreasureRoom();
        } else if (roll < 45) {
            newNode.room = new CustomEventRoom();
        } else if (roll < 60) {
            newNode.room = new ShopRoom();
        } else if (roll < 80) {
            newNode.room = new MonsterRoom();
        } else if (roll < 90) {
            newNode.room = new RestRoom();
        } else {
            newNode.room = new MonsterRoomElite();
            if (!Settings.hasEmeraldKey && AbstractDungeon.mapRng.randomBoolean(0.25F)) {
                newNode.hasEmeraldKey = true;
            }
        }
        String pos = currNode.x + "," + currNode.y;
        if (!loadingData) {
            saveData.put(pos, saveData.getOrDefault(pos, 0) + 1);
        }
        log.info("Create a red room: {}", newNode.toString());
    }

    @Override
    public Map<String, Integer> onSave() {
        return saveData;
    }

    @Override
    public void onLoad(Map<String, Integer> data) {
        SaveFile saveFile = SaveAndContinue.loadSaveFile(AbstractDungeon.player.chosenClass);
        currDungeon = saveFile.level_name;
        floorNum = saveFile.floor_num;
        if (data != null) {
            saveData = data;
        }
    }

    @Override
    public void receivePostGenerateDungeonMap() {
        log.info("dungeon name: {}", AbstractDungeon.id);
        log.info("save dungeon name: {}", currDungeon);
        log.info("floorNum: {}", AbstractDungeon.floorNum);
        log.info("save floorNum: {}", floorNum);
        if (!AbstractDungeon.id.equals(currDungeon) || AbstractDungeon.floorNum < floorNum) {
            log.info("Entered a new act, clearing room saves...");
            saveData.clear();
        }
        for (Map.Entry<String, Integer> e : saveData.entrySet()) {
            int x = Integer.parseInt(e.getKey().split(",")[0]);
            int y = Integer.parseInt(e.getKey().split(",")[1]);
            log.info("amount: {}", e.getValue());
            for (int i=0;i<e.getValue();i++) {
                log.info("creating red room...");
                createNewRoom(AbstractDungeon.map.get(y).get(x), true);
            }
        }
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.floorNum <= 42;
    }
}

package isaacModExtend.relics;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import helpers.SummonHelper;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.interfaces.PreRightClickRelicSubscriber;
import isaacModExtend.interfaces.SpecialActiveItem;
import isaacModExtend.monsters.pet.*;
import relics.*;
import relics.abstracrt.ChargeableRelic;
import relics.abstracrt.ClickableRelic;

import java.util.ArrayList;
import java.util.List;

public class BookOfVirtues extends ChargeableRelic implements PreRightClickRelicSubscriber, CustomSavable<List<String>> {
    public static final String ID = IsaacModExtend.makeID("BookOfVirtues");
    public static final String IMG_PATH = "relics/bookOfVirtues.png";
    private static final Texture IMG = new Texture(IsaacModExtend.getResourcePath(IMG_PATH));
    public static List<SoulWisp> soulWisps = new ArrayList<>();
    public static List<String> exceptions = new ArrayList<>();

    public BookOfVirtues() {
        super(ID, IMG, RelicTier.RARE, LandingSound.MAGICAL, 4);
        if (exceptions.size() == 0) {
            exceptions.add(BloodDonationBag.ID);
            exceptions.add(Diplopia.ID);
            exceptions.add(GuppysPaw.ID);
            exceptions.add(TuHaoJin.ID);
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onRightClick() {
        if (isUsable()) {
            this.use(null);
            this.resetCharge();
            this.stopPulse();
        }
    }

    @Override
    public void atBattleStart() {
        for (SoulWisp wisp : soulWisps) {
            wisp.powers.clear();
            SummonHelper.summonMinion(wisp);
        }
    }

    @Override
    public void onVictory() {
        super.onVictory();
        List<SoulWisp> toRemove = new ArrayList<>();
        for (SoulWisp wisp : soulWisps) {
            if (wisp.isDeadOrEscaped()) {
                toRemove.add(wisp);
            }
        }
        soulWisps.removeAll(toRemove);
    }

    private void use(String relicId) {
        this.flash();
        SoulWisp wisp;
        CardCrawlGame.sound.play("SOUL_WISP_IGNITE");
        if (relicId == null) {
            wisp = new SoulWisp();
            SummonHelper.summonMinion(wisp);
            soulWisps.add(wisp);
            return;
        }
        switch (relicId) {
            case "DeathBook": //死灵书
                wisp = new NecronomiconSoulWisp();
                break;
            case "IsaacExt:AlabasterBox": //雪花石膏盒
                wisp = new SoulWisp();
                for (int i=0;i<6;i++) {
                    SummonHelper.summonMinion(wisp);
                    soulWisps.add(wisp);
                    if (SoulWisp.wispAliveAmount == SoulWisp.wispAmount) return;
                    wisp = new SoulWisp();
                }
                break;
            case "AnarchistCookbook": //无政府主义者食谱
                wisp = new AnarchistCookbookWisp();
                break;
            case "BelialBook": //恶魔书
                wisp = new BelialSoulWisp();
                break;
            case "BlankCard": //白卡
                wisp = new BlankCardSoulWisp();
                break;
            case "BookofShadows": //无敌书
                wisp = new ShadowsSoulWisp();
                break;
            case "IsaacExt:RedKey": //红钥匙
                wisp = new RedKeyWisp();
                break;
            case "DoctorsRemote": //博士遥控器
                wisp = new AnarchistCookbookWisp();
                break;
            case "SatanicBible": //黑书
                wisp = new SatanicBibleWisp();
                break;
            case "BloodDonationBag": //卖血袋
                wisp = new IVBagWisp();
                break;
            case "Diplopia": //复眼
                try {
                    List<SoulWisp> toAdd = new ArrayList<>();
                    for (SoulWisp wisp1 : soulWisps) {
                        if (SoulWisp.wispAliveAmount == SoulWisp.wispAmount - 1) break;
                        wisp = wisp1.getClass().newInstance();
                        SummonHelper.summonMinion(wisp);
                        toAdd.add(wisp);
                    }
                    soulWisps.addAll(toAdd);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                wisp = new DiplopiaWisp();
                break;
            case "GuppysPaw": //猫爪
                wisp = new GuppysPawWisp();
                SummonHelper.summonMinion(wisp);
                soulWisps.add(wisp);
                for (int i=0;i<2;i++) {
                    if (SoulWisp.wispAliveAmount < SoulWisp.wispAmount) {
                        wisp = new GuppysPawWisp();
                        SummonHelper.summonMinion(wisp);
                        soulWisps.add(wisp);
                    } else {
                        return;
                    }
                }
                return;
            case "TheBible": //圣经
                wisp = new TheBibleWisp();
                break;
            case "TheBean": //屁豆
                wisp = new BeanSoulWisp();
                break;
            case "TuHaoJin": //魔法手指
                wisp = new MagicFingerWisp();
                break;
            case "YumHeart": //美味的心
                wisp = new YumHeartWisp();
                break;
            case "IsaacExt:Mars":
                return;
            case "IsaacExt:Larynx": //喉咙
                wisp = new LarynxWisp().setInitialStrength(AbstractDungeon.player.getRelic(relicId).counter);
                break;
            case "IsaacExt:GoldenRazor": //金刀片
                wisp = new MagicFingerWisp();
                break;
            case "UnicornStump": //断角
                if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() > 0) return;
            default:
                wisp = new SoulWisp();
                break;
        }
        SummonHelper.summonMinion(wisp);
        soulWisps.add(wisp);
    }

    private boolean hasAnyOtherChargeableRelic() {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if ((relic instanceof ChargeableRelic && !relic.relicId.equals(this.relicId)) || relic instanceof SpecialActiveItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsable() {
        return AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !hasAnyOtherChargeableRelic() && SoulWisp.wispAliveAmount < SoulWisp.wispAmount;
    }

    @Override
    public void preRightClickRelic(ClickableRelic relic) {
        if (relic instanceof ChargeableRelic) {
            ChargeableRelic chargeableRelic = (ChargeableRelic) relic;
            if (chargeableRelic.isUsable() && SoulWisp.wispAliveAmount < SoulWisp.wispAmount) {
                this.use(chargeableRelic.relicId);
            }
        } else if (relic instanceof SpecialActiveItem) {
            SpecialActiveItem activeItem = (SpecialActiveItem) relic;
            activeItem.preUse();
            if (activeItem.triggered() && SoulWisp.wispAliveAmount < SoulWisp.wispAmount) {
                this.use(relic.relicId);
            }
        } else if (exceptions.contains(relic.relicId)) {
            if (SoulWisp.wispAliveAmount < SoulWisp.wispAmount) {
                this.use(relic.relicId);
            }
        }
    }

    @Override
    public List<String> onSave() {
        List<String> ret = new ArrayList<>();
        for (SoulWisp wisp : soulWisps) {
            ret.add(wisp.getClass().getName());
        }
        return ret;
    }

    @Override
    public void onLoad(List<String> clsList) {
        SoulWisp.wispAliveAmount = 0;
        for (SoulWisp wisp : soulWisps) {
            wisp.dispose();
            BaseMod.unsubscribe(wisp);
        }
        soulWisps.clear();
        for (int i=0;i<SoulWisp.wispAmount;i++) {
            SoulWisp.wispAlive[i] = false;
        }
        if (clsList != null) {
            try {
                for (String s : clsList) {
                    soulWisps.add((SoulWisp) Class.forName(s).newInstance());
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}

package isaacModExtend.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import isaacModExtend.patches.SaveAndContinuePatch;

import java.io.*;
import java.util.Arrays;

public class RewindCommand extends ConsoleCommand {
    public RewindCommand() {
        maxExtraTokens = 1;
        minExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = false;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        if (!AbstractDungeon.player.isDead) {
            DevConsole.log("This command is only available when the character dies.");
            return;
        }
        DevConsole.log("Copying backup saves...");
        String filepath = AbstractDungeon.player.getSaveFilePath();
        if (Settings.isBeta) {
            filepath = filepath + "BETA";
        }
        File saveFile = new File(filepath);
        if (saveFile.exists()) {
            saveFile.delete();
        }
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File rewindFile = new File(filepath + "REWIND");
        if (SaveAndContinuePatch.copyFile(rewindFile, saveFile)) {
            DevConsole.log("Deleting last run history...");
            File runHistoryDirectory = new File("runs" + File.separator + AbstractDungeon.player.chosenClass.name());
            if (runHistoryDirectory.exists() && runHistoryDirectory.isDirectory()) {
                String[] filenames = runHistoryDirectory.list();
                if (filenames != null) {
                    Arrays.sort(filenames, String::compareToIgnoreCase);
                    DevConsole.log("Last run history file is: " + filenames[filenames.length - 1]);
                    File runHistoryFile = new File("runs" + File.separator + AbstractDungeon.player.chosenClass.name() + File.separator + filenames[filenames.length - 1]);
                    if (runHistoryFile.exists()) {
                        runHistoryFile.delete();
                    }
                }
            }
            DevConsole.log("Saves recovered. Please exit to the main menu and click the 'Continue' button.");
        } else {
            errorMsg();
        }
    }

    @Override
    protected void errorMsg() {
        DevConsole.log("Err: Could not copy backup save.");
    }
}

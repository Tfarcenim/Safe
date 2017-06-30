package com.kamildanak.minecraft.safe.stats;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.text.TextComponentTranslation;

public class ModStats {
    public static final StatBase SAFE_OPENED =
            (new StatBasic("safe:stat.safeOpened",
                    new TextComponentTranslation("safe:stat.safeOpened")))
                    .registerStat();
    public static final StatBase SAFE_FAILED_UNLOCK =
            (new StatBasic("safe:stat.failedToUnlockSafe",
                    new TextComponentTranslation("safe:stat.failedToUnlockSafe")))
                    .registerStat();
    public static final StatBase SAFE_PEEKED_INTO_OPENED_SAFE =
            (new StatBasic("safe:stat.peekedIntoOpenedSafe",
                    new TextComponentTranslation("safe:stat.peekedIntoOpenedSafe")))
                    .registerStat();
    public static final StatBase CRACKED_SAFE =
            (new StatBasic("safe:stat.safeBlownUp",
                    new TextComponentTranslation("safe:stat.safeBlownUp")))
                    .registerStat();
}

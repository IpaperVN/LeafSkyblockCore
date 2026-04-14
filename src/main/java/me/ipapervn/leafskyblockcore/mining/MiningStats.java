package me.ipapervn.leafskyblockcore.mining;

public record MiningStats(double breakingPower, double miningSpeed, double fortune) {
    public static final MiningStats EMPTY = new MiningStats(0, 1, 0);
}

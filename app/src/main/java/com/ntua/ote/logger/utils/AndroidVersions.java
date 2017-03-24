package com.ntua.ote.logger.utils;

public enum AndroidVersions {

    S25("Nougat"),
    S24("Nougat"),
    S23("Marshmallow"),
    S22("Lollipop"),
    S21("Lollipop"),
    S20("KitKat"),
    S18("Jelly Bean"),
    S17("Jelly Bean"),
    S16("Jelly Bean"),
    S15("Ice Cream Sandwich"),
    S14("Ice Cream Sandwich");

    public String versionName;

    AndroidVersions(String versionName) {
        this.versionName = versionName;
    }
}

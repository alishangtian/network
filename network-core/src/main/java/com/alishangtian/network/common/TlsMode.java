package com.alishangtian.network.common;

public enum TlsMode {
    /**
     * 关闭
     */
    DISABLED("disabled"),
    /**
     * 允许
     */
    PERMISSIVE("permissive"),
    /**
     * 推荐
     */
    ENFORCING("enforcing");

    private String name;

    TlsMode(String name) {
        this.name = name;
    }

    public static TlsMode parse(String mode) {
        for (TlsMode tlsMode : TlsMode.values()) {
            if (tlsMode.name.equals(mode)) {
                return tlsMode;
            }
        }

        return PERMISSIVE;
    }

    public String getName() {
        return name;
    }
}

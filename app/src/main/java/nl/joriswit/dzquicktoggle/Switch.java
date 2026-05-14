package nl.joriswit.dzquicktoggle;

import androidx.annotation.NonNull;

public class Switch {
    public final int idx;
    @NonNull
    public final String name;
    public Switch(int idx, @NonNull String name) {
        this.idx = idx;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}

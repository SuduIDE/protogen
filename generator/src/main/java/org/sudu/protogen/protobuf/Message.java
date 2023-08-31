package org.sudu.protogen.protobuf;

import java.util.List;
import java.util.Optional;

public abstract class Message extends EnumOrMessage {

    public abstract List<? extends Field> getFields();

    public final boolean isUnfolded() {
        if (getFields().size() != 1) return false;
        return getUnfoldOption().orElse(false);
    }

    /*
     * Options are not supposed to be used at high-level logic.
     * They return only the value of an option in .proto file.
     * Advanced logic taking into account other options and configuration values
     * is placed at top-level methods such as isUnfolded for getUnfoldOption.
     */

    /**
     * protobuf internal flag to tag map entries
     *
     * @see <a href="https://protobuf.dev/programming-guides/proto3/#backwards">map specification</a>
     */
    public abstract boolean isMap();

    protected abstract Optional<Boolean> getUnfoldOption();
}

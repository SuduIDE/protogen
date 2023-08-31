package org.sudu.protogen.protobuf;

import java.util.List;
import java.util.Optional;

public abstract class Enum extends EnumOrMessage {

    public abstract List<? extends Value> getValues();

    public abstract static class Value {

        public final String generatedName() {
            return getOverriddenNameOption().orElse(getName());
        }

        public final boolean isUnused() {
            return getUnusedOption().orElse(false);
        }

        public abstract String getName();

        protected abstract Optional<String> getOverriddenNameOption();

        protected abstract Optional<Boolean> getUnusedOption();
    }
}

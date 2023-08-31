package org.sudu.protogen.utils;

import org.jetbrains.annotations.NotNull;

public class Name {

    /**
     * Adjust a class generatedName to follow the JavaBean spec.
     * - capitalize the first letter
     * - remove embedded underscores & capitalize the following letter
     * - capitalize letter after a number
     *
     * @param name method generatedName
     * @return lower generatedName
     */
    public static @NotNull String toCamelCase(String name) {
        if (name.isBlank()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(name.charAt(0)));

        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            char prev = name.charAt(i - 1);

            if (c != '_') {
                if (prev == '_' || prev >= '0' && prev <= '9') {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    public static @NotNull String getPackage(String fullyQualifiedName) {
        String[] tokens = fullyQualifiedName.split("\\.");
        if (tokens.length == 1) return "";
        StringBuilder pkg = new StringBuilder();
        for (int i = 0; i < tokens.length - 1; ++i) {
            pkg.append(tokens[i]);
            if (i != tokens.length - 2) pkg.append(".");
        }
        return pkg.toString();
    }

    public static @NotNull String getLastName(String fullyQualifiedName) {
        String[] tokens = fullyQualifiedName.split("\\.");
        return tokens[tokens.length - 1];
    }
}

package com.techdegree.testing_shared_helpers;

public class GenericJsonWithLinkGenerator {

    /**
     * Adds custom property with {@code name} and
     * {@code value} to passed {@code json}, with comma
     * or without it depending on {@code withComma}.
     * @param json : JSON string to which parameter will
     *             be added
     * @param name : name of property
     * @param value : value of property
     * @param withComma : adds comma at the ends, if true,
     *                  nothing - otherwise
     * @return String {@code json} plus property and/or comma:
     * {@code "name":"value"}
     * or
     * {@code "name":"value",}
     */
    public static String addCustomProperty(
            String json,
            String name,
            String value,
            boolean withComma
    ) {
        if (withComma) {
            return json +
                    "\"" + name +
                    "\":\"" + value + "\",";
        } else {
            return json +
                    "\"" + name +
                    "\":\"" + value + "\"";
        }
    }
}

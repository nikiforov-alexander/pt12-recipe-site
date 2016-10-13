package com.techdegree.testing_shared_helpers;

import java.util.List;

public class IterablesConverterHelper {

    /**
     * returns size of iterable
     * @param iterable
     * @return size of passed ${@code iterable}
     */
    public static int getSizeOfIterable(Iterable<?> iterable) {
        List<?> list = (List<?>) iterable;
        return list.size();
    }
}

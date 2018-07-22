package pro.landlabs.pricing.test;

import org.joda.time.DateTime;

import java.util.function.BiFunction;

public class TestCommons {

    public static final BiFunction<DateTime, DateTime, DateTime> LAST_DATE_MERGE =
            (existing, incoming) -> existing.isBefore(incoming) ? incoming : existing;

}

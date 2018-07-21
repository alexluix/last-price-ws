package pro.landlabs.pricing.testdata;

import org.joda.time.DateTime;

import java.util.function.BiFunction;

public class TestCommons {

    public static final BiFunction<DateTime, DateTime, DateTime> LAST_DATE_MERGE =
            (existing, incoming) -> existing.isBefore(incoming) ? incoming : existing;

}

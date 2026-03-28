package net.shlab.hogefugapiyo.framework.core.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CurrentTimeProvider {

    Instant currentInstant();

    LocalDate currentDate();

    LocalDateTime currentDateTime();
}

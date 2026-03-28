package net.shlab.hogefugapiyo.framework.time;

import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;


@Component
public class ClockCurrentTimeProvider implements CurrentTimeProvider {

    private final Clock clock;

    public ClockCurrentTimeProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Instant currentInstant() {
        return Instant.now(clock);
    }

    @Override
    public LocalDate currentDate() {
        return LocalDate.now(clock);
    }

    @Override
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now(clock);
    }
}

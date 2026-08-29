package io.project.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@TestConfiguration
public class TestTimeConfig {

    @Bean
    @Primary
    public TestClock testClock() {

        ZoneId zoneId = ZoneId.of("Asia/Seoul");

        LocalDateTime fixedDateTime =
                LocalDateTime.of(
                        2026, 8, 29,
                        13, 59, 59
                );

        return new TestClock(
                fixedDateTime.atZone(zoneId).toInstant(),
                zoneId
        );
    }
}
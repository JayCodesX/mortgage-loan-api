package com.jaycodesx.mortgage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MortgageApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodIsPublicStaticAndAvailable() throws Exception {
        Method mainMethod = MortgageApplication.class.getDeclaredMethod("main", String[].class);

        assertThat(Modifier.isPublic(mainMethod.getModifiers())).isTrue();
        assertThat(Modifier.isStatic(mainMethod.getModifiers())).isTrue();
    }
}

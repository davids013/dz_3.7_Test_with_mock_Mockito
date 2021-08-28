import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.entity.Country;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;

import java.util.stream.Stream;

class LocalizationServiceImplTest {

    @BeforeAll
    private static void start() { Methods.start(); }

    @BeforeEach
    void newTest() { Methods.newTest(); }

    @AfterEach
    private void endTest() { Methods.endTest(); }

    @AfterAll
    private static void end() { Methods.end(); }

    @ParameterizedTest
    @MethodSource("setCountries")
    void localeTest(Country param) {
        System.out.println("Test country: " + param);
        String expected = (param == Country.RUSSIA) ? "Добро пожаловать" : "Welcome";
        LocalizationService localizationService = new LocalizationServiceImpl();

        String result = localizationService.locale(param);
        System.out.println(result);
        Assertions.assertEquals(expected, result);
    }

    public static Stream<Country> setCountries() {
        return Stream.of(Country.values());
    }
}

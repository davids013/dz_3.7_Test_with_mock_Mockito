import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.netology.sender.MessageSenderImpl.IP_ADDRESS_HEADER;

class MessageSenderImplTest {

    @BeforeAll
    private static void start() { Methods.start(); }

    @BeforeEach
    void newTest() { Methods.newTest(); }

    @AfterEach
    private void endTest() {
        System.out.println();
        Methods.endTest();
    }

    @AfterAll
    private static void end() { Methods.end(); }

//    //without Mockito
//    @Test
//    void sendTest() {
//        Map<String, String> argument = new HashMap<>();
//        argument.put(IP_ADDRESS_HEADER, "96.155.23.65");
//        String expected = "Welcome";
//        GeoService geoService = new GeoServiceImpl();
//        LocalizationService localizationService = new LocalizationServiceImpl();
//        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
//        String result = messageSender.send(argument);
//        Assertions.assertEquals(expected, result);
//    }

    @ParameterizedTest
    @MethodSource("setCountries")
    void sendTestMock(Country param) {
        System.out.println("Test country: " + param);
        Map<String, String> header = new HashMap<>();
        header.put(IP_ADDRESS_HEADER, GeoServiceImpl.NEW_YORK_IP);
        Location location = new Location("city", param, "street", 0);
        Map<Country, String> responds = new HashMap<>();
        responds.put(Country.GERMANY, "Guten morgen");
        responds.put(Country.USA, "Welcome");
        responds.put(Country.RUSSIA, "Добро пожаловать");
        responds.put(Country.BRAZIL, "Bom dia");
        String expected = responds.get(location.getCountry());
        GeoService geoService = mock(GeoServiceImpl.class);
        when(geoService.byIp(String.valueOf(header.get(IP_ADDRESS_HEADER))))
                .thenReturn(location);
        LocalizationService localizationService = mock(LocalizationServiceImpl.class);
        when(localizationService.locale(Country.GERMANY)).thenReturn(responds.get(Country.GERMANY));
        when(localizationService.locale(Country.USA)).thenReturn(responds.get(Country.USA));
        when(localizationService.locale(Country.RUSSIA)).thenReturn(responds.get(Country.RUSSIA));
        when(localizationService.locale(Country.BRAZIL)).thenReturn(responds.get(Country.BRAZIL));
        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);

        String result = messageSender.send(header);
        Assertions.assertEquals(expected, result);
    }

    public static Stream<Country> setCountries() {
        return Stream.of(Country.values());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            GeoServiceImpl.LOCALHOST,
            GeoServiceImpl.MOSCOW_IP,
            GeoServiceImpl.NEW_YORK_IP,
            "199.143.65.17"})
    void send_test_with_mock(String param) {
        Map<String, String> argument = new HashMap<>();
        argument.put(IP_ADDRESS_HEADER, param);
        System.out.println("Test map: " + argument.toString());
        String expected = "Correct";
        GeoService geoService = mock(GeoServiceImpl.class);
        Location location = new Location("New York", Country.USA, " 10th Avenue", 32);
        when(geoService.byIp(argument.get(IP_ADDRESS_HEADER))).thenReturn(location);
        LocalizationService localizationService =
                mock(LocalizationServiceImpl.class);
//                new LocalizationServiceImpl();
        when(localizationService.locale(location.getCountry())).thenReturn(expected);

        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        String result = messageSender.send(argument);
        Assertions.assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {GeoServiceImpl.NEW_YORK_IP, GeoServiceImpl.MOSCOW_IP, "105.141.78.21"})
    void sentLanguageTest(String param) {
        Map<String, String> argument = new HashMap<>();
        argument.put(IP_ADDRESS_HEADER, param);
        final String latinRespond = "Welcome";
        final String cyrillicRespond = "Добро пожаловать";
        final Country country =
                (param.startsWith(GeoServiceImpl.MOSCOW_IP.substring(0, 3))) ? Country.RUSSIA : Country.USA;
        final Location location = new Location("city", country, "street", 0);
        final int expected = (country == Country.RUSSIA) ? 1 : 0;
        GeoService geoService = mock(GeoServiceImpl.class);
        when(geoService.byIp(param)).thenReturn(location);
        LocalizationService localizationService = mock(LocalizationServiceImpl.class);
        when(localizationService.locale(Country.RUSSIA)).thenReturn(cyrillicRespond);
        when(localizationService.locale(Country.USA)).thenReturn(latinRespond);
        when(localizationService.locale(Country.BRAZIL)).thenReturn(latinRespond);
        when(localizationService.locale(Country.GERMANY)).thenReturn(latinRespond);

        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        String result = messageSender.send(argument);
        Assertions.assertTrue(expected == getFullStringLanguage(result));
    }

    @ParameterizedTest
    @MethodSource("setArray")
    void testLang(Map<Integer, String> param) {
        System.out.println(param);
        int expected = -2;
        for (int i = -1; i <= 1; i++) {
            if (param.get(i) != null) {
                expected = i;
                break;
            }
        }
        int result = getFullStringLanguage(param.get(expected));
        System.out.println(expected + " <-> " + result);
        Assertions.assertTrue(expected == result);
    }

    private static Stream<Map<Integer, String>> setArray() {
        Map<Integer, String> map1 = new HashMap<>();
        map1.put(0, "Latin test #1");
        Map<Integer, String> map2 = new HashMap<>();
        map2.put(1, "Кириллический тест #2");
        Map<Integer, String> map3 = new HashMap<>();
        map3.put(-1, "Комбинированный test #3");
        return Stream.of(map1, map2, map3);
                //Stream<Map<Integer, String>>) );
    }

    private int getFullStringLanguage(String s) {
//        latinChars  65-122
//        cyrillicChars  1040-1103
        s = s.replaceAll("[ :;_<>!?.,'`~#@%№^&*/\"=0-9]", "");
        char[] array = s.toCharArray();
        int cyrillicCounter = 0;
        int latinCounter = 0;
        for (char c : array) {
            if (
//                    (int) c >= 1040 && (int) c <= 1103
                    Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.CYRILLIC)
                ) {
                cyrillicCounter++;
            } else if (Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.BASIC_LATIN)) {
                latinCounter++;
            } else return -1;
        }
        if (cyrillicCounter == 0 && latinCounter > 0) {
            return 0;
        } else if (cyrillicCounter > 0 && latinCounter == 0) {
            return 1;
        } else return -1;
    }
}

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
    private LocalizationService localizationService;
    private GeoService geoService;
    private MessageSender messageSender;

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

//    @Test
//    void sendTest() {
//        Map<String, String> argument = new HashMap<>();
//        argument.put(IP_ADDRESS_HEADER, "96.155.23.65");
//        String expected = "Welcome";
//        geoService = new GeoServiceImpl();
//        localizationService = new LocalizationServiceImpl();
//        messageSender = new MessageSenderImpl(geoService, localizationService);
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
        geoService = mock(GeoServiceImpl.class);
        when(geoService.byIp(String.valueOf(header.get(IP_ADDRESS_HEADER))))
                .thenReturn(location);
        localizationService = mock(LocalizationServiceImpl.class);
        when(localizationService.locale(Country.GERMANY)).thenReturn(responds.get(Country.GERMANY));
        when(localizationService.locale(Country.USA)).thenReturn(responds.get(Country.USA));
        when(localizationService.locale(Country.RUSSIA)).thenReturn(responds.get(Country.RUSSIA));
        when(localizationService.locale(Country.BRAZIL)).thenReturn(responds.get(Country.BRAZIL));
        messageSender = new MessageSenderImpl(geoService, localizationService);

        String result = messageSender.send(header);
        Assertions.assertEquals(expected, result);
    }

    public static Stream<Country> setCountries() {
        return Stream.of(Country.values());
    }
}

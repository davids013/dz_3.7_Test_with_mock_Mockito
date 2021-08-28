import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;

public class GeoServiceImplTest {

    @BeforeAll
    private static void start() { Methods.start(); }

    @BeforeEach
    void newTest() { Methods.newTest(); }

    @AfterEach
    private void endTest() { Methods.endTest(); }

    @AfterAll
    private static void end() { Methods.end(); }

    @ParameterizedTest
    @ValueSource(strings = {
            GeoServiceImpl.MOSCOW_IP,
            GeoServiceImpl.NEW_YORK_IP,
            "451.741.79.25"})
    void byIdTest(String param) {
        System.out.println("Test ip: " + param);
        GeoService geoService = new GeoServiceImpl();
        Location expected = null;
        if (GeoServiceImpl.NEW_YORK_IP.equals(param)) {
            expected = new Location("New York", Country.USA, " 10th Avenue", 32);
        } else if (GeoServiceImpl.MOSCOW_IP.equals(param)){
            expected = new Location("Moscow", Country.RUSSIA, "Lenina", 15);
        } else if (param.startsWith(GeoServiceImpl.NEW_YORK_IP.substring(0, 3))) {
            expected = new Location("New York", Country.USA, null,  0);
        } else if (param.startsWith(GeoServiceImpl.MOSCOW_IP.substring(0, 4))) {
            expected = new Location("Moscow", Country.RUSSIA, null, 0);
        }
        System.out.println((expected != null) ? expected.getCity() : null);
        Location result = geoService.byIp(param);
        Assertions.assertTrue(isLocationsEquals(expected, result));
    }

    private boolean isLocationsEquals(Location a, Location b) {
        return (a == null && b == null)
                || (a.getCountry().equals(b.getCountry())
                && a.getCity().equals(b.getCity())
                && a.getStreet().equals(b.getStreet())
                && a.getBuiling() == b.getBuiling());
    }
}

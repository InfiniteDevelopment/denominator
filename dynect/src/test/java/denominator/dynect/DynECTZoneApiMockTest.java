package denominator.dynect;

import com.squareup.okhttp.mockwebserver.MockResponse;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;

import denominator.ZoneApi;
import denominator.model.Zone;

import static denominator.assertj.ModelAssertions.assertThat;
import static denominator.dynect.DynECTTest.noZones;
import static denominator.dynect.DynECTTest.zones;
import static org.testng.Assert.assertFalse;

@Test(singleThreaded = true)
public class DynECTZoneApiMockTest {

  MockDynECTServer server;

  @Test
  public void iteratorWhenPresent() throws Exception {
    server.enqueueSessionResponse();
    server.enqueue(new MockResponse().setBody(zones));

    ZoneApi api = server.connect().api().zones();
    Iterator<Zone> domains = api.iterator();

    assertThat(domains.next())
        .hasName("0.0.0.0.d.6.e.0.0.a.2.ip6.arpa");
    assertThat(domains.next())
        .hasName("126.12.44.in-addr.arpa");
    assertThat(domains.next())
        .hasName("denominator.io");
    assertFalse(domains.hasNext());

    server.assertSessionRequest();
    server.assertRequest().hasPath("/Zone");
  }

  @Test
  public void iteratorWhenAbsent() throws Exception {
    server.enqueueSessionResponse();
    server.enqueue(new MockResponse().setBody(noZones));

    ZoneApi api = server.connect().api().zones();
    assertFalse(api.iterator().hasNext());

    server.assertSessionRequest();
    server.assertRequest().hasPath("/Zone");
  }

  @BeforeMethod
  public void resetServer() throws IOException {
    server = new MockDynECTServer();
  }

  @AfterMethod
  public void shutdownServer() throws IOException {
    server.shutdown();
  }
}

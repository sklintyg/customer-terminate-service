package se.inera.intyg.cts.infrastructure.integration.tellustalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import se.inera.intyg.cts.infrastructure.integration.tellustalk.dto.TellusTalkResponseDTO;

@ExtendWith(MockitoExtension.class)
class SendSMSWithTellusTalkTest {


  //Interacting with MockWebServer from our test cases allows our code to use real HTTP calls to a local endpoint.
  public static MockWebServer mockBackEnd;

  private SendSMSWithTellusTalk sendSMS;

  private final String scheme = "http";
  private final String baseUrl = "localhost";
  private final String tellustalkSendEndpoint = "/send/v1";

  @BeforeAll
  static void setUp() throws IOException {
    mockBackEnd = new MockWebServer();
    mockBackEnd.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockBackEnd.shutdown();
  }

  @BeforeEach
  void initialize() {
    sendSMS = new SendSMSWithTellusTalk(WebClient.create(baseUrl), scheme, baseUrl,
        mockBackEnd.getPort(), tellustalkSendEndpoint, "Username", "Password");
  }

  @Test
  void sendSMS() throws JsonProcessingException, InterruptedException {
    ReflectionTestUtils.setField(sendSMS, "smsOriginatorText", "Inera AB");
    String phoneNumber = "sms:+46701234567";
    String message = "Hej hej";
    TellusTalkResponseDTO smsResponseDTO = new TellusTalkResponseDTO("JobId", "logHref");
    ObjectMapper objectMapper = new ObjectMapper();
    mockBackEnd.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(smsResponseDTO))
        .addHeader("Content-Type", "application/json"));

    TellusTalkResponseDTO response = sendSMS.sendSMS(phoneNumber, message);

    assertEquals(response.job_id(), smsResponseDTO.job_id());
    assertEquals(response.log_href(), smsResponseDTO.log_href());

    RecordedRequest recordedRequest = mockBackEnd.takeRequest();
    assertEquals("POST", recordedRequest.getMethod());
    assertEquals("/localhost/send/v1", recordedRequest.getPath());
    assertTrue(recordedRequest.getBody().toString().contains(phoneNumber));
    assertTrue(recordedRequest.getBody().toString().contains(message));
  }
}

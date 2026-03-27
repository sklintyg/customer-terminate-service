/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.cts.integrationtest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class NotificationIT {

  private TestData testData;

  private static final TypeRef<List<Map<String, List<String>>>> LIST_MESSAGES = new TypeRef<>() {};

  @BeforeEach
  void setUp() {
    RestAssured.baseURI =
        System.getProperty("integration.tests.baseUrl", "http://cts.localtest.me");
    testData = TestData.create();
  }

  @AfterEach
  void tearDown() {
    testData.cleanUp();
    RestAssured.reset();
  }

  @Nested
  class TestNotificationSMS {

    @Test
    void shouldSendSmsNotificationToOrganizationRepresentativeWhenUploadedPackage() {
      testData
          .defaultTermination()
          .certificates(50)
          .collectCertificates()
          .certificateTexts(10)
          .collectCertificateTexts()
          .uploadPackage("password")
          .setup();

      given()
          .when()
          .post("/api/v1/exports/sendNotification")
          .then()
          .statusCode(HttpStatus.OK.value());

      final var notificationSentBySMS = getNotificationSentBySMS();

      assertTrue(
          notificationSentBySMS.startsWith(
              "Hej, du har namngivits som ansvarig för att hämta " + "ett exportfilspaket"));
    }

    @Test
    void shouldSendSmsReminderToOrganizationRepresentativeAfter14Days() {
      testData
          .defaultTermination()
          .certificates(50)
          .collectCertificates()
          .certificateTexts(10)
          .collectCertificateTexts()
          .uploadPackage("password")
          .notificationSent(LocalDateTime.now().minusDays(15L))
          .setup();

      given().when().post("/api/v1/exports/sendReminder").then().statusCode(HttpStatus.OK.value());

      final var reminderSentBySMS = getNotificationSentBySMS();

      assertTrue(
          reminderSentBySMS.startsWith(
              "PÅMINNELSE Du har namngivits som ansvarig för att " + "hämta ett exportfilspaket "));
    }

    @Test
    void shouldNotSendSmsReminderToOrganizationRepresentativeBefore14Days() {
      testData
          .defaultTermination()
          .certificates(50)
          .collectCertificates()
          .certificateTexts(10)
          .collectCertificateTexts()
          .uploadPackage("password")
          .notificationSent(LocalDateTime.now().minusDays(13L))
          .setup();

      given().when().post("/api/v1/exports/sendReminder").then().statusCode(HttpStatus.OK.value());

      final var reminderSentBySMS = getNotificationSentBySMS();

      assertEquals("", reminderSentBySMS);
    }
  }

  @Nested
  class TestNotificationEmail {

    @Test
    void shouldSendEmailNotificationToOrganizationRepresentativeWhenUploadedPackage() {
      final var data =
          testData
              .defaultTermination()
              .certificates(50)
              .collectCertificates()
              .certificateTexts(10)
              .collectCertificateTexts()
              .uploadPackage("password")
              .setup();

      given()
          .when()
          .post("/api/v1/exports/sendNotification")
          .then()
          .statusCode(HttpStatus.OK.value());

      final var notifications = getNotificationSentByEmail();

      final var subject = notifications.get(0).get("subject").get(0);
      final var content = notifications.get(0).get("content").get(0);
      final var senders = notifications.get(0).get("senders");
      final var recipients = notifications.get(0).get("recipients");
      final var status = getStatus(data.terminationIds().get(0));

      assertAll(
          () -> assertEquals(1, notifications.size()),
          () -> assertEquals(1, senders.size()),
          () -> assertEquals(1, recipients.size()),
          () -> assertEquals("Exportfilspaket tillgängligt", subject),
          () -> assertEquals("no-reply.intyg@dev.cts.se", senders.get(0)),
          () -> assertEquals("email@address.se", recipients.get(0)),
          () ->
              assertTrue(
                  content.startsWith(
                      "<p>Hej, du har "
                          + "namngivits som ansvarig för att hämta ett exportfilspaket")),
          () -> assertEquals("NOTIFICATION_SENT", status));
    }

    @Test
    void shouldSendEmailReminderToOrganizationRepresentativeAfter14Days() {
      final var data =
          testData
              .defaultTermination()
              .certificates(50)
              .collectCertificates()
              .certificateTexts(10)
              .collectCertificateTexts()
              .uploadPackage("password")
              .notificationSent(LocalDateTime.now().minusDays(15L))
              .setup();

      given().when().post("/api/v1/exports/sendReminder").then().statusCode(HttpStatus.OK.value());

      final var reminders = getNotificationSentByEmail();

      final var subject = reminders.get(0).get("subject").get(0);
      final var content = reminders.get(0).get("content").get(0);
      final var senders = reminders.get(0).get("senders");
      final var recipients = reminders.get(0).get("recipients");
      final var status = getStatus(data.terminationIds().get(0));

      assertAll(
          () -> assertEquals(1, reminders.size()),
          () -> assertEquals(1, senders.size()),
          () -> assertEquals(1, recipients.size()),
          () -> assertEquals("Påminnelse - Exportfilspaket tillgängligt", subject),
          () -> assertEquals("no-reply.intyg@dev.cts.se", senders.get(0)),
          () -> assertEquals("email@address.se", recipients.get(0)),
          () ->
              assertTrue(
                  content.startsWith(
                      "<p>PÅMINNELSE<br>Du "
                          + "har namngivits som ansvarig för att hämta ett exportfilspaket")),
          () -> assertEquals("REMINDER_SENT", status));
    }

    @Test
    void shouldNotSendEmailReminderToOrganizationRepresentativeBefore14Days() {
      final var data =
          testData
              .defaultTermination()
              .certificates(50)
              .collectCertificates()
              .certificateTexts(10)
              .collectCertificateTexts()
              .uploadPackage("password")
              .notificationSent(LocalDateTime.now().minusDays(13L))
              .setup();

      given().when().post("/api/v1/exports/sendReminder").then().statusCode(HttpStatus.OK.value());

      final var reminders = getNotificationSentByEmail();

      final var status = getStatus(data.terminationIds().get(0));
      assertAll(
          () -> assertEquals(0, reminders.size()), () -> assertEquals("NOTIFICATION_SENT", status));
    }
  }

  private String getNotificationSentBySMS() {
    return given()
        .baseUri("http://localhost:18000")
        .pathParam("phoneNumber", TestData.PHONE_NUMBER)
        .when()
        .get("/testability-tellustalk/v1/smsNotifications/{phoneNumber}")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .asString();
  }

  private List<Map<String, List<String>>> getNotificationSentByEmail() {
    return given()
        .baseUri("http://localhost:18000")
        .pathParam("emailAddress", TestData.EMAIL_ADDRESS)
        .when()
        .get("/testability-email/v1/{emailAddress}")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .as(LIST_MESSAGES);
  }

  private String getStatus(String terminationId) {
    return given()
        // .baseUri("http://localhost:18010")
        .pathParam("terminationId", terminationId)
        .when()
        .get("/testability/v1/terminations/{terminationId}/status")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .asString();
  }
}

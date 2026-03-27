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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import se.inera.intyg.cts.application.dto.CreateTerminationDTO;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.application.dto.UpdateTerminationDTO;

class TerminationIT {

  private TestData testData;

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

  @Test
  void shallCreateTermination() {
    testData.setup();

    final var createTerminationDTO =
        new CreateTerminationDTO(
            "CREATORHSA-ID",
            "Creator Name",
            "HSA-ID",
            "ORG-NO",
            "191212121212",
            "000-1111-2222",
            "email@address.se");

    final var terminationDTO =
        given()
            .contentType(ContentType.JSON)
            .body(createTerminationDTO)
            .when()
            .post("/api/v1/terminations")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response()
            .as(TerminationDTO.class);

    testData.terminationId(terminationDTO.terminationId().toString());

    assertNotNull(terminationDTO.terminationId());
  }

  @Test
  void shallUpdateTermination() {
    testData.defaultTermination().setup();

    final var terminationDTO =
        given()
            .pathParam("terminationId", testData.terminationIds().get(0))
            .when()
            .get("/api/v1/terminations/{terminationId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response()
            .as(TerminationDTO.class);

    final var updateTerminationDTO =
        new UpdateTerminationDTO(
            "NEWHSA-ID", "2012121212-1212", "070-44556677", "newEmail@address.se");

    final var actualTerminationDTO =
        given()
            .pathParam("terminationId", testData.terminationIds().get(0))
            .contentType(ContentType.JSON)
            .body(updateTerminationDTO)
            .when()
            .post("/api/v1/terminations/{terminationId}")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response()
            .as(TerminationDTO.class);

    assertAll(
        () -> assertEquals(updateTerminationDTO.hsaId(), actualTerminationDTO.hsaId()),
        () -> assertEquals(updateTerminationDTO.personId(), actualTerminationDTO.personId()),
        () -> assertEquals(updateTerminationDTO.phoneNumber(), actualTerminationDTO.phoneNumber()),
        () ->
            assertEquals(updateTerminationDTO.emailAddress(), actualTerminationDTO.emailAddress()));
  }

  @Test
  void shallReturnTermination() {
    testData.defaultTermination().setup();

    final var terminationDTO =
        given()
            .pathParam("terminationId", testData.terminationIds().get(0))
            .when()
            .get("/api/v1/terminations/{terminationId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response()
            .as(TerminationDTO.class);

    assertEquals(testData.terminationIds().get(0), terminationDTO.terminationId().toString());
  }

  @Test
  void shallReturnTerminations() {
    testData.defaultTerminations(10).setup();

    final var terminationDTO =
        given()
            .when()
            .get("/api/v1/terminations")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response()
            .as(TerminationDTO[].class);

    testData
        .terminationIds()
        .forEach(
            terminationId ->
                assertTrue(
                    Stream.of(terminationDTO)
                        .anyMatch(
                            termination ->
                                terminationId.equalsIgnoreCase(
                                    termination.terminationId().toString())),
                    String.format("Missing termination with id '%s'", terminationId)));
  }
}

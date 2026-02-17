# Customer Terminate Service (CTS)

This is a service that has the responsibility to handle termination of customers.

The service doesn't have it own GUI but exposed a Rest API for handling terminations i.e.
registration of terminations.

In order to export a customers certificates, notify representatives, erasing data etc. the service
is dependent on a number of other services.

See [Integrations](##Integrations) for a description of each integration.

## Running CTS locally

You can either run CTS locally inside IntelliJ (using Run-configurations) or by using the included
Gradle wrapper.

When starting CTS locally for development and testing, you want to make sure that the following
Spring profiles are active: `testability,dev`

They are activated automatically when running Gradle (`./gradlew bootRun`), but in IntelliJ you have
to add the Spring profiles to your Run-configuration (`Active profiles`) before starting the
service.

## Integrations

**Intygstjanst** - Erase related data to terminated customer.

**Intygsstatistik** - Erase related data to terminated customer.

**Privatlakarportal** - Erase related data to terminated customer.

**SMTP Service** - Send emails to organisation representatives.

**SJUT** - Export encrypted package to SJUT for organisation representatives to download the package
safely.

**TellusTalk** - Send sms to organisation representatives.

**Webcert** - Erase related data to terminated customer.

### Integrations locally

When running CTS locally in the development environment, the default configuration uses
core-stub-service (CSS)to handle external services like SMTP, SJUT and TellusTalk.

By default local running services of Intygstjanst, Intygstatistik and Webcert are used. But CTS can
be configured to use CSS for all dependent services by changing `*.baseUrl`
in `application-dev.properties`.

## Swagger Api Documentation

Local URL: http://localhost:18010/swagger-ui/index.html
URL: https://cts.localtest.me/swagger-ui/index.html

## Licens

Copyright (C) 2026 Inera AB (http://www.inera.se)

Terminate Service is free software: you can redistribute it and/or modify it under the terms of the
GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Terminate Service is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

Se även [LICENSE.md](LICENSE.md). 

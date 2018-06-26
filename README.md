# OGEL service

Maintains and provides information about OGELs (Open General Export Licences) for LITE frontend applications.

## Running locally

* `git clone git@github.com:uktrade/lite-ogel-service.git`
* `cd lite-ogel-service` 
* `cp src/main/resources/sample-config.yaml src/main/resources/config.yaml`
* `./gradlew run`

A PostgreSQL database is used. To populate it, you can use bootstrap
data from the `lite-infrastructure-config` repo:

* `git clone git@github.com:uktrade/lite-infrastructure-config.git`
* `cd lite-infrastructure-config/service-bootstrap`
* `curl -X PUT -T data/ogels.json -H "Content-Type: application/json" -u user:pass http://localhost:8080/ogels`
* `curl -X PUT -T data/ogel-control-code-conditions.json -H "Content-Type: application/json" -u user:pass http://localhost:8080/control-code-conditions`

## Service overview

This service combines OGEL data retrieved from SPIRE (`SpireOgel`) with locally maintained content data (`LocalOgel`) 
for display in a frontend application (see `OgelFullView`). The local data is stored in a PostgreSQL database and maintained
using REST endpoints.
 
The service also provides OGEL matching logic for the permissions finder, based on the SPIRE OGEL specifications.

## Endpoint summary

* `/admin` (`AdminResource`)

Administrative endpoints for the service. Currently exposes `/validate` which checks that all local data correctly matches
SPIRE data.

* `/applicable-ogels` (`ApplicableOgelResource`)

Retrieves a list of OGELs which can be used with the given parameters (goods' control code, source/destination countries, etc).
Used by the permissions finder to find a list of OGELs to offer to a user.

* `/ogels` (`OgelResource`)

Maintains and exposes additional content data about an OGEL - namely the plain English "can/can't/must/how to use" lists 
which are displayed in the permissions finder. These are used instead of the standard legalese of the OGEL licence description.

* `/virtual-eu` (`VirtualEuResource`)

Endpoint to determine if the given export details are covered under the "virtual EU" OGEL (and therefore do not require any licence).

## SPIRE integration

The `SpireOgelClient` is used to retrieve OGEL data from SPIRE, which is currently the canonical data source. Data is cached
in the `SpireOgelService`. The `RefreshCacheJob` refreshes this data on a daily basis.

## Local database

OGEL plain English descriptions are maintained in the `LocalOgelDAO`.


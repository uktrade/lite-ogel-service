# OGEL service

Maintains and provides information about OGELs (Open General Export Licences) for LITE frontend applications.

## Running locally

* `git clone git@github.com:uktrade/lite-ogel-service.git`
* `cd lite-ogel-service`
* `cp src/main/resources/sample-config.yaml src/main/resources/config.yaml`
* `./gradlew run`

A PostgreSQL database is used. To populate it, you can use bootstrap data from the `service-config` repo:

* `git clone git@gitlab.ci.uktrade.io:lite/service-config.git`
* `cd service-config`
* `curl -X PUT -T data/ogel_service.json -H "Content-Type: application/json" -u admin:password http://localhost:8080/ogels`

## Service overview

This service combines OGEL data retrieved from SPIRE (`SpireOgel`) with locally maintained content data (`LocalOgel`)
for display in a frontend application (see `OgelFullView`). The local data is stored in a PostgreSQL database and maintained
using REST endpoints.

The service also provides OGEL matching logic for the permissions finder, based on the SPIRE OGEL specifications.

## Endpoint summary

* `/validate` (`ValidateResource`)

Currently exposes `/validate` which checks that all local data correctly matches SPIRE data.

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

### GDS PaaS Deployment

This repo contains a pre-packed deployment file, lite-ogel-service-xxxx.jar.  This can be used to deploy this service manually from the CF cli.  Using the following command:

* cf push [app_name] -p lite-ogel-service-xxxx.jar

For this application to work the following dependencies need to be met:

* Bound PG DB (all services share the same backend db)
* Env VARs will need to be set

### Archive state

This repo is now archived. If you need to deploy this application, you can find a copy of the DB and VARs in the DIT AWS account.

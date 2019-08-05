# Nukr, a new social media product by Nu Everything S/A.

> Nukr is a prototype service that provides a REST API where we can simulate connections between people, and explore how we would offer new connection suggestions.

## Build, run and tests

Nukr was written in [Clojure](https://clojure.org) and it uses [Leiningen](https://leiningen.org/) to support build and tests tasks. The project was based on [pedestal service template](https://github.com/pedestal/pedestal/tree/master/service-template). To run tasks, at command line, `cd` to nukr root directory then:

### Run tests

	lein test
	
### Run nukr REST API

	lein run
	
Web application will listen to 8080 port. If this port is not available, edit [src/nukr/service.clj](src/nukr/service.clj) (service configuration section close to end of file) in order to change it.

REST API routes are described in depth at the [REST API Routes](#rest-api-routes) section. You may use [nukr.postman_collection.json](nukr.postman_collection.json) to bootstrap [postman](https://www.getpostman.com/) pre-configurated to nukr REST API routes.

In order to quickly get fun with REST API, you may invoke the [build sample network](#build-sample-network) to get it populate with some profiles and connections.

### Build standalone uberjar

	lein uberjar
	
### Build [docker](https://www.docker.com/) image

Build uberjar described at [build standalone uberjar](#build-standalone-uberjar) step above then:

	sudo docker build -t nukr .
	
### Run docker container

After executed [build standalone uberjar](#build-standalone-uberjar) and [build docker image](#build-docker-image) steps:

	sudo docker run -p 8080:8080 nukr

## REST API routes

### Create profile

Create a new profile.

	POST /v1/profiles

#### Parameters

| Name    | Type                      | Location    | Description   |
|---------|---------------------------|-------------|---------------|
| profile | [profile-in](#profile-in) | body (json) | Profile data. |

#### Responses

| Status | Type                                         | Description         |
|--------|----------------------------------------------|---------------------|
| 201    | [profile-details-out](#profile-details-out)  | Created profile.    |
| 400    | [prof. input errors](#profile-input-errors)  | Invalid input data. |
| 507    | [net. over capacity](#network-over-capacity) | Over capacity.      |

### Edit profile

Edit an existing profile.

	PUT /v1/profiles/:id

#### Parameters

| Name    | Type                      | Location    | Description         |
|---------|---------------------------|-------------|---------------------|
| id      | `uuid`                    | path        | Profile identifier. |
| profile | [profile-in](#profile-in) | body (json) | Profile data.       |

#### Responses

| Status | Type                                        | Description         |
|--------|---------------------------------------------|---------------------|
| 200    | [profile-details-out](#profile-details-out) | Updated profile.    |
| 400    | [prof. input errors](#profile-input-errors) | Invalid input data. |
| 404    | [profile not found](#profile-not-found)     | Profile not found.  |

### Profile details

Get the profile details for a given profile id (on route).

	GET /v1/profiles/:id

#### Parameters

| Name | Type   | Location | Description         |
|------|--------|----------|---------------------|
| id   | `uuid` | path     | Profile identifier. |

#### Responses

| Status | Type                                        | Description        |
|--------|---------------------------------------------|--------------------|
| 200    | [profile-details-out](#profile-details-out) | Profile details.   |
| 404    | [profile not found](#profile-not-found)     | Profile not found. |

### List profiles

List profiles on network.

	GET /v1/profiles

#### Parameters

| Name       | Type                      | Location | Description         |
|------------|---------------------------|----------|---------------------|
| pagination | [paginat-in](#paginat-in) | query    | Pagination options. |

#### Responses

| Status | Type                                            | Description   |
|--------|-------------------------------------------------|---------------|
| 200    | [profile-list-page-out](#profile-list-page-out) | Profile list. |

### Connect profiles

Connect a profile (from profile id on route) to another profile provided on body (json).

	POST /v1/profiles/:id/connections

#### Parameters

| Name       | Type                     | Location    | Description            |
|------------|--------------------------|-------------|------------------------|
| id         | `uuid`                   | path        | Profile identifier.    |
| connect-to | [connect-to](#connect-to)| body (json) | Profile to connect to. |

#### Responses

| Status | Type                                        | Description           |
|--------|---------------------------------------------|-----------------------|
| 200    | [profile-details-out](#profile-details-out)| New connection details.|
| 400    | [connecting-errors](#connecting-errors)    | Invalid connection.    |
| 404    | [profile not found](#profile-not-found)    | Profile not found.     |

### Profile connections

List connections for a given profile id (on route).

	GET /v1/profiles/:id/connections

#### Parameters

| Name       | Type                      | Location | Description         |
|------------|---------------------------|----------|---------------------|
| id         | `uuid`                    | path     | Profile identifier. |
| pagination | [paginat-in](#paginat-in) | query    | Pagination options. |

#### Responses

| Status| Type                                            | Description        |
|-------|-------------------------------------------------|--------------------|
| 200   | [profile-list-page-out](#profile-list-page-out) | Connection list.   |
| 404   | [profile not found](#profile-not-found)         | Profile not found. |

### Connection suggestions

Generate a list of new connection suggestions for a given profile id (on route).

	GET /v1/profiles/:id/suggestions

#### Parameters

| Name       | Type                      | Location | Description         |
|------------|---------------------------|----------|---------------------|
| id         | `uuid`                    | path     | Profile identifier. |
| pagination | [paginat-in](#paginat-in) | query    | Pagination options. |

#### Responses

| Status | Type                                           | Description        |
|--------|------------------------------------------------|--------------------|
| 200    | [profile-list-page-out](#profile-list-page-out)| Suggestion list.   |
| 404    | [profile not found](#profile-not-found)        | Profile not found. |

### Build sample network

Reset database and build a sample network with some profiles and connections.

	POST /v1/build_sample_network
	
This route takes no parameters and responds with 200 status code with informational text.

## Models

### profile-in

Input model for [create profile](#create-profile) and [edit profile](#edit-profile).

| Name    | Type     | Required | Description                                |
|---------|----------|----------|--------------------------------------------|
| name    | `string` | yes      | Profile's name.                            |
| email   | `string` | yes      | Profile's email.                           |
| visible | `bool`   | no       | Visibility for the connection suggestions. |

Notes:

- Profile's email must be unique over the network and match regexp `^[a-z0-9.+_-]+@[a-z0-9]{2,}(\.[a-z0-9]{2,})+$`.
- **visible** default value is `true`.

Example:

```json
{
    "name": "Foo Bar",
    "email": "foo.bar@nukr.com",
    "visible": true
}
```

Applies to:

- [Create profile](#create-profile) - `POST /v1/profiles`
- [Edit profile](#edit-profile) - `PUT /v1/profiles/:id`

### profile-details-out

Output model for [create profile](#create-profile), [edit profile](#edit-profile), [profile details](#profile-details), [connect profiles](#connect-profiles), [remove profile](#remove-profile) and [remove connection](#remove-connection).

| Name        | Type       | Description                                |
|-------------|------------|--------------------------------------------|
| id          | `uuid`     | Profile identifier.                        |
| name        | `string`   | Profile name.                              |
| email       | `string`   | Profile email.                             |
| visible     | `bool`     | Visibility for the connection suggestions. |
| createdat   | `datetime` | Creation date.                             |
| updatedat   | `datetime` | Last update date (could be null).          |
| connections | `int`      | Number of connections.                     |

Notes:

- If profile never updated, updatedat will be `null`.
- Datetime in UTC [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) format.

Example:

```json
{
    "id": "7a0b5891-ce51-455d-b8d7-a46e223be220",
    "name": "Foo",
    "email": "foo@bar.com",
    "visible": true,
    "createdat": "2019-08-03T13:54:09Z",
    "updatedat": null,
    "connections": 0
}
```

Applies to:

- [Create profile](#create-profile) - `POST /v1/profiles`
- [Edit profile](#edit-profile) - `PUT /v1/profiles/:id`
- [Profile details](#profile-details) - `GET /v1/profiles/:id`
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections`

### paginat-in

| Name    | Type  | Default | Description               |
|---------|-------|---------|---------------------------|
| page    | `int` | 1       | Page number to retrieve.  |
| perpage | `int` | 10      | Number of items per page. |

Notes:
- **perpage** value is limited to 50.

Examples:
- `/v1/profiles/?page=4`
- `/v1/profiles/?page=2&perpage=20`

Applies to:
- [List profiles](#list-profiles) - `GET /v1/profiles`
- [Connection suggestions](#connection-suggestions) - `GET /v1/profiles/:id/suggestions`
- [Profile connections](#profile-connections) - `GET /v1/profiles/:id/connections`

### profile-list-page-out

| Name     | Type                                              | Description   |
|----------|---------------------------------------------------|---------------|
| total    | `int`                                             | Total items.  |
| showing  | `int`                                             | Items on page.|
| page     | `int`                                             | Current page. |
| pages    | `int`                                             | Total pages.  |
| dropping | `int`                                             | Skiped items. |
| items    | [profile-sintetic-out](#profile-sintetic-out) list| Item list.    |

Examples:

Response for request `GET /v1/profiles/?page=1&perpage=5` with total of 50 profile on network:
```json
{
    "total": 50,
    "showing": 5,
    "page": 1,
    "pages": 10,
    "dropping": 0,
    "items": [
        {
            "id": "2f083fc1-3fa2-41c8-a9b7-92bedf1dc35f",
            "name": "Brett Goodwin"
        },
        {
            "id": "d06c6599-828e-4c89-b722-325422428ac6",
            "name": "Luz Richardson"
        },
        {
            "id": "b077300a-a62d-4a39-b42e-46d7dcc8ba26",
            "name": "Anton Hardy"
        },
        {
            "id": "c24aea74-f9f3-43f5-9f39-27962825ef40",
            "name": "Cadence Goodman"
        },
        {
            "id": "581be11c-85a5-465e-b38e-9ca3076b45e2",
            "name": "Griffin Cervantes"
        }
    ]
}
```
Response for request `GET /v1/profiles/?page=2&perpage=5` with total of 50 profile on network:
```json
{
    "total": 50,
    "showing": 5,
    "page": 2,
    "pages": 10,
    "dropping": 5,
    "items": [
        {
            "id": "4f168fd5-6791-4d7e-8d33-38172f692e8f",
            "name": "Darnell Blake"
        },
        {
            "id": "99612d69-0289-41d7-a336-14cc85d86cd7",
            "name": "Tyshawn Diaz"
        },
        {
            "id": "1451d911-1aed-4bbf-beb3-d392618c6698",
            "name": "Paul Moran"
        },
        {
            "id": "958e7d58-c2ce-44ea-a570-6ca304a31697",
            "name": "Leticia Clements"
        },
        {
            "id": "f8076982-afe3-45de-95e1-a2fe95ff772b",
            "name": "Rhianna Grant"
        }
    ]
}
```

Applies to:
- [List profiles](#list-profiles) - `GET /v1/profiles`
- [Connection suggestions](#connection-suggestions) - `GET /v1/profiles/:id/suggestions`
- [Profile connections](#profile-connections) - `GET /v1/profiles/:id/connections`

### profile-sintetic-out

| Name | Type     | Description         |
|------|----------|---------------------|
| id   | `uuid`   | Profile identifier. |
| name | `string` | Profile name.       |

Applies to:
- [profile-list-page-out](#profile-list-page-out)

### connect-to

| Name | Type   | Description                       |
|------|--------|-----------------------------------|
| id   | `uuid` | Connecting to profile identifier. |

Example

```json
{
    "id": "d06c6599-828e-4c89-b722-325422428ac6"
}
```

Applies to:
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections`

### core-error-coll

| Name   | Type                                        | Description        |
|--------|---------------------------------------------|--------------------|
| errors | List of [core-error-item](#core-error-item) | Error reason list. |

Note: see [validation errors model](validation-errors-model) section for more details.

Example:

```json
{
    "errors": [
        {
            "key": "profile-invalid-email",
            "msg": "Profile e-mail is invalid."
        },
        {
            "key": "profile-name-required",
            "msg": "Profile name is required."
        }
    ]
}
```

Applies to:
- [Profile input errors](#profile-input-errors)
- [Profile not found](#profile-not-found)
- [Connecting errors](#connecting-errors)

### core-error-item

| Name | Type     | Description    |
|------|----------|----------------|
| key  | `string` | Error key.     |
| msg  | `string` | Error message. |

Applies to:
- [core-error-coll](#core-error-coll)

## Validation errors model

Validation errors model is [core-error-coll](#core-error-coll) which consist in a collection of [core-error-item](#core-error-item) containing a `key`/`msg` for each error. Possibles [core-error-item](#core-error-item) for each situation are described below.

### Profile input errors

The table below enumerate the possibles [core-error-item](#core-error-item) while [creating](#create-profile) or [editing](#edit-profile) profile by its key and condition.

| Key                      | Condition                                   |
|--------------------------|---------------------------------------------|
| `profile-name-required`  | Name is empty or blank.                     |
| `profile-email-required` | Email is empty or blank.                    |
| `profile-invalid-email`  | Email did'nt match email regexp validation. |
| `profile-email-exists`   | Email already exists in network.            |

Applies to:
- [Create profile](#create-profile) - `POST /v1/profiles`
- [Edit profile](#edit-profile) - `PUT /v1/profiles/:id`

### Profile not found

Profile not found error happens while trying to retrive data or perform operations for a specific profile by providing its id on route and there is no profile for the given id.

| Key                  | Condition                            |
|----------------------|--------------------------------------|
| `profile-not-found`  | Profile not found for the given id.  |

Applies to:

- [Edit profile](#edit-profile) - `PUT /v1/profiles/:id`
- [Profile details](#profile-details) - `GET /v1/profiles/:id`
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections`
- [Connection suggestions](#connection-suggestions) - `GET /v1/profiles/:id/suggestions`
- [Profile connections](#profile-connections) - `GET /v1/profiles/:id/connections`

### Connecting errors

The table below enumerate the possibles [core-error-item](#core-error-item) while [connecting profiles](#connect-profiles) by its key and condition.

| Key                            | Condition                                   |
|--------------------------------|---------------------------------------------|
| `to-connect-profile-not-found` | Trying to connect to a nonexistent profile. |
| `could-not-connect-itself`     | Trying to connect to itself.                |
| `profiles-already-connected`   | Profiles are already connected.             |
| `conn-limit-reached`           | Max number of connections reached (1000).   |
| `conn-to-limit-reached`        | Connecting to max number of conn. reached.  |

Applies to:
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections`

### Network over capacity

In order to network's healthiness garanties, total number of profiles is limited to 10000. The `network-over-capacity` error reason will be raised while trying to [create profile](#create-profile) and this limit was reached. A warnning log will be generated for this event in order to backend team get knowledge of the issue.

| Key                     | Condition                                        |
|-------------------------|--------------------------------------------------|
| `network-over-capacity` | Not accepting new profiles due capacity reasons. |

Applies to:
- [Create profile](#create-profile) - `POST /v1/profiles`

## Uncaught exceptions/errors handling

If an unforeseen error happens, nukr will generate an identifier (uuid), log the error/exception linked to it and expose the generated id on response in order to provide a chance to track the error for a specific request, on logs.

Response status code will be 500 (internal server error). Response model is:

| Name    | Type     | Description                                |
|---------|----------|--------------------------------------------|
| errorid | `uuid`   | The generated error identifier.            |
| msg     | `string` | Generic message for internal server error. |

Example:

```json
{
    "errorid": "4f168fd5-6791-4d7e-8d33-38172f692e8f",
    "msg": "Internal error has occurred. Error details was logged, in order to see what happens, use errorid value to find the details on logs (look for :error-id key)."
}
```

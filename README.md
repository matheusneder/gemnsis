# nukr

FIXME

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
3. Read your app's source code at src/nukr/service.clj. Explore the docs of functions
   that define routes and responses.
4. Run your app's tests with `lein test`. Read the tests at test/nukr/service_test.clj.
5. Learn more! See the [Links section below](#links).

## Routes

### Create profile
Create a new profile.

	POST /v1/profiles/

#### Parameters

| Name    | Type                      | Location    | Description   |
|---------|---------------------------|-------------|---------------|
| profile | [profile-in](#profile-in) | body (json) | Profile data. |

#### Responses

| Status | Type                                         | Description         |
|--------|----------------------------------------------|---------------------|
| 201    | [profile-details-out](#profile-details-out)  | Created Profile .   |
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

### Remove profile
Perform logical deletion of profile (flag it as deleted) for a given profile id (on route). 

	DELETE /v1/profiles/:id

#### Parameters

| Name | Type   | Location | Description         |
|------|--------|----------|---------------------|
| id   | `uuid` | path     | Profile identifier. |

#### Responses

| Status | Type                                        | Description        |
|--------|---------------------------------------------|--------------------|
| 200    | [profile-details-out](#profile-details-out) | Removed profile.   |
| 404    | [profile not found](#profile-not-found)     | Profile not found. |

### List profiles
List profiles on network.

	GET /v1/profiles/

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

	POST /v1/profiles/:id/connections/

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

### Remove connection
Remove connection association for the given profile id and connid (the both on route).

	DELETE /v1/profiles/:id/connections/:connid
	
#### Parameters

| Name   | Type   | Location | Description              |
|--------|--------|----------|--------------------------|
| id     | `uuid` | path     | Profile identifier.      |
| connid | `uuid` | path     | Connection to remove id. |

#### Responses

| Status| Type                                       | Description             |
|-------|--------------------------------------------|-------------------------|
| 200   | [profile-details-out](#profile-details-out)| Removed connection.     |
| 404   | [profile not found](#profile-not-found)    | Prof. or conn. not found|

### Profile connections
List connections for a given profile id (on route).

	GET /v1/profiles/:id/connections/

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

	GET /v1/profiles/:id/suggestions/

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
- Datetime in UTC [ISO 8601](https://pt.wikipedia.org/wiki/ISO_8601) format.

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
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections/`
- [Remove profile](#remove-profile) - `DELETE /v1/profiles/:id`
- [Remove connection](#remove-connection) - `DELETE /v1/profiles/:id/connections/:connid`

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
- [List profiles](#list-profiles) - `GET /v1/profiles/`
- [Connection suggestions](#connection-suggestions) - `GET /v1/profiles/:id/suggestions/`
- [Profile connections](#profile-connections) - `GET /v1/profiles/:id/connections/`

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
- [List profiles](#list-profiles) - `GET /v1/profiles/`
- [Connection suggestions](#connection-suggestions) - `GET /v1/profiles/:id/suggestions/`
- [Profile connections](#profile-connections) - `GET /v1/profiles/:id/connections/`

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
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections/`

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
- [Create profile](#create-profile) - `POST /v1/profiles/`
- [Edit profile](#edit-profile) - `PUT /v1/profiles/:id`

### Profile not found

Profile not found error happens while trying to retrive data or perform operations for a specific profile by providing its id on route and there is no profile for the given id.

| Key                  | Condition                            |
|----------------------|--------------------------------------|
| `profile-not-found`  | Profile not found for the given id.  |

Applies to:

- [Edit profile](#edit-profile) - `PUT /v1/profiles/:id`
- [Profile details](#profile-details) - `GET /v1/profiles/:id`
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections/`
- [Connection suggestions](#connection-suggestions) - `GET /v1/profiles/:id/suggestions/`
- [Profile connections](#profile-connections) - `GET /v1/profiles/:id/connections/`
- [Remove profile](#remove-profile) - `DELETE /v1/profiles/:id`
- [Remove connection](#remove-connection) - `DELETE /v1/profiles/:id/connections/:connid`

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
- [Connect profiles](#connect-profiles) - `POST /v1/profiles/:id/connections/`

### Network over capacity
In order to network's healthiness garanties, total number of profiles is limited to 10000. The `network-over-capacity` error reason will be raised while trying to [create profile](#create-profile) and this limit was reached. A warnning log will be generated for this event in order to backend team get knowledge of the issue.

| Key                     | Condition                                        |
|-------------------------|--------------------------------------------------|
| `network-over-capacity` | Not accepting new profiles due capacity reasons. |

Applies to:
- [Create profile](#create-profile) - `POST /v1/profiles/`

## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Configure your service to accept incoming connections (edit service.clj and add  ::http/host "0.0.0.0" )
2. Build an uberjar of your service: `lein uberjar`
3. Build a Docker image: `sudo docker build -t nukr .`
4. Run your Docker image: `docker run -p 8080:8080 nukr`

### [OSv](http://osv.io/) unikernel support with [Capstan](http://osv.io/capstan/)

1. Build and run your image: `capstan run -f "8080:8080"`

Once the image it built, it's cached.  To delete the image and build a new one:

1. `capstan rmi nukr; capstan build`


## Links
* [Other Pedestal examples](http://pedestal.io/samples)

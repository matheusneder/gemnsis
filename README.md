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

```
POST /v1/profiles/
```

##### Parameters

| Name    | Type                      | Location    | Description         |
|---------|---------------------------|-------------|---------------------|
| profile | [profile-in](#profile-in) | body (json) | Profile data.       |

##### Responses

| Status | Type                                        | Description          |
|--------|---------------------------------------------|----------------------|
| 201    | [profile-details-out](#profile-details-out) | Profile created.     |
| 400    | [core-error-coll](#core-error-coll)         | Precondition failed. |

**Important**: See [profile input errors](#profile-input-errors) section for details about erros while creating profile.

### Edit profile
```
PUT /v1/profiles/:id
```
##### Parameters

| Name    | Type                      | Location    | Description         |
|---------|---------------------------|-------------|---------------------|
| id      | `uuid`                    | path  | Profile identifier. |
| profile | [profile-in](#profile-in) | body (json) | Profile data.       |

##### Responses

| Status | Type                                        | Description          |
|--------|---------------------------------------------|----------------------|
| 200    | [profile-details-out](#profile-details-out) | Profile updated.     |
| 400    | [core-error-coll](#core-error-coll)         | Precondition failed. |
| 404    | [core-error-coll](#core-error-coll)         | Profile not found.   |

**Important**: See [profile input errors](#profile-input-errors) section for details about erros while editing profile.

### Profile details
```
GET /v1/profiles/:id
```
##### Parameters

| Name    | Type                      | Location    | Description         |
|---------|---------------------------|-------------|---------------------|
| id      | `uuid`                    | path  | Profile identifier. |

##### Responses

| Status | Type                                        | Description          |
|--------|---------------------------------------------|----------------------|
| 200    | [profile-details-out](#profile-details-out) | Profile updated.     |
| 404    | [core-error-coll](#core-error-coll)         | Profile not found. |

### List profiles
```
GET /v1/profiles/
```
##### Parameters

| Name       | Type                            | Location | Description         |
|------------|---------------------------------|----------|---------------------|
| pagination | [pagination-in](#pagination-in) | query    | Pagination options. |

##### Responses

| Status | Type                                        | Description          |
|--------|---------------------------------------------|----------------------|
| 200    | [profile-list-page-out](profile-list-page-out) | Profile list.     |

## Models

### profile-in

Input model for [create profile](#create-profile) and [edit profile](#edit-profile).

| Name    | Type     | Required | Description                                |
|---------|----------|----------|--------------------------------------------|
| name    | `string` | yes      | Profile's name.                            |
| email   | `string` | yes      | Profile's email.                           |
| visible | `bool`   | no       | Visibility for the connection suggestions. |

Notes

- Profile's email must be unique over the network and match regexp `^[a-z0-9.+_-]+@[a-z0-9]{2,}(\.[a-z0-9]{2,})+$`.
- **visible** default value is `true`.

Example

```json
{
    "name": "Foo Bar",
    "email": "foo.bar@nukr.com",
    "visible": true
}
```
### profile-details-out

Output model for [create a new profile](#create-a-new-profile), [edit profile](#edit-profile) and [profile details](#profile-details).

| Name        | Type       | Description                                |
|-------------|------------|--------------------------------------------|
| id          | `uuid`     | Profile identifier.                        |
| name        | `string`   | Profile name.                              |
| email       | `string`   | Profile email.                             |
| visible     | `bool`     | Visibility for the connection suggestions. |
| createdat   | `datetime` | Creation date.                             |
| updatedat   | `datetime` | Last update date (could be null).          |
| connections | `int`      | Number of connections.                     |

Notes

- If profile never updated, updatedat will be `null`.
- Datetime in [ISO 8601](https://pt.wikipedia.org/wiki/ISO_8601) format.

Example

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

### core-error-coll

| Name   | Type                                        | Description        |
|--------|---------------------------------------------|--------------------|
| errors | List of [core-error-item](#core-error-item) | Error reason list. |

Example

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

### core-error-item

| Name | Type     | Description    |
|------|----------|----------------|
| key  | `string` | Error key.     |
| msg  | `string` | Error message. |

### pagination-in

| Name    | Type  | Default | Description               |
|---------|-------|----------|---------------------------|
| page    | `int` | 1       | Page number to retrieve.  |
| perpage | `int` | 10       | Number of items per page. |

Notes
- **perpage** value is limited to 50.

Examples 
- `/v1/profiles/?page=4`
- `/v1/profiles/?page=2&perpage=20`

### profile-list-page-out

| Name     | Type                            | Description                   |
|----------|---------------------------------|-------------------------------|
| total    | `int`                           | Number of total items in coll.|
| showing  | `int`                           | Num. of items on current page.|
| page     | `int`                           | Current page number.          |
| pages    | `int`                           | Number of total pages.        |
| dropping | `int`                           | Items being dropped/skiped.   |
| items    | [prof-sint-out](#prof-sint-out) | Collection for current page.  |

## Profile validation

### Profile input errors

Profile input error model is [core-error-coll](#core-error-coll). The table bellow enlist the possibles [core-error-item](#core-error-item) while creating/editing profile by its key and condition.

| Key                      | Condition                                 |
|--------------------------|---------------------------------------------|
| `profile-name-required`  | Name is empty or blank.                     |
| `profile-email-required` | Email is empty or blank.                    |
| `profile-invalid-email`  | Email did'nt match email regexp validation. |
| `profile-email-exists`   | Email already exists in network.            |

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


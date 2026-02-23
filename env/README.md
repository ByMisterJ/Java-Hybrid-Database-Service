# Databases

## Development (Docker)

### Start Postgresql service

Be in the same working directory as this Readme.

Build docker image:

``` shell
$ docker build . -t my-postgres:1.0
```

Run docker image:

``` shell
$ docker run -d -p 5432:5432 --volume ./postgresql-data:/var/lib/postgresql --name my-postgres my-postgres:1.0
```

You can connect to Postgres with the URL
`postgresql://postgres:postgrespass@localhost:5432`.

Like:

``` shell
$ psql --host=examen-pres.c7kke8i4kl72.us-east-1.rds.amazonaws.com --port=5432 --user=postgres --password
$ psql postgresql://postgres:yfimGukMieiFC7RCnqhY@examen-pres.c7kke8i4kl72.us-east-1.rds.amazonaws.com:5432/examen
```

### Start Dynamodb service

Run the DynamoDB container:

``` shell
$ docker run -d -p 8000:8000 --volume ./dynamodb-data:/home/dynamodblocal/data --name my-dynamodb circleci/dynamodb
```

Pull the DynamoDB local image:

``` shell
$ docker pull circleci/dynamodb:latest
```

Run the script with:

``` shell
$ uv run main.py
```

## Production (AWS)

TODO





[default]
aws_access_key_id=ASIAY4RHSS6AJFQKQIEC
aws_secret_access_key=ATu73RDvWdL+eBPiBXdGxFJer2cFd3O32v2jOr2j
aws_session_token=IQoJb3JpZ2luX2VjEOn//////////wEaCXVzLXdlc3QtMiJHMEUCIQCefYyQftWY+VfugXf5U7Dhh1KRUDJnxEOvPiKcluz+VgIgJW4tpukiARRP9jiopZbq4g2rvyjVr1l07Ao6gIMbx3AqyAIIsv//////////ARAAGgw2MTEwNDIxMDUyMTYiDKCGQGzjdCZ5RcX82iqcApIV5JWLNZ6HXf9Y/+o9xoe2xeaB0F7r/UQxam44n/XSfYj9WagmaeexJpdJYbiMSVUDE+tbxK1cDY9JIs0x8tHDvZahoLdUq7hYeM6xpgIE0bvSwXWA/aP9smBhgYtLwZrbONHjrwcdwbzcJlxhJ2AdWDRBVOva//EbYctmGtgxVWlmlyDSSCPCBfHeQhxfi8BkiyVr4HXSosz1cC+wt0Q453CguVlbenqbCWa0njHK2ltst86VJu+l3bVHmKQx1wZg9Ch24/ubFLkPopBnWEM34hGQWoqhy5WJ9rxlEWG3ZF3UlOiV2LsTUHRC/bawqJ6kmbnlJJWpL5IgOt4yf2DKb1enE/JPv9DDs6nIUfb0fpS8ptMCsKu3brdpMJbDpckGOp0BHCmKcW9YpWHXLKFDmwjIoNErat0ljjsT3kfQdYXg8HMgWuJngFpsTJ7yo4UB1yyiUdYeTGan8gbmYfnI1zKY3EwV/RtmiP31uO3ScEvlJtx0xiluE5YHQeSkljl4FbOc7mp1m/0UkGHC77/fMrPfGJxOXwIS2KULW8W2G/eCAoBgDljJ6Mx/9lu8TkcqKB3/wxiPd8ejBrpRwjkshQ==

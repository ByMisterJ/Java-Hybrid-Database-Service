import psycopg2
import boto3


# Development
# POSTGRES_URL = "postgresql://postgres:postgrespass@localhost:5432"
# DYNAMO_URL = "http://localhost:8000"

# Production - AD-PRES
# Usuario maestro: postgres
# Password: presencial1234
# Conexion: examen-pres.c7kke8i4kl72.us-east-1.rds.amazonaws.com
# POSTGRES_URL = "postgresql://postgres:presencial1234@examen-pres.c7kke8i4kl72.us-east-1.rds.amazonaws.com:5432"
# DYNAMO_URL = None

# Examen - AD-SEMI-EXAMEN
# Usuario maestro: postgres
# Password: examen12345
# Conexion: examen-semi.c9su20guuft1.us-east-1.rds.amazonaws.com
POSTGRES_URL = "postgresql://postgres:examen12345@examen-semi.c9su20guuft1.us-east-1.rds.amazonaws.com:5432"
DYNAMO_URL = None

USERS = (
    # Presencial
    "unaalirib",
    "josarapar",
    "carbarbab",
    "danbelpag",
    "davberamo",
    "ivabermel",
    "carberman",
    "salbrogar",
    "vicchabel",
    "maugarsot",
    "adrgargar",
    "mihgid",
    "geomanrib",
    "giomarser",
    "sebmer",
    "jornavmar",
    "abzrodrod",
    "davromfor",
    "davsalcas",
    "iketorcan",
    "benvarzam",
    # Semipresencial
    "helbrabou",
    "guichiver",
    "youeloelo",
    "marespmar",
    "carferrod",
    "andghenea",
    "ramguabou",
    "vanjimfer",
    "alvlopper",
    "manmarper",
    "sanolusan",
    "juarodgra",
    "iossor",
    "jortelriz",
    "pabvicmon"
)

def getPostgresUrl(user):
    return POSTGRES_URL + f"/{user}"

def createPostgres(users):
    con = psycopg2.connect(POSTGRES_URL)
    con.set_session(autocommit=True)
    cur = con.cursor()
    for user in users:
        cur.execute(f"CREATE DATABASE {user} WITH OWNER=postgres ENCODING=UTF8 ALLOW_CONNECTIONS=true")
    con.set_session(autocommit=False)

def dropPostgres(users):
    con = psycopg2.connect(POSTGRES_URL)
    con.set_session(autocommit=True)
    cur = con.cursor()
    for user in users:
        cur.execute(f"DROP DATABASE IF EXISTS {user}")
    con.set_session(autocommit=False)

def checkPostgresExist(users):
    con = psycopg2.connect(POSTGRES_URL)
    cur = con.cursor()
    cur.execute("SELECT datname FROM pg_database;")
    allDBs = {db[0] for db in cur.fetchall() }
    con.commit()
    print("-- Existing databases in the system:")
    allDBs.discard("template0")
    allDBs.discard("template1")
    allDBs.discard("postgres")
    print(allDBs)
    anyMissing = False
    for user in users:
        if user not in allDBs:
            anyMissing = True
    return not anyMissing

def checkPopulatedPostgres(users):
    allPopulated = True
    for user in users:
        con = psycopg2.connect(getPostgresUrl(user))
        cur = con.cursor()
        print(f"Checking database {user} population...")
        cur.execute("SELECT table_name FROM information_schema.tables"
                    " WHERE table_schema = 'public';")
        tables = {table[0] for table in cur.fetchall()}
        if len(tables) != 2:
            allPopulated = False
            print(f"Not populated correctlye")
        print(tables)
    return allPopulated

def populateAllPostgres(users):
    for user in users:
        print(f"Populating database {user}...")
        con = psycopg2.connect(getPostgresUrl(user))
        cur = con.cursor()
        cur.execute("CREATE TABLE IF NOT EXISTS usuarios ("
                    " id_usuario SERIAL PRIMARY KEY,"
                    " nombre_completo VARCHAR(100) NOT NULL,"
                    " email VARCHAR(100) NOT NULL UNIQUE);")
        cur.execute("CREATE TABLE IF NOT EXISTS contenidos_reproducidos ("
                    " id SERIAL PRIMARY KEY,"
                    " id_usuario INT NOT NULL,"
                    " contenido VARCHAR(100) NOT NULL,"
                    " fecha TIMESTAMP NOT NULL,"
                    " FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario));")
        con.commit()

def checkDynamoDBExists(ddb, users):
    response = ddb.list_tables()
    tableNames = response["TableNames"]
    print("-- Existing DynamoDB tables:")
    print(tableNames)
    return len(tableNames) == len(users)

def createDynamoDBTables(ddb, users):
    for user in users:
        table = ddb.create_table(
            TableName=f"{user}-historial",
            KeySchema=[
                {
                    "AttributeName": "idUsuario",
                    "KeyType": "HASH"
                }
            ],
            AttributeDefinitions=[
                {
                    "AttributeName": "idUsuario",
                    "AttributeType": "S"
                }
            ],
            ProvisionedThroughput={
                'ReadCapacityUnits': 5,
                'WriteCapacityUnits': 5
            }
        )
        tableName = table['TableDescription']['TableName']
        print(f"Created table {tableName}")

def deleteDynamoDBTables(ddb, users):
    response = ddb.list_tables()
    tableNames = response["TableNames"]
    for user in users:
        tname = f"{user}-historial"
        if tname in tableNames:
            table = ddb.delete_table(TableName=tname)
            tableName = table['TableDescription']['TableName']
            print(f"Deleted table {tableName}")


def main():
    print("-- Starting in DEVELOPMENT mode.")

    if DYNAMO_URL is None:
        ddb = boto3.client(
            "dynamodb",
            aws_access_key_id="ASIAVRUVPIDT4O3SWR5Q",
            aws_secret_access_key="ximTF+4Na2Z4ItE00Iw/7pJ1vMJC8/42u49hNxjD",
            aws_session_token="IQoJb3JpZ2luX2VjEGMaCXVzLXdlc3QtMiJGMEQCIGjeHKCPA28Cv80IdnyjdZsIy5hTY1ZXas6wSM/CXd1uAiBM68sK8KEDIeYgzyeO9uK5em58fAikbt5EhzVS59GGUyq/AggrEAAaDDM4MTQ5MTgyMjgyMyIMy++FE2NMHvenHMo6KpwCApfIpdh6+hdigYWSEknA5sHgYRMVv2JCmZimNFqwQVpqyh8DYzq/eRvBOy2UGa4sbIAkQKBj5C7R5YQ31m0mJ6wjNGp13IEUIxUZrZ6KXOzB+HsEakv/MtNAP/tLbbQXxZo4StB9fdxY8/zgErTv0lr/RyzoDkygH/dYLE0kXcqt9XWLxhSoJ6haaNjc9Q6GQOJ4NUZrVCw+u0RSbphWaTTuvpt1QYSig09aDYV1sNUyMoFSddCKjVJAT73Bz19DCdQse0tqIPGLJRsR4PddRUk7LIv+2VzJ5Qn8qVd3KBzSJSa4sGma302hPeOxQ99qU22cQwHiv8B4zomzrQtHuDp0193Z8Sfvd/wqSgaObn2YF/5Qxswh+5B4BKAwg53AyQY6ngEksG1mNs0aR6lgfO+4db0fXiInfes2uGnji53gOgN+vFYH7yVI93Y6humq4MJLzw7LIFnl5JdSkc0Ihfo3pFT/mfW2MtYXj4cqwbF8XmURpKpsHGC7Ntk+bdVCDhSH/K+vmkkhsBTz0jdLr3Ww3wAcTpZfnIacFpBZgHRpkThqCNq6qwLsETxmOzsFjTYBXtYQ3XM7ueee9+X/fgEzoA==",
            region_name="us-east-1"
        )
    else:
        ddb = boto3.client(
            "dynamodb", endpoint_url=DYNAMO_URL,
            aws_access_key_id="anything", aws_secret_access_key="anything",
            region_name="us-east-1"
        )

    choice = None
    while choice != "q":
        print("-- What do you want to do?")
        print("(pa) check if all postgres exists")
        print("(pd) drop all postgres")
        print("(pc) create all postgres")
        print("(ps) check if all postgres are populated")
        print("(pp) populate all postgres")
        print(" -----------")
        print("(da) check if all DynamoDB tables exists")
        print("(dd) delete all DynamoDB tables")
        print("(dc) create all DynamoDBs tables")
        print("(q) quit")
        choice = input("choose one: ")

        if choice == "pa":
            print("-- Checking if all postgres exist...")
            DBsExist = checkPostgresExist(USERS)
            if DBsExist:
                print("All postgres already exist.")
            else:
                print("Not all postgres exist.")

        elif choice == "pd":
            print("-- Dropping all postgres...")
            dropPostgres(USERS)
            print("All postgres dropped.")

        elif choice == "pc":
            print("-- Creating all postgres...")
            createPostgres(USERS)
            print("All postgres created.")

        elif choice == "ps":
            print("-- Check if all postgres are already populated (they should already exist)...")
            allPopulated = checkPopulatedPostgres(USERS)

            if allPopulated:
                print("All postgres are populated.")
            else:
                print("Not all postgres are populated.")

        elif choice == "pp":
            print("-- Polulating all postgres (they should already exist)...")
            populateAllPostgres(USERS)
            print("All postgres populated correctly.")

        elif choice == "da":
            print("-- Checking if all DynamoDB tables exist...")
            allDDBExist = checkDynamoDBExists(ddb, USERS)
            if allDDBExist:
                print("All DynamoDB tables exist")
            else:
                print("Not all DynamoDB tables exist")

        elif choice == "dd":
            print("-- Deleting all DynamoDB tables...")
            deleteDynamoDBTables(ddb, USERS)
            print("All DynamoDB tables deleted")

        elif choice == "dc":
            print("-- Creating all DynamoDB tables...")
            createDynamoDBTables(ddb, USERS)
            print("All DynamoDB tables created")

if __name__ == "__main__":
    main()

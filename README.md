# Adding Data
Please use the file [data.sql](src/main/resources/data.sql) to add / update bus rates.
The following assumptions have been made:
1. The input [taps.csv](src/test/resources/taps.csv) is sent daily or hourly and contains data in sequence. Thus every tap-off is preceded by a tap-on.
2. There are three tables:
   1. `STOP` — different bus companies have their own bus stop IDs and zones. For example, I've assumed a "Victoria Transport Company" (`bus_company_id = 1`) has "`Southbank`" bus stop in its Zone **1**, whilst "Buses Australia Company" (`bus_company_id = 2`) has in its designation "`Southbank`" bus stop in its Zone **2**. 
   2. `ZONE` — Each `STOP` will fall into its respective zone per the bus company. Rates are decided on the basis of zones. Entries in this table will be made for both ends of the bus route, i.e., there'd be an entry for zone `1` to zone `1`, zone `1` to zone `2` and zone `2` to zone `1`. An entry for zone `1` to zone `2` doesn't guarantee vice versa. (ideally I would've preferred implicit for the reverse route, however I am running out of time)
   3. `TAPS` — This is the table for staging the data temporily before its transformed and outputted as a `trips.csv` file. After successfull export, the records are removed. (Ideally I would've liked to keep the records and add an indexed column for datetime of ingestion .. but time's running out)

# Running the tests
- clone from github and import into intellij (or `gradlew clean test`).
- [data.sql](src/test/resources/data.sql) and [taps.csv](src/test/resources/taps.csv) can be used to change the input data.
- input file location is `app.input-file-path` and output file location is `app.output-file-path` in [application-test.yaml](src/test/resources/application-test.yaml) 

# Running the job
- On the terminal `./gradlew bootRun`
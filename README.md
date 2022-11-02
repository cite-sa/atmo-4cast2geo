## Atmo4Cast2Geo

Atmo4cast2geo is a service that converts Atmo4cast csv output files to files that can be used by geographical software.
The service can output the following formats: geoTiff(Tiff image with geographical data), Shape file, geoJSON (special formatted JSON for use on online Maps)

### Included Files

- config/application-prod.yml: The main configuration file for the service
- logging/logback-prod.xml: Logging configuration (you can set where the log files will be stored)
- atmo4cast2geo-web-1.0.0.jar: The main jar file for the service
- launch.bat/.sh: Windows Batch and Linux Shell Script files in order to start the service (they bound the console window from where they will be called)

### Empty folders:
- tmp: Temporary folder used to store data for processing
- disposed: Old files from the temporary storage that can be deleted
- logs: Where the service logger will store it's log files

### System Requirements:
- Any system that supports at least Java 8 or newer
- Enough storage for it to operate

### Configuration:
In config/application-prod.yml you will find the nesecary configuration for the service.

Those are:
- server.port: The port in which the service will listen to
- server.ssl: (Commented out) Configurations for setting up the service in order to use secure http (requires PKCS12 .p12 key store file)
- server.ssl.key-store-type: The type of the key store file (JKS, PKCS12)
- server.ssl.key-store: The path of the key store file
- server.ssl.key-store-password: The password for the key store file
- server.ssl.key-alias: The alias of the key store file
- paths.temp-path: The path for the Service temporary storage
- disposer.location: The path where disposed files will end to
- disposer.scheduling.interval: The time interval for how often the file disposer will run
- disposer.scheduling.unit: The time unit of the disposer's frequency(NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS)

In the logging/logback-prod.xml you can edit only these two options:
- file on line 10: File path of the current log file
- filePattern on line 12: File path pattern where the old log files will be stored

### Running the service:
#### Without Docker:
1) Make sure that Java 8 or later is installed to your system and can be used on your system console
2) Run any of the two launch files
#### With Docker:
1) Open a console on the current folder
2) Type docker-compose up -d (might require admin rights)

### Updating the service:
#### Update configuration files:
Both without or with docker you just restart the service
### Update the jar file:
#### Without docker:
1) Close service
2) Replace jar file
3) Start agin the service
#### With Docker:
1) Close service (on the current folder run on a console docker-compose down)
2) Replace jar file
3) Destroy docker image (on the current folder run on a console docker image ls and then docker image rm <image id you can get from the list of the previous command>)
4) Start again the service (on the current folder run on a console docker-compose up -d)

### Using the Service:
In order to use the service you need to do a post call to the endpoint /api/generateGeoData with body a form data with a single field named
zip and having as value a zip file with one or more .csv(.cvs) files.

The service can determine what output it should return based on the csv's header, but you can override the type by adding the parameter type
to the url(tiff, shapefile, json) (NOTE: If the data were meant to be outputed on a different format than what you ask it to, then the data might be corrupted)

### Result:
After using the service, the service will return a zip file named result-(date and hour).zip, inside that .zip file there are going to be
other .zip files for each .csv file the zip file you give to the service. (NOTE: if you requested to get json output it will only have the geojson files
without been enclosed to other .zip files) 

### Logging:
The service comes with the default spring boot logger, logback.

The logger aside from printing to the console it's also writing the same log messages to log files, that are stored (by default) to the logs folder.

The current.log is the logfile where the service write at the moment, while the dated are older log files followed by the date they where originaly created.
 
### File Diposer:
The service contains a file disposer. The disposer DOESN'T delete files but instead it moves files older than 24 hours from the temp folder
to it's own folder. After that you can handle the files however you want. 


# License

Licensed under the EUPL-1.2-or-later

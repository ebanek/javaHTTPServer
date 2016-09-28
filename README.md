###Java HTTP server

First, clone this repository.
(Install Java 8 and maven if necessary)

Then, if you wish to run this server, go to the project directory, where the pom.xml is located, and run:
```sh
mvn compile
mvn exec:exec
```

The server supports dynamically running simple scripts. Just add a script into the webroot folder.  
In your browser, go to link: _127.0.0.1:5721/scripts/basic.smscr_, to run the following script (while the server is running):
```
This is sample text.
{$ FOR i 1 10 1 $}  This is {$= i $}-th time this message is generated.
{$END$}
{$FOR i 0 10 2 $}  sin({$=i$}^2) = {$= i i * @sin "0.000" @decfmt $}
{$END$}
```
There are also some facilities for testing persistency accros requests. For that, check out the script _longTimePersistent.smscr_, and the _src/main/java/hr/fer/zemris/java/custom/SmartHttpServer_ class  
(The implementation for the parser of the script is in the _src/main/java/hr/fer/zemris/java/custom/_ folder)

Server supports serving images (located in the webroot folder): _127.0.0.1:5721/apple.png_,  
and also telling it to draw an image dynamically: _127.0.0.1:5721/cw_.

For configuration of server parameters, go to _config/_ folder.


exceute commands: 

git clone https://github.com/6oP/exrate.git <br>
cd exrate<br>
./gradlew run<br>
<br>
Excpect to see smht like this:<br>
<pre>
> Task :run
2022-04-28 00:16:49.766 [main] INFO  ktor.application - Autoreload is disabled because the development mode is off.
2022-04-28 00:16:49.849 [main] DEBUG i.m.c.u.i.l.InternalLoggerFactory - Using SLF4J as the default logging framework
2022-04-28 00:16:49.852 [main] INFO  i.m.c.i.push.PushMeterRegistry - publishing metrics for LoggingMeterRegistry every 1m
2022-04-28 00:16:49.936 [main] INFO  ktor.application - Application auto-reloaded in 0.167 seconds.
2022-04-28 00:16:50.089 [DefaultDispatcher-worker-2] INFO  ktor.application - Responding at http://0.0.0.0:8080
<==========---> 80% EXECUTING [31s]
> :run
</pre>

To test rate exchange API:
GET http://0.0.0.0:8080/exchangeRates/NZD?symbols=USD

To call metrics API
GET http://0.0.0.0:8080/metrics

To call origin micrometer prometheus scrape
GET http://0.0.0.0:8080/metrics-micrometer

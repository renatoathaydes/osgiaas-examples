package com.athaydes.osgiaas.examples.weather

import com.athaydes.osgiaas.cli.CommandCompleter
import com.athaydes.osgiaas.cli.CommandHelper
import com.athaydes.osgiaas.cli.args.ArgsSpec
import com.github.fedy2.weather.YahooWeatherService
import com.github.fedy2.weather.data.Forecast
import com.github.fedy2.weather.data.unit.DegreeUnit
import com.github.fedy2.weather.data.unit.DegreeUnit.CELSIUS
import com.github.fedy2.weather.data.unit.DegreeUnit.FAHRENHEIT
import org.apache.felix.shell.Command
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.bind.JAXBException

@Component(name = "weather-command")
class WeatherCommand : Command {

    @Volatile
    var service: YahooWeatherService? = null

    companion object {
        val NAME = "weather"
        val VERBOSE_KEY = "-v"
        val UNITS_KEY = "-u"

        val argsSpec = ArgsSpec.builder()
                .accepts(UNITS_KEY, "--units")
                .withArgs("unit")
                .withDescription("Temperature units to be used (C for Celsius, F for Fahrenheit)")
                .end()
                .accepts(VERBOSE_KEY, "--verbose")
                .withDescription("Show verbose output")
                .end()
                .build()
    }

    @Activate
    fun start() {
        try {
            service = YahooWeatherService()
        } catch (e: JAXBException) {
            service = null
        }
    }

    override fun getName() = NAME

    override fun getUsage() = "$NAME ${argsSpec.usage} <location>"

    override fun getShortDescription() = """
    |Shows current weather and forecast for a given location.
    |
    |Options:
    |${argsSpec.getDocumentation("  ")}
    |""".trimMargin()

    override fun execute(line: String, out: PrintStream, err: PrintStream) {
        // freeze local service reference
        val service = service

        if (service == null) {
            err.println("Yahoo service could not be created. Restart this bundle.")
            return
        }

        val invocation = try {
            argsSpec.parse(line)
        } catch (e: IllegalArgumentException) {
            err.println(e.message)
            return
        }

        val location = invocation.unprocessedInput

        if (location.isBlank()) {
            CommandHelper.printError(err, usage, "Please provide a valid location")
        } else {
            val unit = if (invocation.hasOption(UNITS_KEY))
                parseUnit(invocation.getFirstArgument(UNITS_KEY))
            else DEFAULT_UNIT

            if (unit == null) {
                err.println("Invalid unit provided. Valid units are " +
                        "'${CELSIUS.unitKey()}', '${CELSIUS.name}' or " +
                        "'${FAHRENHEIT.unitKey()}', '${FAHRENHEIT.name}'")
            } else {
                val verbose = invocation.hasOption(VERBOSE_KEY)

                showWeatherAt(location, unit, verbose, service, out, err)
            }
        }
    }

}

@Component(name = "weather-command-completer")
class WeatherCommandCompleter :
        CommandCompleter by WeatherCommand.argsSpec.getCommandCompleter(WeatherCommand.NAME)

val Date.formatted: String
    get() = SimpleDateFormat("EEE yyyy-MM-dd").let { it.format(this) }

fun DegreeUnit.unitKey() = when (this) {
    CELSIUS -> "c"
    FAHRENHEIT -> "f"
}

val DEFAULT_UNIT = CELSIUS

fun parseUnit(input: String?): DegreeUnit? = when {
    input == null -> DEFAULT_UNIT
    input.equals(CELSIUS.unitKey(), ignoreCase = true) ||
            input.equals(CELSIUS.name, ignoreCase = true) ->
        CELSIUS
    input.equals(FAHRENHEIT.unitKey(), ignoreCase = true) ||
            input.equals(FAHRENHEIT.name, ignoreCase = true) ->
        FAHRENHEIT
    else -> null
}

fun showWeatherAt(location: String,
                  unit: DegreeUnit,
                  verbose: Boolean,
                  service: YahooWeatherService,
                  out: PrintStream,
                  err: PrintStream) {
    try {
        val channels = service.getForecastForLocation(location, unit)
                .first(1)
        if (channels.isEmpty()) {
            out.println("Weather information for '$location' could not be found")
        } else {
            val channel = channels[0]

            val temperature = channel.item.condition.temp
            val conditions = channel.item.condition.text
            val units = channel.units.temperature.name[0]
            val place = channel.location.let { loc ->
                "${loc.city}, ${loc.region} - ${loc.country}"
            }

            out.println("Weather in '$place' now: $temperature$units - $conditions")

            if (verbose) {
                fun Forecast.prettyForecast() =
                        "${date.formatted}: Low $low$units, High $high$units - $text"

                out.println("Forecast:\n${channel.item.forecasts
                        .map { it.prettyForecast() }.joinToString("\n")}")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace(err)
    }
}

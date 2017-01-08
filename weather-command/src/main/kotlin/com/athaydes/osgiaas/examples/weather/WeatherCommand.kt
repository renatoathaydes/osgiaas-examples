package com.athaydes.osgiaas.examples.weather

import com.athaydes.osgiaas.cli.CommandHelper
import com.github.fedy2.weather.YahooWeatherService
import com.github.fedy2.weather.data.Forecast
import com.github.fedy2.weather.data.unit.DegreeUnit
import org.apache.felix.shell.Command
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import java.io.PrintStream
import javax.xml.bind.JAXBException

@Component(name = "weather-command")
class WeatherCommand : Command {

    var service: YahooWeatherService? = null

    @Activate
    fun start() {
        try {
            service = YahooWeatherService()
        } catch (e: JAXBException) {
            service = null
        }
    }

    override fun getName() = "weather"

    override fun getUsage() = "weather <location>"

    override fun getShortDescription() = "Shows current weather and forecast for a given location"

    override fun execute(line: String, out: PrintStream, err: PrintStream) {
        // freeze local service reference
        val service = service

        if (service == null) {
            err.println("Yahoo service could not be created. Restart this bundle.")
            return
        }

        val arguments = CommandHelper.breakupArguments(line)

        when (arguments.size) {
            1 -> // no arguments provided by the user
                CommandHelper.printError(err, usage, "Please provide a valid location")
            2 -> { // The user gave an argument, print the argument instead
                val location = arguments[1]
                showWeatherAt(location, service, out, err)
            }
            else -> // too many arguments provided by the user
                CommandHelper.printError(err, usage, "Too many arguments")
        }
    }

}

fun showWeatherAt(location: String,
                  service: YahooWeatherService,
                  out: PrintStream,
                  err: PrintStream) {
    try {
        val channels = service.getForecastForLocation(location, DegreeUnit.CELSIUS)
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

            fun Forecast.prettyForecast() =
                    "$date: Low $low$units, High $high$units - $text"

            out.println("Forecast:\n${channel.item.forecasts
                    .map { it.prettyForecast() }.joinToString("\n")}")
        }
    } catch (e: Exception) {
        e.printStackTrace(err)
    }
}

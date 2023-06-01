package dev.joshhalvorson.materialweather.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.joshhalvorson.materialweather.R
import dev.joshhalvorson.materialweather.data.models.weather.AstroData
import dev.joshhalvorson.materialweather.data.models.weather.MoonPhase

@Composable
fun AstroCard(
    modifier: Modifier = Modifier,
    loading: Boolean,
    astroData: AstroData?
) {
    WeatherCard(modifier = modifier) {
        if (astroData != null) {
            CardContent(astroData = astroData)
        } else if (loading) {
            CardContent(astroData = AstroData.LOADING_DATA)
        }
    }
}

//TODO make look better on smaller screens/larger font/screen zoom
@Composable
private fun CardContent(astroData: AstroData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.sunrise),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = astroData.getSunriseDisplay())
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.sunset),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = astroData.getSunsetDisplay())
                }
            }

            Image(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = R.drawable.ic_sun),
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.moonrise),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = astroData.getMoonriseDisplay())
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.moonset),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = astroData.getMoonsetDisplay())
                }
            }

            val image = when (MoonPhase.getPhase(phaseString = astroData.moonPhase)) {
                MoonPhase.THIRD_QUARTER -> R.drawable.ic_third_quarter
                MoonPhase.WANING_GIBBOUS -> R.drawable.ic_waning_gibbous
                MoonPhase.FULL_MOON -> R.drawable.ic_full_moon
                MoonPhase.WAXING_GIBBOUS -> R.drawable.ic_waxing_gibbous
                MoonPhase.FIRST_QUARTER -> R.drawable.ic_first_quarter
                MoonPhase.WAXING_CRESCENT -> R.drawable.ic_waxing_crescent
                MoonPhase.NEW_MOON -> R.drawable.ic_new_moon
                MoonPhase.WANING_CRESCENT -> R.drawable.ic_waning_crescent
            }

            Image(
                modifier = Modifier
                    .size(64.dp)
                    .scale(.95f),
                painter = painterResource(id = image),
                contentDescription = null
            )

            Text(text = astroData.moonPhase)
            Text(
                text = stringResource(
                    R.string.moon_percent_illuminated,
                    astroData.moonIllumination
                )
            )
        }
    }
}
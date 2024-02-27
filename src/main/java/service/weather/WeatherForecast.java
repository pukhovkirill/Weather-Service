package service.weather;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class WeatherForecast {
    private CurrentWeather current;
    private DailyWeather daily;
}

package cornelius.weatherappv2;

import java.util.Date;

/**
 * Created by bacraig on 4/26/2015.
 */

public class DayForecast
{
    public Date day;
    public String icon;
    public double precipitation;
    public ForecastPeriod amForecast;
    public ForecastPeriod pmForecast;

    public enum DayPeriod
    {
        AM, PM
    }


}

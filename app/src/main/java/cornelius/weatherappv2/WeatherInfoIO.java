package cornelius.weatherappv2;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Static helper class to load CurrentObservations from Weather.gov XML
 * Created by bacraig on 4/11/2015.
 */
public class WeatherInfoIO
{
    public static void loadFromUrl(String url, WeatherListener listener)
    {
        try
        {
            Downloader<WeatherInfo> downloadInfo = new Downloader<>(listener);
            downloadInfo.execute(url);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Loads weather information from an input stream containing weather xml information
     * from www.weather.gov
     *
     * @param ios InputStream of XML data
     * @return CurrentObservations object with the parsed weather values
     */
    public static WeatherInfo loadFromXmlStream(InputStream ios)
    {
        try
        {
            WeatherXmlParser parser = new WeatherXmlParser(ios);
            return parser.parse();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Loads weather information from an XML asset file accompanying the application.
     *
     * @param assetMgr Application's asset manager
     * @param filename Filename relative to the /assets/ directory
     * @return CurrentObservations object with the parsed weather values
     */
    public static WeatherInfo loadFromAsset(AssetManager assetMgr, String filename)
    {
        try
        {
            return loadFromXmlStream(assetMgr.open(filename));
        } catch (IOException ioe)
        {
            Log.e("WeatherInfoIO", ioe.getMessage());
            ioe.printStackTrace();
        }
        return null;
    }

    public static abstract class WeatherListener implements Downloader.DownloadListener<WeatherInfo>
    {
        @Override
        public WeatherInfo parseResponse(InputStream in)
        {
            try
            {
                WeatherXmlParser wxp = new WeatherXmlParser(in);
                return wxp.parse();
            } catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
    }

}

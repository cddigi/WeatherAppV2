/*
 	Authors: Cornelius Donley, Tessa Olownuik, and Victor Bashorun
 	File: MainActivity.java
 	Description: Basic weather app that gets a zip code from the user
		and displays the current weather of that location. Also allows
		for the 7-day forecast of that area, switch between imperial
		and metric, and launching of maps application.

    Issues: Forecast not updating the currently displayed fragment.
	Not implemented: Downloading, displaying, and storing the current weather
		image, toasts, and notifications.
 	Date: 5/19/2015
 */

package cornelius.weatherappv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
        implements CurrentWeatherFragment.OnFragmentInteractionListener,
        ForecastFragment.OnFragmentInteractionListener, Downloader.DownloadListener<JSONObject>
{
    String zipcode;
    String[] zipStringArray;
    ArrayList<String> recentZipcodes;
    Boolean imperialPreferred,
            recentZipsExist;
    android.app.FragmentManager fragmentManager;
    android.app.FragmentTransaction fragmentTransaction;
    CurrentWeatherFragment weatherFragment;
    ForecastFragment forecastFragment;
    WeatherInfo weatherInfo;
    int selectedFragment;
    int units;
    int forecastPage;
    final static int NUM_PAGES = 7;
    final static int MAX_LIST_SIZE = 5;
    private ViewPager mObservationPager;
    private ViewPager mForecastPager;
    private PagerAdapter mObservationPagerAdapter;
    private PagerAdapter mForecastPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zipcode = "60563";
        recentZipsExist = false;
        forecastPage = 0;
        fragmentManager = getFragmentManager();

//        mObservationPager = (ViewPager) findViewById(R.id.pager);
//        mObservationPagerAdapter = new ObservationScreenSlidePagerAdapter(getSupportFragmentManager());
//        mObservationPager.setAdapter(mObservationPagerAdapter);
//        mObservationPager.setVisibility(View.VISIBLE);

//        mForecastPager = (ViewPager) findViewById(R.id.pager);
//        mForecastPagerAdapter = new ForecastScreenSlidePagerAdapter(getSupportFragmentManager());
//        mForecastPager.setAdapter(mForecastPagerAdapter);
//        mForecastPager.setVisibility(View.INVISIBLE);

        selectedFragment = 0;
        units = 0;

        // get stored zips and measurement preference from previous sessions
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        recentZipcodes = new ArrayList<>();
        String zipString = sharedPref.getString("zipcodes","");
        if(zipString.length() > 1)
        {
            recentZipsExist = true;
            zipString = zipString.substring(1, zipString.length()-1);
            zipStringArray = zipString.split(",");
            for(int i = 0; i < zipStringArray.length; i++)
                recentZipcodes.add(zipStringArray[i].trim());
        }

        imperialPreferred = sharedPref.getBoolean("imperial", true);
    }

    //Click on text: "current location" above picture, to launch GEO intent
    public void onClick(View V)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<lat>,<long>?q=<lat>,<long>(Label+Name)"));
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getLocation(zipcode);
    }

    @Override
    protected void onStop()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(recentZipcodes.size() > 0)
            editor.putString("zipcodes", recentZipcodes.toString());

        editor.putBoolean("imperial", imperialPreferred);

        // Commit the edits!
        editor.commit();
        super.onStop();
    }
    

    @Override
    public void onBackPressed()
    {
        if(mObservationPager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            mObservationPager.setCurrentItem(mObservationPager.getCurrentItem() - 1);

        if(mForecastPager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            mForecastPager.setCurrentItem(mForecastPager.getCurrentItem() - 1);
    }

//    private class ObservationScreenSlidePagerAdapter extends FragmentStatePagerAdapter
//    {
//        public ObservationScreenSlidePagerAdapter(FragmentManager fm)
//        {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position)
//        {
//            weatherFragment = new CurrentWeatherFragment().newInstance();
//            weatherFragment.weatherInfo = weatherInfo;
//            return weatherFragment;
//        }
//
//        @Override
//        public int getCount()
//        {
//            return 1;
//        }
//    }

    private class ForecastScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public ForecastScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.i("ForecastFragment", "" + position);
            forecastFragment = new ForecastFragment().newInstance(weatherInfo);
            forecastFragment.forecastPage = position;
            //forecastFragment.displayForecast();
            return forecastFragment;
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.zipcode:
                zipcode();
                return true;

            case R.id.recent_zipcode:
                recent_zipcodes();
                return true;

            case R.id.current_weather:
                selectedFragment = 0;
                current_weather();
                return true;

            case R.id.forecast:
                selectedFragment = 1;
                forecast_weather();
                return true;

            case R.id.units:
                units();
                return true;

            case R.id.about:
                about();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Displays about message
    private void about()
    {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("This is a weather app. Created by Cornelius, Tessa, & Victor. " +
                        "Data from www.weather.gov")
                .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void zipcode()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("zipcode")
                .setMessage("Enter Zipcode of Desired Location");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                zipcode = input.getText().toString().trim();

                if(zipStringArray != null)
                {
                    Boolean shouldAdd = true;

                    for(int i = 0; i < zipStringArray.length; i++)
                    {
                        //zip code already exists within the list
                        if(zipStringArray[i].trim().equals(zipcode))
                        {
                            shouldAdd = false;
                            break;
                        }
                    }

                    if(shouldAdd) {
                        if(recentZipcodes.size() >= MAX_LIST_SIZE)
                            recentZipcodes.remove(0);
                        recentZipcodes.add(zipcode);
                    }
                }
                else if(recentZipsExist == true)
                    recentZipcodes.add(zipcode);

                Log.i("zipcode()", "recent zip codes: " + recentZipcodes.toString());
                getLocation(zipcode);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void recent_zipcodes()
    {
        if(recentZipcodes != null)
        {
            String zipString = recentZipcodes.toString();
            zipString = zipString.substring(1, zipString.length()-1);
            zipStringArray = zipString.split(",");
            zipString = "";
            for(int i = 0; i < zipStringArray.length; i++)
                zipString += zipStringArray[i].trim() + "\n";


            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Recent Zipcodes").setMessage(zipString)
                    .setNeutralButton("okay", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id) {
                            // ok
                        }
                    });

            builder.show();
        }
    }

    private void current_weather()
    {
        mForecastPager.setVisibility(View.INVISIBLE);
        //mObservationPager.setVisibility(View.VISIBLE);

        fragmentManager.popBackStack();
        weatherFragment = new CurrentWeatherFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
        //fragmentTransaction.replace(R.id.container, weatherFragment);
        fragmentTransaction.add(R.id.container, weatherFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //weatherFragment.updateWeather(units);
    }

    private void forecast_weather()
    {
        //mObservationPager.setVisibility(View.INVISIBLE);
        fragmentManager.popBackStack();
        mForecastPager.setVisibility(View.VISIBLE);
    }

    //Allows user to select what units they would like to be displayed
    private void units()
    {
        //final CurrentWeatherFragment fragment = CurrentWeatherFragment.newInstance();
        new AlertDialog.Builder(this)
                .setTitle("Units")
                .setMessage("Choose what units you would like your weather displayed in:")
                .setPositiveButton("Metric", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedFragment == 0)
                            weatherFragment.metric();
                        units = 1;
                    }
                })
                .setNegativeButton("Imperial", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(selectedFragment == 0)
                            weatherFragment.imperial();
                        units = 0;
                    }
                })
                .show();
    }


    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    /**
     * @param zipcode URL amended with zipcode to Downloader to get JSONObject
     */
    public void getLocation(String zipcode)
    {
        weatherInfo = null;
        Downloader<JSONObject> downloadInfo = new Downloader<>(this);
        downloadInfo.execute("http://craiginsdev.com/zipcodes/findzip.php?zip=" + zipcode);
    }

    /**
     * @param in InputStream from the web request
     * @return jsonObject JSONObject parsed from InputStream
     */
    @Override
    public JSONObject parseResponse(InputStream in)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            JSONObject jsonObject = new JSONObject(reader.readLine());
            return jsonObject;
        }
        catch(IOException ex)
        {
            Log.e("parseResponse", ex.getMessage());
        }
        catch(JSONException ex)
        {
            Log.i("parseResponse", ex.getMessage());
        }

        return null;
    }

    /**
     * Latitude and longitude are passed to WeatherInfoIO
     *
     * @param result Object of type <code>T</code> created in the override parseResponse function
     */
    @Override
    public void handleResult(JSONObject result)
    {
        WeatherInfoIO.WeatherListener weatherListener = new WeatherInfoIO.WeatherListener()
        {
            @Override
            public void handleResult(WeatherInfo result)
            {
                weatherInfo = result;

                mForecastPager = (ViewPager) findViewById(R.id.pager);
                mForecastPagerAdapter = new ForecastScreenSlidePagerAdapter(getSupportFragmentManager());
                mForecastPager.setAdapter(mForecastPagerAdapter);
                mForecastPager.setVisibility(View.INVISIBLE);

                if(selectedFragment == 0)
                {
                    CurrentWeatherFragment.weatherInfo = weatherInfo;
                    if(weatherFragment != null)
                        weatherFragment.updateWeather(units);
                    else
                        current_weather();
                }
            }
        };

        try
        {
            WeatherInfoIO.loadFromUrl("http://forecast.weather.gov/MapClick.php?lat=" +
                    result.getDouble("latitude") + "&lon=" + result.getDouble("longitude")
                    +"&unit=0&lg=english&FcstType=dwml", weatherListener);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}


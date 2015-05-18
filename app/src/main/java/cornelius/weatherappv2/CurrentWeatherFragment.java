package cornelius.weatherappv2;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentWeatherFragment extends Fragment
{
    public static WeatherInfo weatherInfo;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CurrentWeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentWeatherFragment newInstance()
    {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CurrentWeatherFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.current_fragment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateWeather(0);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void header()
    {
        final ImageView image = (ImageView)getActivity().findViewById(R.id.picture);
        final TextView location = (TextView)getActivity().findViewById(R.id.location);
        final TextView time = (TextView)getActivity().findViewById(R.id.time);
        final TextView conditions = (TextView)getActivity().findViewById(R.id.conditions);
        final TextView humidity = (TextView)getActivity().findViewById(R.id.humid);

        location.setText(weatherInfo.location.name);
        time.setText(weatherInfo.current.timestamp);
        conditions.setText(weatherInfo.current.summary);
        humidity.setText(weatherInfo.current.humidity + "%");
    }

    public void metric()
    {
        final TextView temp = (TextView)getActivity().findViewById(R.id.temperature);
        final TextView dew = (TextView)getActivity().findViewById(R.id.dew);
        final TextView pressure = (TextView)getActivity().findViewById(R.id.pressure);
        final TextView gust = (TextView)getActivity().findViewById(R.id.gusts);
        final TextView wind = (TextView)getActivity().findViewById(R.id.windspeed);
        final TextView visibility = (TextView)getActivity().findViewById(R.id.textView12);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        temp.setText(nf.format((weatherInfo.current.temperature - 32) * 5 / 9) + "º C");
        dew.setText(nf.format((weatherInfo.current.dewPoint - 32) * 5 / 9) + "º C");
        pressure.setText(nf.format(weatherInfo.current.pressure * 25.4) + " mmHg");
        gust.setText(nf.format(weatherInfo.current.gusts * 1.6093) + " kph");
        wind.setText(weatherInfo.current.windDirectionStr() + " @ " + nf.format(weatherInfo.current.windSpeed * 1.6093) + " kph");
        visibility.setText(nf.format(weatherInfo.current.visibility * 1.6093) + " km");
    }

    public void imperial()
    {
        final TextView temp = (TextView)getActivity().findViewById(R.id.temperature);
        final TextView dew = (TextView)getActivity().findViewById(R.id.dew);
        final TextView pressure = (TextView)getActivity().findViewById(R.id.pressure);
        final TextView gust = (TextView)getActivity().findViewById(R.id.gusts);
        final TextView wind = (TextView)getActivity().findViewById(R.id.windspeed);
        final TextView visibility = (TextView)getActivity().findViewById(R.id.textView12);

        temp.setText(weatherInfo.current.temperature + "º F");
        dew.setText(weatherInfo.current.dewPoint + "º F");
        pressure.setText(weatherInfo.current.pressure + " inHg");
        gust.setText(weatherInfo.current.gusts + " mph");
        wind.setText(weatherInfo.current.windDirectionStr() + " @ " + weatherInfo.current.windSpeed + " mph");
        visibility.setText(weatherInfo.current.visibility + " mi");
    }

    public void updateWeather(int units)
    {
        header();
        if(units == 0)
            imperial();
        else
            metric();
    }
}

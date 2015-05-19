package cornelius.weatherappv2;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends android.support.v4.app.Fragment
{
    public int forecastPage;
    static WeatherInfo weatherInfo;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastFragment newInstance(WeatherInfo weather)
    {
        weatherInfo = weather;
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ForecastFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup pager,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.forecast_fragment, pager, false);
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
        displayForecast();
        super.onResume();

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

    public void displayForecast()
    {
        final TextView location = (TextView)getActivity().findViewById(R.id.location2);
        final TextView day = (TextView)getActivity().findViewById(R.id.currDay);
        final TextView high = (TextView)getActivity().findViewById(R.id.high);
        final TextView low = (TextView)getActivity().findViewById(R.id.low);
        final TextView am = (TextView)getActivity().findViewById(R.id.AM);
        final TextView pm = (TextView)getActivity().findViewById(R.id.PM);
        final TextView details = (TextView)getActivity().findViewById(R.id.textForecast);

        location.setText(weatherInfo.location.name);
        day.setText(weatherInfo.forecast.get(forecastPage).day + "");
        high.setText(weatherInfo.forecast.get(forecastPage).pmForecast.temperature + "");
        low.setText(weatherInfo.forecast.get(forecastPage).amForecast.temperature + "");
        am.setText(weatherInfo.forecast.get(forecastPage).amForecast.description);
        pm.setText(weatherInfo.forecast.get(forecastPage).amForecast.description);
        details.setText(weatherInfo.forecast.get(forecastPage).amForecast.details);
    }
}

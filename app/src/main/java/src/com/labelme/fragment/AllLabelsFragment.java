package src.com.labelme.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.adapter.ScheduleLabelsAdapter;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.JSONParser;
import src.com.labelme.model.ScheduleItem;

/**
 * Fragment relativo a tutte le annotazioni create dagli utenti registrati all'applicazione.
 */

public class AllLabelsFragment extends Fragment {

    // progress dialog
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // creating JSON parser object
    JSONParser jsonParser = new JSONParser();

    List<ScheduleItem> scheduleItemList;

    // session manager
    private int success;

    public AllLabelsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // loading json_library in background thread
        loadSchedule();
        return view;
    }

    private void loadSchedule() {
        int result = CheckNetwork.isInternetAvailable(getActivity());
        if (result == 1) {
            new loadSchedule().execute();
        } else {
            showToast(result);
        }
    }

    private void showToast(int type) {
        String message = "";
        switch (type) {
            case -1:
                message = getResources().getString(R.string.airplane_mode_on);
                break;
            case 0:
                message = getResources().getString(R.string.connection_off);
                break;
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {

    }

    /**
     * Background Async Task to load all labels by making HTTP request
     */
    class loadSchedule extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
            progressDialog.setMessage(getResources().getString(R.string.load_schedule));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * getting all labels from url
         */
        protected String doInBackground(String... args) {
            // building parameters
            List<NameValuePair> params = new ArrayList<>();
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_schedule, "GET", params);
            // check your log cat for JSON response
            Log.d("Create response: ", json.toString());

            try {
                // checking for SUCCESS TAG
                success = json.getInt(AppConfig.TAG_SUCCESS);
                if (success == 1) {
                    // json_schedule found
                    // getting array of labels
                    JSONArray json_schedule = json.optJSONArray(AppConfig.TAG_SCHEDULE_ARRAY);
                    scheduleItemList = new ArrayList<>();

                    // looping through all labels
                    for (int i = 0; i < json_schedule.length(); i++) {
                        JSONObject post = json_schedule.optJSONObject(i);
                        ScheduleItem item = new ScheduleItem();
                        item.setThumbnail(post.optString(AppConfig.TAG_CROPPED_IMAGE));
                        item.setLabel(post.optString(AppConfig.TAG_LABEL));
                        item.setAuthor(post.optString(AppConfig.TAG_AUTHOR));

                        scheduleItemList.add(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the progress dialog after getting all labels
            progressDialog.dismiss();

            String toast_message;
            switch (success) {
                case 1:
                    ScheduleLabelsAdapter adapter = new ScheduleLabelsAdapter(getActivity(), scheduleItemList);
                    recyclerView.setAdapter(adapter);
                    break;
                case 0:
                    toast_message = getResources().getString(R.string.no_labels_found);
                    Toast.makeText(getActivity(), toast_message, Toast.LENGTH_LONG).show();
                    break;
                case -1:
                    toast_message = getResources().getString(R.string.required_fields);
                    Toast.makeText(getActivity(), toast_message, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}

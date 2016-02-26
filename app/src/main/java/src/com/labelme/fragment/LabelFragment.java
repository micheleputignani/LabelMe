package src.com.labelme.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.adapter.ListViewAdapter;
import src.com.labelme.helper.JSONParser;

public class LabelFragment extends Fragment {

    // Progress dialog
    private ProgressDialog pDialog;
    private ListView listView;

    // Creating JSON parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> labelsList;

    // Arraylist for storing image and title
    private ArrayList<String> images;
    private ArrayList<String> titles;

    // url to get all labels list
    private static String url_all_labels = "http://androidlabelme.altervista.org/url_all_labels.php";

    // JSON Node name
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LABELS = "labels";
    private static final String TAG_IMAGE = "cropped_image";
    private static final String TAG_TITLE = "label";

    // json labels JSONArray
    JSONArray json_labels = null;

    public LabelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_labels, container, false);

        listView = (ListView) view.findViewById(R.id.listview);

        images = new ArrayList<>();
        titles = new ArrayList<>();

        // hashmap for ListView
        labelsList = new ArrayList<HashMap<String, String>>();

        // loading json_labels in background thread
        new loadAllLabels().execute();
        return view;
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        // on selecting single list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Clicked list item", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Background Async Task to load all labels by making HTTP request
     */
    class loadAllLabels extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading labels, please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting all lables from url
         */
        protected String doInBackground(String... args) {
            // building parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_labels, "GET", params);

            // check your log cat for JSON response
            Log.d("All json_labels: ", json.toString());

            try {
                // checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // json_labels found
                    // getting array of labels
                    json_labels = json.getJSONArray(TAG_LABELS);

                    // looping through all labels
                    for (int i = 0; i < json_labels.length(); i++) {
                        JSONObject c = json_labels.getJSONObject(i);

                        // storing each json item in variable
                        String image = c.getString(TAG_IMAGE);
                        String label = c.getString(TAG_TITLE);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_IMAGE, image);
                        map.put(TAG_TITLE, label);

                        images.add(image);
                        titles.add(label);

                        labelsList.add(map);
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
            // dismiss the dialog after getting all labels
            pDialog.dismiss();

            // updating UI from background thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // updating parsed JSON data into ListView

                    // creating ListViewAdapter obejct
                    ListViewAdapter listViewAdapter = new ListViewAdapter(getActivity(), images, titles);
                    // adding adapter to listview
                    listView.setAdapter(listViewAdapter);
                }
            });
        }
    }

}

package com.nikoo28.server;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.nikoo28.brainnet.R;
import com.nikoo28.util.HttpRequest;

/**
 * Created by nikoo28 on 11/29/17.
 */

public class DownloadFromServer extends AsyncTask<Void, Void, Void> {

    Activity mActivity;
    String response;
    private static String SERVER;
    private String GET_RESPONSE_URL;

    @Override
    protected Void doInBackground(Void... voids) {

        response = HttpRequest.get(GET_RESPONSE_URL).body();

        return null;
    }

    public DownloadFromServer(Activity activity, String server) {
        this.mActivity = activity;
        SERVER = server;
//        GET_RESPONSE_URL = SERVER + "/RESTfulMessenger/restapi/messages/";
        GET_RESPONSE_URL = SERVER + "/login";
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        TextView responseText = mActivity.findViewById(R.id.textView_result_response);
        responseText.setText(formatToJson(response));
    }

    private static String formatToJson(String text) {

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }
}

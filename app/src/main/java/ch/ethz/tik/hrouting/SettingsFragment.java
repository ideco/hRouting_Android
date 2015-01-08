package ch.ethz.tik.hrouting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ch.ethz.tik.hrouting.providers.HistoryDbHelper;

/**
 * This fragment shows the preferences for the first header.
 */
public class SettingsFragment extends PreferenceFragment {

    static AlertDialog alertDialog = null;

    public SettingsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference delHistPref = findPreference("delete_history");
        delHistPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                deleteHistory();
                return true;
            }
        });

        Preference updateDataPref = findPreference("fetch_polldata");
        updateDataPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                updateData();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null)
            view.setBackgroundColor(Color.parseColor("#BEFFFFFF"));
        return view;
    }

    private void deleteHistory() {
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Clear History")
                .setMessage("Remove all entries from the history table?")
                .setPositiveButton("Ok", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).create();
        alertDialog.show();
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    new HistoryDbHelper(getActivity()).deleteHistory();
                    Toast.makeText(getActivity(), "History cleared",
                            Toast.LENGTH_SHORT).show();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // No button clicked
                    break;
            }
            alertDialog = null;
        }
    };

    private void updateData() {
        // The data can not be updated at the moment.
        Toast.makeText(getActivity(), "Pollution data is up to date",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(alertDialog != null)
            alertDialog.show();
    }
}

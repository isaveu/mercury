package it.skarafaz.mercury.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.adapter.LogListAdapter;
import it.skarafaz.mercury.manager.SettingsManager;

public class LogFragment extends ListFragment {
    private static final Logger logger = LoggerFactory.getLogger(LogFragment.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reload();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                clearLog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reload() {
        setListAdapter(new LogListAdapter(getActivity(), readLog()));
    }

    private List<String> readLog() {
        File file = new File(getActivity().getDir(SettingsManager.getInstance().getLogDir(), Context.MODE_PRIVATE), SettingsManager.getInstance().getLogFile());
        List<String> lines = new ArrayList<>();
        try {
            lines = FileUtils.readLines(file, "UTF-8");
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        return lines;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void clearLog() {
        File logDir = getActivity().getDir(SettingsManager.getInstance().getLogDir(), Context.MODE_PRIVATE);
        File logFile = new File(logDir, SettingsManager.getInstance().getLogFile());
        try {
            FileUtils.write(logFile, "");
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        Collection<File> oldFiles = FileUtils.listFiles(logDir, new String[]{ "old" }, false);
        for (File file : oldFiles) {
            file.delete();
        }
        reload();
    }
}

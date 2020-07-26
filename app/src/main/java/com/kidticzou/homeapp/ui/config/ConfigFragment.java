package com.kidticzou.homeapp.ui.config;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.myapp;

public class ConfigFragment extends Fragment {
    private ListView mListConfig1;
    private myapp appdata;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_config, container, false);
        appdata= (myapp) getActivity().getApplication();
        mListConfig1=root.findViewById(R.id.lv_config_1);
        mListConfig1.setAdapter(new configAdapter(root.getContext(),appdata.mConfigItem));
        mListConfig1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(appdata.mConfigItem[i].equals("最新版本下载")){
                    //Toast.makeText(root.getContext(),"pos最新版本下载",Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(appdata.mConfigUpdate));
                    it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    getContext().startActivity(it);
                }
                else if(appdata.mConfigItem[i].equals("关于")){
                    Toast.makeText(root.getContext(),"关于",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return root;
    }
}
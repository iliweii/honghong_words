package honghong.beidanci.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import heima.chendu.R;
import honghong.beidanci.pojo.Kaoyan;
import honghong.beidanci.pojo.Record;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private TextView home_date;
    private TextView home_mean;
    private TextView home_yinbiao;
    private EditText home_word;
    private TextView home_res;
    private Button home_submit;
    private Button home_look;
    private TextView home_metting;
    private TextView home_count;

    private List<Kaoyan> kaoyans;
    private String nowDate;
    private SharedPreferences mSharedPreferences;
    private Integer total_meeting = 0, total_word = 0;
    private Integer count_today = 0, count_total = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        home_date = root.findViewById(R.id.home_date);
        home_mean = root.findViewById(R.id.home_mean);
        home_yinbiao = root.findViewById(R.id.home_yinbiao);
        home_word = root.findViewById(R.id.home_word);
        home_res = root.findViewById(R.id.home_res);
        home_submit = root.findViewById(R.id.home_submit);
        home_look = root.findViewById(R.id.home_look);
        home_metting = root.findViewById(R.id.home_meeting);
        home_count = root.findViewById(R.id.home_count);

        Gson gson = new Gson();

        // ??????????????????
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        nowDate = dateFormat.format(date);

        // ?????????????????? records
        mSharedPreferences = getActivity().getSharedPreferences("kaoyan", 0);
        String recordJson = mSharedPreferences.getString("records", "");
        List<Record> recordList = null;
        if ("".equals(recordJson)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("records", "[]");
            editor.commit();
            recordList = new ArrayList<Record>();
        } else {
            Type recordListType = new TypeToken<ArrayList<Record>>(){}.getType();
            recordList = gson.fromJson(recordJson, recordListType);
        }

        // ?????????????????? wrongs
        String wrongJson = mSharedPreferences.getString("wrongs", "");
        List<Record> wrongList = null;
        if ("".equals(wrongJson)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("wrongs", "[]");
            editor.commit();
            wrongList = new ArrayList<Record>();
        } else {
            Type wrongListType = new TypeToken<ArrayList<Record>>(){}.getType();
            wrongList = gson.fromJson(wrongJson, wrongListType);
        }

        // ???????????????????????????
        List<String> meeting_list = new ArrayList<String>();
        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getDate().equals(nowDate)) {
                Record record = recordList.get(i);
                count_today++;
            }
            if (!meeting_list.contains(recordList.get(i).getKaoyan().getWord())) {
                meeting_list.add(recordList.get(i).getKaoyan().getWord());
                total_meeting++;
            }
            count_total++;
        }

        // ?????????????????? kaoyans
        String kaoyanJson = mSharedPreferences.getString("kaoyans", "");
        if ("".equals(kaoyanJson)) {
            // ?????? kaoyan.json ????????????
            StringBuilder stringBuilder = new StringBuilder();
            try {
                InputStream is = getResources().getAssets().open("kaoyan.json");
                InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
                BufferedReader bReader = new BufferedReader(isReader);
                String mimeTypeLine = null;
                while ((mimeTypeLine = bReader.readLine()) != null) {
                    stringBuilder.append(mimeTypeLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // ????????????????????????kaoyans
            kaoyanJson = stringBuilder.toString();
            Type kaoyanListType = new TypeToken<ArrayList<Kaoyan>>(){}.getType();
            List<Kaoyan> kaoyanList = gson.fromJson(kaoyanJson, kaoyanListType);
            kaoyans = kaoyanList;
            // ????????????
            for (Kaoyan ky : kaoyans) {
                ky.setWeight(100);
            }
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("kaoyans", gson.toJson(kaoyans));
            editor.commit();
        } else {
            Type kaoyanListType = new TypeToken<ArrayList<Kaoyan>>(){}.getType();
            List<Kaoyan> kaoyanList = gson.fromJson(kaoyanJson, kaoyanListType);
            kaoyans = kaoyanList;
        }

        // ??????????????????
        total_word = kaoyans.size();
        home_metting.setText(total_meeting + " / " + total_word);
        home_count.setText(count_today + " / " + count_total);
        home_date.setText(nowDate);

        // ????????????
        NextQuestion();

        // ????????????????????????
        home_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ??????????????????
                if (home_submit.getText().equals("??????")) {
                    // Toast.makeText(getContext(), "??????", Toast.LENGTH_SHORT).show();
                    home_submit.setText("?????????");
                    String user_input = home_word.getText().toString();
                    String user_res = home_res.getText().toString();
                    if (user_input.equals(user_res)) {
                        // ????????????????????????
                        setWeightDown(user_res);
                        // ?????????????????????????????????
                        NextQuestion();
                    } else {
                        // ??????????????????????????????????????????
                        home_res.setVisibility(View.VISIBLE);
                        home_word.setTextColor(getResources().getColor(R.color.red));
                        // ??????????????? ?????????
                        RecordWrong(user_res);
                        setWeightUp(user_res);
                    }
                    // ?????????????????????
                    RecordRefresh(user_res);
                } else {
                    // ???????????????????????????????????????
                    NextQuestion();
                }

            }
        });

        // ???????????????????????????
        home_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_res = home_res.getText().toString();
                home_res.setVisibility(View.VISIBLE);
                // ??????????????? ?????????
                RecordWrong(user_res);
                setWeightUp(user_res);
            }
        });

        return root;
    }

    /**
     * ????????????????????????
     * @param key ???????????????
     * @return ??????????????????
     */
    public List<Kaoyan> getKaoyanList(String key) {
        List<Kaoyan> kaoyanList = new ArrayList<Kaoyan>();
        for (Kaoyan ky : kaoyans) {
            if (ky.getWord().indexOf(key) > -1)
                kaoyanList.add(ky);
        }
        List<Kaoyan> kaoyanList1 = kaoyanList.stream().sorted(Comparator.comparing(Kaoyan::getId)).collect(Collectors.toList());
        return kaoyanList1;
    }

    /**
     * ????????????????????????????????????
     * @return ?????????????????????
     */
    public Kaoyan getKaoyan() {
        Random random = new Random();
        List<Kaoyan> kaoyanList = getKaoyanList("");
        if (kaoyanList.size() == 0) {
            return null;
        }
        int weight = 0;
        for (Kaoyan ky : kaoyanList) {
            weight += ky.getWeight();
        }
        int index = random.nextInt(weight + 1);
        weight = 0;
        for (Kaoyan ky : kaoyanList) {
            weight += ky.getWeight();
            if (weight >= index) {
                return ky;
            }
        }
        return null;
    }

    /**
     * ??????????????????????????????
     * @param word ??????
     * @return ????????????
     */
    public Kaoyan getKaoyanByWord(String word) {
        for (Kaoyan ky : kaoyans) {
            if (word.equals(ky.getWord()))
                return ky;
        }
        return null;
    }

    /**
     * ???????????????
     */
    private void NextQuestion() {
        home_res.setVisibility(View.INVISIBLE);
        home_word.setTextColor(getResources().getColor(R.color.black));
        home_submit.setText("??????");
        home_word.setText("");
        home_word.requestFocus();
        Kaoyan chendu = getKaoyan();
        if (chendu == null) {
            home_mean.setText("?????????????????????");
            home_yinbiao.setText("");
            home_res.setText("");
        } else {
            home_mean.setText(chendu.getMean());
            home_yinbiao.setText(chendu.getYinbiao());
            home_res.setText(chendu.getWord());
        }
    }

    /**
     * ???????????????
     * @param word
     */
    private void RecordRefresh(String word) {
        // ??????????????????
        String recordJson = mSharedPreferences.getString("records", "");
        Type recordListType = new TypeToken<ArrayList<Record>>(){}.getType();
        Gson gson = new Gson();
        List<Record> recordList = gson.fromJson(recordJson, recordListType);
        // ??????????????????
        Record newRecord = new Record();
        newRecord.setDate(nowDate);
        newRecord.setKaoyan(getKaoyanByWord(word));
        if (!recordList.stream().filter(m->m.getKaoyan().getWord().equals(word)).findAny().isPresent()) {
            total_meeting ++;
        }
        count_today ++;
        count_total ++;
        recordList.add(newRecord);

        // ????????????????????????
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("records", gson.toJson(recordList));
        editor.commit();
        // ????????????
        home_metting.setText(total_meeting + " / " + total_word);
        home_count.setText(count_today + " / " + count_total);
    }

    /**
     * ????????????
     * @param word
     */
    private void RecordWrong(String word) {
        // ??????????????????
        String wrongJson = mSharedPreferences.getString("wrongs", "");
        Type wrongListType = new TypeToken<ArrayList<Record>>(){}.getType();
        Gson gson = new Gson();
        List<Record> wrongList = gson.fromJson(wrongJson, wrongListType);
        // ??????????????????
        Record newRecord = new Record();
        newRecord.setDate(nowDate);
        newRecord.setKaoyan(getKaoyanByWord(word));
        if (!wrongList.stream().filter(m->m.getKaoyan().getWord().equals(word)).findAny().isPresent()) {
            wrongList.add(newRecord);
        }
        // ????????????????????????
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("wrongs", gson.toJson(wrongList));
        editor.commit();
    }

    /**
     * ????????????
     * @param word ?????????????????????
     */
    private void setWeightDown(String word) {
        Kaoyan ky = getKaoyanByWord(word);
        setWeightDo(ky, 10);
    }

    /**
     * ????????????
     * @param word ?????????????????????
     */
    private void setWeightUp(String word) {
        Kaoyan ky = getKaoyanByWord(word);
        setWeightDo(ky, 100000);
    }

    /**
     * ?????????????????????
     * @param ky ????????????
     * @param weight ??????
     */
    private void setWeightDo(Kaoyan ky, Integer weight) {
        // ????????????
        for (Kaoyan kaoyan : kaoyans) {
            if (ky.getWord().equals(kaoyan.getWord())) {
                kaoyan.setWeight(weight);
                break;
            }
        }
        // ??????
        Gson gson = new Gson();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("kaoyans", gson.toJson(kaoyans));
        editor.commit();
    }

}
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

        // 获取当前日期
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        nowDate = dateFormat.format(date);

        // 获取存储数据 records
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

        // 获取存储数据 wrongs
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

        // 对存储数据进行处理
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

        // 获取存储数据 kaoyans
        String kaoyanJson = mSharedPreferences.getString("kaoyans", "");
        if ("".equals(kaoyanJson)) {
            // 请求 kaoyan.json 获取数据
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

            // 解析数据，赋值到kaoyans
            kaoyanJson = stringBuilder.toString();
            Type kaoyanListType = new TypeToken<ArrayList<Kaoyan>>(){}.getType();
            List<Kaoyan> kaoyanList = gson.fromJson(kaoyanJson, kaoyanListType);
            kaoyans = kaoyanList;
            // 存储数据
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

        // 页面展示部分
        total_word = kaoyans.size();
        home_metting.setText(total_meeting + " / " + total_word);
        home_count.setText(count_today + " / " + count_total);
        home_date.setText(nowDate);

        // 展示题目
        NextQuestion();

        // 确定按钮点击事件
        home_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确定状态下：
                if (home_submit.getText().equals("确定")) {
                    // Toast.makeText(getContext(), "确认", Toast.LENGTH_SHORT).show();
                    home_submit.setText("下一题");
                    String user_input = home_word.getText().toString();
                    String user_res = home_res.getText().toString();
                    if (user_input.equals(user_res)) {
                        // 正确状态下先降权
                        setWeightDown(user_res);
                        // 正确状态下再跳转下一题
                        NextQuestion();
                    } else {
                        // 错误状态下提示答案并调为红色
                        home_res.setVisibility(View.VISIBLE);
                        home_word.setTextColor(getResources().getColor(R.color.red));
                        // 记录错词本 并加权
                        RecordWrong(user_res);
                        setWeightUp(user_res);
                    }
                    // 记录题数并更新
                    RecordRefresh(user_res);
                } else {
                    // 下一题状态下直接跳转下一题
                    NextQuestion();
                }

            }
        });

        // 看答案按钮点击事件
        home_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_res = home_res.getText().toString();
                home_res.setVisibility(View.VISIBLE);
                // 记录错词本 并加权
                RecordWrong(user_res);
                setWeightUp(user_res);
            }
        });

        return root;
    }

    /**
     * 获取考研单词列表
     * @param key 搜索关键字
     * @return 考研单词列表
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
     * 获取随机考研单词（带权）
     * @return 随机的考研单词
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
     * 根据单词获取考研单词
     * @param word 单词
     * @return 考研单词
     */
    public Kaoyan getKaoyanByWord(String word) {
        for (Kaoyan ky : kaoyans) {
            if (word.equals(ky.getWord()))
                return ky;
        }
        return null;
    }

    /**
     * 跳转下一题
     */
    private void NextQuestion() {
        home_res.setVisibility(View.INVISIBLE);
        home_word.setTextColor(getResources().getColor(R.color.black));
        home_submit.setText("确定");
        home_word.setText("");
        home_word.requestFocus();
        Kaoyan chendu = getKaoyan();
        if (chendu == null) {
            home_mean.setText("获取单词失败了");
            home_yinbiao.setText("");
            home_res.setText("");
        } else {
            home_mean.setText(chendu.getMean());
            home_yinbiao.setText(chendu.getYinbiao());
            home_res.setText(chendu.getWord());
        }
    }

    /**
     * 记录并刷新
     * @param word
     */
    private void RecordRefresh(String word) {
        // 获取存储数据
        String recordJson = mSharedPreferences.getString("records", "");
        Type recordListType = new TypeToken<ArrayList<Record>>(){}.getType();
        Gson gson = new Gson();
        List<Record> recordList = gson.fromJson(recordJson, recordListType);
        // 处理存储数据
        Record newRecord = new Record();
        newRecord.setDate(nowDate);
        newRecord.setKaoyan(getKaoyanByWord(word));
        if (!recordList.stream().filter(m->m.getKaoyan().getWord().equals(word)).findAny().isPresent()) {
            total_meeting ++;
        }
        count_today ++;
        count_total ++;
        recordList.add(newRecord);

        // 存储处理完的数据
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("records", gson.toJson(recordList));
        editor.commit();
        // 更新页面
        home_metting.setText(total_meeting + " / " + total_word);
        home_count.setText(count_today + " / " + count_total);
    }

    /**
     * 记录错题
     * @param word
     */
    private void RecordWrong(String word) {
        // 获取存储数据
        String wrongJson = mSharedPreferences.getString("wrongs", "");
        Type wrongListType = new TypeToken<ArrayList<Record>>(){}.getType();
        Gson gson = new Gson();
        List<Record> wrongList = gson.fromJson(wrongJson, wrongListType);
        // 处理存储数据
        Record newRecord = new Record();
        newRecord.setDate(nowDate);
        newRecord.setKaoyan(getKaoyanByWord(word));
        if (!wrongList.stream().filter(m->m.getKaoyan().getWord().equals(word)).findAny().isPresent()) {
            wrongList.add(newRecord);
        }
        // 存储处理完的数据
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("wrongs", gson.toJson(wrongList));
        editor.commit();
    }

    /**
     * 降权操作
     * @param word 需要降权的单词
     */
    private void setWeightDown(String word) {
        Kaoyan ky = getKaoyanByWord(word);
        setWeightDo(ky, 10);
    }

    /**
     * 升权操作
     * @param word 需要升权的单词
     */
    private void setWeightUp(String word) {
        Kaoyan ky = getKaoyanByWord(word);
        setWeightDo(ky, 100000);
    }

    /**
     * 更新单词的权值
     * @param ky 考研单词
     * @param weight 权值
     */
    private void setWeightDo(Kaoyan ky, Integer weight) {
        // 更新权值
        for (Kaoyan kaoyan : kaoyans) {
            if (ky.getWord().equals(kaoyan.getWord())) {
                kaoyan.setWeight(weight);
                break;
            }
        }
        // 存储
        Gson gson = new Gson();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("kaoyans", gson.toJson(kaoyans));
        editor.commit();
    }

}
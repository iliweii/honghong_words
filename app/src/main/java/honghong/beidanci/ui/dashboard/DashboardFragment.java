package honghong.beidanci.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.stream.Collectors;

import heima.chendu.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

//    private TextView list_date;
//    private ListView list_view;
//
//    private List<Chendu> chendus;
//    private String nowDate;
//    private SharedPreferences mSharedPreferences;
//    // TTS对象
//    private TextToSpeech mTextToSpeech;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        list_date = root.findViewById(R.id.list_date);
//        list_view = root.findViewById(R.id.list_view);
//
//        Gson gson = new Gson();
//
//        // 获取当前日期
//        Date date = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        nowDate = dateFormat.format(date);
//
//        // 请求 heima_chendu.json 获取数据
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            InputStream is = getResources().getAssets().open("heima_chendu.json");
//            InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
//            BufferedReader bReader = new BufferedReader(isReader);
//            String mimeTypeLine = null;
//            while ((mimeTypeLine = bReader.readLine()) != null) {
//                stringBuilder.append(mimeTypeLine);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 解析数据，赋值到chendus
//        String chenduJson = stringBuilder.toString();
//        Type chenduListType = new TypeToken<ArrayList<Chendu>>() {
//        }.getType();
//        List<Chendu> chenduList = gson.fromJson(chenduJson, chenduListType);
//        chendus = chenduList;
//
//        // 将数据展示到页面中
//        List<Chendu> chendus = getChenduList(nowDate);
//        if (chendus.size() == 0) {
//            Chendu chendu = new Chendu();
//            chendu.setWord("今天没有单词~");
//            chendu.setYinbiao("");
//            chendu.setMean("");
//            chendus.add(chendu);
//        }
//        ListviewAdapter listviewAdapter = new ListviewAdapter(getContext(), R.layout.list_item, chendus);
//        list_view.setAdapter(listviewAdapter);
//        setListViewHeightBasedOnChildren(list_view);
//        list_date.setText(nowDate);

        return root;
    }

//    /**
//     * 获取日期列表
//     *
//     * @return
//     */
//    public List<String> getDateList() {
//        List<String> datelist = new ArrayList<String>();
//        for (int i = 0; i < chendus.size(); i++) {
//            datelist.add(chendus.get(i).getDate());
//        }
//        List<String> dates = datelist.stream().distinct().collect(Collectors.toList());
//        return dates;
//    }
//
//    /**
//     * 获取晨读单词列表
//     *
//     * @param date
//     * @return
//     */
//    public List<Chendu> getChenduList(String date) {
//        List<Chendu> chenduList = new ArrayList<Chendu>();
//        for (Chendu cd : chendus) {
//            if (cd.getDate().indexOf(date) > -1) {
//                cd.setWord(cd.getWord().trim());
//                cd.setYinbiao(cd.getYinbiao().replaceAll("(\\\r\\\n|\\\r|\\\n|\\\n\\\r)", ""));
//                chenduList.add(cd);
//            }
//        }
//        List<Chendu> chenduList1 = chenduList.stream().sorted(Comparator.comparing(Chendu::getId)).collect(Collectors.toList());
//        return chenduList1;
//    }
//
//    /**
//     * 获取随机晨读单词
//     *
//     * @param date
//     * @return
//     */
//    public Chendu getChendu(String date) {
//        List<Chendu> chenduList = getChenduList(date);
//        int index = RandomNum(0, chenduList.size() - 1);
//        Chendu cd = chenduList.get(index);
//        cd.setWord(cd.getWord().trim());
//        cd.setYinbiao(cd.getYinbiao().replaceAll("(\\\r\\\n|\\\r|\\\n|\\\n\\\r)", ""));
//        return cd;
//    }
//
//    /**
//     * 获取范围内随机数
//     *
//     * @param min
//     * @param max
//     * @return
//     */
//    public static Integer RandomNum(int min, int max) {
//        int i = (int) (Math.random() * (max - min + 1) + min);
//        return i;
//    }
//
//    /**
//     * 修正listview高度
//     * @param listView
//     */
//    public void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += (listItem.getMeasuredHeight() + 15);
//
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight
//                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }

}
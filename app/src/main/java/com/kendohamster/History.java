package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    CalendarView calendarView;
    Button btnRecord, btnDraw, btnTestPython;
    TextView txtResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        calendarView = findViewById(R.id.calendarView);
        btnRecord = findViewById(R.id.btnRecord);
        btnDraw = findViewById(R.id.btnDraw);

        btnTestPython = findViewById(R.id.btnTestPython);
        txtResults = findViewById(R.id.txtResults);

        /*calendarView.getDate();
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Toast.makeText(this,sdf.format(calendarView.getDate()),Toast.LENGTH_SHORT).show();
            }
        });*/

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(History.this, HistoryDailyRecord.class);
                startActivity(i);
            }
        });

        //此按鈕是用於嘗試java繪圖的功能
        //所以點這邊的按鈕，會進到一個 DrawPictureTry 這個 activity 裡
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(History.this, DrawPictureTry.class);
                startActivity(i);
            }
        });

        btnTestPython.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView;
                double sk1[] = {0, 0, 0.4573170731707317, 0.17119565217391303, 0.5457317073170732, 0.17527173913043478, 0.6158536585365854, 0.35054347826086957, 0, 0, 0.3597560975609756, 0.14266304347826086, 0.3384146341463415, 0.30978260869565216, 0.31097560975609756, 0.4891304347826087, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.4817073170731707, 0.028532608695652176, 0.38109756097560976, 0.024456521739130432};
                double sk2[] = {0, 0, 0.4298780487804878, 0.19157608695652176, 0.5182926829268293, 0.1875, 0.5884146341463414, 0.3586956521739131, 0.5792682926829268, 0.5135869565217391, 0.3353658536585366, 0.17119565217391303, 0.31097560975609756, 0.30570652173913043, 0.29878048780487804, 0.47282608695652173, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.46646341463414637, 0.06521739130434782, 0.3719512195121951, 0.05706521739130435};
                double sk3[] = {0, 0, 0.4176829268292683, 0.21195652173913043, 0.5060975609756098, 0.19972826086956524, 0.5670731707317073, 0.3586956521739131, 0.551829268292683, 0.4891304347826087, 0.3201219512195122, 0.19565217391304346, 0.3018292682926829, 0.30978260869565216, 0.2804878048780488, 0.46875, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.45121951219512196, 0.08559782608695651, 0.36585365853658536, 0.08152173913043478};
                double sk4[] = {0, 0, 0.3871951219512195, 0.23233695652173914, 0.47560975609756095, 0.21603260869565216, 0.5182926829268293, 0.3586956521739131, 0.5274390243902439, 0.4891304347826087, 0.2896341463414634, 0.23233695652173914, 0.27134146341463417, 0.3586956521739131, 0.25914634146341464, 0.5054347826086957, 0.4481707317073171, 0.47282608695652173, 0.4481707317073171, 0.65625, 0, 0, 0.3628048780487805, 0.4483695652173913, 0, 0, 0, 0, 0, 0, 0, 0, 0.42073170731707316, 0.1141304347826087, 0.34146341463414637, 0.11820652173913043};
                double sk5[] = {0, 0, 0.3719512195121951, 0.22418478260869565, 0.4573170731707317, 0.21603260869565216, 0.4969512195121951, 0.34239130434782605, 0.524390243902439, 0.46875, 0.28353658536585363, 0.2282608695652174, 0, 0, 0, 0, 0.4115853658536585, 0.46467391304347827, 0, 0, 0, 0, 0.3353658536585366, 0.46875, 0, 0, 0, 0, 0, 0, 0, 0, 0.4115853658536585, 0.11820652173913043, 0.3353658536585366, 0.12635869565217392};
                double sk6[] = {0, 0, 0.3597560975609756, 0.22418478260869565, 0.4451219512195122, 0.22010869565217395, 0.47865853658536583, 0.3342391304347826, 0.5152439024390244, 0.4605978260869565, 0.2804878048780488, 0.22418478260869565, 0.24695121951219512, 0.3586956521739131, 0, 0, 0.4146341463414634, 0.47282608695652173, 0.35365853658536583, 0.6603260869565217, 0, 0, 0.31402439024390244, 0.46875, 0, 0, 0, 0, 0, 0, 0, 0, 0.40853658536585363, 0.12635869565217392, 0.3323170731707317, 0.13043478260869565};
                double sk7[] = {0, 0, 0.35060975609756095, 0.2404891304347826, 0.4329268292682927, 0.2404891304347826, 0.4573170731707317, 0.35054347826086957, 0.4878048780487805, 0.4565217391304348, 0.2774390243902439, 0.23641304347826086, 0.2621951219512195, 0.33016304347826086, 0, 0, 0.3932926829268293, 0.4850543478260869, 0.3597560975609756, 0.6317934782608695, 0.35060975609756095, 0.7133152173913043, 0.3048780487804878, 0.46875, 0.32621951219512196, 0.6317934782608695, 0, 0, 0, 0, 0, 0, 0.4054878048780488, 0.15489130434782608, 0.3353658536585366, 0.15489130434782608};
                double sk8[] = {0, 0, 0.3475609756097561, 0.25271739130434784, 0.4176829268292683, 0.2608695652173913, 0.43597560975609756, 0.375, 0.4634146341463415, 0.4891304347826087, 0.2804878048780488, 0.25271739130434784, 0.25914634146341464, 0.3342391304347826, 0, 0, 0.3780487804878049, 0.4932065217391305, 0.3475609756097561, 0.6358695652173914, 0, 0, 0.2926829268292683, 0.4605978260869565, 0.2926829268292683, 0.6154891304347826, 0, 0, 0, 0, 0, 0, 0.39939024390243905, 0.17527173913043478, 0.3353658536585366, 0.16304347826086957};
                double sk9[] = {0.4024390243902439, 0.1875, 0.34146341463414637, 0.24864130434782605, 0.4054878048780488, 0.24456521739130435, 0.4115853658536585, 0.3586956521739131, 0.4329268292682927, 0.4565217391304348, 0.28353658536585363, 0.24864130434782605, 0, 0, 0, 0, 0.36585365853658536, 0.47282608695652173, 0.3445121951219512, 0.6195652173913043, 0, 0, 0.29573170731707316, 0.4565217391304348, 0.3079268292682927, 0.5991847826086957, 0, 0, 0.4024390243902439, 0.17934782608695654, 0, 0, 0.3902439024390244, 0.17527173913043478, 0.3353658536585366, 0.16304347826086957};
                ArrayList<SK> sk = new ArrayList<>();
                ArrayList<String> results = new ArrayList<>();

                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(History.this));
                }

                Python py = Python.getInstance();
                //PyObject pyobj = py.getModule("myscript");
                //PyObject pyobj = py.getModule("./src/try");
                PyObject pyobj = py.getModule("src/try");

                sk.add(new SK(sk1)); sk.add(new SK(sk2)); sk.add(new SK(sk3)); sk.add(new SK(sk4)); sk.add(new SK(sk5)); sk.add(new SK(sk6)); sk.add(new SK(sk7)); sk.add(new SK(sk8)); sk.add(new SK(sk9));
                for(int i = 0; i < sk.size(); i++){
                    PyObject obj = pyobj.callAttr("main", sk.get(i).getSk());
                    results.add(obj.toString());
                }
                //PyObject obj = pyobj.callAttr("main", sk1);

                //textView.setText(obj.toString());
                txtResults.setText(results.toString());
            }
        });
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.drawer_open , R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }
    public void selectItem(int position) {
        Intent i = null;
        switch(position) {
            case (R.id.action_action):
                i = new Intent(this,MotionList.class);
                break;
            case R.id.action_history:
                i = new Intent(this, History.class);
                break;
            case R.id.action_menu:
                i = new Intent(this, TrainingMenu.class);
                break;
            case R.id.action_setting:
                i = new Intent(this, Settings.class);
                break;
            default:
                break;
        }

        startActivity(i);
    }
}
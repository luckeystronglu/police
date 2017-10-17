package com.yzh.rfidbike_police.activity;

//public class MainActivity extends BaseActivity {
//    private LocationFragment mLocalFragment;
//    private RegistrationFragment mCheckInFragment;
//    private SettingFragment mSettingFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        if (mLocalFragment == null) {
//            mLocalFragment = new LocationFragment();
//            transaction.add(R.id.container, mLocalFragment);
//            transaction.commit();
//        }
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navi);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//    }
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.location:
//                    FragmentTransaction transactionLocation = getFragmentManager()
//                            .beginTransaction();
//
//                    if (mLocalFragment == null) {
//                        mLocalFragment = new LocationFragment();
//                    }
//                    transactionLocation.replace(R.id.container, mLocalFragment);
//                    transactionLocation.commit();
//                    return true;
//                case R.id.bike_near:
//
//                    return true;
//                case R.id.check_in:
//                    FragmentTransaction transactionCheckIn = getFragmentManager()
//                            .beginTransaction();
//
//                    if (mCheckInFragment == null) {
//                        mCheckInFragment = new RegistrationFragment();
//                    }
//                    transactionCheckIn.replace(R.id.container, mCheckInFragment);
//                    transactionCheckIn.commit();
//                    return true;
//                case R.id.setting:
//                    FragmentTransaction transactionSetting = getFragmentManager()
//                            .beginTransaction();
//
//                    if (mSettingFragment == null) {
//                        mSettingFragment = new SettingFragment();
//                    }
//                    transactionSetting.replace(R.id.container, mSettingFragment);
//                    transactionSetting.commit();
//                    return true;
//            }
//            return false;
//        }
//
//    };
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        if (BaseApplication.getInstance().needGoToMessageCenter) {
//            Intent intentNew = new Intent();
////            intentNew.putExtras(intent.getExtras());
//            intentNew.setClass(context.getApplicationContext(), MessageCenterPoliceActivity.class);
//            intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.getApplicationContext().startActivity(intentNew);
//        }
//    }
//
//    //连续按键两次退出app
//    private long presstime;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
///**
// * web.canGoBack()判断webview是否有可以返回的页面
// */
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (System.currentTimeMillis() - presstime < 2000) {
//                App.exitSystem(this);
//            } else {
//                Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
//                presstime = System.currentTimeMillis();
//            }
//
//        }
//        return true;
//    }
//
//    protected boolean isMessageCenterTop() {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
//        return name.equals(MessageCenterPoliceActivity.class);
//    }
//}

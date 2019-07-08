最近公司接到一个海外的项目，我第一个想到的就是多语言的适配。所以就提前先学习一下，省的到时候麻烦！

# 本文知识点
- Android中多语言的使用
- 关于适配的问题
- 一些常见的问题

## Android中多语言的使用
> 其实在Android中多语言状态的切换其实还是很好处理的，基本上就那么几个步骤：
> - 添加多语言文件
> - 持久化存储语言设置
> - 更新 Configuration 中的 locale 属性
> - 重启 MainActivity


这里面注意一个问题，在处理多语言的时候，很多都是跳转到相应的MainActivity，主要是切换语言之后不能立即生效，所以都是重启相应的页面，但是如果你单独重启了一个页面的话，对于打开的Activity就不能做到同步更改语言，所以一般都是重新打开MainActivity这样就能确保所有页面都能重新处理了！

这里大概说明一下流程，首先在设置语言的地方通过SharedPreferences保存相应的语言设置，然后通过这个保存的字段，重新设置Configuration中的locale属性，重新启动应用(就是上面说的这个逻辑)。这样就达到了相应处理语言的逻辑！

### 1. 添加多语言文件
> 这一步其实创建相应的文件还是很好处理的，麻烦在于相应文档的翻译！这个才是费事的地方！

![演示图片1](https://github.com/AngleLong/LanguageDemo/tree/master/img/Snip20190707_15.png)

按照这个顺序点了之后，然后你按照这个和你们的需求选择一下就好了

![演示图片2](https://github.com/AngleLong/LanguageDemo/tree/master/img/Snip20190707_17.png)

这样就创建完成了相应的语言环境了，这里还有一个需要注意，必须每个文件中都有相同的key，否则会有相应的报错提示！这里你看到报错，基本上都是缺少或者少写了！

### 2. 持久化存储语言设置
> 这里随便在网上找了一个相应的SharedPreferences工具类，这里还有一个问题说明下，千万不要和你项目中的SharedPreferences混在一起，否则清空用户数据的时候，语言这块没有办法拆分出来。

```java
/**
 * @author hejinlong
 * 语言设置的SP
 */
public class SPUtil {

    
    private final String SP_NAME = "language_setting";
    /**
     * 语言字段的选择名称
     */
    private final String TAG_LANGUAGE = "language_select";
    private static volatile SPUtil instance;

    private final SharedPreferences mSharedPreferences;

    private Locale systemCurrentLocal = Locale.ENGLISH;


    private SPUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取保存的语言类型
     *
     * @param select 相应的语言类型
     */
    public void saveLanguage(int select) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(TAG_LANGUAGE, select);
        edit.apply();
        edit.commit();
    }

    /**
     * 获取当前的语言类型
     *
     * @return 当前的语言类型
     */
    public int getSelectLanguage() {
        return mSharedPreferences.getInt(TAG_LANGUAGE, 0);
    }

    /**
     * 获取当前系统的语言
     *
     * @return 当前系统的语言
     */
    public Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    /**
     * 设置当前系统的语言
     * @param local 设置语言
     */
    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }
}
```

没有什么特别的地方，这里它定义了一个语言，应该是在后面获取不到语言的时候，提供一个默认值！我觉得应该是！😂


### 3. 更新 Configuration 中的 locale 属性
> 其实这里很简单的代码就能实现，但是要考虑到相应的适配，其实这里存在7.0的适配，因为7.0之后所有系统的语言规则就变了。这里直接贴出代码，相关的适配都在里面！

```
    //通过修改Configuration中的locale来实现app语言的切换，
    Resources resources = context.getApplicationContext().getResources();
    DisplayMetrics dm = resources.getDisplayMetrics();
    Configuration config = resources.getConfiguration();
    //根据本地存储来获取locale
    Locale locale = getSetLanguageLocale(context);
    config.locale = locale;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        config.setLocales(localeList);
        context.getApplicationContext().createConfigurationContext(config);
        Locale.setDefault(locale);
    }
    resources.updateConfiguration(config, dm);
```

这个是按照官网文档弄得，基本上就这么多代码。但是考虑到横竖屏切换，还有相应和适配的操作。这里我又从网上找到一个工具类！

```java
/**
 * 语言管理工具类
 */
public class LocalManageUtil {

    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SPUtil.getInstance(context).getSystemCurrentLocal();
    }

    /**
     * 获取当前的语言状态文字
     *
     * @param context 上下文
     * @return 当前的语言状态
     */
    public static String getSelectLanguage(Context context) {
        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return context.getString(R.string.language_auto);
            case 1:
                return context.getString(R.string.language_cn);
            case 2:
                return context.getString(R.string.language_traditional);
            case 3:
            default:
                return context.getString(R.string.language_en);
        }
    }

    /**
     * 获取选择的语言设置
     *
     * @param context 上下文
     * @return 当前的语言类型
     */
    public static Locale getSetLanguageLocale(Context context) {
        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return getSystemLocale(context);
            case 1:
                return Locale.CHINA;
            case 2:
                return Locale.TAIWAN;
            case 3:
            default:
                return Locale.ENGLISH;
        }
    }

    /**
     * 保存语言设置的
     * @param context 上下文
     * @param select 语言表示
     */
    public static void saveSelectLanguage(Context context, int select) {
        SPUtil.getInstance(context).saveLanguage(select);
        setApplicationLanguage(context);
    }

    /**
     * 设置/更新 本地语言
     * @param context 上下文
     * @return 上下文
     */
    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    /**
     * 设置/更新 本地语言
     * @param context 上下文
     * @param locale 语言
     * @return 设置完成后的上下文
     */
    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 设置全局Application的语言类型
     */
    public static void setApplicationLanguage(Context context) {
        //通过修改Configuration中的locale来实现app语言的切换，
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        //根据本地存储来获取locale
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    /**
     * 获取当前的系统语言
     * @param context  上下文
     */
    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0以上获取相应的语言，因为在7.0以上的系统语言栏上不止一个语言
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        Log.d(TAG, locale.getLanguage());
        SPUtil.getInstance(context).setSystemCurrentLocal(locale);
    }

    /**
     * 系统变更的时候调用
     * @param context 上下文
     */
    public static void onConfigurationChanged(Context context) {
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }
}
```

里面的注释我已经写得很明白了！所以我就不去做什么解释了！我仔细看了一下相应的代码。这里你主要看onConfigurationChanged这个方法，它涵盖了所有内容，其实就相当于重新初始化的一个步骤！我们再来看看Application中的代码：

```java
public class MyApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        //这个方法是在onCreate前调用，这里获取的是系统的然后设置到这里
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(base);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalManageUtil.setApplicationLanguage(this);
    }
}
```

看了这段代码之后，你就能很好的理解上面代码的含义了！然后，在BaseActivity中重新复写attachBaseContext这个方法!

```java
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }
}
```

上面的这些代码，就能满足相应的多语言切换的场景了！有些代码我还没有深挖，可能还不是很透彻！

### 4. 重启 MainActivity
> 这个之前说了，相当于重新启动应用。所以代码就可以这样弄！

```
Intent intent = new Intent(context, MainActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
context.startActivity(intent);
```

这样切换回MainActivity就可以实现相应的全局替换文字了，其实就是重新启动一个Task并且把之前的Task清理了！

---
关于多语言切换，总结下来的内容就是这样了！如果有什么异常情况，还请告知，我好改一下！防止误导别人！
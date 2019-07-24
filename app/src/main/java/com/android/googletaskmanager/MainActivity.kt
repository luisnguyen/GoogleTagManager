package com.android.googletaskmanager

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.pagerTabStrip
import kotlinx.android.synthetic.main.activity_main.viewPager
import java.util.*

class MainActivity : AppCompatActivity() {

    /**
        private static final String TAG = "MainActivity";
        private static final String KEY_FAVORITE_FOOD = "favorite_food";

        private static final ImageInfo[] IMAGE_INFOS = {
        new ImageInfo(R.drawable.favorite, R.string.pattern1_title, R.string.pattern1_id),
        new ImageInfo(R.drawable.flash, R.string.pattern2_title, R.string.pattern2_id),
        new ImageInfo(R.drawable.face, R.string.pattern3_title, R.string.pattern3_id),
        new ImageInfo(R.drawable.whitebalance, R.string.pattern4_title, R.string.pattern4_id),
        };
     */

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_FAVORITE_FOOD = "favorite_food"
        private val IMAGE_INFOS = arrayOf (
            ImageInfo(R.drawable.favorite, R.string.pattern1_title, R.string.pattern1_id),
            ImageInfo(R.drawable.flash, R.string.pattern2_title, R.string.pattern2_id),
            ImageInfo(R.drawable.face, R.string.pattern3_title, R.string.pattern3_id),
            ImageInfo(R.drawable.whitebalance, R.string.pattern4_title, R.string.pattern4_id)
        )
    }


    /**
     * private ImagePagerAdapter mImagePagerAdapter;
     * private ViewPager mViewPager;
     * private FirebaseAnalytics mFirebaseAnalytics;
     * private String mFavoriteFood;
     */
    private lateinit var mImagePagerAdapter : ImagePagerAdapter
    private lateinit var mFirebaseAnalytics : FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
         *
           String userFavoriteFood = getUserFavoriteFood();
                if (userFavoriteFood == null) {
                    askFavoriteFood();
                } else {
                setUserFavoriteFood(userFavoriteFood);
           }
         *
         */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val userFavoriteFood = getUserFavoriteFood()
        if (userFavoriteFood == null) {
            askFavoriteFood()
        } else {
            setUserFavoriteFood(userFavoriteFood)
        }

        /**
         * mImagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), IMAGE_INFOS);
         */
        mImagePagerAdapter = ImagePagerAdapter(supportFragmentManager, IMAGE_INFOS)

        /**
            mViewPager = findViewById(R.id.viewPager);
            mViewPager.setAdapter(mImagePagerAdapter);
         */
        viewPager.adapter = mImagePagerAdapter

        /**
            ViewPager.LayoutParams params = (ViewPager.LayoutParams)
            findViewById(R.id.pagerTabStrip).getLayoutParams();
            params.isDecor = true;
         */
        val params = pagerTabStrip.layoutParams as ViewPager.LayoutParams
        params.isDecor = true

        /**
         *   mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    recordImageView();
                    recordScreenView();
                }
            });
         */
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                recordImageView()
                recordScreenView()
            }
        })

        recordImageView()
    }

    public override fun onResume() {
        super.onResume()
        recordScreenView()
    }

    private fun askFavoriteFood() {
        val choices = resources.getStringArray(R.array.food_items)
        val ad = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.food_dialog_title)
            .setItems(choices) { _, which ->
                val food = choices[which]
                setUserFavoriteFood(food)
            }.create()

        ad.show()
    }

    private fun getUserFavoriteFood(): String? {
        return PreferenceManager.getDefaultSharedPreferences(this)
            .getString(KEY_FAVORITE_FOOD, null)
    }

    private fun setUserFavoriteFood(food: String) {
        Log.d(TAG, "setFavoriteFood: $food")

        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putString(KEY_FAVORITE_FOOD, food)
            .apply()

        mFirebaseAnalytics.setUserProperty("favorite_food", food)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_share) {
            val name = getCurrentImageTitle()
            val text = "I'd love you to hear about $name"
            Log.d(TAG, "shareFavoriteFood: $text")

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)

            // [START custom_event]
            val params = Bundle()
            params.putString("image_name", name)
            params.putString("full_text", text)
            mFirebaseAnalytics.logEvent("share_image", params)
            // [END custom_event]
        }
        return false
    }

    private fun getCurrentImageTitle(): String {
        val position = viewPager.currentItem
        val info = IMAGE_INFOS[position]
        return getString(info.title)
    }

    private fun getCurrentImageId(): String {
        val position = viewPager.currentItem
        val info = IMAGE_INFOS[position]
        return getString(info.id)
    }

    private fun recordImageView() {
        val id = getCurrentImageId()
        val name = getCurrentImageTitle()

        // [START image_view_event]
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        // [END image_view_event]
    }

    private fun recordScreenView() {
        // This string must be <= 36 characters long in order for setCurrentScreen to succeed.
        val screenName = "${getCurrentImageId()}-${getCurrentImageTitle()}"

        // [START set_current_screen]
        mFirebaseAnalytics.setCurrentScreen(this, screenName, null /* class override */)
        // [END set_current_screen]
    }

    inner class ImagePagerAdapter(
        fm: FragmentManager,
        private val infos: Array<ImageInfo>
    ) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val info = infos[position]
            return ImageFragment.newInstance(info.image)
        }

        override fun getCount() = infos.size

        override fun getPageTitle(position: Int): CharSequence? {
            if (position < 0 || position >= infos.size) {
                return null
            }
            val l = Locale.getDefault()
            val info = infos[position]
            return getString(info.title).toUpperCase(l)
        }
    }

}
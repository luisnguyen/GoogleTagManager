package com.android.googletaskmanager

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * This fragment displays a featured, specified image.
 */
class ImageFragment : Fragment(), View.OnClickListener {

    private var resId: Int = 0
    private lateinit var mFirebaseAnalytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            resId = it.getInt(ARG_PATTERN)
        }
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.imageView) {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, Bundle().apply {
                // ITEM_ID ("item_id") or ITEM_NAME("item_name") is required
                putString(FirebaseAnalytics.Param.ITEM_ID, "product158")
                putString(FirebaseAnalytics.Param.ITEM_NAME, "Awesome product158")
            })
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, null)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(resId)
        imageView.setOnClickListener(this)
        val view_image_fragment = Bundle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
            view_image_fragment.putString("DateOpen", formatter.format(date))
        } else {
            val date = Date()
            val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
            view_image_fragment.putString("DateOpen", formatter.format(date))
        }
        view_image_fragment.putLong("LoadImage", resId.toLong())
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.context!!)
        mFirebaseAnalytics.logEvent("view_image_fragment", view_image_fragment)

        return view
    }

    companion object {
        private const val ARG_PATTERN = "pattern"

        /**
         * Create a [ImageFragment] displaying the given image.
         *
         * @param resId to display as the featured image
         * @return a new instance of [ImageFragment]
         */
        fun newInstance(resId: Int): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putInt(ARG_PATTERN, resId)
            fragment.arguments = args
            return fragment
        }
    }
}

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
    private lateinit var date_open : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            resId = it.getInt(ARG_PATTERN)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        if (v!!.id == R.id.imageView) {
            /** Modify Event - View Image Fragment **/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val date = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
                date_open = formatter.format(date)
            } else {
                val date = Date()
                val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
                date_open = formatter.format(date)
            }
            mFirebaseAnalytics.logEvent("view_image_fragment", Bundle().apply {
                putString("date_open", date_open)
                putString("app_load_image", v.id.toString())
            })
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, null)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(resId)
        imageView.setOnClickListener(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.context!!)

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

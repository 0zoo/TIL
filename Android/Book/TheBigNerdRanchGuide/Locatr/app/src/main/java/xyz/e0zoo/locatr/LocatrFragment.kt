package xyz.e0zoo.locatr


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageView

class LocatrFragment : Fragment() {

    private lateinit var mImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_locatr, container, false)
        mImageView = v.findViewById(R.id.image)

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance() = LocatrFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_locatr, menu)
    }
}

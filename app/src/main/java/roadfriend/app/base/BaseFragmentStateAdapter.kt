package roadfriend.app.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class BaseFragmentStateAdapter(
    private val fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    constructor(fragment: Fragment) : this(fragment.childFragmentManager, fragment.lifecycle)

    constructor(fragmentActivity: FragmentActivity) : this(
        fragmentActivity.supportFragmentManager,
        fragmentActivity.lifecycle
    )

    abstract fun getFragmentIds(): List<Long>

    override fun getItemId(position: Int): Long {
        return getFragmentIds()[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return getFragmentIds().contains(itemId)
    }

    fun getCurrentFragment(position: Int): Fragment? {
        return fragmentManager.findFragmentByTag(PREFIX_FRAGMENT.plus(getItemId(position)))
    }

    companion object {
        private const val PREFIX_FRAGMENT = "f"

    }

}
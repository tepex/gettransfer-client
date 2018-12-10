package android.support.v4.app 

import android.app.Activity

import java.lang.reflect.Field 

/** 
 * Fragment & Loader hacks to fix memory leak. 
 * From https://code.google.com/p/android/issues/detail?id=227136 
 * TODO Remove when the bug is fixed in support-v4. 
 */ 
object MemoryLeakUtils { 

    /** 
     * Hack to force update the LoaderManager's host to avoid a memory leak in retained/detached fragments. 
     * Call this in {@link Fragment#onAttach(Activity)} 
     */ 
     /*
    public static void updateLoaderManagerHostController(Fragment fragment) { 
        if (fragment.mHost != null) { 
            fragment.mHost.getLoaderManager(fragment.mWho, fragment.mLoadersStarted, false); 
        } 
    } 
    */

    /** 
     * This hack is to prevent the root loader manager to leak previous instances of activities 
     * accross rotations. It should be called on activities using loaders directly (not via a fragments). 
     * If the activity has fragment, you also have to also {@link #updateLoaderManagerHostController(Fragment)} above 
     * for each fragment. 
     * Call this in {@link FragmentActivity#onCreate} 
     * 
     * @param activity an actvity that uses a loader and leaks on rotation. 
     */
     /*
    fun updateLoaderManagerHostController(FragmentActivity activity) { 
        if (activity.mFragments != null) { 
            try { 
                final Field mHostField = activity.mFragments.getClass().getDeclaredField("mHost"); 
                mHostField.setAccessible(true); 
                FragmentHostCallback mHost = (FragmentHostCallback) mHostField.get(activity.mFragments); 
                mHost.getLoaderManager("(root)", false, true  the 2 last params are not taken into account); 
            } catch (IllegalAccessException e) { 
                e.printStackTrace(); 
            } catch (NoSuchFieldException e) { 
                e.printStackTrace(); 
            } 
        } 
    } 
    */
    fun updateLoaderManagerHostController(activity: FragmentActivity) {
        if(activity.mFragments == null) return
        try {
            val mHostField = activity.mFragments::class.java.getDeclaredField("mHost")
            mHostField.setAccessible(true)
            val obj = mHostField.get(activity.mFragments)
            println("obj: ${obj::class.qualifiedName}")
            val mHost = mHostField.get(activity.mFragments) as FragmentHostCallback<*>
            println("fragment manager: ${mHost.mFragmentManager.mAdded}")
            //mHost.getLoaderManager("(root)", false, true)
        }
        catch(e: Exception) { e.printStackTrace() }
    }
}

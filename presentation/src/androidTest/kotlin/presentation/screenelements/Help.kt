package presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Help : Screen<Help>() {

    val weSpeak  = KView { withId(R.id.tvWeSpeak) }
    val aboutUs  = KView { withId(R.id.aboutUs) }
    val becomeCarrier  = KView { withId(R.id.becomeCarrier) }
    val messageUs  = KView { withId(R.id.tvMessageUs) }
    val languages  = KView { withId(R.id.ourLanguages) }
    val socialNetwork  = KView { withId(R.id.socialNetwork) }
    val ourLanguages = KSwipeView { withId(R.id.ourLanguages) }
    val network = KSwipeView { withId(R.id.socialNetwork) }
    val writeUs  = KView { withId(R.id.tvWriteUs) }
    val include  = KView { withId(R.id.include) }
    val email  = KView { withId(R.id.fabEmail) }
    val callUs  = KView { withId(R.id.tvCallUs) }
    val viewpager  = KView { withId(R.id.viewpager) }
    val next  = KButton { withId(R.id.btnNext) }
    val btn_continue  = KButton { withId(R.id.btn_continue) }
    val tv_title  = KView { withId(R.id.tv_title) }
    val ivBack  = KView { withId(R.id.ivBack) }
}

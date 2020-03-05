package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Help : Screen<Help>() {

    val tvWeSpeak = KView { withId(R.id.tvWeSpeak) }
    val tvAboutUs = KView { withId(R.id.aboutUs) }
    val tvMessageUs = KView { withId(R.id.tvMessageUs) }
    val tvLanguages = KView { withId(R.id.ourLanguages) }
    val tvSocialNetwork = KView { withId(R.id.socialNetwork) }
    val tvOurLanguages = KSwipeView { withId(R.id.ourLanguages) }
    val tvNetwork = KSwipeView { withId(R.id.socialNetwork) }
    val tvWriteUs = KView { withId(R.id.tvWriteUs) }
    val include = KView { withId(R.id.include) }
    val tvEmail = KView { withId(R.id.fabEmail) }
    val tvCallUs = KView { withId(R.id.tvCallUs) }
    val tvViewPager = KView { withId(R.id.viewpager) }
    val btnNext = KButton { withId(R.id.btnNext) }
    val btnContinue = KButton { withId(R.id.btn_continue) }
    val tvTitle = KView { withId(R.id.tv_title) }
}

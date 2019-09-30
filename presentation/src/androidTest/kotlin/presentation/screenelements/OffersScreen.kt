package presentation.screenelements

import android.view.View

import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView

import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

import org.hamcrest.Matcher

object OffersScreen : Screen<OffersScreen>() {
    val btnBack = KButton { withId(R.id.btnBack) }
    val recycler: KRecyclerView = KRecyclerView({ withId(R.id.rvOffers) }, itemTypeBuilder = { itemType(::Item) })

    class Item(parent: Matcher<View>) : KRecyclerItem<Item>(parent) {
        val btnBook = KButton { withId(R.id.btn_book) }
    }
}

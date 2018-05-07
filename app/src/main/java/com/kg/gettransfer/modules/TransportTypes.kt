package com.kg.gettransfer.modules


import android.content.Context
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.secondary.TransportType
import io.realm.Realm
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypes(val context: Context, val realm: Realm, val api: HttpApi) : KoinComponent {
    val typesMap: Map<String, TransportType>

    init {
        val ids = arrayOf(
                "economy", "business",
                "premium", "van",
                "minibus", "bus",
                "limousine", "helicopter")

        val names = arrayOf(
                context.getString(R.string.economy), context.getString(R.string.business),
                context.getString(R.string.premium), context.getString(R.string.van),
                context.getString(R.string.minibus), context.getString(R.string.bus),
                context.getString(R.string.limousine), context.getString(R.string.helicopter))

        val pax = arrayOf(
                3, 3,
                3, 8,
                16, 50,
                20, 5)

        val lug = arrayOf(
                3, 3,
                3, 6,
                16, 50,
                10, 2)

        typesMap = ids
                .mapIndexed { i, id ->
                    val t = TransportType()
                    t.id = id
                    t.title = names[i]
                    t.paxMax = pax[i]
                    t.luggageMax = lug[i]
                    t
                }
                .associateBy({ it.id }, { it })
                .toMap()
    }

    val typesList: List<TransportType> = typesMap.values.toList()


    fun getNames(ids: List<String?>?): String {
        if (ids == null) return "-"

        val s = typesMap.asSequence()
                .filter { ids.contains(it.key) }
                .map { it.value.title }
                .joinToString(separator = ", ")

        return if (s.isEmpty()) "-" else s
    }
}
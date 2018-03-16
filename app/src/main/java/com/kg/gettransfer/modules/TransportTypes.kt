package com.kg.gettransfer.modules


import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.TransportType
import io.realm.Realm
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypes(val realm: Realm, val api: HttpApi) : KoinComponent {
    private val log = Logger.getLogger("TransportTypes")

    private val types: List<TransportType>

    init {
        val ids = arrayOf(
                1, 2, 3,
                4, 5, 6,
                7, 8)

        val names = arrayOf(
                "Economy", "Business", "Premium",
                "Van", "Minibus", "Bus",
                "Limousine", "Helicopter")

        val pax = arrayOf(
                3, 3, 3,
                8, 16, 50,
                20, 5)

        val lug = arrayOf(
                3, 3, 3,
                6, 16, 50,
                10, 2)

        types = ids.mapIndexed { i, id ->
            val t = TransportType()
            t.id = id
            t.title = names[i]
            t.paxMax = pax[i]
            t.luggageMax = lug[i]
            t
        }.toList()
    }

    fun get(): List<TransportType> = types


    fun getNames(ids: List<Integer?>?): String {
        if (ids == null) return "-"

        var s = ""
        val types = realm.where(TransportType::class.java).findAll()

        types.asSequence()
                .filter { ids.contains(it.id as Integer) }
                .forEach { s += ", " + it.title }

        return if (s.isEmpty()) "-" else s.substring(2)
    }
}
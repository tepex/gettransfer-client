package com.kg.gettransfer


import android.util.Log
import com.getkeepsafe.relinker.ReLinker
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Assert.*
import java.util.logging.Logger


class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun subscriber() {
        System.out.println("before test")

        val obs = Observable.create<Int> { s ->
            System.out.println("start")
            s.onNext(3)
            s.onNext(4)
            s.onComplete()
            System.out.println("complete")
        }

        Observable
                .just(3,4,5)
                .map { ")".repeat(it) }
                .subscribe{System.out.println(it)}

        obs.subscribe { System.out.println("next: " + it) }

        System.out.println("after test")
    }
}

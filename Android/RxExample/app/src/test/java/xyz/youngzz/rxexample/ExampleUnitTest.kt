package xyz.youngzz.rxexample

import io.reactivex.Observable
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        val cities = listOf<String>("Suwon", "Seoul", "Daegu", "Busan")

        cities.mapNotNull { city ->
            if (city.startsWith("S"))
                city
            else
                null
        }.forEach { println(it) }

    }

    @Test
    fun rx_example() {

        // 비동기적으로 실행됨.

        val observable =  Observable.just("Suwon", "Seoul")


        Observable
                .just("Suwon", "Seoul")
                .subscribe { e ->
                    println(e)
                }
    }

}

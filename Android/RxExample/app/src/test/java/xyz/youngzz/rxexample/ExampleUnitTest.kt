package xyz.youngzz.rxexample

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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


    // Sequence
    // Stream   => 지연된 연산
    @Test
    fun kotlin_libraries() {
        // val cities = listOf("Seoul", "Suwon", "Daegu", "Busan")

        // Transform - map
        // : 각각의 원소를 변환한다.

        // fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R>
        // val lengths = cities.map({ e -> e.length })
        // val lengths = cities.map { e -> e.length }
        // print(lengths)

        // T: String
        // R: String?
        /*
        cities.map { city ->
            if (city.startsWith("S"))
                city
            else
                null
        }.forEach { println(it) }
        */

        /*
        cities.mapNotNull { city ->
            if (city.startsWith("S"))
                city
            else
                null
        }.forEach { println(it) }
        */

        // flatMap
        // 1 -> N
        // val numbers = 1..6
        // fun <T, R> Iterable<T>.flatMap(transform: (T) -> R): List<R>
        // numbers.map { number -> 1..number }.forEach { print("$it ") }
        // [ 1, [1, 2], [1, 2, 3], [1, 2, 3, 4] ]
        // [1, 1, 2, 1, 2, 3, 1, 2, 3, 4]


        val cities = listOf("Seoul", "Suwon", "Daegu", "Busan")
        // groupBy

        // Map<String, List<String>>
        cities.groupBy { city ->
            if (city.startsWith("S"))
                "A"
            else
                "B"
        }.forEach { print("$it ") }
    }


    @Test
    fun kotlin_libraries2() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        // distinct: 중복된 항목을 걸러냅니다.
        println(list.map { it % 2 }.distinct())

        // distinctBy: 중복된 항목을 직접 처리합니다.
        val users = listOf("Tom", "Bob", "Alice")
        println(users.distinctBy { it.length })

        // first: 조건을 만족하는 첫번째 원소를 가져옵니다.
        println(users.first())
        println(users.first { it.length > 3 })
        // println(list.first { it > 10 })
        println(list.firstOrNull { it > 10 })

        list.take(5).forEach { print("$it ") }
        list.takeLast(3).forEach { print("$it ") }

        list.takeWhile { it < 3 }.forEach { print("$it ") }
        println()
        list.drop(1).forEach { print("$it ") }
    }

    // zip: 두 컬렉션의 자료들을 조합해서 새로운 자료를 만듭니다.
    @Test
    fun kotlin_libraries3() {
        val countries = listOf("Korea", "United States", "China", "Japan")
        val codes = listOf("KR", "US", "CN", "JP")

        // countries.zip(codes).forEach { println(it) }
        countries.zip(codes) { country, code ->
            "$code - $country"
        }.forEach(::println)
    }


    // 비동기(Asynchronous)
    //  : 결과의 완료시점을 알 수 없다.
    //  => Callback
    //  => 문제점?
    //    : 흐름을 제어하기가 어렵다.

    //  Callback Hell(콜백 지옥)
    //   : 과도한 콜백함수의 중첩으로 인해 코드의 가독성이 현저하게 떨어진다.

    // a(() -> {
    //   b(() -> {
    //      c(() -> {
    //      })
    //   })
    // })

    // 동기(Synchronous)
    // a()
    // b()
    // c()

    // x = a();
    // y = b();
    // z = c(x, y);


    // RxJava / Kotlin
    // : Callback 방식과는 달리 발생하는 이벤트를 이벤트 스트림에 전달하고,
    //   이벤트 스트림을 관찰하다가 원하는 이벤트가 감지되면 이에 따른 동작을 수행하는 패턴을 사용합니다.

    // 장점
    //  => 비동기 이벤트를 컬렉션을 처리하는 개념으로 일반화해서 처리할 수 있다.

    // 요소
    // 1. Observable
    //   : 이벤트를 만들어내는 주제로, 이벤트 스트림을 통해 만든 이벤트를 내보냅니다.
    //     한개부터 여러개의 이벤트를 만들 수 있으며, 만들지 않는 경우도 있습니다.

    // 2. Observer
    //   : Observable 에서 만들어진 이벤트가 반응(React)하며, 이벤트를 받았을 때 수행할 작업을 정의합니다.
    //     * Observer가 Observable를 구독(subscribe) 한다고 합니다.

    // 3. Operator
    //   : 연산자는 이벤트 스트림을 통해 전달되는 이벤트를 변환합니다.
    //     이벤트가 가지고 있는 값을 다른 형태로 변환하는 것도 가능하고, -> map, flatMap
    //     특정 조건을 만족하는 이벤트 스트림을 흘려보내거나, 개수를 변경하는 작업등을 수행할 수 있습니다. -> filter, first, last

    // 4. Scheduler
    //   : 해당 작업을 수행할 스레드를 지정합니다.
    //    UI - main thread
    //    IO / Worker / New Thread
    //    observerOn 메소드를 사용해서 지정하며, 이 메소드를 호출한 직후에 오는 연산자나 옵저버에서 수행되는 작업의 스레드가 변경됩니다.

    // 5. Disposable
    //   : Observer가 Observable을 구독할 때 생성되는 객체로서, Observable에서 만드는 이벤트 스트림과 이에 필요한
    //     리소스를 관리합니다.
    //     Observable로 부터 이벤트를 받지 않기 위해서는 이 객체를 통해 구독해지가 가능합니다.
    //     'CompositeDisposable'을 사용하면 여러개의 Disposable 객체를 하나의 객체에서 관리할 수 있습니다.

    @Test
    fun iter_example() {
        val iterable: Iterable<Int> = listOf(10, 20, 30, 40)
        val iterator: Iterator<Int> = iterable.iterator()

        while (iterator.hasNext()) {
            println(iterator.next())
        }

    }


    @Test
    fun rx_example1() {
        // 1. Observable - Event Stream
        val observable = Observable.just("Hello", "world", "Show", "Me")

        // 2. Observer
        // onNext
        // onError
        // onCompleted
        val disposable = observable.subscribe({ e: String ->
            println("onNext: $e")
        }, { err ->
            println(err)
        }, {
            println("onCompleted")
        })

        // 3. 더 이상 사용하지 않는다.
        disposable.dispose()
    }


    @Test
    fun rx_example2() {
        val disposeBag = CompositeDisposable()

        val d1 = Observable.just("Hello", "world", "Show", "Me")
                .subscribe { e ->

                }
        disposeBag.add(d1)

        val d2 = Observable.just("Hello", "world", "Show", "Me")
                .subscribe { e ->

                }
        disposeBag.add(d2)


        // d1.dispose()
        // d2.dispose()

        disposeBag.dispose()
    }

    @Test
    fun rx_example3() {
        val disposeBag = CompositeDisposable()

//        disposeBag.add(Observable.just("Hello", "world", "Show", "Me")
//                .subscribe { e ->
//
//                })
//
//
//        disposeBag.add(Observable.just("Hello", "world", "Show", "Me")
//                .subscribe { e ->
//
//                })

        disposeBag += Observable.just("Hello", "world", "Show", "Me")
                .subscribe { e ->

                }


        disposeBag += Observable.just("Hello", "world", "Show", "Me")
                .subscribe { e ->

                }



        disposeBag.dispose()
        // disposeBag.clear()
    }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}
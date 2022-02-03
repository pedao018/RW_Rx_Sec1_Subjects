import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject

fun main(args: Array<String>) {
    exampleOf("PublishSubject") {
        /*
        You’re using the default RxJava subscribe method rather than the fancier subscribeBy since you only care about the next event for now
        * */
        val publishSubject = PublishSubject.create<Int>()
        publishSubject.onNext(0)
        val subscriptionOne = publishSubject.subscribe { int ->
            println(int)
        }
        publishSubject.onNext(1)
        publishSubject.onNext(2)

        val subscriptionTwo = publishSubject
            .subscribe { int ->
                printWithLabel("2)", int)
            }
        publishSubject.onNext(3)

        subscriptionOne.dispose()
        publishSubject.onNext(4)

        //Send the complete event through the subject via the onComplete method. This effectively terminates the subject’s observable sequence.
        publishSubject.onComplete()
        //Send another element 5 into the subject. This won’t be emitted and printed, though, because the subject has already terminated.
        publishSubject.onNext(5)
        //Don’t forget to dispose of subscriptions when you’re done!
        subscriptionTwo.dispose()
        val subscriptionThree = publishSubject.subscribeBy(
            onNext = { printWithLabel("3)", it) },
            onComplete = { printWithLabel("3)", "Complete") }
        )

        publishSubject.onNext(6)

    }

    exampleOf("BehaviorSubject") {
        val subscriptions = CompositeDisposable()
        val behaviorSubject =
            BehaviorSubject.createDefault("Initial value")
        behaviorSubject.onNext("X")
        val subscriptionOne = behaviorSubject.subscribeBy(
            onNext = { printWithLabel("1)N", it) },
            onError = { printWithLabel("1)E", it) }
        )

        behaviorSubject.onError(RuntimeException("Error!"))

        /*subscriptions.add(behaviorSubject.subscribeBy(
            onNext = { printWithLabel("2)", it) },
            onError = { printWithLabel("2)", it) }
        ))*/

        val subscriptionTwo = behaviorSubject.subscribeBy(
            onNext = { printWithLabel("2)N", it) },
            onError = { printWithLabel("2)E", it) }
        )
        subscriptionTwo.dispose()
    }

    exampleOf("BehaviorSubject State") {
        val subscriptions = CompositeDisposable()
        val behaviorSubject = BehaviorSubject.createDefault(0)

        println(behaviorSubject.value)
        subscriptions.add(behaviorSubject.subscribeBy {
            printWithLabel("1)", it)
        })

        behaviorSubject.onNext(1)
        println(behaviorSubject.value)
        subscriptions.dispose()
    }

    exampleOf("ReplaySubject") {

        val subscriptions = CompositeDisposable()
        // 1
        val replaySubject = ReplaySubject.createWithSize<String>(2)
        // 2
        replaySubject.onNext("1")

        replaySubject.onNext("2")

        replaySubject.onNext("3")
        // 3
        subscriptions.add(replaySubject.subscribeBy(
            onNext = { printWithLabel("N1)", it) },
            onError = { printWithLabel("E1)", it) }
        ))

        subscriptions.add(replaySubject.subscribeBy(
            onNext = { printWithLabel("N2)", it) },
            onError = { printWithLabel("E2)", it) }
        ))

        replaySubject.onNext("4")

        replaySubject.onError(RuntimeException("Error!"))

        subscriptions.add(replaySubject.subscribeBy(
            onNext = { printWithLabel("N3)", it) },
            onError = { printWithLabel("E3)", it) }
        ))

    }

    exampleOf("AsyncSubject") {
        val subscriptions = CompositeDisposable()
        // 1
        val asyncSubject = AsyncSubject.create<Int>()
        // 2
        subscriptions.add(asyncSubject.subscribeBy(
            onNext = { printWithLabel("1)", it) },
            onComplete = { printWithLabel("1)", "Complete") }
        ))
        // 3
        asyncSubject.onNext(0)
        asyncSubject.onNext(1)
        asyncSubject.onNext(2)
        // 4
        asyncSubject.onComplete()

        subscriptions.dispose()
    }


    exampleOf("RxRelay") {
        val subscriptions = CompositeDisposable()

        val publishRelay = PublishRelay.create<Int>()

        subscriptions.add(publishRelay.subscribeBy(
            onNext = { printWithLabel("1)", it) }
        ))

        publishRelay.accept(1)
        publishRelay.accept(2)
        publishRelay.accept(3)
    }


    exampleOf("Challenge PublishSubject") {

        val subscriptions = CompositeDisposable()

        val dealtHand = PublishSubject.create<List<Pair<String, Int>>>()

        fun deal(cardCount: Int) {
            val deck = cards
            var cardsRemaining = 52
            val hand = mutableListOf<Pair<String, Int>>()

            (0 until cardCount).forEach { _ ->
                val randomIndex = (0 until cardsRemaining).random()
                hand.add(deck[randomIndex])
                deck.removeAt(randomIndex)
                cardsRemaining -= 1
            }

            // Add code to update dealtHand here
            if(points(hand) > 21){
                dealtHand.onError(HandError.Busted())
            }else{
                dealtHand.onNext(hand)
            }
        }

        // Add subscription to dealtHand here
        val subscription = dealtHand.subscribeBy(
            onNext = { hand -> println("${cardString(hand)} for ${points(hand)} points") },
            onError = { error -> println(error) }
        )

        subscriptions.add(subscription)

        deal(3)

        subscriptions.dispose()
    }

}
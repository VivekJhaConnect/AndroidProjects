package com.example.vivek.rxjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val professor = PublishSubject.create<String>()
        professor.subscribe(getFirstStudent())
        professor.onNext("Kevin")
        professor.onNext("David")
        professor.onNext("Kathy")

        professor.subscribe(getLastStudent())
        professor.onNext("Steve")
        professor.onComplete()
    }

    private fun getFirstStudent(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(t: String?) {
                println("First Our professor take us lecture :" + t)
            }

            override fun onError(e: Throwable?) {
                println("Error...")
            }

            override fun onComplete() {
                println("The lecture is ended")
            }

        }
    }

    private fun getLastStudent(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(t: String?) {
                println("Last Our professor take us lecture :" + t)
            }

            override fun onError(e: Throwable?) {
                println("Error")
            }

            override fun onComplete() {
                println("The lecture is ended")
            }

        }
    }
}
/*
 * Developed for the OpenBot project (https://openbot.org) by:
 *
 * Ivo Zivkov
 * izivkov@gmail.com
 *
 * Date: 2020-12-27, 10:59 p.m.
 */

package org.openbot.openbotcontroller

import io.reactivex.subjects.PublishSubject

object StatusEventBus {
    private val subjects: HashMap<String, PublishSubject<String?>> = HashMap()

    fun addSubject(name: String) {
        val subject: PublishSubject<String?> = PublishSubject.create()
        subjects[name] = subject
    }

    fun getProcessor(name: String): PublishSubject<String?>? {
        return subjects[name]
    }

    fun emitEvent(event: String, name: String) {
        subjects[name]?.onNext(event)
    }
}
package com.ongtonnesoup.konvert.di

interface Injector<T> {

    fun inject(target: T)
}
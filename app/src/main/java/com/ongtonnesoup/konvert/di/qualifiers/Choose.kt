package com.ongtonnesoup.konvert.di.qualifiers;

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ContextType(val value: Type = Type.APPLICATION)

enum class Type {
    APPLICATION,
    ACTIVITY
}
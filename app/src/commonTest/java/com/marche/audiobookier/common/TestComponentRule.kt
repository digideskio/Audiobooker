package com.marche.audiobookier.common

import com.marche.audiobookier.AudiobookerApplication
import com.marche.audiobookier.common.injection.component.DaggerTestComponent
import com.marche.audiobookier.common.injection.component.TestComponent
import com.marche.audiobookier.common.injection.module.ApplicationTestModule
import com.marche.audiobookier.data.DataManager
import android.content.Context
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Test rule that creates and sets a Dagger TestComponent into the application overriding the
 * existing application component.
 * Use this rule in your test case in order for the app to use mock dependencies.
 * It also exposes some of the dependencies so they can be easily accessed from the tests, e.g. to
 * stub mocks etc.
 */
class TestComponentRule(val context: Context) : TestRule {

    val testComponent: TestComponent

    init {
        val application = AudiobookerApplication.get(context)
        testComponent = DaggerTestComponent.builder()
                .applicationTestModule(ApplicationTestModule(application))
                .build()
    }

    val mockDataManager: DataManager
        get() = testComponent.dataManager()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val application = AudiobookerApplication.get(context)
                application.component = testComponent
                base.evaluate()
            }
        }
    }
}
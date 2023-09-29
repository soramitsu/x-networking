package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest

abstract class BasicCases<T> {
    private val map = mutableMapOf<String, T>()

    fun getCase(caseName: String): T {
        return map[caseName] ?: provideInstance(caseName).also {
            map[caseName] = it
        }
    }

    protected abstract fun provideInstance(caseName: String): T
}

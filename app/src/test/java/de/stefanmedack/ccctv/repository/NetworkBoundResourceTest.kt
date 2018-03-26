package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.model.Resource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


@Suppress("IllegalIdentifier")
class NetworkBoundResourceTest {

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
    }

    @Test
    fun `fetch from local should not fetch from network if data is fresh`() {
        val exampleTestData = TestData("local")
        val unchanged = TestData("should be unchanged")
        val toTest = TestableNetworkBoundResource(
                localFetcher = Flowable.just(exampleTestData),
                remoteFetcher = Single.just(TestData("network")).slowDown(),
                localData = unchanged
        )

        val result = toTest.resource.test().await()

        result.assertValueAt(0, Resource.Loading())
        result.assertValueAt(1, Resource.Success(exampleTestData))
        toTest.localData shouldEqual unchanged
    }

    @Test
    fun `fetch from local should not fetch from network when data is fresh and network would have been faster`() {
        val exampleTestData = TestData("local")
        val unchanged = TestData("should be unchanged")
        val toTest = TestableNetworkBoundResource(
                localFetcher = Flowable.just(exampleTestData).slowDown(),
                remoteFetcher = Single.just(TestData("network")),
                localData = unchanged
        )

        val result = toTest.resource.test().await()

        result.assertValueAt(0, Resource.Loading())
        result.assertValueAt(1, Resource.Success(exampleTestData))
        toTest.localData shouldEqual unchanged
    }

    @Test
    fun `fetch from network when local is stale overwrites local`() {
        val exampleTestData = TestData("network")
        val toTest = TestableNetworkBoundResource(
                localFetcher = Flowable.empty<TestData>(),
                remoteFetcher = Single.just(exampleTestData).slowDown(),
                localData = null
        )

        val result = toTest.resource.test().await()

        result.assertValueAt(0, Resource.Loading())
        result.assertValueAt(1, Resource.Success(exampleTestData))
        toTest.localData shouldEqual exampleTestData
    }

    @Test
    fun `network fetch failure`() {
        val toTest = TestableNetworkBoundResource(
                localFetcher = Flowable.empty<TestData>(),
                remoteFetcher = Single.error(Exception()),
                localData = null
        )

        val result = toTest.resource.test().await()

        result.assertValueAt(0, Resource.Loading())
//        result.assertValueAt(1, Resource.Error("Error")) // TODO
        toTest.localData.shouldBeNull()
    }

    private fun <T> Flowable<T>.slowDown() =
            Flowable.timer(1, TimeUnit.SECONDS).flatMap { this }

    private fun <T> Single<T>.slowDown() =
            Single.timer(1, TimeUnit.SECONDS).flatMap { this }

    data class TestData(val name: String?)

    class TestableNetworkBoundResource(
            val localFetcher: Flowable<TestData> = Flowable.empty<TestData>(),
            val remoteFetcher: Single<TestData> = Single.just(TestData(null)),
            var localData: TestData? = null
    ) : NetworkBoundResource<TestData, TestData>() {

        override fun fetchLocal(): Flowable<TestData> = localFetcher

        override fun saveLocal(data: TestData) {
            localData = data
        }

        override fun isStale(localResource: Resource<TestData>): Boolean = when (localResource) {
            is Resource.Success -> localResource.data.name == null
            is Resource.Loading -> false
            is Resource.Error -> true
        }

        override fun fetchNetwork(): Single<TestData> = remoteFetcher

        override fun mapNetworkToLocal(data: TestData): TestData = data
    }
}
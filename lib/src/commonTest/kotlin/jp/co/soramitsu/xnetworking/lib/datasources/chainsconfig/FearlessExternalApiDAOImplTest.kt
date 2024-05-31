package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.FearlessConfigDAOImpl
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FearlessExternalApiDAOImplTest {

    private companion object {
        const val chainId = "sora"
    }

    @Mock
    private val configFetcher = mock(classOf<ConfigFetcher>())

    private val configDAO: ConfigDAO =
        FearlessConfigDAOImpl(
            configFetcher = configFetcher
        )

    @Test
    fun `TEST FearlessExternalApiDAO_historyType EXPECT ExternalApiDAOException_NullType`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "history" to JsonObject(
                                content = emptyMap()
                            )
                        )
                    )
                )
            )
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        assertFailsWith<ExternalApiDAOException.NullType> {
            configDAO.historyType(chainId)
        }

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)
    }

    @Test
    fun `TEST FearlessExternalApiDAO_historyType EXPECT success`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "history" to JsonObject(
                                content = mapOf(
                                    "type" to JsonPrimitive("sora")
                                )
                            )
                        )
                    )
                )
            )

        val expectedResult = ExternalApiType.SORA
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        val result = configDAO.historyType(chainId)

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)

        assertTrue { result === expectedResult }
    }

    @Test
    fun `TEST FearlessExternalApiDAO_historyUrl EXPECT ExternalApiDAOException_NullUrl`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "history" to JsonObject(
                                content = mapOf()
                            )
                        )
                    )
                )
            )
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        assertFailsWith<ExternalApiDAOException.NullUrl> {
            configDAO.historyUrl(chainId)
        }

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)
    }

    @Test
    fun `TEST FearlessExternalApiDAO_historyUrl EXPECT success`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "history" to JsonObject(
                                content = mapOf(
                                    "url" to JsonPrimitive("sora.url")
                                )
                            )
                        )
                    )
                )
            )

        val expectedResult = "sora.url"
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        val result = configDAO.historyUrl(chainId)

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)

        assertTrue { result === expectedResult }
    }

    @Test
    fun `TEST FearlessExternalApiDAO_stakingType EXPECT ExternalApiDAOException_NullType`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "staking" to JsonObject(
                                content = mapOf()
                            )
                        )
                    )
                )
            )
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        assertFailsWith<ExternalApiDAOException.NullType> {
            configDAO.stakingType(chainId)
        }

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)
    }

    @Test
    fun `TEST FearlessExternalApiDAO_stakingType EXPECT success`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "staking" to JsonObject(
                                content = mapOf(
                                    "type" to JsonPrimitive("sora")
                                )
                            )
                        )
                    )
                )
            )

        val expectedResult = ExternalApiType.SORA
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        val result = configDAO.stakingType(chainId)

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)

        assertTrue { result === expectedResult }
    }

    @Test
    fun `TEST FearlessExternalApiDAO_stakingUrl EXPECT ExternalApiDAOException_NullUrl`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "staking" to JsonObject(
                                content = mapOf()
                            )
                        )
                    )
                )
            )
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        assertFailsWith<ExternalApiDAOException.NullUrl> {
            configDAO.stakingUrl(chainId)
        }

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)
    }

    @Test
    fun `TEST FearlessExternalApiDAO_stakingUrl EXPECT success`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "externalApi" to JsonObject(
                        content = mapOf(
                            "staking" to JsonObject(
                                content = mapOf(
                                    "url" to JsonPrimitive("sora.url")
                                )
                            )
                        )
                    )
                )
            )

        val expectedResult = "sora.url"
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        val result = configDAO.stakingUrl(chainId)

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)

        assertTrue { result === expectedResult }
    }

    @Test
    fun `TEST FearlessExternalApiDAO_staking EXPECT success`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "assets" to JsonArray(
                        content = listOf(
                            JsonObject(
                                content = mapOf(
                                    "isUtility" to JsonPrimitive(true),
                                    "staking" to JsonPrimitive("parachain")
                                )
                            )
                        )
                    )
                )
            )

        val expectedResult = StakingOption.PARACHAIN
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        val result = configDAO.staking(chainId)

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)

        assertTrue { result === expectedResult }
    }

    @Test
    fun `TEST FearlessExternalApiDAO_staking EXPECT null`() = runTest {
        // Test Data Start
        val configResponseToReturn =
            JsonObject(
                content = mapOf(
                    "chainId" to JsonPrimitive(chainId),
                    "assets" to JsonArray(
                        content = listOf(
                            JsonObject(
                                content = mapOf(
                                    "isUtility" to JsonPrimitive(true)
                                )
                            )
                        )
                    )
                )
            )

        val expectedResult = null
        // Test Data End

        // Mock Preparation Start
        coEvery {
            configFetcher.fetch(chainId)
        }.returns(configResponseToReturn)
        // Mock Preparation End

        val result = configDAO.staking(chainId)

        // Verification & Assertion
        coVerify {
            configFetcher.fetch(chainId)
        }.wasInvoked(1)

        assertTrue { result === expectedResult }
    }

}
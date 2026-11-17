package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class ActorverseRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun ensureProfileInitialized_authenticatedUser_createsProfileAndChecklist() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = ActorverseRepository(firestore, auth)

        val profile = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureProfileInitialized("Alice Vance", "en")
        }
        assertEquals(uid, profile.userId)
        assertEquals("free", profile.subscriptionTier)

        val checklist = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeChecklist(uid).first { it.isNotEmpty() }
        }
        assertTrue(checklist.size >= 10)
    }

    @Test
    fun logCompletedSession_authenticatedOwner_writesSessionAndUpdatesXp() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = ActorverseRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureProfileInitialized("Alice Vance", "en")
        }

        val sessionId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.logCompletedSession(
                exerciseId = "breath_box_${UUID.randomUUID()}",
                exerciseTitle = "Box Breathing",
                departmentId = "breathing",
                durationSeconds = 120L,
                repetitions = 4L,
                notes = "Steady breath control"
            )
        }
        assertTrue(sessionId.isNotBlank())

        val sessions = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeSessions(uid).first { list -> list.any { it.id == sessionId } }
        }
        assertTrue(sessions.any { it.id == sessionId })
    }

    @Test
    fun createAndObserveCommunityPost_authenticatedUser_succeedsWithAlignedQuery() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = ActorverseRepository(firestore, auth)

        val postId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createCommunityPost(
                authorStageName = "AliceV",
                country = "United States",
                language = "en",
                category = "discussion",
                title = "Daily Vocal Warmup ${UUID.randomUUID()}",
                body = "Lip trills and humming helped my breath support today."
            )
        }
        assertTrue(postId.isNotBlank())

        val posts = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCommunityPosts(uid).first { list -> list.any { it.id == postId } }
        }
        assertTrue(posts.any { it.id == postId })
    }

    @Test
    fun observeSessions_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = ActorverseRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.ensureProfileInitialized("Alice Vance", "en")
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = ActorverseRepository(firestore, auth)
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeSessions(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED for cross-user session access")
        } catch (e: Exception) {
            val firestoreException = e.unwrapFirestoreException()
            assertNotNull("Expected nested FirebaseFirestoreException, got $e", firestoreException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
        }
    }

    @Test
    fun observeCommunityPosts_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = ActorverseRepository(firestore, auth)
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeCommunityPosts("unauth_user").first()
            }
            fail("Expected PERMISSION_DENIED for unauthenticated caller")
        } catch (e: Exception) {
            val firestoreException = e.unwrapFirestoreException()
            assertNotNull("Expected nested FirebaseFirestoreException, got $e", firestoreException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice_actorverse@test.com"
        const val BOB_EMAIL = "bob_actorverse@test.com"
        const val DEFAULT_TIMEOUT_MS = 8000L
        const val FLOW_TIMEOUT_MS = 5000L
    }
}

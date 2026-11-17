const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");
const nowTimestamp = () => new Date(Date.now() - 1000);

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_EMAIL = "abdulsamadrind350@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfile(uid) {
  return {
    userId: uid,
    fullName: "Alice Vance",
    stageName: "AliceV",
    country: "United States",
    city: "Los Angeles",
    preferredLanguage: "en",
    experienceLevel: "Intermediate",
    interests: ["Film acting", "Screen acting"],
    actingGoals: "Lead dramatic screen roles",
    isAdultConfirmed: true,
    hidePublicRanking: false,
    dailyGoalMinutes: 30,
    onboardingComplete: true,
    xp: 0,
    streakDays: 0,
    totalPracticeSeconds: 0,
    exercisesCompleted: 0,
    voiceScore: 10,
    actingScore: 10,
    movementScore: 10,
    subscriptionTier: "free",
    isSuspended: false,
    createdAt: nowTimestamp(),
    updatedAt: nowTimestamp(),
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("community_posts").where("isHidden", "==", false).get());
  await assertFails(unauthDb.collection("custom_exercises").where("isPublished", "==", true).get());
});

test("Authenticated user can create and read own profile, cannot read Bob's profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID)));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("User cannot self-escalate subscriptionTier or inject shadow fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const escalated = { ...validProfile(ALICE_UID), subscriptionTier: "premium" };
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).set(escalated));

  const ghost = { ...validProfile(ALICE_UID), isAdmin: true };
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).set(ghost));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID)));
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      subscriptionTier: "pro",
      updatedAt: nowTimestamp(),
    })
  );
});

test("User subcollections (sessions, checklist, recordings, career_items) are strictly owner-isolated", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("sessions").doc("sess_1").set({
      userId: ALICE_UID,
      exerciseId: "breath_box",
      exerciseTitle: "Box Breathing",
      departmentId: "breathing",
      durationSeconds: 120,
      repetitions: 4,
      xpEarned: 40,
      notes: "Felt calm",
      createdAt: nowTimestamp(),
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("sessions").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("sessions").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("recordings").doc("rec_1").set({
      userId: ALICE_UID,
      title: "Hack",
      studioType: "voice",
      localFilePath: "/tmp/x.m4a",
      durationSeconds: 10,
      createdAt: nowTimestamp(),
      updatedAt: nowTimestamp(),
    })
  );
});

test("Community posts allow aligned query with isHidden == false and reject unaligned query", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("community_posts").doc("post_1").set({
      userId: ALICE_UID,
      authorStageName: "AliceV",
      country: "United States",
      language: "en",
      category: "discussion",
      title: "Vocal warm-up tips",
      body: "Humming for 3 minutes before lip trills helps immensely.",
      likesCount: 0,
      reportCount: 0,
      isHidden: false,
      createdAt: nowTimestamp(),
      updatedAt: nowTimestamp(),
    })
  );

  await assertSucceeds(bobDb.collection("community_posts").where("isHidden", "==", false).get());
  await assertFails(bobDb.collection("community_posts").get());

  // Like increment allowed
  await assertSucceeds(
    bobDb.collection("community_posts").doc("post_1").update({
      likesCount: 1,
      updatedAt: nowTimestamp(),
    })
  );

  // Bob cannot edit Alice's body
  await assertFails(
    bobDb.collection("community_posts").doc("post_1").update({
      body: "Tampered",
      updatedAt: nowTimestamp(),
    })
  );
});

test("Reports require valid postId and admin privileges for custom exercises", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const adminDb = testEnv
    .authenticatedContext("admin_uid", { email: ADMIN_EMAIL, email_verified: true })
    .firestore();

  await assertSucceeds(
    aliceDb.collection("community_posts").doc("post_2").set({
      userId: ALICE_UID,
      authorStageName: "AliceV",
      country: "Pakistan",
      language: "ur",
      category: "challenge",
      title: "Urdu Diction Challenge",
      body: "Share your clean articulation drill progress!",
      likesCount: 0,
      reportCount: 0,
      isHidden: false,
      createdAt: nowTimestamp(),
      updatedAt: nowTimestamp(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("reports").doc("rep_1").set({
      reporterId: ALICE_UID,
      postId: "post_2",
      reason: "Test moderation report",
      status: "open",
      createdAt: nowTimestamp(),
      updatedAt: nowTimestamp(),
    })
  );

  // Regular user cannot create custom exercises
  await assertFails(
    aliceDb.collection("custom_exercises").doc("ex_1").set({
      title: "Unauthorized Exercise",
      departmentId: "voice",
      difficulty: "Beginner",
      durationSeconds: 120,
      instructions: "Test instructions",
      isPublished: true,
      createdBy: ALICE_UID,
      createdAt: nowTimestamp(),
      updatedAt: nowTimestamp(),
    })
  );

  // Bootstrapped admin can create custom exercises
  await assertSucceeds(
    adminDb.collection("custom_exercises").doc("ex_1").set({
      title: "Studio Resonance Drill",
      departmentId: "voice",
      difficulty: "Intermediate",
      durationSeconds: 180,
      instructions: "Hum gently at chest pitch for 3 minutes.",
      isPublished: true,
      createdBy: "admin_uid",
      createdAt: nowTimestamp(),
      updatedAt: nowTimestamp(),
    })
  );
});

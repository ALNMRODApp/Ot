const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

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

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can read and write own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      displayName: "Alice",
      theme: "dark_modern",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      email: "bob@example.com",
      displayName: "Bob",
      theme: "dark_modern",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: can create and read own project and file", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const projRef = aliceDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj1");
  await assertSucceeds(
    projRef.set({
      id: "proj1",
      userId: ALICE_UID,
      name: "My App",
      description: "Sample project",
      template: "python",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );

  const fileRef = aliceDb.collection("users").doc(ALICE_UID).collection("files").doc("file1");
  await assertSucceeds(
    fileRef.set({
      id: "file1",
      userId: ALICE_UID,
      projectId: "proj1",
      name: "main.py",
      path: "main.py",
      language: "python",
      content: "print('Hello STF Code')",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(projRef.get());
  await assertSucceeds(fileRef.get());
});

test("Authenticated user: can create and read own custom agent", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const agentRef = aliceDb.collection("users").doc(ALICE_UID).collection("custom_agents").doc("agent1");
  await assertSucceeds(
    agentRef.set({
      id: "agent1",
      userId: ALICE_UID,
      name: "Security Auditor",
      description: "Finds vulnerabilities",
      systemInstruction: "You are a senior security researcher.",
      iconName: "Shield",
      accentColorHex: "#F14C4C",
      category: "security",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
  await assertSucceeds(agentRef.get());
});

test("Cross-user access: Bob cannot access Alice's files or agents", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("files").doc("file1").set({
      id: "file1",
      userId: ALICE_UID,
      projectId: "proj1",
      name: "secret.py",
      path: "secret.py",
      language: "python",
      content: "SECRET_KEY = 12345",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
    await context.firestore().collection("users").doc(ALICE_UID).collection("custom_agents").doc("agent1").set({
      id: "agent1",
      userId: ALICE_UID,
      name: "Secret Agent",
      description: "Top secret",
      systemInstruction: "Classified",
      iconName: "Lock",
      accentColorHex: "#007ACC",
      category: "general",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("files").doc("file1").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("custom_agents").doc("agent1").get()
  );
});

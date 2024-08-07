@Library('jenkins-library') _

def android_pipeline = new org.android.ShareFeature(
  steps: this,
  test: true,
  agentImage: "build-tools/android-build-box:jdk17",
  buildCmd: 'clean build',
  testCmd: 'test --info',
  publishCmd: ':lib:publishAndroidReleasePublicationToScnRepoRepository',
  sonarProjectKey: "sora:x-networking",
  sonarProjectName: "x-networking",
  dojoProductType: "sora-mobile"
)

def ios_pipeline = new org.ios.AppPipeline(
    steps: this,
    appEnable: false,
    appTests: false,
    disableUpdatePods: true,
    disableInstallPods: true,
    label: "mac-sora",
    gradleCmd: "kmmBridgePublish"
)

android_pipeline.runPipeline()
ios_pipeline.runPipeline('x-networking')
@Library('jenkins-library@feature/DOPS-3339/x-networking') _

def jobParams = [
  booleanParam(defaultValue: false, description: 'push to the dev profile', name: 'prDeployment')
]

def android_pipeline = new org.android.ShareFeature(
  steps: this,
  test: true,
  agentImage: "build-tools/android-build-box:jdk17",
  buildCmd: 'clean build',
  testCmd: 'test --info',
  publishCmd: ':lib:publishAndroidReleasePublicationToScnRepoRepository',
  sonarProjectKey: "sora:x-networking",
  sonarProjectName: "x-networking",
  dojoProductType: "sora-mobile",
  jobParams: jobParams
)

def ios_pipeline = new org.ios.AppPipeline(
    steps: this,
    appEnable: false,
    appTests: false,
    disableUpdatePods: true,
    disableInstallPods: true,
    label: "mac-sora",
    gradleCmd: "kmmBridgePublish",
    jobParams: jobParams
)

android_pipeline.runPipeline()
ios_pipeline.runPipeline('x-networking')
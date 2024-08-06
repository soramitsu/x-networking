@Library('jenkins-library') _

def jobParams = [
  booleanParam(defaultValue: false, description: 'push to the dev profile', name: 'prDeployment')
]

def pipeline = new org.android.ShareFeature(
  steps: this,
  test: true,
  agentImage: "build-tools/android-build-box:jdk17",
  buildCmd: 'clean build',
  testCmd: 'test --info',
  publishCmd: ':lib:basic:publishAndroidReleasePublicationToScnRepoRepository :lib:sorawallet:publishAndroidReleasePublicationToScnRepoRepository :lib:fearlesswallet:publishAndroidReleasePublicationToScnRepoRepository',
  sonarProjectKey: "sora:x-networking",
  sonarProjectName: "x-networking",
  dojoProductType: "sora-mobile",
  jobParams: jobParams
)

pipeline.runPipeline()
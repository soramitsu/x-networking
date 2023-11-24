@Library('jenkins-library') _

def jobParams  = [
  booleanParam(defaultValue: false, name: 'prDeployment'),
]

def pipeline = new org.android.ShareFeature().call(
  steps: this,
  jobParams: jobParams,
  detekt: false,
  test: true,
  dockerImage: "build-tools/android-build-box:jdk17",
  nexusCredentials: "bot-soramitsu-rw",
  buildCmd: 'clean build',
  testCmd: 'test --info',
  publishCmd: ':lib:basic:publishAndroidReleasePublicationToScnRepoRepository :lib:sorawallet:publishAndroidReleasePublicationToScnRepoRepository :lib:fearlesswallet:publishAndroidReleasePublicationToScnRepoRepository',
  dojo: true,
  dojoProductType: "fearless"
)

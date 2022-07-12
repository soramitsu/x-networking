Pod::Spec.new do |spec|
    spec.name                     = 'X-Networking'
    spec.version                  = '0.0.41'
    spec.homepage                 = 'Link to the Shared Module homepage'
    spec.source                   = { :git => 'https://github.com/soramitsu/x-networking.git', :tag => '0.0.41' }
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Some description for the Shared Module'
    spec.vendored_frameworks      = 'AppCommonNetworking/X_Networking/build/XCFrameworks/release/X_Networking.xcframework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '11.0'
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':commonNetworking',
        'PRODUCT_MODULE_NAME' => 'X-Networking',
    }
end
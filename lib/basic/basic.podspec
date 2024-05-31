Pod::Spec.new do |spec|
    spec.name                     = 'basic'
    spec.version                  = '0.2.6'
    spec.homepage                 = 'Link to the Shared Module homepage'
    spec.source                   = { :git => 'https://github.com/soramitsu/x-networking.git', :tag => '0.2.6' }
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Some description for the Shared Module'
    spec.vendored_frameworks      = 'lib/basic/build/XCFrameworks/release/basic.xcframework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '11.0'
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':core:basic',
        'PRODUCT_MODULE_NAME' => 'XNetworking.Basic',
    }
                
end
Pod::Spec.new do |spec|
    spec.name                     = 'remote'
    spec.version                  = '1.0.0'
    spec.homepage                 = 'https://gptinvestorapp.com'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Remote module for GPT Investor'
    spec.vendored_frameworks      = 'build/cocoapods/framework/Remote.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target    = '16.0'
                
                
    if !Dir.exist?('build/cocoapods/framework/Remote.framework') || Dir.empty?('build/cocoapods/framework/Remote.framework')
        raise "

        Kotlin framework 'Remote' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :remote:remote:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.xcconfig = {
        'ENABLE_USER_SCRIPT_SANDBOXING' => 'NO',
    }
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':remote:remote',
        'PRODUCT_MODULE_NAME' => 'Remote',
    }
                
    spec.script_phases = [
        {
            :name => 'Build remote',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end
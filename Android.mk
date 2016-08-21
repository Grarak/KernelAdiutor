# Gradle Build Script for KernelAdiutor
# To be used in Android Platform Source
# 
# Copyright (C) 2016 Simao Gomes Viana
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 

# 
# To build this, use make KernelAdiutor or mmm packages/apps/KernelAdiutor
# Note: You must export ANDROID_HOME as your Android SDK directory
# Don't forget to install the necessary build tools etc. in order to build KA
# 

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := KernelAdiutorGradle
LOCAL_MODULE_TAGS := optional
LOCAL_INSTALL_APK := $(OUT)/system/app/KernelAdiutor/KernelAdiutor.apk
LOCAL_OUTPUT_APK  := $(LOCAL_PATH)/app/build/outputs/apk/app-release-unsigned.apk
LOCAL_PATH_ := $(LOCAL_PATH)

# Build with gradle
$(LOCAL_OUTPUT_APK):
	@echo "Building KernelAdiutor..."
	@echo "Entering directory $(LOCAL_PATH_)"
	cd $(LOCAL_PATH_); \
	./gradlew clean build assembleRelease

# Add rule
all_modules: $(LOCAL_OUTPUT_APK)
.PHONY: $(LOCAL_OUTPUT_APK)

# Sign and install the apk
include $(CLEAR_VARS)

LOCAL_INSTALL_APK := $(OUT)/system/app/KernelAdiutor/KernelAdiutor.apk
LOCAL_OUTPUT_APK  := app/build/outputs/apk/app-release-unsigned.apk
LOCAL_MODULE := KernelAdiutor
LOCAL_SRC_FILES := $(LOCAL_OUTPUT_APK)
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := platform

include $(BUILD_PREBUILT)

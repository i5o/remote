FROM gradle:jdk8-alpine

USER root

# Download and untar Android SDK tools
RUN mkdir -p /usr/local/android-sdk-linux && \
    wget https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip -O tools.zip && \
    unzip tools.zip -d /usr/local/android-sdk-linux && \
    rm tools.zip

# Set environment variable
ENV ANDROID_HOME /usr/local/android-sdk-linux
ENV PATH ${ANDROID_HOME}/tools:$ANDROID_HOME/tools/bin:$PATH

RUN ls /usr/local/android-sdk-linux/tools/bin
RUN yes | sdkmanager "add-ons;addon-google_apis-google-19"
RUN yes | sdkmanager "build-tools;23.0.1"
RUN yes | sdkmanager "build-tools;25.0.2"
RUN yes | sdkmanager "extras;android;m2repository"
RUN yes | sdkmanager "extras;google;m2repository"
RUN yes | sdkmanager "extras;m2repository;com;android;support;constraint;constraint-layout-solver;1.0.2"
RUN yes | sdkmanager "extras;m2repository;com;android;support;constraint;constraint-layout;1.0.2"
RUN yes | sdkmanager "platform-tools"
RUN yes | sdkmanager "platforms;android-22"
RUN yes | sdkmanager "platforms;android-23"
RUN yes | sdkmanager "platforms;android-24"
RUN yes | sdkmanager "platforms;android-25"
RUN yes | sdkmanager "platforms;android-28"
RUN yes | sdkmanager "system-images;android-25;google_apis;armeabi-v7a"
RUN yes | sdkmanager "system-images;android-21;google_apis;armeabi-v7a"
RUN yes | sdkmanager "system-images;android-22;google_apis;armeabi-v7a"
RUN yes | sdkmanager "tools"

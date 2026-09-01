var javaCppVersion = "1.5.14"
var ffmpegVersion = "8.1.2-$javaCppVersion"
var tesseractVersion = "5.5.3-$javaCppVersion"
var leptonicaVersion = "1.87.0-$javaCppVersion"

plugins {
    id("java-library-conventions")
}

dependencies {
    // javacv & javacpp
    api("org.bytedeco:javacv:$javaCppVersion") {
        exclude(group = "org.bytedeco", module = "artoolkitplus")
        exclude(group = "org.bytedeco", module = "flycapture")
        exclude(group = "org.bytedeco", module = "libdc1394")
        exclude(group = "org.bytedeco", module = "libfreenect")
        exclude(group = "org.bytedeco", module = "libfreenect2")
        exclude(group = "org.bytedeco", module = "librealsense")
        exclude(group = "org.bytedeco", module = "librealsense2")
        exclude(group = "org.bytedeco", module = "opencv")
        exclude(group = "org.bytedeco", module = "openblas")
        exclude(group = "org.bytedeco", module = "videoinput")
    }
    api("org.bytedeco:javacpp-platform:$javaCppVersion") {
        exclude(group = "org.bytedeco", module = "javacpp")
    }
    api("org.bytedeco:javacpp:$javaCppVersion")
    api("org.bytedeco:javacpp:$javaCppVersion:linux-x86_64")
    api("org.bytedeco:javacpp:$javaCppVersion:macosx-arm64")
    api("org.bytedeco:javacpp:$javaCppVersion:windows-x86_64")

    // ffmpeg
    api("org.bytedeco:ffmpeg-platform:$ffmpegVersion") {
        exclude(group = "org.bytedeco", module = "ffmpeg")
        exclude(group = "org.bytedeco", module = "javacpp-platform")
    }
    api("org.bytedeco:ffmpeg:$ffmpegVersion")
    api("org.bytedeco:ffmpeg:$ffmpegVersion:linux-x86_64")
    api("org.bytedeco:ffmpeg:$ffmpegVersion:macosx-arm64")
    api("org.bytedeco:ffmpeg:$ffmpegVersion:windows-x86_64")

    // tesseract
    api("org.bytedeco:tesseract-platform:$tesseractVersion") {
        exclude(group = "org.bytedeco", module = "tesseract")
        exclude(group = "org.bytedeco", module = "javacpp-platform")
        exclude(group = "org.bytedeco", module = "leptonica-platform")
    }
    api("org.bytedeco:tesseract:$tesseractVersion")
    api("org.bytedeco:tesseract:$tesseractVersion:linux-x86_64")
    api("org.bytedeco:tesseract:$tesseractVersion:macosx-arm64")
    api("org.bytedeco:tesseract:$tesseractVersion:windows-x86_64")

    // leptonica
    api("org.bytedeco:leptonica-platform:$leptonicaVersion") {
        exclude(group = "org.bytedeco", module = "leptonica")
        exclude(group = "org.bytedeco", module = "javacpp-platform")
    }
    api("org.bytedeco:leptonica:$leptonicaVersion")
    api("org.bytedeco:leptonica:$leptonicaVersion:linux-x86_64")
    api("org.bytedeco:leptonica:$leptonicaVersion:macosx-arm64")
    api("org.bytedeco:leptonica:$leptonicaVersion:windows-x86_64")
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("platform-commons-parser")
}

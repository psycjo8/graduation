#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/asset_manager_jni.h>
#include <android/log.h>

using namespace cv;
using namespace std;

extern "C"{
JNIEXPORT void JNICALL
Java_com_example_user_myapplication_PreprocessImage_loadImage(JNIEnv *env, jobject instance,
                                                           jstring imageFileName_, jlong img) {
    Mat &img_input = *(Mat *) img;

    const char *nativeFileNameString = env->GetStringUTFChars(imageFileName_, JNI_FALSE);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    img_input = imread(pathDir, IMREAD_COLOR);
}

JNIEXPORT void JNICALL
Java_com_example_user_myapplication_PreprocessImage_imageprocessing(JNIEnv *env, jobject instance,
                                                                 jlong inputImage,
                                                                 jlong outputImage) {
    Mat &img_input = *(Mat *) inputImage;
    Mat &img_output = *(Mat *) outputImage;
    Mat element5(5, 5, CV_8U, cv::Scalar(1));

    cvtColor(img_input, img_input, CV_BGR2GRAY);
    GaussianBlur(img_input, img_input, Size(7,7), 1.5, 1.5);
    adaptiveThreshold(img_input, img_input, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 31, 5);
    morphologyEx(img_input, img_input, MORPH_CLOSE, element5);
    erode(img_input, img_output, element5, Point(-1,-1), 1, BORDER_DEFAULT, morphologyDefaultBorderValue());

}

}
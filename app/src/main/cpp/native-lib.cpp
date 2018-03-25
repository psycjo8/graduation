#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/asset_manager_jni.h>
#include <android/log.h>

using namespace cv;
using namespace std;

extern "C"{
JNIEXPORT void JNICALL
Java_kr_ac_kpu_block_smared_ImageProcessingActivity_loadImage(JNIEnv *env, jobject instance,
                                                           jstring imageFileName_, jlong img) {
    Mat &img_input = *(Mat *) img;

    const char *nativeFileNameString = env->GetStringUTFChars(imageFileName_, JNI_FALSE);

    string baseDir("/storage/emulated/0/SmaRed/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    img_input = imread(pathDir, IMREAD_COLOR);
}

JNIEXPORT void JNICALL
Java_kr_ac_kpu_block_smared_ImageProcessingActivity_imageprocessing(JNIEnv *env, jobject instance,
                                                                 jlong inputImage,
                                                                 jlong outputImage) {
    Mat &img_input = *(Mat *) inputImage;
    Mat &img_output = *(Mat *) outputImage;
    Mat element5(5, 5, CV_8U, cv::Scalar(1));

    cvtColor(img_input, img_input, CV_BGR2GRAY);
    GaussianBlur(img_input, img_input, Size(7,7), 1.5, 1.5);
    adaptiveThreshold(img_input, img_input, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 31, 5);
    morphologyEx(img_input, img_output, MORPH_CLOSE, element5);
    //erode(img_input, img_output, element5, Point(-1,-1), 1, BORDER_DEFAULT, morphologyDefaultBorderValue());



}
/*
apply Otsu threshold to the region in mask
*/
Mat thr_roi_otsu(Mat& mask, Mat& im)
{
    Mat bw = Mat::ones(im.size(), CV_8U) * 255;

    vector<unsigned char> pixels(countNonZero(mask));
    int index = 0;
    for (int r = 0; r < mask.rows; r++)
    {
        for (int c = 0; c < mask.cols; c++)
        {
            if (mask.at<unsigned char>(r, c))
            {
                pixels[index++] = im.at<unsigned char>(r, c);
            }
        }
    }
    // threshold pixels
    threshold(pixels, pixels, 0, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);
    // paste pixels
    index = 0;
    for (int r = 0; r < mask.rows; r++)
    {
        for (int c = 0; c < mask.cols; c++)
        {
            if (mask.at<unsigned char>(r, c))
            {
                bw.at<unsigned char>(r, c) = pixels[index++];
            }
        }
    }

    return bw;
}

/*
apply k-means to the region in mask
*/
Mat thr_roi_kmeans(Mat& mask, Mat& im)
{
    Mat bw = Mat::ones(im.size(), CV_8U) * 255;

    vector<float> pixels(countNonZero(mask));
    int index = 0;
    for (int r = 0; r < mask.rows; r++)
    {
        for (int c = 0; c < mask.cols; c++)
        {
            if (mask.at<unsigned char>(r, c))
            {
                pixels[index++] = (float)im.at<unsigned char>(r, c);
            }
        }
    }
    // cluster pixels by gray level
    int k = 2;
    Mat data(pixels.size(), 1, CV_32FC1, &pixels[0]);
    vector<float> centers;
    vector<int> labels(countNonZero(mask));
    kmeans(data, k, labels, TermCriteria(CV_TERMCRIT_EPS+CV_TERMCRIT_ITER, 10, 1.0), k, KMEANS_PP_CENTERS, centers);
    // examine cluster centers to see which pixels are dark
    int label0 = centers[0] > centers[1] ? 1 : 0;
    // paste pixels
    index = 0;
    for (int r = 0; r < mask.rows; r++)
    {
        for (int c = 0; c < mask.cols; c++)
        {
            if (mask.at<unsigned char>(r, c))
            {
                bw.at<unsigned char>(r, c) = labels[index++] != label0 ? 255 : 0;
            }
        }
    }

    return bw;
}

/*
apply procfn to each connected component in the mask,
then paste the results to form the final image
*/
Mat proc_parts(Mat& mask, Mat& im, Mat (procfn)(Mat&, Mat&))
{
    Mat tmp = mask.clone();
    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;
    findContours(tmp, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));

    Mat byparts = Mat::ones(im.size(), CV_8U) * 255;

    for(int idx = 0; idx >= 0; idx = hierarchy[idx][0])
    {
        Rect rect = boundingRect(contours[idx]);
        Mat msk = mask(rect);
        Mat img = im(rect);
        // process the rect
        Mat roi = procfn(msk, img);
        // paste it to the final image
        roi.copyTo(byparts(rect));
    }

    return byparts;
}

int _tmain(int argc, char* argv[])
{
    Mat im = imread("1.jpg", 0);
    // detect text regions
    Mat morph;
    Mat kernel = getStructuringElement(MORPH_ELLIPSE, Size(3, 3));
    morphologyEx(im, morph, CV_MOP_GRADIENT, kernel, Point(-1, -1), 1);
    // prepare a mask for text regions
    Mat bw;
    threshold(morph, bw, 0, 255, THRESH_BINARY | THRESH_OTSU);
    morphologyEx(bw, bw, CV_MOP_DILATE, kernel, Point(-1, -1), 10);

    Mat bw2x, im2x;
    pyrUp(bw, bw2x);
    pyrUp(im, im2x);

    // apply Otsu threshold to all text regions, then upsample followed by downsample
    Mat otsu1x = thr_roi_otsu(bw, im);
    pyrUp(otsu1x, otsu1x);
    pyrDown(otsu1x, otsu1x);

    // apply k-means to all text regions, then upsample followed by downsample
    Mat kmeans1x = thr_roi_kmeans(bw, im);
    pyrUp(kmeans1x, kmeans1x);
    pyrDown(kmeans1x, kmeans1x);

    // upsample input image, apply Otsu threshold to all text regions, downsample the result
    Mat otsu2x = thr_roi_otsu(bw2x, im2x);
    pyrDown(otsu2x, otsu2x);

    // upsample input image, apply k-means to all text regions, downsample the result
    Mat kmeans2x = thr_roi_kmeans(bw2x, im2x);
    pyrDown(kmeans2x, kmeans2x);

    // apply Otsu threshold to individual text regions, then upsample followed by downsample
    Mat otsuparts1x = proc_parts(bw, im, thr_roi_otsu);
    pyrUp(otsuparts1x, otsuparts1x);
    pyrDown(otsuparts1x, otsuparts1x);

    // apply k-means to individual text regions, then upsample followed by downsample
    Mat kmeansparts1x = proc_parts(bw, im, thr_roi_kmeans);
    pyrUp(kmeansparts1x, kmeansparts1x);
    pyrDown(kmeansparts1x, kmeansparts1x);

    // upsample input image, apply Otsu threshold to individual text regions, downsample the result
    Mat otsuparts2x = proc_parts(bw2x, im2x, thr_roi_otsu);
    pyrDown(otsuparts2x, otsuparts2x);

    // upsample input image, apply k-means to individual text regions, downsample the result
    Mat kmeansparts2x = proc_parts(bw2x, im2x, thr_roi_kmeans);
    pyrDown(kmeansparts2x, kmeansparts2x);

    return 0;
}
}
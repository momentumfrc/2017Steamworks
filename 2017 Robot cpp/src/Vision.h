/*
 * Vision.h
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#ifndef SRC_VISION_H_
#define SRC_VISION_H_

#include <cscore.h>
#include <opencv2/core/core.hpp>
#include <CameraServer.h>
#include <opencv2/imgproc/imgproc.hpp>

enum BlurType {BOX, GAUSSIAN, MEDIAN, BILATERAL};
typedef struct {
	double min;
	double max;
} Range;

class Vision {
public:
	Vision(std::string name, int dev, int w, int h);
	Vision(std::string name, int dev);
	virtual ~Vision();

protected:
	cs::UsbCamera cam;
	cs::CvSink imagesink;
	cs::CvSource imagesource;
	cv::Mat image;
	std::vector<std::vector<cv::Point>> contours;

	cv::Mat temp;

	virtual void Pocess();
	virtual void CameraInit();

	void DrawText(std::string text, int x, int y, double size, double r, double g, double b, int thickness);
	void DrawCircle(cv::Point center, int radius, double r, double g, double b);
	void DrawCircle(double x, double y, int radius, double r, double g, double b);
	void DrawContours(double r, double g, double b);
	void DrawContour(int index, double r, double g, double b);
	void blur(BlurType type, double doubleRadius);
	void HsvThreshold(Range hue, Range sat, Range val);
	void findContours(bool ExternalOnly);
	void filterContours(double minArea, double minPerimeter, Range Width, Range Height, Range Solidity, Range VertexCount, Range Ratio);

	std::vector<cv::Point> Centers();

	// The first dimension represents each contour, and the second dimension gives the width and height.
	std::vector<std::vector<double>> wH();

	std::thread start();
private:
	frc::CameraServer *CamServ = frc::CameraServer::GetInstance();
};



#endif /* SRC_VISION_H_ */

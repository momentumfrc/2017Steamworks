/*
 * Vision.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */
#include "Vision.h"

Vision::Vision(std::string name, int dev, int w, int h) {
	this->cam = new cs::UsbCamera(name, dev);
	this->cam.SetResolution(w,h);
	CameraInit();
	imagesink = new cs::CvSink(name + " Sink");
	imagesink.SetSource(this->cam);
	CamServ->PutVideo(name + " Source", w, h);
}

Vision::Vision(std::string name, int dev) {
	this->Vision(name, dev, 640, 480);
}

void Vision::DrawText(std::string text, int x, int y, double size, double r, double g, double b, int thickness) {
	cv::putText(cv::InputOutputArray(image), text, new cv::Point(x,y), cv::HersheyFonts::FONT_HERSHEY_SIMPLEX, size, new cv::Scalar(r,g,b), thickness, cv::LineTypes::LINE_8, false);
}

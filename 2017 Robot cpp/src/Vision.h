/*
 * Vision.h
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#ifndef SRC_VISION_H_
#define SRC_VISION_H_

#include <cscore.h>
#include <cscore_c.h>

class Vision {
public:

protected:
	cs::UsbCamera cam;
	cs::CvSink imagesink;
	cs::CvSource imagesource;
	cv::Mat image;
private:

}



#endif /* SRC_VISION_H_ */

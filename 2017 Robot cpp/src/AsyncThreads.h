/*
 * AsyncThreads.h
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#ifndef SRC_ASYNCTHREADS_H_
#define SRC_ASYNCTHREADS_H_

class TurnThread: public std::thread {
public:
	TurnThread(DriveSystem *drive, double degrees, bool debug);
	void Stop();
private:
	bool stop = false;
	void run(DriveSystem *drive, double degrees, bool debug);
};
class MoveThread: public std::thread {
public:
	MoveThread(DriveSystem *drive, double dist, bool debug);
	void Stop();
private:
	bool stop = false;
	void run(DriveSystem *drive, double dist, bool debug);
};

#endif /* SRC_ASYNCTHREADS_H_ */

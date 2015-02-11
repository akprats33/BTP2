package mas.localScheduling.plan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import mas.job.job;

/**
 * @author Anand Prajapati
 * 
 *  To calculate utilization and average processing times, it stores a queue of 
 *  completed jobs with a maximum size limit of 200;
 *  
 *  list 'sizeQueue' contains snapshots of size of queue of jobs
 *  Hence average size will simply be
 */

public class StatsTracker {

	private int PERIOD = 15;
	private final int SIZE_LIMIT = 200;
	private Queue<job> doneJobs;
	private Queue<BigDecimal> sizeQueue = new LinkedList<BigDecimal>();
	private BigDecimal cumulatedQueueSize;

	public StatsTracker() {
		this.doneJobs = new LinkedList<job>();
		this.cumulatedQueueSize = BigDecimal.ZERO;
	}

	public void storeJob(job complete) {
		if( this.doneJobs.size() >= SIZE_LIMIT){
			this.doneJobs.remove();
		}
		this.doneJobs.add(complete);
	}

	public void addSize(double num) {
		BigDecimal bNum = new BigDecimal(num);
		cumulatedQueueSize = cumulatedQueueSize.add(bNum);
		Boolean b = sizeQueue.add(bNum);
		if (sizeQueue.size() > PERIOD) {
			cumulatedQueueSize = cumulatedQueueSize.subtract(sizeQueue.remove());
		}
	}

	public BigDecimal getAverageQueueSize() {

		if (sizeQueue.isEmpty()) return BigDecimal.ZERO; 

		BigDecimal divisor = BigDecimal.valueOf(sizeQueue.size());
		return cumulatedQueueSize.divide(divisor, 2, RoundingMode.HALF_UP);
	}

	public double geUtilization(){
		double busyTime = 0, makeSpan = 0, utilization = 0;
		job lastOne = null;

		if (doneJobs.size() < SIZE_LIMIT && doneJobs.size() > 0)
		{
			Iterator<job> it = doneJobs.iterator();
			while(it.hasNext()){
				lastOne = it.next();
				busyTime = busyTime + lastOne.getProcessingTime();
			}

			makeSpan = lastOne.getCompletionTime().getTime() - 
					doneJobs.peek().getStartTime().getTime();
		}
		else if (doneJobs.size() > SIZE_LIMIT)
		{
			int i=0;
			Iterator<job> it = doneJobs.iterator();
			while(i++ < SIZE_LIMIT && it.hasNext()){
				busyTime= busyTime + it.next().getProcessingTime();
			}
		}
		if(makeSpan > 0)
			utilization = busyTime/makeSpan*SIZE_LIMIT;
		return utilization;
	}

	public double getAvgProcessingTime(){
		double procTimes = 0;

		if (doneJobs.size() < 100 && doneJobs.size() > 0)
		{
			int size = doneJobs.size();
			Iterator<job> it = doneJobs.iterator();
			while(it.hasNext()){
				procTimes = procTimes + it.next().getProcessingTime();
			}
			procTimes = procTimes/size;
		}
		else if (doneJobs.size() > SIZE_LIMIT)
		{
			Iterator<job> it = doneJobs.iterator();
			while(it.hasNext()){
				procTimes = procTimes + it.next().getProcessingTime();
			}
			procTimes = procTimes/SIZE_LIMIT;
		}
		return procTimes;
	}
}

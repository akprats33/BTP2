package mas.customer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import mas.job.OperationType;
import mas.job.job;
import mas.job.jobAttribute;
import mas.job.jobDimension;
import mas.job.jobOperation;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JobGenerator extends JobGeneratorIFace {

	// processing time is input as seconds. Convert it into milliseconds
	private int timeUnitConversion = 1; //keep this 1 as it helps in debugging ~Nikhil

	private EnumeratedIntegerDistribution distribution;
	private int NumJobs;
	private ArrayList<XSSFSheet> sheets;

	private String jobFilePath;
	private ArrayList<String> jobIdList;
	private ArrayList<jobOperation> jobOperations;
	private ArrayList<Double> jobCPNs;
	private ArrayList<Long> jobDueDates;  //due date specified by customer to complete job. 
	//Its GSA's job to calculate local / global due dates
	//its in SECONDS
	private ArrayList<Integer> jobQuantity;
	private ArrayList<Double> jobPenalties;
	int countJob = 1;
	private Logger log=LogManager.getLogger();

	public JobGenerator() {
		this.jobIdList = new ArrayList<String>();
		this.jobOperations = new ArrayList<jobOperation>();
		this.jobQuantity = new ArrayList<Integer>();
		this.jobCPNs = new ArrayList<Double>();
		this.jobDueDates = new ArrayList<Long>();
		this.jobPenalties = new ArrayList<Double>();

		this.sheets = new ArrayList<XSSFSheet>();
		this.jobFilePath = System.getProperty("user.dir");
	}

	@Override
	public void readFile() {
		XSSFWorkbook wb;
		try{
			FileInputStream file=new FileInputStream(this.jobFilePath +
					"\\jobdata.xlsx");	
			wb = new XSSFWorkbook(file);
			this.NumJobs = wb.getNumberOfSheets();

			XSSFSheet localSheet;
			for(int i = 0 ; i < NumJobs ; i++) {
				localSheet = wb.getSheetAt(i);
				sheets.add(localSheet);
				readSheet(localSheet);
			}

			randomGenInit();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void readSheet(XSSFSheet currSheet) {

		Iterator<Row> rows = currSheet.rowIterator();
		XSSFRow row = (XSSFRow) rows.next();

		// first read the second row of job file
		// skip the first header line
		row = (XSSFRow) rows.next();

		Iterator<Cell> cells = row.cellIterator();

		int count = 0; 
		while(cells.hasNext()) {
			XSSFCell cell = (XSSFCell) cells.next();

			switch(count) {
			case 0:
				jobIdList.add(cell.getNumericCellValue() + "");
				break;
			case 1:
				jobQuantity.add((int) cell.getNumericCellValue());
				break;
			case 2:
				jobCPNs.add(cell.getNumericCellValue());
				break;
			case 3:
				jobDueDates.add((long) (cell.getNumericCellValue()*timeUnitConversion));
//				log.info((long) (cell.getNumericCellValue()*timeUnitConversion));
				break;
			case 4:
				jobPenalties.add(cell.getNumericCellValue());
				break;
			}
			count ++;
		}

		// Now read operations for the job
		// Skip the header row for operations
		row = (XSSFRow) rows.next();

		while( rows.hasNext() ) {

			row = (XSSFRow) rows.next();
			cells = row.cellIterator();

			jobOperation currOperation = new jobOperation();
			count = 0; 
			while(cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();

				switch(count) {
				case 0:
					// Operation type for the job
					currOperation.setJobOperationType(OperationType.Operation_1);
					break;

				case 1:
					// Processing time for this operation
					currOperation.
					setProcessingTime((long) cell.getNumericCellValue()*timeUnitConversion);
					break;

				case 2:
					// Dimensions for this operation
//					log.info(cell.getCellType());
					cell.setCellType(1);
					String s = cell.getStringCellValue();
					String temp[] = s.split(",");
					//			            		  System.out.println("length="+temp.length);
					ArrayList<jobDimension> tempDimList = new ArrayList<jobDimension>();
					jobDimension tempDim = new jobDimension();
					for(int i=0; i < temp.length; i++){
						tempDim.setTargetDimension(Double.parseDouble(temp[i]));
						tempDimList.add(tempDim );
					}
					currOperation.setjDims(tempDimList);
					break;
					
				case 3:
					// Attributes for this operation
					String Attr=cell.getStringCellValue();
					String tempAttr[]=Attr.split(",");

					ArrayList<jobAttribute> tempAttrList = new ArrayList<jobAttribute>();

					for(int i=0; i < tempAttr.length; i++){
						jobAttribute tempAttribute = new jobAttribute(tempAttr[i]);
						tempAttrList.add(tempAttribute );
					}
					currOperation.setjAttributes(tempAttrList);
					
					break;
				}
				count++;
			}
			this.jobOperations.add(currOperation);
		}
	}

	private void randomGenInit() {
		int size = jobQuantity.size();
		int[] numsToGenerate = new int[size];
		double sum = 0.0;
		double[] discreteProbabilities = new double[size];
		int i;
		for(i = 0;i < size; i++) {
			discreteProbabilities[i] = jobQuantity.get(0);
			sum += jobQuantity.get(0);
			numsToGenerate[i] = i;
		}
		for(i = 0;i < size; i++) {
			discreteProbabilities[i] = jobQuantity.get(i)/sum;
			//			System.out.print("discreteProbabilities["+i+"]="+temp[i]+"/"+sum +":"+weights[i]);
		}

		distribution = new EnumeratedIntegerDistribution(
				numsToGenerate, discreteProbabilities);
	}

	/**
	 * Generate and return the next job to be dispatched
	 */
	@Override
	public Object getNextJob() {
		int index = runif();
//		log.info(jobDueDates.get(index));
		long due = (long) (jobDueDates.get(index)*1000) + System.currentTimeMillis();
//		log.info(new Date(due));
		long generationTime = System.currentTimeMillis();

		job j = new job.Builder(jobIdList.get(index))
		.jobCPN(jobCPNs.get(index))
		.jobDueDateTime(due)
		.jobGenTime(generationTime)
		.jobOperation(this.jobOperations)
		.jobPenalty(this.jobPenalties.get(index))
		.build() ;

		j.setJobNo(countJob++);

//		log.info(j.getPenaltyRate());
		return j;
	}

	/**
	 * Generate random number(between 0 and 1) following discrete distribution of weights 
	 * @return
	 */
	public int runif() {
		return distribution.sample();
	}
}


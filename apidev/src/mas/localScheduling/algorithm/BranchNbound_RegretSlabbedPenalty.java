package mas.localScheduling.algorithm;


import java.util.ArrayList;
import java.util.Collections;

import mas.customer.JobGenerator;
import mas.job.job;

public class BranchNbound_RegretSlabbedPenalty implements ScheduleSequenceIFace{
	/** Minimizes penalty of a given sequence of jobs by using branch and bound algorithm 
	 *  To use it, create a node with state equal to ArrayList of a sequence of jobs
	 *  The solution for the given job sequence will be calculated and stored in the ArrayList "best"
	 *  Minimum penalty will be given by the variables "lowBound".
	 *  @author Anand Prajapati 
	 *  @Email  anandprajapati389@gmail.com
	 */ 
	
	ArrayList<job> best = new ArrayList<job>();		// best solution stored in this ArrayList when algo runs
	Double lowBound = Double.MAX_VALUE;			// Upper bound for solutions
	public Node rootNode;
	public job fixedJob;
	
	public BranchNbound_RegretSlabbedPenalty(ArrayList<job> s){
		fixedJob = s.get(0);
//		s.remove(0);
		/**
		 * Store the position of all the jobs in the initial sequence so that 
		 * we can compare regret factor of jobs after sequencing
		 */
		for(int i= 0; i < s.size() ;i++){
			s.get(i).setPosition(i);
		}
		
		this.rootNode = new Node(s);
	}
	
	public class Node 
	{
		ArrayList<job> state;		//sequence of jobs
		Node[] child;				//  child nodes at any point
		Node parent;				// parent node
		int depth;					// depth of node in tree
		int rank;					// to differentiate between different child at same level 
		double penalty;				// penalty of node
		
		public Node(ArrayList<job> s) {
			depth = 0;		
			rank = -1;
			penalty = 0;
			this.state = new ArrayList<job>(s);
			int n = state.size();
			child = new Node [n];		// i'th child will be equal to that of its parent and one job kept at last
			
			for( int i=0 ; i < n ; i++)
			{
				child[i]= null;
			}
		}
		
		Node getChildNode(int index)				// giving child of any node at a position index starting zero from left in the solution tree 
		{
			if (child[index] == null)
				child[index] = new Node(this.state);
			
			child[index].state = new ArrayList<job>(this.state);
			child[index].rank = index;				// rank becomes equal to node's position from left in the solution tree
			child[index].depth = this.depth + 1;
			child[index].parent = this;
			
			Collections.swap(child[index].state,				// job at rank's position is kept fixed (with suitable offset in this case)
								child[index].depth-1,
								child[index].depth + index -1);
			
			return child[index];
		}

		public double getValue()
		{
			// Penalty of a child is calculated by adding its
			// individual penalty to parent's penalty
			int n = this.state.size();
			long makeSpan = 0;
			long startingTime = 0;
			/**
			 * Calculate penalty of job by putting all jobs up to index 'depth-1'
			 * before it and based on lateness and regret multiplier
			 * find its penalty
			 */
			
			for (int i = this.depth - 1; i < n ; i++) {
				makeSpan += this.state.get(i).getProcessingTime();
			}
			/**
			 * update start time of all jobs.
			 * update regret of all jobs
			 */
			/**
			 * Penalty will be calculated by using a multiplier which in turn depends on regret factor.
			 * First update start and processing times of the job and then the regret factor.
			 * 
			 */
			startingTime = makeSpan - this.state.get(this.depth-1).getProcessingTime();
			int elements = this.depth;
			this.state.get(elements-1).setStartTime(startingTime);
			
			for (int k = elements-2; k >= 0; k--){
	    		this.state.get(k).setStartTime(this.state.get(k+1).getStartTime().getTime()	+ 
	    										this.state.get(k+1).getProcessingTime());
			}
			updateRegret(this);
			//-------------------------------------------------------
			
			job j = this.state.get(depth-1);
			double lateness = (makeSpan - j.getDuedate().getTime())*j.getCPN()*j.getRegretMultiplier();
			if(lateness < 0) {
//				System.out.println("negative " + lateness + "with "+parent.penalty);
				lateness = 0;
			}
			if( this.parent != null)
				lateness += this.parent.penalty;
			
			this.penalty = lateness;
			return lateness;
		}
	}
	
	public ArrayList<job> solve() {
		/**
		 * Don't forget to add the first job which is fixed throughout
		 * in the final sequence of jobs
		 */
		if(this.rootNode.state.size() == 0) {
			this.best.add(fixedJob);
			return this.best;
		}
		else if(this.rootNode.state.size() == 1) {
			this.best.add(fixedJob);
			this.best.add(this.rootNode.state.get(0));
			return this.best;
		}
		RecursiveSolver(this.rootNode);
		/** reverse the sequence as the correct schedule in the tree goes from
		 * bottom to top
		 * 
		 */
		Collections.reverse(this.best);
//		this.best.add(0,fixedJob);
		return this.best;
	}
	
	private void RecursiveSolver(Node node)
	{
		int size = node.state.size();
		if( node.depth == size){
			double val = node.penalty;
//			System.out.println("Hit " + val + "d " + node.depth);
			if(val < this.lowBound) {
				best = node.state;
				lowBound = node.getValue();
			}
			return ;
		}
		
		if(node.penalty > lowBound)
			return;
		
		// end is the number of children that any particular node will have in the tree
		int end = node.state.size() - node.depth ;
//		System.out.println("Childs are " + end + "wt depth " + node.depth);
		for ( int i = 0; i < end ; i++) {
			Node child = node.getChildNode(i);
			child.getValue();
			
			RecursiveSolver(child);
		}
	}
	
	public void updateRegret(Node node)
	{
		int elements = node.depth;
		double lateness;					
		for ( int i = 0; i < elements ; i++){
				lateness =	node.state.get(i).getStartTime().getTime() +
							node.state.get(i).getProcessingTime() -
							node.state.get(i).getDuedate().getTime();
				
				if(lateness < 0)
					lateness = 0;
				
				node.state.get(i).setRegret(lateness/node.state.get(i).getSlack());
		}
	}
	
	public static void main(String[] args){
		ArrayList<job> q = new ArrayList<job>();
		
		JobGenerator jGen = new JobGenerator();
		jGen.readFile();
		int j=0;
		while(j++ < 4){
			job jj = (job) jGen.getNextJob();
			jj.slack=1;
			q.add(jj);
		}
		
		for(int i =0 ; i < q.size() ; i++ ) {
			System.out.println("-->" + q.get(i).getJobID());
			System.out.println("due date"+ q.get(i).getDuedate());
		}
		System.out.println();
		
		BranchNbound_RegretSlabbedPenalty bnb = new BranchNbound_RegretSlabbedPenalty(q);
		ArrayList<job> best = bnb.solve();
		
		for(int i =0 ; i < best.size() ; i++ ) {
			System.out.print("-->" + best.get(i).getJobID());
		}
		System.out.println("Penalty" + bnb.lowBound);
	}
	
}
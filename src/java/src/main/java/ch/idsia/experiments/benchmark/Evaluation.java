package ch.idsia.experiments.benchmark;


import ch.idsia.crema.inference.approxlp.CredalApproxLP;
import ch.idsia.crema.model.graphical.DAGModel;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.concurrent.Callable;

public class Evaluation implements Callable<Boolean> {

	DAGModel hmodel;
	int target;
	TIntIntHashMap evidence;

	public Evaluation(DAGModel hmodel, int target, TIntIntHashMap evidence){
		this.hmodel = hmodel;
		this.target = target;
		this.evidence = evidence;
	}

	public Boolean call() throws InterruptedException {
		boolean eval = true;
		try {
			CredalApproxLP inf = new CredalApproxLP();
			inf.query(hmodel, evidence, target);
		}catch (Exception e){
			//e.printStackTrace();
			eval = false;
		}
		return eval;
	}

}
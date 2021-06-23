package ch.idsia.experiments.benchmark;


import ch.idsia.crema.factor.FilterableFactor;
import ch.idsia.crema.factor.GenericFactor;
import ch.idsia.crema.inference.approxlp.CredalApproxLP;
import ch.idsia.crema.model.graphical.DAGModel;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.concurrent.Callable;

public class Evaluation<F extends FilterableFactor<F>> implements Callable<Boolean> {

	DAGModel<F> hmodel;
	int target;
	TIntIntHashMap evidence;

	public Evaluation(DAGModel<F> hmodel, int target, TIntIntHashMap evidence){
		this.hmodel = hmodel;
		this.target = target;
		this.evidence = evidence;
	}

	public Boolean call() throws InterruptedException {
		boolean eval = true;
		try {
			CredalApproxLP<F> inf = new CredalApproxLP<>();
			inf.query(hmodel, evidence, target);
		}catch (Exception e){
			//e.printStackTrace();
			eval = false;
		}
		return eval;
	}

}
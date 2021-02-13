package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.factor.credal.linear.IntervalFactor;
import ch.idsia.crema.inference.approxlp.CredalApproxLP;
import ch.idsia.crema.inference.ve.CredalVariableElimination;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.utility.ArraysUtil;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class GenQueries {
	public static void main(String[] args) throws IOException, InterruptedException {

		ArrayList<String> fail = new ArrayList<>();

		String prj_folder = ".";
		String modelFolder = prj_folder+"/networks/vmodel/";
		String queryFolder = prj_folder+"/queries/";

		for(String modelFile : getFiles(modelFolder)) {
			System.out.println("Reading "+modelFile);
			String queryStr = getMaximalQuery(modelFile);
			if(queryStr != null) {
				System.out.println(queryStr);
				saveQueryCSV(queryFolder, modelFile, queryStr);
			}else{
				System.out.println("FAILED TO FIND QUERY");
				fail.add(modelFile);
			}
		}

		System.out.println("Failed\n==============");
		fail.forEach(System.out::println);

	}

	public static void saveQueryCSV(String queryFolder, String modelFile, String queryStr) throws FileNotFoundException {
		String content = "target,observed,size\n";
		content+=queryStr;
		content+="\n";


		String filename = modelFile.substring(modelFile.lastIndexOf("/"));
		filename = filename
				.replace(".uai", ".csv")
				.replace("vmodel-","query-")
				.replace("hmodel-","query-");

		File csvOutputFile = new File(queryFolder+filename);
		try(PrintWriter pw = new PrintWriter(csvOutputFile)){
			pw.print(content);
		}
	}

	public static String getMaximalQuery(String vmodelPath) throws IOException, InterruptedException {
		// marginal or conditional query with a maximal inference network

		DAGModel model = (DAGModel) IO.readUAI(vmodelPath);
		CredalVariableElimination ve = new CredalVariableElimination(model);

		int target = -1;
		int maxSize = 0;
		int observed = -1;


		//[List(double<x,y>), ... List(double<x,y>), ]
		ArrayList[] sizes = IntStream.rangeClosed(0,model.getVariables().length).mapToObj(i-> new ArrayList<int[]>()).toArray(ArrayList[]::new);


		for(int x : model.getVariables()){
			for(int y: ArraysUtil.append(model.getVariables(), -1)) {
				if(x!=y){
					TIntIntHashMap evidence = new TIntIntHashMap();
					if(y!=-1)
						evidence.put(y,0);
					int s = ve.getInferenceModel(x, evidence).getVariables().length;
					sizes[s].add(new int[]{x,y});

				}
			}
		}

		for(int i = sizes.length-1; i>=0; i--){
			System.out.println(i);

			for(Object p : sizes[i]){
				target = ((int[])p)[0];
				observed =  ((int[])p)[1];
				System.out.println(target+"|"+observed);

				if(isApproxEval(vmodelPath, target, observed)){
					String queryStr = target +",";
					if(observed != -1){
						queryStr += observed;
					}
					queryStr+=","+i;

					System.out.println(maxSize+"/"+model.getVariables().length+" nodes:\t"+queryStr);
					return queryStr;
				}

			}
		}

		return null;



	}

	public static boolean isApproxEval(String vmodelPath, int target, int observed) throws IOException, InterruptedException {
		String hmodelPath = vmodelPath.replace("vmodel","hmodel");
		DAGModel hmodel = (DAGModel)IO.read(hmodelPath);
		CredalApproxLP inf = new CredalApproxLP(hmodel);

		boolean eval = true;

		TIntIntHashMap evidence = new TIntIntHashMap();
		if (observed != -1)
			evidence.put(observed, 0);


		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Boolean> future = executorService.submit(new Evaluation(hmodel, target, evidence));
		try {
			eval = future.get(30L, TimeUnit.MINUTES);
		} catch (ExecutionException e) {
			e.printStackTrace();
			eval = false;
		} catch (TimeoutException e) {
			e.printStackTrace();
			eval = false;
		}
		return eval;
	}


	public static List<String> getFiles(String folder) throws IOException {
		return StreamSupport
				.stream(Files.newDirectoryStream(
						Paths.get(folder),
						path -> path.toString().endsWith(".uai")
				).spliterator(), false)
				.map(f -> f.toString())
				.collect(Collectors.toList());
	}




}


